//
// Arc+Auth.swift
//



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
