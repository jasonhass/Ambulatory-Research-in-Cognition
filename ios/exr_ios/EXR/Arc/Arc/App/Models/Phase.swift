//
// Phase.swift
//



import Foundation
public protocol Phase {
	
	associatedtype PhasePeriod
	
	static func from(studyId:Int) -> PhasePeriod
	static func from(startDate:Date, currentDate:Date) -> PhasePeriod
	static func from(weeks:Int) -> PhasePeriod
    static func from(days:Int) -> PhasePeriod
	func statesForSession(week:Int, day:Int, session:Int) -> [State]
	func statesFor(session: Session) -> [State]
	
}
