//
// SurveyResponse.swift
//



import Foundation

public struct AnyResponse : QuestionResponse {
	public var text_value: String?
	
	public var type: QuestionType?
	
	public var value: Any?
	
	private enum CodingKeys : CodingKey {
		case type, value, text_value
	}
	public init(type:QuestionType, value:Any?, textValue:String? = nil) {
		self.type = type
		self.value = value
		self.text_value = textValue
	}
	
	public init(from decoder: Decoder) throws {
		let container = try decoder.container(keyedBy: CodingKeys.self)
		
		self.type = try container.decodeIfPresent(QuestionType.self, forKey: .value)
		self.text_value = try container.decodeIfPresent(String.self, forKey: .text_value)
		if let type = type {
			switch type {
			case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number:
				self.value = try container.decodeIfPresent(String.self, forKey: .value)

			case .slider:
				self.value = try container.decodeIfPresent(Float.self, forKey: .value)
				
			case .choice, .picker:
				self.value = try container.decodeIfPresent(Int.self, forKey: .value)

				
			case .checkbox:
				self.value = try container.decodeIfPresent([Int].self, forKey: .value)
            case .image, .calendar, .signature:
                break
			}
	
		}
	}
	public func encode(to encoder: Encoder) throws {
		var container = encoder.container(keyedBy: CodingKeys.self)
		try container.encode(type, forKey: .type)
		try container.encodeIfPresent(text_value, forKey: .text_value)

		if let type = type {

			switch type {
			case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number:
				let v = self.value as? String
				try container.encodeIfPresent(v, forKey: .value)

			case .slider:
				let v = self.value as? Float
				try container.encodeIfPresent(v, forKey: .value)
			
			case .choice, .picker:
				let v = self.value as? Int
				try container.encodeIfPresent(v, forKey: .value)

				
			case .checkbox:
				let v = self.value as? [Int]
				try container.encodeIfPresent(v, forKey: .value)
            case .image, .calendar, .signature:
                break
			}
		}
		
	}
}

public struct SurveyResponse : HMCodable {
	
	
	public static var dataType: SurveyType = .unknown
	
//	enum SurveyType : String, Codable {
//		case ema, edna, mindfulness, schedule, gridTest, priceTest, symbolsTest, context
//	}
	public struct Question : QuestionResponse {
		public var text_value: String?
		
        public var question_id:String
        private var _question:String
        public var question:String {
            get {
                return _question
            }
            set {
                _question = newValue.replacingOccurrences(of: "*", with: "")
            }
        }
		public var type:QuestionType? = .none
		public var response_time:TimeInterval?
		public var display_time:TimeInterval?
        public var value:Any?
		
        init(question_id:String, question:String, response:Any?) {
            self._question = question
            self.question_id = question_id
            self.question = question
            self.value = response
        }
		
		private enum CodingKeys : CodingKey {
			case question_id, question, type, response_time, display_time, value, text_value
		}
		
    }
    public var id:String?
    public var start_date : TimeInterval?         //Optional
    public var type:SurveyType?                      //Required
    public var questions:Array<Question>?
    public var model_version:String?
    
    init(id:String, type:SurveyType) {
        self.id = id
        self.questions = []
		self.type = type
    }
	
	
	
}
//Question response codability
public extension SurveyResponse.Question {
    init(from decoder: Decoder) throws {
		let container = try decoder.container(keyedBy: CodingKeys.self)
		
		self.question_id = try container.decode(String.self, forKey: .question_id)
		self._question = try container.decode(String.self, forKey: .question)
		self.type = try container.decodeIfPresent(QuestionType.self, forKey: .type)
		self.response_time = try container.decodeIfPresent(TimeInterval.self, forKey: .response_time)
		self.display_time = try container.decodeIfPresent(TimeInterval.self, forKey: .display_time)
		self.text_value = try container.decodeIfPresent(String.self, forKey: .text_value)
		if let type = type {

			//Decoding Any from type
			switch type {
			case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number:
				self.value = try container.decodeIfPresent(String.self, forKey: .value)
				
			case .slider:
				self.value = try container.decodeIfPresent(Float.self, forKey: .value)
				
				
			case .choice, .picker:
				self.value = try container.decodeIfPresent(Int.self, forKey: .value)
				
				
			case .checkbox:
				self.value = try container.decodeIfPresent([Int].self, forKey: .value)
            case .image, .calendar, .signature:
                break
			}
		}
		
	}
    func encode(to encoder: Encoder) throws {
		var container = encoder.container(keyedBy: CodingKeys.self)
		try container.encode(question_id, forKey: .question_id)
		try container.encode(question, forKey: .question)
		try container.encodeIfPresent(type, forKey: .type)
		try container.encodeIfPresent(response_time, forKey: .response_time)
		try container.encodeIfPresent(display_time, forKey: .display_time)
		try container.encodeIfPresent(text_value, forKey: .text_value)

		if let type = type {

			//Encoding Any to various types
			switch type {
			case .none, .text, .time, .duration, .password, .segmentedText, .multilineText, .number:
				let v = self.value as? String
				try container.encodeIfPresent(v, forKey: .value)
			
			case .slider:
				let v = self.value as? Float
				try container.encodeIfPresent(v, forKey: .value)
				
			case .choice, .picker:
				let v = self.value as? Int
				try container.encodeIfPresent(v, forKey: .value)
				
				
			case .checkbox:
				let v = self.value as? [Int]
				try container.encodeIfPresent(v, forKey: .value)
            case .image, .calendar, .signature:
                break
			}
		
		}
		
	}
}
