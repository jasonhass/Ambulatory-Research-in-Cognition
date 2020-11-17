//
// ACSurveyType.swift
//



import Foundation

public enum SurveyType : String, Codable {
	case unknown, auth, ema, edna, mindfulness, schedule, mindfulnessReminder, context, finished, finishedNoQuestions, gridTest, priceTest, symbolsTest, cognitive, wake, chronotype, region, language, onboarding

	public var metatype: HMCodable.Type {
		switch self {
		case .gridTest:
			return GridTestResponse.self
		case .symbolsTest:
			return GridTestResponse.self
		case .priceTest:
			return GridTestResponse.self
		case .cognitive:
			return CognitiveTest.self
		default :
			return SurveyResponse.self
		}
	}
}
