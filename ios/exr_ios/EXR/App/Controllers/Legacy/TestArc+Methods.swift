//
// TestArc+Methods.swift
//



import Foundation
import UIKit
import CoreData
import Arc

////////// DEFAULT VALUES ///////////////


var MIN_SPACING:TimeInterval = 7200; // min spacing betweeen tests, 2 hours
var SESSIONS_PER_DAY:Int =  4; // sessions per day of arc
var TEST_TIMEOUT:TimeInterval = 300; // 5 minute timeout if the application is closed
var TEST_START_ALLOWANCE:TimeInterval = -300; // 5 minute window before actual start time


#if CS
    
var InterArcWeeks: Dictionary<Int, Int> = [1: 0];
var DAYS_PER_ARC:Int = 7; //number of days an visit lasts
#elseif EXR

var InterArcWeeks: Dictionary<Int, Int> = [1: 26,
                                           2: 26,
                                           3: 26,
                                           4: 26,
                                           5: 26,
                                           6: 26,
                                           7: 26,
                                           8: 26,
                                           9: 26,
                                           10: 26,
                                           11: 26,
                                           12: 26,
                                           13: 26,
                                           14: 26,
                                           15: 26,
                                           16: 26,
                                           17: 26,
                                           18: 26,
                                           19: 26,
                                           20: 26,
                                           21: 26,
                                           22: 26,
                                           23: 26,
                                           24: 26,
                                           25: 26,
                                           26: 26,
                                           27: 26,
                                           28: 26,
                                           29: 26];
var DAYS_PER_ARC:Int = 7; //number of days an visit lasts
#else


// This contains the info for deciding how many weeks are between each Arc.
// The key is the index of the "next" Arc, and the value is the weeks from the "previous" Arc.
// So for instance, Arc 2 is 12 weeks after the start date of Arc 1, and Arc 4 is 16 weeks after Arc 3.
var InterArcWeeks: Dictionary<Int, Int> = [1: 0,
                                           2: 12,
                                           3: 12,
                                           4: 16,
                                           5: 12,
                                           6: 12,
                                           7: 12,
                                           8: 12,
                                           9: 16,
                                           10: 12,
                                           11: 12,
                                           12: 12,
                                           13: 16,
                                           14: 12,
                                           15: 12,
                                           16: 12,
                                           17: 16,
                                           18: 12,
                                           19: 12,
                                           20: 12,
                                           21: 16,
                                           22: 12,
                                           23: 12,
                                           24: 12,
                                           25: 16,
                                           26: 12,
                                           27: 12,
                                           28: 12,
                                           29: 16];
var DAYS_PER_ARC:Int = 7; //number of days an visit lasts
#endif


extension TestVisit
{
    
    //MARK: - static methods
    
    static func getAllVisits() -> [TestVisit]
    {
        let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        
        do
        {
            let results = try EXCoreDataStack.shared.managedObjectContext.fetch(request);
            
            return results;
        }
        catch
        {
            HMLog("error retrieving cached data: \(error)");
        }
        
        return [];
    }
    
    static func getPastVisits() -> [TestVisit]
    {
        let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        
        let now = NSDate();
        request.predicate = NSPredicate(format: "userEndDate<=%@",now, now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
			let results = try EXCoreDataStack.shared.managedObjectContext.fetch(request);

            return results;
        }
        catch
        {
            HMLog("error retrieving cached data: \(error)");
        }

        return [];
    }
    
