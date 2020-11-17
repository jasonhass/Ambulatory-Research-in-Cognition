//
// ContactInfoResponse.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




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
