//
// ScheduleController+ScheduleEntry.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

//Schedule entry handling
public extension ScheduleController {
    
    //Create a schedule entry
    /**
     For Wrap around time ranges just peform two separate creates
     tuesday(23:00 - 00:30) = 'tuesday'(23:00 - 23:59) + 'wednesday'(00:00 + 00:30)
     */
    
    public func create(entry startTime:String, endTime:String, weekDay:WeekDay, participantId:Int, shouldSave:Bool = true) -> ScheduleEntry? {
		let entry:ScheduleEntry = new()
        entry.participantID = Int64(participantId)
        entry.availabilityStart = startTime
        entry.availabilityEnd = endTime
        entry.weekday = weekDay.rawValue
        entry.createdOn = Date()
        entry.modifiedOn = Date()
        let schedule = get(participantId: participantId)?.first ?? create(participantId: participantId)
        
        schedule.addToScheduleEntries(entry)
        if shouldSave {
            save()
        }
        return entry
    }
	public func create(entries startTime:String, endTime:String, weekDays:ClosedRange<WeekDay>, participantId:Int) -> [ScheduleEntry]? {
        var entries:[ScheduleEntry] = []
        for day in weekDays {
			_ = delete(weekDay: day, participantId: participantId)
            if let entry = create(entry: startTime, endTime: endTime, weekDay: day, participantId: participantId, shouldSave: false) {
                entries.append(entry)
            }
        }
		
        save()
		
        return entries
    }
    
    //Create a list of schedule entries
    
    //Get a schedule entry
    public func get(allEntriesForId participantId:Int) -> [ScheduleEntry]? {
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i", participantId),
                                                sort:[NSSortDescriptor(key: "weekday", ascending: true)])
		for r in result! {
//			print("\(r.day) \(String(describing: r.availabilityStart)) \(String(describing: r.availabilityEnd))")
		}
        return result
        
    }
    
    //Get a list of schedule entries by range
    /**
     For wrap around day ranges just perform two separate gets
     Wednesday ... Tuesday = (Wendesday ... Saturday) + (Sunday ... Tuesday)
     */
    public func get(entriesForDays days:ClosedRange<WeekDay>, forParticipant participantId:Int) -> [ScheduleEntry]? {
        
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i AND weekday<=%@ AND weekday>=%@", participantId, days.lowerBound.rawValue, days.upperBound.rawValue),
                                                sort:[NSSortDescriptor(key: "weekday", ascending: true)])
        
        return result
    }
    
    public func get(entriesForDay day:WeekDay, forParticipant participantId:Int) -> [ScheduleEntry]? {
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i AND weekday==%i AND weekday>=%i", participantId, day.rawValue, day.rawValue))
        
        return result
		
    }
    
    
	//Delete a schedule entry
	public func delete(schedulesForParticipant participantId:Int) -> Bool {
		if let schedules:[Schedule] = fetch(predicate: NSPredicate(format: "participantID == %i", participantId), sort: nil) {
			
			for s in schedules {
				delete(s)
			}
			
		}
		return true
		
	}
    
    
    //Delete a schedule entry
	public func delete(scheduleId: Int, participantId:Int) -> Bool {
		if let schedules:[Schedule] = fetch(predicate: NSPredicate(format: "participantID == %i AND scheduleID==%@", participantId, "\(scheduleId)"), sort: nil) {
			
			for s in schedules {
				delete(s)
			}
		}
		return true
		
	}
    ///This will delete all entries for a single day if we need alternate deletes
    ///create them here.
	
	
	public func delete(weekDay:WeekDay, participantId:Int) -> Bool {
		if let result = get(participantId: participantId)?.first {
			
			let entries = result.entries.filter({ (entry) -> Bool in
				return entry.weekday == weekDay.rawValue
			})
			result.removeFromScheduleEntries(NSSet(set: entries))
			
			save()
			return true
		}
		//Could not delete because it didn't exist
		//If we throw then the error will provide more information
		return false
	}
}
