//
// Arc+Auth.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

public extension Arc {
    
    public func isAuthorized() -> Bool {
        return authController.isAuthorized()
    }
    public func clearAuth() {
        authController.clear()
    }
    
    public func setAuth(userName:String) {
        _ = authController.set(username: userName)
    }
    public func setAuth(password:String) {
        _ = authController.set(password: password)
    }
    public func setAuth(username:String, password:String) {
        setAuth(userName: username)
        setAuth(password: password)
    }
	public func authenticate(completion:@escaping ((Int64?, String?)->())) {
        authController.authenticate(completion: completion)
    }
}
