//
// HMAPI.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


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
	
	

}
