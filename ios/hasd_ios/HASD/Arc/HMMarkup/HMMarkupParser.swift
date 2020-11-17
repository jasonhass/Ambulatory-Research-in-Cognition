//
// HMMarkupParser.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public struct HMMarkupParser {
	public static func parse(text: String) -> [HMMarkupNode] {
		var parser = HMMarkupParser(text: text)
		return parser.parse()
	}
	
	private var tokenizer: HMMarkupTokenizer
	private var openingDelimiters: [UnicodeScalar] = []
	
	private init(text: String) {
		tokenizer = HMMarkupTokenizer(string: text)
	}
	
	private mutating func parse() -> [HMMarkupNode] {
		var elements: [HMMarkupNode] = []
		
		while let token = tokenizer.nextToken() {
			switch token {
			case .text(let text):
				elements.append(.text(text))
				
			case .leftDelimiter(let delimiter):
				// Recursively parse all the tokens following the delimiter
				openingDelimiters.append(delimiter)
				elements.append(contentsOf: parse())
				
			case .rightDelimiter(let delimiter) where openingDelimiters.contains(delimiter):
				guard let containerNode = close(delimiter: delimiter, elements: elements) else {
					fatalError("There is no MarkupNode for \(delimiter)")
				}
				return [containerNode]
				
			default:
				elements.append(.text(token.description))
			}
		}
		
		// Convert orphaned opening delimiters to plain text
		let textElements: [HMMarkupNode] = openingDelimiters.map { .text(String($0)) }
		elements.insert(contentsOf: textElements, at: 0)
		openingDelimiters.removeAll()
		
		return elements
	}
	
	private mutating func close(delimiter: UnicodeScalar, elements: [HMMarkupNode]) -> HMMarkupNode? {
		var newElements = elements
		
		// Convert orphaned opening delimiters to plain text
		while openingDelimiters.count > 0 {
			let openingDelimiter = openingDelimiters.popLast()!
			
			if openingDelimiter == delimiter {
				break
			} else {
				newElements.insert(.text(String(openingDelimiter)), at: 0)
			}
		}
		
		return HMMarkupNode(delimiter: delimiter, children: newElements)
	}
}
