//
// HMAPI+Convenience.swift
//



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
