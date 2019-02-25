//
// ScheduleResponse.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

/*
{
"participant_id" : "111111",
"sessions" : [
{
"session_id": "[some unique identifier that will always identify this test session]",
"session_date": 1538437544,
"type": "cognitive"
},
{
"session_id": "[some unique identifier that will always identify this test session]",
"session_date": 1538400986,
"type": "cognitive"
},
{
"session_id": "[some unique identifier that will always identify this test session]",
"session_date": 1538407454,
"type": "ema"
},
],

"device_id" : "[device id]",
"device_info" : "iOS|iPhone8,4|10.1.1",
"app_version": "1.2.4",
"model_version": "1"
}
*/
import Foundation
public struct TestScheduleRequestData : Codable {
	public struct Entry : Codable {
		let week:Int64
		let day:Int64
		let session:Int64
		let session_id: String
		let session_date: TimeInterval
		let types: [String]
	}
	var participant_id:String? //"111111",

	var sessions:[Entry]

	var device_id:String? // "[device id]",
	var device_info:String? // "iOS|iPhone8,4|10.1.1",
	var app_version:String? // "1.2.4",
	var model_version:String? // "1",


	public init(withStudyPeriod studyPeriod: StudyPeriod) {



		participant_id = "\(Arc.shared.participantId!)"
		if let objs = studyPeriod.sessions {
			sessions = objs.map({ (obj) -> Entry in
				let s = obj as! Session
				
				return Entry(week: s.week, day: s.day, session: s.session, session_id: "\(s.sessionID)", session_date: s.sessionDate!.timeIntervalSince1970, types: s.typeNamesForSession())
			})
		} else {
			fatalError("Need sessions to continue.")
		}

		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"

		//All sessions have been prefilled with json data at the begining of the study



	}
	
	public init(withStudyPeriods studyPeriods: [StudyPeriod]) {
		
		
		var allSessions:[Entry] = []
		participant_id = "\(Arc.shared.participantId!)"
		for studyPeriod in studyPeriods {
			if let objs = studyPeriod.sessions {
				let sessions = objs.map({ (obj) -> Entry in
					let s = obj as! Session
					
					return Entry(week: s.week, day: s.day, session: s.session, session_id: "\(s.sessionID)", session_date: s.sessionDate!.timeIntervalSince1970, types: s.typeNamesForSession())
				})
				allSessions.append(contentsOf: sessions)
			} else {
				fatalError("Need sessions to continue.")
			}
			
			
		}
		sessions = allSessions
		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"
		
		//All sessions have been prefilled with json data at the begining of the study
		
	}
	
}

public struct WakeSleepScheduleRequestData : Codable {
	public struct Entry : Codable {
		let wake:String
		let bed:String
		let weekday: String
		
		init(entry:ScheduleEntry) {
			wake = entry.availabilityStart!
			bed = entry.availabilityEnd!
			weekday = entry.day.toString().capitalized

		}
	}
	var participant_id:String //"111111",
	
	var wake_sleep_data:[Entry]
	

	var device_id:String // "[device id]",
	var device_info:String // "iOS|iPhone8,4|10.1.1",
	var app_version:String // "1.2.4",
	var model_version:String // "1",
	
	
	init(withStudyPeriod studyPeriod: StudyPeriod) {
		

		
		participant_id = "\(Arc.shared.participantId!)"
		let schedule = Arc.shared.scheduleController.get(confirmedSchedule: Arc.shared.participantId!)
		let entries = schedule!.entries.sorted { (lhs, rhs) -> Bool in
			return lhs.day < rhs.day
		}
		self.wake_sleep_data = []
		for entry in entries {
			self.wake_sleep_data.append(Entry(entry: entry))
		}

		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		model_version = "\(Arc.shared.arcVersion)"
		
		//All sessions have been prefilled with json data at the begining of the study
		
		
		
	}
	
}


public struct HeartbeatRequestData : Codable {
	var participant_id:String //"111111",

	var device_id:String // "[device id]",
	var device_info:String // "iOS|iPhone8,4|10.1.1",
	var app_version:String // "1.2.4",
	
	init() {
		device_id = Arc.shared.deviceId
		device_info = Arc.shared.deviceInfo()
		app_version = Arc.shared.versionString
		participant_id = "\(Arc.shared.participantId!)"
	}
}
