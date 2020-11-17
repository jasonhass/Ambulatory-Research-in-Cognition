//
// ScheduleResponse.swift
//


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
	public struct Response : Codable {
		public struct Body : Codable {
			public let success:Bool
			public var test_schedule:TestScheduleRequestData?
		}
		public var response:Body?
		public let errors: [String:[String]]
	}
	
	public struct Entry : Codable {
		public let week:Int64
		public let day:Int64
		public let session:Int64
		public let session_id: String
		public let session_date: TimeInterval
		public let types: [String]
	}
	public var participant_id:String? //"111111",
    public var cycle_progress:CycleProgress.Response?
	public var sessions:[Entry]

	public var device_id:String? // "[device id]",
	public var device_info:String? // "iOS|iPhone8,4|10.1.1",
	public var app_version:String? // "1.2.4",
	public var model_version:String? // "1",
    public var timezone_name:String? //name of timezone ie "Central Standard Time"
    public var timezone_offset:String? //offset from utc ie "UTC-05:00"

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
        timezone_name = TimeZone.current.description
        timezone_offset = (TimeZone.current.secondsFromGMT() / 3600).toString()
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
		timezone_name = TimeZone.current.description
        timezone_offset = (TimeZone.current.secondsFromGMT() / 3600).toString()
		//All sessions have been prefilled with json data at the begining of the study
		
	}
	
}

public struct WakeSleepScheduleRequestData : Codable {
	public struct Response : Codable {
		
		public struct Body : Codable {
			public let success:Bool
			public let wake_sleep_schedule:WakeSleepScheduleRequestData?
		}
		public let response:Body?
		public let errors:[String:[String]]
	}
	
	
	public struct Entry : Codable {
		public let wake:String
		public let bed:String
		public let weekday: String
		
		public init(entry:ScheduleEntry) {
			wake = entry.availabilityStart!
			bed = entry.availabilityEnd!
			weekday = entry.day.toString().capitalized

		}
	}
	public var participant_id:String //"111111",
	
	public var wake_sleep_data:[Entry]
	

	public var device_id:String // "[device id]",
	public var device_info:String // "iOS|iPhone8,4|10.1.1",
	public var app_version:String // "1.2.4",
	public var model_version:String // "1",
    public var timezone_name:String //name of timezone ie "Central Standard Time"
    public var timezone_offset:String //offset from utc ie "UTC-05:00"
	
	public init(withStudyPeriod studyPeriod: StudyPeriod) {
		

		
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
        timezone_name = TimeZone.current.description
        timezone_offset = (TimeZone.current.secondsFromGMT() / 3600).toString()
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
