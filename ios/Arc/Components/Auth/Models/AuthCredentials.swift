//
// AuthCredentials.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

public struct AuthCredentials : Codable, Equatable, Hashable {
    var userName:String?
    var password:String?
    var device_id:String?
    var deviceInfo:String?
    var appVersion:String?
    var override:Bool?
    private enum CodingKeys: String, CodingKey {
        case userName = "participant_id"
        case password = "authorization_code"
        case device_id = "device_id"
        case deviceInfo = "device_info"
        case appVersion = "app_version"
        case override = "override"
    }
    
    
    init() {
        
    }
    
    init(userName:String, password:String) {
        self.userName = userName
        self.password = password
        
    }
}
