//
// SessionController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
import CoreData

open class SessionController:MHController {
	
	@discardableResult
	open func create(sessionAt date:Date) -> Session
	{
		
		let newSession:Session = new();
		newSession.sessionDate = date;
		newSession.expirationDate = date.addingHours(hours: 2);
		
		
		return newSession;
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
	
	open func uploadSession(session:Session) {
		guard session.uploaded == false else {
			return
		}
		let full:FullTestSession = .init(withSession: session)
		HMLog(full.toString())
		let md5 = full.encode()?.MD5()
		let submitTest:HMAPIRequest<FullTestSession, HMResponse> = .post("submit-test")
		submitTest.execute(data: full) { (response, data, _) in
            guard !HMRestAPI.shared.blackHole else {
                return
            }
			MHController.dataContext.perform {
				HMLog("Session: \(full.session_id ?? ""), received response \(data?.toString() ?? "") on \(Date())", silent: false)
				if data?.errors.count == 0 {
					session.uploaded = true
					if md5 == data?.response?.md5 {
						self.save()
					} else {
						HMLog("\(md5 ?? "") does not match \(data?.response?.md5 ?? "")")
					}
				} else {
					print(data?.errors.toString())
				}
				
			}
		}
	}
	open func uploadSchedule(studyPeriod:StudyPeriod) {
		guard studyPeriod.scheduleUploaded == false else {
			return
		}
		let data:TestScheduleRequestData = .init(withStudyPeriod: studyPeriod)
		
		let md5 = data.encode()?.MD5()
		let submitTestSchedule:HMAPIRequest<TestScheduleRequestData, HMResponse> = .post("submit-test-schedule")
		submitTestSchedule.execute(data: data) { (response, obj, _) in
			HMLog("Participant: \(data.participant_id ?? ""), received response \(obj?.toString() ?? "") on \(Date())", silent: false)
            guard !HMRestAPI.shared.blackHole else {
                Arc.shared.appController.testScheduleUploaded = true
                return
            }
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
		let data:TestScheduleRequestData = .init(withStudyPeriods: studyPeriods)
		
		let md5 = data.encode()?.MD5()
		let submitTestSchedule:HMAPIRequest<TestScheduleRequestData, HMResponse> = .post("submit-test-schedule")
		print(data.toString())
		submitTestSchedule.execute(data: data) { (response, obj, _) in
			HMLog("Participant: \(data.participant_id ?? ""), received response \(obj?.toString() ?? "") on \(Date())")
			MHController.dataContext.performAndWait {
                guard !HMRestAPI.shared.blackHole else {
                    Arc.shared.appController.testScheduleUploaded = true
                    return
                }
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
