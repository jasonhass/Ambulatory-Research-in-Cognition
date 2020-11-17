//
// HMFault.swift
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
"success": false
},
"errors": {
"participant_id": [
"Invalid Participant ID or Authorization Code"
]
}
}
*/
import Foundation

public struct HMFault : Codable, Error {
	var message:String
}

public struct HMResponse : Codable {
	
	public struct Body : Codable {
		public var success : Bool?
		public var md5:String?
	}
	
	public var response:Body?
	public var responseData:HTTPURLResponse?
	public var errors : [String:[String]]

	private enum CodingKeys : CodingKey {
		case response, errors
	}
}
