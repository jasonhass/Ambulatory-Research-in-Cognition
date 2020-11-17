//
// QuestionResponse.swift
//



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
