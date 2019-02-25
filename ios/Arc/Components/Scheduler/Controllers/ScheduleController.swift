//
// ScheduleController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
import CoreData

open class ScheduleController : MHController {
	
	
    

    //Create a schedule
    open func create(participantId:Int) -> Schedule {
        
		let schedule:Schedule = new()
        schedule.participantID = Int64(participantId)
        schedule.createdOn = Date()
        save()
		return schedule
    }
	//Create a schedule
	open func create(participantId:Int, scheduleId:Int) -> Schedule {
		
		let schedule:Schedule = new()
		schedule.participantID = Int64(participantId)
		schedule.scheduleID = "\(scheduleId)"

		schedule.createdOn = Date()
		save()
		return schedule
	}
    //get a schedule (get all or one individually)
    open func get(participantId:Int) -> [Schedule]? {
        let result:[Schedule]? = fetch(predicate: NSPredicate(format: "participantID == %i", participantId))
        
        return result

        
    }
	
	open func get(participantId:Int, scheduleId:Int) -> [Schedule]? {
		let result:[Schedule]? = fetch(predicate: NSPredicate(format: "participantID == %i AND scheduleID == %i", participantId, scheduleId))
		
		return result
		
		
	}
	open func get(confirmedSchedule participantId:Int) -> Schedule? {
		if let schedule = get(participantId: participantId)?.first {
			
			return schedule
		}
		return nil
	}
	open func upload(confirmedSchedule studyId:Int) {
		if let study = Arc.shared.studyController.get(study: studyId) {
		
			let data = WakeSleepScheduleRequestData(withStudyPeriod: study)

			HMAPI.submitWakeSleepSchedule.execute(data: data) { (response, obj, _) in
                guard !HMRestAPI.shared.blackHole else {
                    Arc.shared.appController.wakeSleepUploaded = true
                    return
                }
				if let errors = obj?.errors, errors.count > 0 {
					HMLog("Participant: \(data.participant_id), received response \(obj?.toString() ?? "") on \(Date())")
				} else {
					HMLog("Participant: \(data.participant_id), received response \(obj?.toString() ?? "") on \(Date())")
					Arc.shared.appController.wakeSleepUploaded = true
				}
				

			}
		} else {
			fatalError("Incorrect study id supplied.")
		}
	}
    //update a schedule
	
    //delete a schedule
    open func delete(participantId:Int) -> Bool {
        if let result = get(participantId: participantId), let first = result.first {
            delete(first)
            return true
        }
        //Could not delete because it didn't exist
        //If we throw then the error will provide more information
        return false
    }
    
    
}
