//
// HMAPI.swift
//



import Foundation
open class HMAPI {
    static public var baseUrl = ""
    static public let shared = HMAPI()

    public var clientId:String?
    
    
    public init() {
        HMRestAPI.shared.setBaseURL(url: HMAPI.baseUrl)
		clientId = Arc.shared.deviceId
    }
    
    static public let deviceRegistration:HMAPIRequest<AuthCredentials, HMResponse> = .post("device-registration")
	static public let deviceHeartbeat:HMAPIRequest<HeartbeatRequestData, HMResponse> = .post("device-heartbeat")
	

	static public let submitWakeSleepSchedule:HMAPIRequest<WakeSleepScheduleRequestData, HMResponse> = .post("submit-wake-sleep-schedule")
	
	static public let getSessionInfo:HMAPIRequest<Data, SessionInfoResponse> = .get("get-session-info")
	static public let getTestSchedule:HMAPIRequest<Data, TestScheduleRequestData.Response> = .get("get-test-schedule")
	static public let getWakeSleep:HMAPIRequest<Data, WakeSleepScheduleRequestData.Response> = .get("get-wake-sleep-schedule")

	static public let getContactInfo:HMAPIRequest<Data, ContactInfoResponse> = .get("get-contact-info")

    static public let getEarningOverview:HMAPIRequest<Data, EarningOverview> = .get("/earning-overview")
    static public let getEarningDetail:HMAPIRequest<Data, EarningDetail> = .get("/earning-details")
    
    static public let getStudyProgress:HMAPIRequest<Data, StudyProgress> = .get("/study-progress")
    static public let getStudySummary:HMAPIRequest<Data, StudySummary> = .get("/study-summary")

    static public let getCycleProgress:HMAPIRequest<CycleProgressRequestData, CycleProgress> = .get("/cycle-progress")
    static public let getDayProgress:HMAPIRequest<DayProgressRequestData, StudyProgress> = .get("/day-progress")


}
