//
// AuthController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

open class AuthController:MHController {
    private var _credential:AuthCredentials?
    private var _isAuthorized:Bool = false
	
    public func isAuthorized() -> Bool {
	
        return _isAuthorized
    }
    
    public func clear() {
        _isAuthorized = false
        _credential = nil
    }
    
    public func set(username:String, password:String) -> AuthCredentials? {
        let credential = AuthCredentials(userName: username, password: password)
        self._credential = credential
        return credential
    }
    
    public func set(username:String) -> AuthCredentials? {
        var credential = self._credential ?? AuthCredentials()
        
        credential.userName = username
        self._credential = credential
        
        return credential
        
    }
    
    public func set(password:String) -> AuthCredentials? {
        var credential = self._credential ?? AuthCredentials()
        
        credential.password = password
        self._credential = credential
        
        return credential
    }
	
	public func getPassword() -> String? {
		return self._credential?.password
	}
	public func getUserName() -> String? {
		return self._credential?.userName
	}
	public func checkAuth() -> Int64? {
		if let results:[AuthEntry] = fetch(), let entry = results.last {
			_isAuthorized = true
			return entry.participantID
		}
		return nil
	}
	open func authenticate(completion:@escaping ((Int64?, String?)->())) {
		if let id = checkAuth() {
			completion(id, nil)
			return
		}
        guard _credential?.userName != nil, _credential?.password != nil else {
			completion(nil, "No username.")
            return
        }
		
        
        _credential?.appVersion = Arc.shared.versionString
        _credential?.device_id = Arc.shared.deviceId
        _credential?.deviceInfo = Arc.shared.deviceInfo()
//		_credential?.override = true
//        print(_credential.toString())
        HMAPI.deviceRegistration.execute(data: _credential) { (response, obj, err) in
            if HMRestAPI.shared.blackHole {
                completion(00000000, nil)
                return
            }
			if obj?.errors.count == 0 {
                self._isAuthorized = true
//				DispatchQueue.main.async {

				HMAPI.getSessionInfo.execute(data: nil, completion: { (res, resObj, err) in
					guard  resObj?.errors.count == 0 else {
						HMLog(obj?.toString() ?? "")
						let r = response as? HTTPURLResponse
						let failureMessage = self.getAuthIssue(from: r?.statusCode)
						completion(nil, failureMessage)
						return
					}
					
					guard let value = self._credential?.userName, let id = Int64(value) else {
						
						completion(nil, "Sorry, our app is currently experiencing issues. Please try again later.")
						return
					}
				
					MHController.dataContext.perform {
						Arc.shared.studyController.firstTest = resObj?.response.first_test
						Arc.shared.studyController.latestTest = resObj?.response.latest_test
						
						let entry:AuthEntry = self.new()
						entry.authDate = Date()
						
						
						entry.participantID = id
						self.save()
						completion(id, nil)

					}
					
				})

			} else {
				HMLog(obj?.toString() ?? "")
                let r = response as? HTTPURLResponse
                let failureMessage = self.getAuthIssue(from: r?.statusCode)
				completion(nil, failureMessage)
			}
        }
        
    }
    
    open func getAuthIssue(from code:Int?) -> String {
        if let code = code {
            if code == 401 {
                return "Invalid Arc ID or Password"
            }
            if code == 409 {
                return "Already enrolled on another device"
            }
        }
        return "Sorry, our app is currently experiencing issues. Please try again later."
    }
    
}
