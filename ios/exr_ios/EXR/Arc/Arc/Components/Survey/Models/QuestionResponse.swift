//
// QuestionResponse.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public protocol QuestionResponse : Codable {
	var type:QuestionType? {get set}
	var value:Any? {get set}
	var text_value:String? {get set}
	
}
public extension QuestionResponse {
    //Each control can have different representations of empty
	func getValue<T>() -> T? {
		return value as? T
	}
    func isEmpty() -> Bool {
		if let type = type {
			switch type {
			case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number, .calendar:
				if let value = value as? String {
					return value == "-99" || value.count == 0
				} else {
					return true
				}
				
			case .slider:
				if let value = value as? Float {
					return value == -99
				} else {
					return true
				}
				
			case .choice, .picker:
				if let value = value as? Int {
					return value == -99
				} else {
					return true
				}
				
			case .checkbox:
				if let value = value as? [Int] {
					let values = value.compactMap({ (v) -> Int? in
						return (v == -99) ? nil : v
					})
					return values.count == 0
				} else {
					return true
				}
            case .image, .signature:
                if let value = value as? Data {
                    return value.count == 0
                } else {
                    return false
                }
			}
            
		}
		return false
	}
}
