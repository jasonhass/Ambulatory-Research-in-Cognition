//
// Schedule+CoreDataClass.swift
//



import Foundation
import CoreData


open class Schedule: NSManagedObject {
	var entries:Set<ScheduleEntry> {
		get {
			return scheduleEntries as? Set<ScheduleEntry> ?? []
		}
		set {
			scheduleEntries = NSSet(set: newValue)
		}
	}
}
