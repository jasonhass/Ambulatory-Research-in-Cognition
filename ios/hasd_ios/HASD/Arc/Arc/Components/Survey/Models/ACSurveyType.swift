//
// ACSurveyType.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




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
