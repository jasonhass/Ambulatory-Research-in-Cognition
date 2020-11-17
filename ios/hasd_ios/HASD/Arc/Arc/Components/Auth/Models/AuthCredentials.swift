//
// AuthCredentials.swift
//



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
