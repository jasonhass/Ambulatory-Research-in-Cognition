//
// ContactInfoResponse.swift
//



import Foundation


public struct ContactInfoResponse: Codable
{
    
    
    public struct Body : Codable {
        public struct ContactInfo: Codable {
            public let phone: String?
            public let email: String?
        }
        public let success:Bool
        
        public let contact_info:ContactInfo?
    }
    public let response:Body?
    public let errors:[String:[String]]
}
