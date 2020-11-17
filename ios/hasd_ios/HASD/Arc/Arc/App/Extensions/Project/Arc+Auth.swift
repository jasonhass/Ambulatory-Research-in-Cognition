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
    
    func isAuthorized() -> Bool {
        return authController.isAuthorized()
    }
    func clearAuth() {
        authController.clear()
    }
    
    func setAuth(userName:String) {
        _ = authController.set(username: userName)
    }
    func setAuth(password:String) {
        _ = authController.set(password: password)
    }
    func setAuth(username:String, password:String) {
        setAuth(userName: username)
        setAuth(password: password)
    }
    func authenticate(completion:@escaping ((Int64?, String?)->())) {
        authController.authenticate(completion: completion)
    }
}
