//
// ScheduleEntry+CoreDataClass.swift
//



import Foundation
import CoreData

//@objc(ScheduleEntry)
open class ScheduleEntry: NSManagedObject {
	public var day:WeekDay {
		get {
			return WeekDay(rawValue:self.weekday)!
		}
		set {
			self.weekday = newValue.rawValue
		}
	}
	public func startTimeOn(date:Date) -> Date? {
		let formatter = DateFormatter()
		formatter.defaultDate = date
		formatter.dateFormat = "h:mm a"
		
		return formatter.date(from: availabilityStart ?? "")
	}
	
    // returns the end time for the given date.
    // If the availabilityEnd is "before" availabilityStart
    // (ie they wake up at 2pm and go to bed at 4 pm)
    // then we increment the date by one.
    
	public func endTimeOn(date:Date) -> Date? {
		let formatter = DateFormatter()
		formatter.defaultDate = date
		formatter.dateFormat = "h:mm a"
		
		var endTime = formatter.date(from: availabilityEnd ?? "")
        let startTime = self.startTimeOn(date: date);
        
        if let e = endTime, let s = startTime, s.compare(e) == .orderedDescending
        {
            endTime = e.addingDays(days: 1);
        }
        
        return endTime;
	}
}
