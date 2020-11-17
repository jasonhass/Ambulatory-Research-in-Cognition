//
// SessionInfoResponse.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



/*
{
	"response": {
		"success": true,

		"first_test": {
			"session_date": 1540505077,
			"week": 0,
			"day": 0,
			"session": 0,
			"session_id": "0"
		},

		"latest_test": {
			"session_date": 1551319989,
			"week": null,
			"day": null,
			"session": null,
			"session_id": "378"

		}
	},

	"errors": {}
}

*/

import Foundation
public struct SessionInfoResponse : Codable {
	public struct DeviceState : Codable {
		public var os_type:String
		public var os_version:String
		public var app_version:String
	}
	public struct TestState : Codable {
		public var session_date : TimeInterval
		public var week : Int
		public var day : Int
		public var session : Int
		public var session_id : String
        
        public init() {
            session_date = Date().timeIntervalSince1970
            week = 0
            day = 0
            session = 0
            session_id = "0"
        }
        
	}
	public struct Body : Codable {
		public var success : Bool?
		public var first_test: TestState?
		public var latest_test: TestState?
		public var current_device: DeviceState?
		public var previous_device: DeviceState?

	}
	
	public var response:Body
	public var errors : [String:[String]]
    
    

}
