//
// TestSession.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
public struct AnyTest : Codable {
	public var data:HMCodable
	
	public init (_ data:HMCodable) {
		self.data = data
	}
	private enum CodingKeys : CodingKey {
		case type, data
	}
	public init(from decoder: Decoder) throws {
		let container = try decoder.container(keyedBy: CodingKeys.self)
		
		let type = try container.decode(SurveyType.self, forKey: .type)
		self.data = try type.metatype.init(from: decoder)
	}
	public func encode(to encoder: Encoder) throws {
		var container = encoder.container(keyedBy: CodingKeys.self)
		try container.encode(type(of: data).dataType, forKey: .type)
		try data.encode(to: encoder)
		
	}
}
public struct CognitiveTest : HMCodable {
	
	
	public static var dataType: SurveyType = .cognitive
	
	public var id:String?
	public var type:SurveyType? = .cognitive
	var context_survey:SurveyResponse?
	var wake_survey:SurveyResponse?
	var chronotype_survey:SurveyResponse?
	var grid_test:GridTestResponse?
	var price_test:PriceTestResponse?
	var symbol_test:SymbolsTestResponse?
	init() {
		
	}
}
public struct FullTestSession : Codable {
	var id:String?
	var week:Int64?
	var	day:Int64?
	var session:Int64?
	var	session_id:String? //"[some unique identifier that will always identify this session]",
	var	session_date:TimeInterval? //1503937447.437798,
	var start_time:TimeInterval? // 1503937447.732328,
	var participant_id:String? //"111111",
	var interrupted:Int64?// 0,
	var missed_session:Int64? // 0,
	var finished_session:Int64? // 1,
	var device_id:String? // "[device id]",
	var device_info:String? // "iOS|iPhone8,4|10.1.1",
	var app_version:String? // "1.2.4",
	var model_version:String? // "1",
	
	
	var tests: [AnyTest]

	
	
	init(withSession session: Session) {
		
		
		week = session.week
		day = session.day
		self.session = session.session
		session_id = "\(session.sessionID)"
		session_date = session.sessionDate?.timeIntervalSince1970
		start_time = session.startTime?.timeIntervalSince1970
		participant_id = "\(Arc.shared.participantId!)"
		interrupted = (session.interrupted) ? 1 : 0
		missed_session = (session.missedSession) ? 1 : 0
		finished_session = (session.finishedSession) ? 1 : 0
		
		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"
		
		//All sessions have been prefilled with json data at the begining of the study
		tests = []
	
		
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .ema)?.get() {
			survey.id = nil
			tests.append(AnyTest(survey))
		}
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .edna)?.get() {
			survey.id = nil
			tests.append(AnyTest(survey))
		}
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .mindfulness)?.get() {
			survey.id = nil
			tests.append(AnyTest(survey))
		}

		
		
		var cognitive:CognitiveTest = CognitiveTest()
		
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .context)?.get() {
			survey.id = nil
			cognitive.context_survey = survey
			cognitive.id = survey.id
		}
		
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .chronotype)?.get() {
			survey.id = nil

			cognitive.chronotype_survey = survey
		}
		if var survey:SurveyResponse = session.getSurveyFor(surveyType: .wake)?.get() {
			survey.id = nil

			cognitive.wake_survey = survey
		}
		
		if var survey:GridTestResponse = session.getSurveyFor(surveyType: .gridTest)?.get() {
			survey.id = nil

			cognitive.grid_test = survey
		}
		if var survey:PriceTestResponse = session.getSurveyFor(surveyType: .priceTest)?.get() {
			survey.id = nil

			cognitive.price_test = survey
		}
		if var survey:SymbolsTestResponse = session.getSurveyFor(surveyType: .symbolsTest)?.get() {
			survey.id = nil

			cognitive.symbol_test = survey

		}
		if cognitive.context_survey != nil {
			tests.append(AnyTest(cognitive))

		}
		
		
		id = "\(session.sessionID)"
		
	}
}

public struct CognitiveTestSession : Codable {
	var id:String
	var week:Int64?
	var	day:Int64?
	var session:Int64?
	var	session_id:String? //"[some unique identifier that will always identify this session]",
	var	sessionDate:TimeInterval? //1503937447.437798,
	var startTime:TimeInterval? // 1503937447.732328,
	var participant_id:String? //"111111",
	
	var missedSession:Int64? // 0,
	var finishedSession:Int64? // 1,
	var device_id:String? // "[device id]",
	var device_info:String? // "iOS|iPhone8,4|10.1.1",
	var app_version:String? // "1.2.4",
	var model_version:String? // "1",
	
	var contextSurvey: SurveyResponse
	var gridTest:GridTest
	var symbolsTest:SymbolsTest
	var priceTest:PriceTest

	init(withSession session: Session) {
	
		
		week = session.week
		day = session.day
		self.session = session.session
		session_id = "\(session.sessionID)"
		sessionDate = session.sessionDate?.timeIntervalSince1970
		startTime = session.startTime?.timeIntervalSince1970
		participant_id = "\(Arc.shared.participantId!)"
		
		missedSession = (session.missedSession) ? 1 : 0
		finishedSession = (session.finishedSession) ? 1 : 0
		
		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"

		//All sessions have been prefilled with json data at the begining of the study
		contextSurvey = session.getSurveyFor(surveyType: .context)!.get()!
		gridTest = session.getSurveyFor(surveyType: .gridTest)!.get()!
		priceTest = session.getSurveyFor(surveyType: .priceTest)!.get()!
		symbolsTest = session.getSurveyFor(surveyType: .symbolsTest)!.get()!
		
		id = contextSurvey.id!

	}
}

public struct SurveySession : Codable {
	var id:String
	var week:Int64?
	var	day:Int64?
	var session:Int64?
	var	session_id:String? //"[some unique identifier that will always identify this session]",
	var	sessionDate:TimeInterval? //1503937447.437798,
	var startTime:TimeInterval? // 1503937447.732328,
	var participant_id:String? //"111111",
	
	var missedSession:Int64? // 0,
	var finishedSession:Int64? // 1,
	var device_id:String // "[device id]",
	var device_info:String // "iOS|iPhone8,4|10.1.1",
	var app_version:String // "1.2.4",
	var model_version:String // "1",
	
	var survey: SurveyResponse
	
	
	init(withSession session: Session, pullingSurvey surveyType:SurveyType) {
		let data = session.getSurveyFor(surveyType: surveyType)
		id = data!.id!
		
		week = session.week
		day = session.day
		self.session = session.session
		session_id = "\(session.sessionID)"
		sessionDate = session.sessionDate?.timeIntervalSince1970
		startTime = session.startTime?.timeIntervalSince1970
		participant_id = "\(Arc.shared.participantId!)"
		
		missedSession = (session.missedSession) ? 1 : 0
		finishedSession = (session.finishedSession) ? 1 : 0
		
		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"
		
		survey = data!.get()!
		
	}
	
	
	
}

