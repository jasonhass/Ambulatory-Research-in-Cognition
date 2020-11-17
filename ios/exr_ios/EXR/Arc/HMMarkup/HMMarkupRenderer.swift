//
// HMMarkupRenderer.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
#if os(iOS) || os(tvOS) || os(watchOS)
import UIKit.UIFont
public typealias Font = UIFont
#elseif os(OSX)
import AppKit.NSFont
public typealias Font = NSFont
#endif

public final class HMMarkupRenderer {
    public struct Config {
        public var translation:Dictionary<String, String>?
        
        //Setting to false could result in seeing keys
        public var shouldTranslate:Bool = true
        public var translationIndex:Int = 1
        public init() {
            
        }
        
    }
    static public var config:Config?
	private let baseFont: Font
	
	public init(baseFont: Font) {
		self.baseFont = baseFont
	}
	
	public func render(text: String) -> NSAttributedString {
        var text = text
        if let config = HMMarkupRenderer.config, config.shouldTranslate {
            text = config.translation?[text] ?? text
        }
		let elements = HMMarkupParser.parse(text: text)
		let attributes = [NSAttributedString.Key.font: baseFont]
		
		return elements.map { $0.render(withAttributes: attributes) }.joined()
	}
	public func render(text: String, template:Dictionary<String, String>) -> NSAttributedString {
        var text = text
        if let config = HMMarkupRenderer.config, config.shouldTranslate {
           text = config.translation?[text] ?? text
        }
		for (key, value) in template {
			text = text.replacingOccurrences(of: "{\(key)}", with: value)
		}
		return render(text: text)
	}
}

private extension HMMarkupNode {
	func render(withAttributes attributes: [NSAttributedString.Key: Any]) -> NSAttributedString {
		guard let currentFont = attributes[NSAttributedString.Key.font] as? Font else {
			fatalError("Missing font attribute in \(attributes)")
		}
		
		switch self {
		case .text(let text):
			return NSAttributedString(string: text, attributes: attributes)
			
		case .strong(let children):
			var newAttributes = attributes
			newAttributes[NSAttributedString.Key.font] = currentFont.boldFont()
			return children.map { $0.render(withAttributes: newAttributes) }.joined()
			
		case .emphasis(let children):
			var newAttributes = attributes
			newAttributes[NSAttributedString.Key.font] = currentFont.italicFont()
			return children.map { $0.render(withAttributes: newAttributes) }.joined()
			
		case .delete(let children):
			var newAttributes = attributes
			newAttributes[NSAttributedString.Key.strikethroughStyle] = NSUnderlineStyle.single.rawValue
			newAttributes[NSAttributedString.Key.baselineOffset] = 0
			return children.map { $0.render(withAttributes: newAttributes) }.joined()
		case .underline(let children):
			var newAttributes = attributes
			newAttributes[NSAttributedString.Key.underlineStyle] = NSUnderlineStyle.single.rawValue
			newAttributes[NSAttributedString.Key.baselineOffset] = 0
			return children.map { $0.render(withAttributes: newAttributes) }.joined()
		}
	}
}

extension Array where Element: NSAttributedString {
	func joined() -> NSAttributedString {
		let result = NSMutableAttributedString()
		for element in self {
			result.append(element)
		}
		return result
	}
}

#if os(iOS) || os(tvOS) || os(watchOS)
extension UIFont {
	func boldFont() -> UIFont? {
		return addingSymbolicTraits(.traitBold)
	}
	
	func italicFont() -> UIFont? {
		return addingSymbolicTraits(.traitItalic)
	}
	
	func addingSymbolicTraits(_ traits: UIFontDescriptor.SymbolicTraits) -> UIFont? {
		let newTraits = fontDescriptor.symbolicTraits.union(traits)
		guard let descriptor = fontDescriptor.withSymbolicTraits(newTraits) else {
			return nil
		}
		
		return UIFont(descriptor: descriptor, size: 0)
	}
}
#elseif os(OSX)
extension NSFont {
	func boldFont() -> NSFont? {
		return NSFontManager.shared.convert(self, toHaveTrait: .boldFontMask)
	}
	
	func italicFont() -> NSFont? {
		return NSFontManager.shared.convert(self, toHaveTrait: .italicFontMask)
	}
}
#endif
