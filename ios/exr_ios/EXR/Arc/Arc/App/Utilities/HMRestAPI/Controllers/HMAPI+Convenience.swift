//
// HMAPI+Convenience.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

public extension HMAPI {
    
    static func defaultHeaders() -> [String:String] {
        return [
            "Accept":"application/json",
            "Content-Type":"application/json"
        ]
    }
    
    static func authHeaders() -> [String:String] {
        	var headers = defaultHeaders()
        headers["Authorization"] = ""
        return headers
    }
    
    static func auth(completion:(()->())?) {

        
        completion?()
      
    }
    
    static func placeholder(completion:(()->Void)?){
        completion?()
    }
    
}