    // static: get current arc
    // Finds an Arc whose start date and end date fall around the current date ( startDate <= now <= endDate)
    // And has at least one upcoming (or currently available) TestSession
    static func getCurrentVisit() -> TestVisit?
    {
        let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        
        let now = NSDate();
        request.predicate = NSPredicate(format: "userStartDate<=%@ AND userEndDate>=%@",now, now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
			let results = try EXCoreDataStack.shared.managedObjectContext.fetch(request);

            if(results.count > 0)
            {
                let firstResult = results[0] as TestVisit;
                if firstResult.getUpcomingSessions().count > 0 || firstResult.getAvailableTestSession() != nil || firstResult.getCurrentTestSession() != nil
                {
                    return firstResult;
                }
            }
        }
        catch
        {
            HMLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
	// Is there a currently running test session?
	// Checks all of the TestSessions for one whose sessionDate falls within the current timeframe, has been started, and hasn't been completed or missed or something.
	// Returns the "current" test.
	// Unlike getAvailableTestSession, this will only return a TestSession if it has been started already (startTime != nil)
	
	//NOTE that if you're trying to get a currently running Test Session, you should probably use DNDataManager's currentTestSession value.
	
	func getCurrentTestSession() -> TestSession?
	{
		if let tests = self.testSessions
		{
			let now = Date();
			
			for i in 0..<tests.count
			{
				let test = tests[i] as! TestSession;
				
				if test.startTime == nil || test.finishedSession == true || test.missedSession == true
				{
					continue;
				}
				
				if let sessionTime = test.sessionDate?.addingTimeInterval(TEST_START_ALLOWANCE), let sessionEndTime = test.expirationDate
				{
					// the current time should be at least sessionTime, and not more than sessionEndTime
					
					if sessionTime.compare(now) == .orderedAscending && sessionEndTime.compare(now) == .orderedDescending
					{
						return test;
					}
				}
			}
		}
		return nil;
	}
	//MARK: session-related methods
	
	// can user take a test right now?
	// Is there a test available at this moment that hasn't already been started?
	// Checks all of the TestSessions for one whose sessionDate falls within the current timeframe, and hasn't been started or missed or something.
	// Unlike getCurrentTestSession, this will only return a TestSession if it has not been started yet (startTime == nil)
	func getAvailableTestSession() -> TestSession?
	{
		if let tests = self.testSessions
		{
			let now = Date();
			
			for i in 0..<tests.count
			{
				let test = tests[i] as! TestSession;
				
				if test.startTime != nil || test.finishedSession == true || test.missedSession == true
				{
					continue;
				}
				
				if let sessionTime = test.sessionDate?.addingTimeInterval(TEST_START_ALLOWANCE), let sessionEndTime = test.expirationDate
				{
					// the current time should be at least sessionTime, and not more than sessionTime plus one hour
					
					if sessionTime.compare(now) == .orderedAscending && sessionEndTime.compare(now) == .orderedDescending
					{
						return test;
					}
				}
			}
		}
		return nil;
	}    // static: get upcoming arc
    // Finds the most recent upcoming Arc, not including any currently running ( userStartDate >= now)
    // includeToday will return any Arcs that would start at the beginning of the current day (this is useful for finding newly created Arcs
    // that we haven't scheduled tests for yet)
    static func getUpcomingVisit(includeToday:Bool = false) -> TestVisit?
    {
        let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        
        var now = NSDate();
        if includeToday
        {
            now = (now as Date).startOfDay() as NSDate;
        }
        request.predicate = NSPredicate(format: "userStartDate>=%@", now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
			let results = try EXCoreDataStack.shared.managedObjectContext.fetch(request);

            if(results.count > 0)
            {
                let firstResult = results[0] as TestVisit;
                return firstResult;
            }
        }
        catch
        {
            HMLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
    
    static func getMostRecentVisit() -> TestVisit?
    {
        let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:false)];
        
        do
        {
			let results = try EXCoreDataStack.shared.managedObjectContext.fetch(request);

            if(results.count > 0)
            {
                let firstResult = results[0] as TestVisit;
                return firstResult;
            }
        }
        catch
        {
            HMLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
    
    
    
	
    
	
    
	
	
    
    
    func getUpcomingSessions() -> [TestSession]
    {
        var upcomingTests:Array<TestSession> = Array();
        
        let now:Date = Date();
        
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if let start = test.sessionDate
                {
                    if start.compare(now) == .orderedDescending
                    {
                        upcomingTests.append(test);
                    }
                }
            }
        }
        
        return upcomingTests;
    }
    
	
    
}
