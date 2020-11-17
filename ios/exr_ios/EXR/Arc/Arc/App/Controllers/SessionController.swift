//
// SessionController.swift
//



import Foundation
import CoreData

open class SessionController:MHController {
	public var sessionUploads:Set<String> = []
    public var signatureUploads:Set<String> = []
	@discardableResult
	open func create(sessionAt date:Date) -> Session
	{
		
		let newSession:Session = new();
		newSession.sessionDate = date;
		newSession.expirationDate = date.addingHours(hours: 2);
		
		
		return newSession;
	}
	open func getUploadedSessions() -> [Session]
	{
		let predicate = NSPredicate(format: "uploaded == TRUE");
		let sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
		
		let results:[Session] = fetch(predicate: predicate, sort: sortDescriptors) ?? []
		return results;
		
		
		
	}
	
	open func getFinishedSessionsForUploading() -> [Session]
	{
		let predicate = NSPredicate(format: "uploaded == FALSE AND finishedSession == TRUE");
		let sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
		
		let results:[Session] = fetch(predicate: predicate, sort: sortDescriptors) ?? []
		return results;
	
		
		
	}
	
	open func getMissedSessionsForUploading() -> [Session]
	{
		let predicate = NSPredicate(format: "uploaded == FALSE AND missedSession == TRUE");
		let sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
		
		let results:[Session] = fetch(predicate: predicate, sort: sortDescriptors) ?? []
		return results;
		
	}
	

