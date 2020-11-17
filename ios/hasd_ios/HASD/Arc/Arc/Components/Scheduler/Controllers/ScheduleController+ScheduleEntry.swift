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
    
    func create(entry startTime:String, endTime:String, weekDay:WeekDay, participantId:Int, shouldSave:Bool = true) -> ScheduleEntry? {
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
    func create(entries startTime:String, endTime:String, weekDays:ClosedRange<WeekDay>, participantId:Int) -> [ScheduleEntry]? {
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
    func get(scheduleForDay currentDate:Date, participantID:Int) -> ScheduleEntry {
        
        let day = WeekDay.getDayOfWeek(currentDate)
        
        guard let schedules = get(entriesForDay: day, forParticipant: participantID), let schedule = schedules.first  else {
            fatalError("No valid schedule exists.")
        }
        
        return schedule
    }
	func formatTime(time:String, with currentDate:Date) -> Date {
        
        var formatter = DateFormatter.h24mm
        formatter.defaultDate = currentDate

		var newTime = formatter.date(from:  time);
        if newTime == nil {
            formatter = DateFormatter.h12mm
            formatter.defaultDate = currentDate

            newTime = formatter.date(from:  time)
        }

		if newTime == nil {
			formatter.amSymbol = "a.m."
			formatter.pmSymbol = "p.m."
			newTime = formatter.date(from:  time)
		}

		if newTime == nil {
			formatter.amSymbol = "am"
			formatter.pmSymbol = "pm"
			newTime = formatter.date(from:  time)
		}

        if newTime == nil {
            formatter.amSymbol = "AM"
            formatter.pmSymbol = "PM"
            newTime = formatter.date(from:  time)
        }

		guard let result = newTime else {
			fatalError("Failed to create date.")
		}

		return result
	}
	
    func get(startTimeForDate currentDate:Date, participantID:Int) -> Date {
        
        let schedule = get(scheduleForDay: currentDate, participantID: participantID)
		guard let start = schedule.availabilityStart else {
				fatalError("No schedule configured")
		}
		
		let startTime = formatTime(time: start, with: currentDate)
		
        return startTime
    }
    
    func get(endTimeForDate currentDate:Date, participantID:Int) -> Date {
		
        let schedule = get(scheduleForDay: currentDate, participantID: participantID)
		guard let start = schedule.availabilityStart,
			let end = schedule.availabilityEnd else {
			fatalError("No schedule configured")
		}
		let startTime = formatTime(time: start, with: currentDate)
		let endTime = formatTime(time: end, with: currentDate)

        if endTime.timeIntervalSince1970 < startTime.timeIntervalSince1970 {
            return endTime.addingDays(days: 1)
        }
        
        return endTime
    }
    
    func get(allEntriesForId participantId:Int) -> [ScheduleEntry]? {
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i", participantId),
                                                sort:[NSSortDescriptor(key: "weekday", ascending: true)])
//        for r in result! {
////            print("\(r.day) \(String(describing: r.availabilityStart)) \(String(describing: r.availabilityEnd))")
//        }
        return result
        
    }
    
    //Get a list of schedule entries by range
    /**
     For wrap around day ranges just perform two separate gets
     Wednesday ... Tuesday = (Wendesday ... Saturday) + (Sunday ... Tuesday)
     */
    func get(entriesForDays days:ClosedRange<WeekDay>, forParticipant participantId:Int) -> [ScheduleEntry]? {
        
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i AND weekday<=%@ AND weekday>=%@", participantId, days.lowerBound.rawValue, days.upperBound.rawValue),
                                                sort:[NSSortDescriptor(key: "weekday", ascending: true)])
        
        return result
    }
    
    func get(entriesForDay day:WeekDay, forParticipant participantId:Int) -> [ScheduleEntry]? {
        let result:[ScheduleEntry]? = fetch(predicate: NSPredicate(format: "participantID == %i AND weekday==%i", participantId, day.rawValue))
        
        return result
		
    }
    
    
	//Delete a schedule entry
    func delete(schedulesForParticipant participantId:Int) -> Bool {
		if let schedules:[Schedule] = fetch(predicate: NSPredicate(format: "participantID == %i", participantId), sort: nil) {
			
			for s in schedules {
				delete(s)
			}
			
		}
		return true
		
	}
    
    
    //Delete a schedule entry
    func delete(scheduleId: Int, participantId:Int) -> Bool {
		if let schedules:[Schedule] = fetch(predicate: NSPredicate(format: "participantID == %i AND scheduleID==%@", participantId, "\(scheduleId)"), sort: nil) {
			
			for s in schedules {
				delete(s)
			}
		}
		return true
		
	}
    ///This will delete all entries for a single day if we need alternate deletes
    ///create them here.
	
	
    func delete(weekDay:WeekDay, participantId:Int) -> Bool {
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
