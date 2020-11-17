//
// ScheduleController.swift
//



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
	open func get(rangeForCurrentDay date:Date, participantId:Int) -> ClosedRange<Date> {
		var left = get(startTimeForDate: date, participantID: participantId)
		var right = get(endTimeForDate: date, participantID: participantId)
		
		//If the date is before the left handle
		if left > date{
			
			//To get date to fall into the range we will move left to be the right handle
			right = left
			
			//This way the left handle is always before the date and the right handle is after the date this will prevent comparison errors.
			left = left.addingDays(days: -1)
			
		}
		return (left ... right)
	}
    open func get(rangeUpToNextDayFrom date:Date, participantId:Int) -> ClosedRange<Date> {
        var left = get(startTimeForDate: date, participantID: participantId)
        var right = get(startTimeForDate: date.addingDays(days: 1), participantID: participantId)

        //If the date is before the left handle
        if left > date{

            //To get date to fall into the range we will move left to be the right handle
            right = left

            //This way the left handle is always before the date and the right handle is after the date this will prevent comparison errors.
            left = left.addingDays(days: -1)

        }
        return (left ... right)
    }
	open func get(rangeForUpcomingDay date:Date, participantId:Int) -> ClosedRange<Date> {
		let left = get(startTimeForDate: date, participantID: participantId)
		let right = get(endTimeForDate: date, participantID: participantId)
		
		
		return (left ... right)
	}
	open func get(isActiveFor date:Date, participantId:Int) -> Bool {
		let left = get(startTimeForDate: date, participantID: participantId)
		let right = get(endTimeForDate: date, participantID: participantId)
		
		
		return (left ... right).contains(date)
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
					HMLog("received response \(obj?.toString() ?? "") on \(Date())")
				} else {
					HMLog("received response \(obj?.toString() ?? "") on \(Date())")
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
    
	open func generateSchedule(start:Date, end:Date) -> [ScheduleEntry] {
		let scheduleController = Arc.shared.scheduleController
		var entries:[ScheduleEntry] = []
		for day in WeekDay.allCases {
			dump(day)
			let formatter = DateFormatter()
			formatter.dateFormat = "h:mm a"
			formatter.isLenient = true
			
			if let entry = scheduleController.create(entry: formatter.string(from: start),
													 endTime: formatter.string(from: end),
													 weekDay: day,
													 participantId: Arc.shared.participantId!) {
				
				entries.append(entry)
			}
			
		}
		return entries
	}
}
