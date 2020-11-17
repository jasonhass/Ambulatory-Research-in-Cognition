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
	public func clearAuth() {
		if let results:[AuthEntry] = fetch(), let entry = results.last {
			_isAuthorized = false
			_credential = nil
			delete(results)
		}
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
            let statusCode = StatusCode.with(response: response)
            if statusCode.succeeded {
                self._isAuthorized = true
//				DispatchQueue.main.async {

				HMAPI.getSessionInfo.execute(data: nil, completion: { (res, resObj, err) in
                    let statusCode = StatusCode.with(response: response)

					guard statusCode.succeeded else {
						
                        completion(nil, statusCode.failureMessage)
						return
					}
					
					guard let value = self._credential?.userName, let id = Int64(value) else {
						
						completion(nil, "Sorry, our app is currently experiencing issues. Please try again later.".localized(ACTranslationKey.login_error3))
						return
					}
				
					MHController.dataContext.perform {
						Arc.shared.studyController.firstTest = resObj?.response.first_test
						Arc.shared.studyController.latestTest = resObj?.response.latest_test
						if Arc.shared.studyController.latestTest != nil {
							Arc.apply(forVersion: "2.0.0")
						}
						let entry:AuthEntry = self.new()
						entry.authDate = Date()
						
						
						entry.participantID = id
						//Set this value
						let value = Int(id)
						Arc.shared.participantId = value
						self.save()
						NotificationCenter.default.post(name: .ACStartEarningsRefresh, object: nil)

						completion(id, nil)

						

					}
					
				})

			} else {
				

                completion(nil, statusCode.failureMessage)
			}
        }
		
		
        
    }
	open func pullData<T:Phase>(phaseType:T.Type, completion:@escaping ((Error?)->())) where T.PhasePeriod == T {
		guard let participantID = Arc.shared.participantId else {
			return completion(nil)
		}
		HMAPI.getWakeSleep.execute(data: nil, completion: { (res, obj, err) in
			guard err == nil && obj?.errors.isEmpty ?? true else {
				completion(err)
				return
				
				
			}
			guard let data = obj?.response?.wake_sleep_schedule else {
				completion(nil)
				return
			}
			
			MHController.dataContext.performAndWait {
				let controller = Arc.shared.scheduleController
				for entry in data.wake_sleep_data {
					
					let _ = controller.create(entry: entry.wake,
									  endTime: entry.bed,
									  weekDay: WeekDay.fromString(day: entry.weekday),
									  participantId: participantID)
					
					
				}
				controller.save()
				
				
				
			}
			
            HMAPI.getCycleProgress.execute() { (cycleResponse, cycleObject, cycleProgressError) in
                HMAPI.getTestSchedule.execute(data: nil, completion: { (res, obj, err) in
                    guard err == nil && obj?.errors.isEmpty ?? true else {
                        return completion(err)
                    }
                    guard var data = obj?.response?.test_schedule else {
                        return completion(nil)
                    }
                    
                    data.cycle_progress = cycleObject?.response
                    MHController.dataContext.performAndWait {
                        let controller = Arc.shared.studyController
                        if controller.create(testSessionsWithSchedule: data, with: phaseType) {
                            controller.save()
                        } else {
                            print("Error creating sessions from schedule")
                        }
                    }
                    completion(nil)
                })
            }
		})
		
	}

    open func getAuthIssue(from code:Int?) -> String {
        if let code = code {
            if code == 401 {
                return "Invalid Rater ID or ARC ID".localized(ACTranslationKey.login_error1)
            }
            if code == 409 {
                return "Already enrolled on another device".localized(ACTranslationKey.login_error2)
            }
        }
        return "Sorry, our app is currently experiencing issues. Please try again later.".localized(ACTranslationKey.login_error3)
    }
    
}