    open func getSignaturesForUploading() -> [Signature]
    {
        
        
        let results:[Signature] = fetch() ?? []
        return results;
        
    }
	open func sendFinishedSessions()
	{
		
		MHController.dataContext.perform {
			let sessions = self.getFinishedSessionsForUploading();
			
			for i in 0..<sessions.count
			{
				self.uploadSession(session: sessions[i])
				
			}
		}
	}
	open func clearUploadedSessions()
	{
		
		MHController.dataContext.perform {
			let sessions = self.getUploadedSessions();
			
			for i in 0..<sessions.count
			{
				Arc.shared.studyController.clearData(sessionId: Int(sessions[i].sessionID))
				
			}
			
		}
	}
	open func sendMissedSessions()
	{
		MHController.dataContext.perform {
			
			let sessions = self.getMissedSessionsForUploading();
			
			for i in 0..<sessions.count
			{
				self.uploadSession(session: sessions[i])
				
			}
		}
	}
    open func sendSignatures() {
        MHController.dataContext.performAndWait {
            let signatures = self.getSignaturesForUploading()
             for i in 0 ..< signatures.count {
                self.uploadSignature(signature: signatures[i])
            }
        }
    }
    open func uploadSignature(signature:Signature) {
        guard signature.isUploaded == false else {
            return
        }
        guard let md5 = signature.data?.encode()?.MD5() else {return}
        guard signatureUploads.insert(md5).inserted == true else {return}
        guard let data = signature.data else {
            return
        }
        //let md5 = data.encode()?.MD5()
        
        let r:HMAPIRequest<Data, HMResponse> = .post("/signature-data")
        r.executeMultipart(data:data ,
                           params: [
                            "participant_id":"\(Arc.shared.participantId ?? -1)",
                            "device_id": Arc.shared.deviceId,
                            "session_id": "\(signature.sessionId)"])
        { (response, data, _) in
            guard !HMRestAPI.shared.blackHole else {
                return
            }
            if data?.errors.count == 0 {
                //if md5 == data?.response?.md5 {
                    signature.isUploaded = true
                    
                    self.save()
                //} else {
                  //  HMLog("\(md5 ?? "") does not match \(data?.response?.md5 ?? "")")
                //}
            } else {
                print(data?.errors.toString() as Any)
            }
            
            
            
        }
        
        
    }
	open func uploadSession(session:Session) {
		guard !HMRestAPI.shared.blackHole else {
			return
		}
        guard session.uploaded == false else {
			return
		}
		let full:FullTestSession = .init(withSession: session)
		//HMLog(full.toString())
		let md5 = full.encode()?.MD5()
        guard sessionUploads.insert("\(session.sessionID)").inserted == true else { return}
		let submitTest:HMAPIRequest<FullTestSession, HMResponse> = .post("submit-test")
		submitTest.execute(data: full) { [weak self] (response, data, err) in
			
			MHController.dataContext.performAndWait { [weak self] in
				guard let weakSelf = self else {


					return
				}
				if let err = err {
					 print(err.localizedDescription)
					weakSelf.sessionUploads.remove("\(session.sessionID)")
					NotificationCenter.default.post(name: .ACSessionUploadFailure, object: weakSelf.sessionUploads)
					return
				}
				HMLog("Session: \(full.session_id ?? ""), received response \(data?.toString() ?? "") on \(Date())", silent: false)
				if data?.errors.count == 0 {
					session.uploaded = true
					Arc.shared.studyController.clearData(sessionId: Int(session.sessionID), force: true)
					if md5 == data?.response?.md5 {
						weakSelf.save()
						weakSelf.sessionUploads.remove("\(session.sessionID)")
						if weakSelf.sessionUploads.isEmpty {
							NotificationCenter.default.post(name: .ACSessionUploadComplete, object: weakSelf.sessionUploads)
						}
					} else {
						HMLog("\(md5 ?? "") does not match \(data?.response?.md5 ?? "")")
					}
				} else {
                    print(data?.errors.toString() as Any)
					weakSelf.sessionUploads.remove("\(session.sessionID)")
					NotificationCenter.default.post(name: .ACSessionUploadFailure, object: weakSelf.sessionUploads)

				}
				
			}
		}
	}
	open func uploadSchedule(studyPeriod:StudyPeriod) {
		guard !HMRestAPI.shared.blackHole else {
			Arc.shared.appController.testScheduleUploaded = true
			return
		}
		guard studyPeriod.scheduleUploaded == false else {
			return
		}
		let data:TestScheduleRequestData = .init(withStudyPeriod: studyPeriod)
		
		let md5 = data.encode()?.MD5()
		let submitTestSchedule:HMAPIRequest<TestScheduleRequestData, HMResponse> = .post("submit-test-schedule")
		submitTestSchedule.execute(data: data) { (response, obj, _) in
			HMLog("received response \(obj?.toString() ?? "") on \(Date())", silent: false)
			
			MHController.dataContext.performAndWait {
				
				if obj?.errors.count == 0 {
					studyPeriod.scheduleUploaded = true
					if md5 == obj?.response?.md5 {
						self.save()

						HMLog("\(md5 ?? "") does match \(obj?.response?.md5 ?? "")", silent: false)
						
					} else {
						HMLog("\(md5 ?? "") does not match \(obj?.response?.md5 ?? "")", silent: false)
					}
				} else {
					studyPeriod.scheduleUploaded = false
					//dump(obj?.errors)
//                    print(obj?.errors.toString())



				}
				
			}
		}
	}
	
	open func uploadSchedule(studyPeriods:[StudyPeriod]) {
		guard !HMRestAPI.shared.blackHole else {
			Arc.shared.appController.testScheduleUploaded = true
			return
		}
		let data:TestScheduleRequestData = .init(withStudyPeriods: studyPeriods)
		guard data.sessions.count > 0 else {
			return
		}
		let md5 = data.encode()?.MD5()
		
		let submitTestSchedule:HMAPIRequest<TestScheduleRequestData, HMResponse> = .post("submit-test-schedule")
		submitTestSchedule.execute(data: data) { (response, obj, _) in
			HMLog("received response \(obj?.toString() ?? "") on \(Date())")
			MHController.dataContext.performAndWait {
				
				if obj?.errors.count == 0 {
					studyPeriods.forEach({$0.scheduleUploaded = true})
					if md5 == obj?.response?.md5 {
						self.save()
						HMLog("\(md5 ?? "") does match \(obj?.response?.md5 ?? "")", silent: false)
						Arc.shared.appController.testScheduleUploaded = true

						
					} else {
						HMLog("\(md5 ?? "") does not match \(obj?.response?.md5 ?? "")", silent: false)
					}
				} else {
					studyPeriods.forEach({$0.scheduleUploaded = true})
					dump(obj?.errors)

				}
				
			}
		}
	}

	
}
