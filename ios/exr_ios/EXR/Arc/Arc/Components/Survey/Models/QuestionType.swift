//
// QuestionType.swift
//



import Foundation


public enum QuestionType : String, Codable {
	case none, text, number, slider, choice, checkbox, time, duration, password, segmentedText, multilineText, image, calendar, picker, signature
	
	public var metatype: Codable.Type {
		switch self {
		case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number, .image,  .calendar, .signature:
			return String.self
			
		case .slider:
			return Float.self
		case .choice, .picker:
			return Int.self
			
		case .checkbox:
			return [Int].self
		}
	
	}
	func create(inputWithQuestion question:Survey.Question?) -> SurveyInput? {
		var input:SurveyInput?
		
		switch self {
		case .none:
			break
	
		case .slider:
			let inputView:SliderView = .get()
			guard let question = question else {
				return nil
			}
			inputView.set(min: question.minValue ?? 0,
						  max: question.maxValue ?? 100,
						  minMessage: question.minMessage ?? "",
						  maxMessage: question.maxMessage ?? "")
			
			input = inputView
			

			
		case  .time:
			let inputView:TimeView = .get()
			
			input = inputView
			
		
		case .duration:
			let inputView:DurationView = .get()
		
			input = inputView
		
		
		case .choice:
			let inputView:MultipleChoiceView = .get()
			input = inputView
			guard let question = question else {
				return nil
			}
			inputView.set(question:question)
		
		
		
		case .checkbox:
			let inputView:MultipleChoiceView = .get()
			input = inputView
			inputView.state = .checkBox
			guard let question = question else {
				return nil
			}
			inputView.set(question:question)
		
		
		
		case .password:
			let inputView:PasswordView = .get()
			inputView.openKeyboard()
			input = inputView
		
		case .segmentedText:
			let inputView:SegmentedTextView =  .get()
			inputView.openKeyboard()
			input = inputView
		
		case .text, .multilineText:
			let inputView:MultilineTextView = .get()
			input = inputView
			inputView.minCharacters = 1
		
		
		
		case .number:
			let inputView:MultilineTextView = .get()
			input = inputView
			inputView.maxCharacters = 2
			inputView.minCharacters = 1
			inputView.keyboardType = .numberPad
		
		case .image:
			let inputView:SignatureView =  .get()
			input = inputView
		
		
		case .calendar:
			let inputView:ACCalendarView =  ACCalendarView(frame: .zero)
			input = inputView
		
		case .picker:
			let inputView:ACPickerView = .get()
		
			input = inputView
		
		
	
		case .signature:
			let inputView:SignatureView = .get()
			
			input = inputView
		}
		return input

	}
}

