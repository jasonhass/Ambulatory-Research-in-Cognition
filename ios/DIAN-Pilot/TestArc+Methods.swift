//
//  TestArc+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 5/11/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import CoreData


////////// DEFAULT VALUES ///////////////


var MIN_SPACING:TimeInterval = 7200; // min spacing betweeen tests, 2 hours
var SESSIONS_PER_DAY:Int =  4; // sessions per day of arc
var DAYS_PER_ARC:Int = 7; //number of days an arc lasts
var TEST_TIMEOUT:TimeInterval = 300; // 5 minute timeout if the application is closed

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



/////////// TEST VALUES ////////////////////


//var MIN_SPACING:TimeInterval = 7200; // min spacing betweeen tests
//var SESSIONS_PER_DAY:Int =  4; // sessions per day of arc
//var DAYS_PER_ARC:Int = 7; //number of days an arc lasts
//
//
//// This contains the info for deciding how many weeks are between each Arc.
//// The key is the index of the "next" Arc, and the value is the weeks from the "previous" Arc.
//// So for instance, Arc 2 is 12 weeks after the start date of Arc 1, and Arc 4 is 16 weeks after Arc 3.
//var InterArcWeeks: Dictionary<Int, Int> = [1: 0,
//                                           2: 1,
//                                           3: 1,
//                                           4: 1,
//                                           5: 1,
//                                           6: 1,
//                                           7: 1,
//                                           8: 1,
//                                           9: 1,
//                                           10: 1,
//                                           11: 1,
//                                           12: 1,
//                                           13: 1,
//                                           14: 12,
//                                           15: 12,
//                                           16: 12,
//                                           17: 16,
//                                           18: 12,
//                                           19: 12,
//                                           20: 12,
//                                           21: 16,
//                                           22: 12,
//                                           23: 12,
//                                           24: 12,
//                                           25: 16,
//                                           26: 12,
//                                           27: 12,
//                                           28: 12,
//                                           29: 16];

extension TestArc
{
    
    //MARK: - static methods
    
    static func getAllArcs() -> [TestArc]
    {
        let request:NSFetchRequest<TestArc> = NSFetchRequest<TestArc>(entityName: "TestArc");
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            return results;
        }
        catch
        {
            DNLog("error retrieving cached data: \(error)");
        }
        
        return [];
    }
    
    static func getPastArcs() -> [TestArc]
    {
        let request:NSFetchRequest<TestArc> = NSFetchRequest<TestArc>(entityName: "TestArc");
        
        let now = NSDate();
        request.predicate = NSPredicate(format: "userEndDate<=%@",now, now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            return results;
        }
        catch
        {
            DNLog("error retrieving cached data: \(error)");
        }

        return [];
    }
    
    // static: get current arc
    // Finds an Arc whose start date and end date fall around the current date ( startDate <= now <= endDate)
    // And has at least one upcoming (or currently available) TestSession
    static func getCurrentArc() -> TestArc?
    {
        let request:NSFetchRequest<TestArc> = NSFetchRequest<TestArc>(entityName: "TestArc");
        
        let now = NSDate();
        request.predicate = NSPredicate(format: "userStartDate<=%@ AND userEndDate>=%@",now, now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            if(results.count > 0)
            {
                let firstResult = results[0] as TestArc;
                if firstResult.getUpcomingSessions().count > 0 || firstResult.getAvailableTestSession() != nil || firstResult.getCurrentTestSession() != nil
                {
                    return firstResult;
                }
            }
        }
        catch
        {
            DNLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
    
    // static: get upcoming arc
    // Finds the most recent upcoming Arc, not including any currently running ( userStartDate >= now)
    // includeToday will return any Arcs that would start at the beginning of the current day (this is useful for finding newly created Arcs
    // that we haven't scheduled tests for yet)
    static func getUpcomingArc(includeToday:Bool = false) -> TestArc?
    {
        let request:NSFetchRequest<TestArc> = NSFetchRequest<TestArc>(entityName: "TestArc");
        
        var now = NSDate();
        if includeToday
        {
            now = (now as Date).startOfDay() as NSDate;
        }
        request.predicate = NSPredicate(format: "userStartDate>=%@", now);
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            if(results.count > 0)
            {
                let firstResult = results[0] as TestArc;
                return firstResult;
            }
        }
        catch
        {
            DNLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
    
    static func getMostRecentArc() -> TestArc?
    {
        let request:NSFetchRequest<TestArc> = NSFetchRequest<TestArc>(entityName: "TestArc");
        
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:false)];
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            if(results.count > 0)
            {
                let firstResult = results[0] as TestArc;
                return firstResult;
            }
        }
        catch
        {
            DNLog("error retrieving cached data: \(error)");
        }
        
        return nil;
    }
    
    // Creates a new Arc, sets the arcStartDate and arcEndDate, increments DNDataManager's arcCount
    
    @discardableResult
    static func createArc(forDate: Date) -> TestArc
    {
        
        DNLog("Creating Arc \(DNDataManager.sharedInstance.arcCount) at date: \(DateFormatter.localizedString(from: forDate, dateStyle: .short, timeStyle: .none))");
        let newTestArc:TestArc = NSManagedObject.createIn(context: DNDataManager.backgroundContext);
        newTestArc.arcID = Int64(DNDataManager.sharedInstance.arcCount);
        newTestArc.arcStartDate = forDate.startOfDay() as NSDate;
        newTestArc.arcEndDate = forDate.startOfDay().addingDays(days: DAYS_PER_ARC).endOfDay() as NSDate;
        newTestArc.userStartDate = newTestArc.arcStartDate;
        newTestArc.userEndDate = newTestArc.arcEndDate;
        DNDataManager.sharedInstance.arcCount = DNDataManager.sharedInstance.arcCount + 1;
        DNDataManager.save();
        return newTestArc;
    }
    
    
    // Creates all future Arcs starting from startingID (which references the IDs in the InterArcWeeks dictionary).
    // Also creates the first Date Reminder notification for 4 weeks before the start of the Arc.
    // The first Arc is created on startDate, and each subsequent Arc is created N weeks from that.
    
    static func createAllArcs(startingID:Int, startDate:Date)
    {
        var nextStartDate:Date = startDate;
        var nextId:Int = startingID;
        
        while InterArcWeeks[nextId] != nil
        {
            let newArc = TestArc.createArc(forDate: nextStartDate);
            
           
            nextId += 1;
            if let weeks = InterArcWeeks[nextId]
            {
                nextStartDate = nextStartDate.addingWeeks(weeks: weeks);
            }
            else
            {
                return;
            }
        }
    }
    
    //MARK: - Instance methods
    
    
    //MARK: Arc-related methods
    
    
    func hasTakenChronotypeSurvey() -> Bool
    {
        if let sessions = self.testSessions
        {
            for i in 0..<sessions.count
            {
                let session = sessions[i] as! TestSession;
                if session.hasTakenChronotype
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    func hasTakenWakeSurvey(forDay day:Date) -> Bool
    {
        let sessions = self.getSessionsOnDay(date: day);
        
        for session in sessions
        {
            if session.hasTakenWake
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    // set the userStartDate, and automatically sets the end date
    
    func setStartDate(date:Date)
    {
        self.userStartDate = date.startOfDay() as NSDate;
        self.userEndDate = date.addingDays(days: DAYS_PER_ARC).endOfDay() as NSDate;
        DNDataManager.save();
        DNRestAPI.shared.sendArcDateUpdate(forArc: self);
    }
    
    // get days remaining in Arc
    
    func getDaysRemaining() -> Int
    {
        return (self.userEndDate! as Date).daysSince(date: Date());
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
                
                if let sessionTime = test.sessionDate, let sessionEndTime = test.expirationDate
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
                
                if let sessionTime = test.sessionDate, let sessionEndTime = test.expirationDate
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
    
    func getPastSessions() -> [TestSession]
    {
        var pastTests:Array<TestSession> = Array();
        
        let now:Date = Date();
        
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if let start = test.sessionDate
                {
                    if start.compare(now) == .orderedAscending
                    {
                        pastTests.append(test);
                    }
                }
            }
        }
        
        return pastTests;
    }
    
    // get test sessions on given day
    // searches through all sessions, and finds sessions that fall between the start and end of a given day (midnight of the given date and midnight of the next day)
    
    func getSessionsOnDay(date: Date) -> Array<TestSession>
    {
        var sessions:Array<TestSession> = Array();
        
        let startOfDay = date.startOfDay();
        let endOfDay = date.endOfDay()
        
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if let sessionTime = test.sessionDate
                {
                    if sessionTime.compare(startOfDay) == .orderedDescending
                        && sessionTime.compare(endOfDay) == .orderedAscending
                    {
                        sessions.append(test);
                    }
                }
            }
        }

        sessions.sort { (a, b) -> Bool in
            
            return a.sessionDate!.compare(b.sessionDate! as Date) == .orderedAscending;
        }
        
        return sessions;
    }
    
    
    
    // Returns an array of Sessions from the given index (0-6). This is not correlated with days of the week,
    // but with the 'day' number in the Arc.
    // Each Session is marked with a sessionDayIndex, which indicates which "day" of the Arc it's intended for.
    // If a participant has an odd wake/sleep cycle (like if they work night shifts), then one "day" of the Arc may span
    // over two days.
    
    func getSessionsFromDayIndex(index:Int) -> Array<TestSession>
    {
        var sessions:Array<TestSession> = Array();
        
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if test.sessionDayIndex == Int64(index)
                {
                    sessions.append(test);
                }
            }
        }
        
        sessions.sort { (a, b) -> Bool in
            
            return a.sessionDate!.compare(b.sessionDate! as Date) == .orderedAscending;
        }
        
        return sessions;
    }
    
    func getAllSessions() -> Array<TestSession>
    {
        var sessions:Array<TestSession> = Array();
        
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                sessions.append(test);
            }
        }
        
        return sessions;
    }
    
    // create test sessions
    // creates and schedules TestSessions from self.userStartDate
    
    func createTestSessions(days:Int = DAYS_PER_ARC, sessionsPerDay:Int = SESSIONS_PER_DAY)
    {
        
        guard let startDate = self.userStartDate as? Date else {
            DNLog("HEY DUDE YOU NEED TO SET A START DATE");
            return;
        }
        
        let calendar = Calendar(identifier: .gregorian);
        
        for i in 0..<days
        {
            
            // Get "current" date, wake sleep times for date, and any existing tests.
            
            let currentDate = startDate.addingDays(days: i);
   
            let wakeSleepTimes = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: currentDate);
            let existingTests = self.getSessionsFromDayIndex(index: i);
            
            // if the current day already has all of its sessions for the day, then we don't need to do anything
            if existingTests.count >= sessionsPerDay
            {
                continue;
            }
            
            // starting from the time of the last test, build new tests at intervals
            
            var start = wakeSleepTimes!.wake!;
            let end = wakeSleepTimes!.bed!;
            
            var dayLength = end.timeIntervalSince(start);
            
            // If currentDate is actually today, then we want to start from the current time, instead of the actual wake time.
            
            if start.daysSince(date: Date()) == 0
            {
                start = Date();
                dayLength = end.timeIntervalSince(start);
            }
            
            // if there are already existing tests for this day, then we want to continue our scheduling from the last test.
            if existingTests.count > 0
            {
                // if they're rescheduling tests for today, we don't want to schedule any new tests to start before right now.
                
                start = existingTests.last!.sessionDate! as Date;
                
                //if the start time of the last test is before now, set start to now
                if start.compare(Date()) == .orderedAscending
                {
                    start = Date();
                }
                else    //otherwise, set it to the last test session's start time + 2 hours
                {
                    start = (existingTests.last!.sessionDate! as Date).addingHours(hours: 2);
                }
            }
            
            
            // now that we know our start/end times, we need to actually schedule the tests.
            // We figure out how many sections we actually need for this day, and schedule them accordingly.
            
            var times:Array<Date> = [];
            var createdSessions:Array<TestSession> = Array();
            let sessionsToCreate = sessionsPerDay - existingTests.count;
            
            
            // setup the initial spacing of the tests, spacing them evenly throughout the time given
            let gap = end.timeIntervalSince(start);
            let period = max(10, gap / TimeInterval(sessionsToCreate));
            
            for i in 1...sessionsToCreate
            {
                times.append(start.addingTimeInterval(period * TimeInterval(i) - (0.5 * period) ));
            }
            
            // if we have more than 7 hours, then we want to try to make the spacing more random.
            // for each
            if dayLength >= 7 * 60 * 60
            {
                
                for i in 0..<times.count
                {
                    var minTime:Date?;
                    var maxTime:Date?;
                    
                    // if this is the first test, then we need to set minTime to start. BUT, sometimes start 
                    // might actually be the time of the last test scheuled, in which case we want start + 2 hours
                    // That's why, above, around line 558, we set it to the last sessions' start time + 2 hours
                    if i == 0
                    {
                        minTime = start;
                    }
                    else    //otherwise we want the last scheduled test + 2 hours
                    {
                        minTime = times[i - 1].addingHours(hours: 2);
                    }
                    
                    
                    // if this is the last test, we want to set maxTime to the end of the day
                    if i == times.count - 1
                    {
                        maxTime = end;
                    }
                    else    //otherwise set it to the next text - 2 hours
                    {
                        maxTime = times[i + 1].addingHours(hours: -2);
                    }
                    
                    if let least = minTime, let most = maxTime
                    {
                        let randomTimeInterval =  min(most.timeIntervalSince1970, least.timeIntervalSince1970 + TimeInterval(arc4random_uniform(UInt32(max(0, most.timeIntervalSince1970 - least.timeIntervalSince1970)))));
                        times[i] = Date(timeIntervalSince1970: randomTimeInterval);
                    }
                }
            }
                
            for time in times
            {
                let session = self.scheduleSession(atDate: time);
                createdSessions.append(session);
            }
            
            //If we're scheduling a set of test sessions for today, make the first one start now
            if existingTests.count == 0 && start.daysSince(date: Date()) == 0
            {
                createdSessions[0].sessionDate = NSDate();
            }
        }
        
        
        //Now that we've scheduled all of the tests, let's sort them and set the sessionID accordingly
        self.sortSessions();
        if let createdSessions = self.testSessions
        {
        
            for i in 0..<createdSessions.count
            {
                let aSession = createdSessions[i] as! TestSession;
                aSession.sessionID = Int64(i);
            }
        }
                
        DNDataManager.save();
        DNRestAPI.shared.sendTestSchedule(forArc: self);
    }
    
    
    // create tests randomly spaced between the two dates
    // Start with an array containing just the start and end dates,
    // Then, for each session:
    // - make sure the times array is sorted
    // - find the two consecutive dates with the largest gap
    // - pick a random time between those two dates (allowing room for the minSpacing between times)
    // - creat a new TestSession with that date
    // - add that date to the array of times
    
    
    @discardableResult
    func scheduleSession(atDate:Date) -> TestSession
    {
        
        let newSession:TestSession = TestSession.createIn(context: DNDataManager.backgroundContext);
        newSession.testArc = self;
        newSession.sessionDate = atDate as NSDate;
        newSession.expirationDate = atDate.addingHours(hours: 2) as NSDate;
        DNDataManager.save();
        
        return newSession;
    }
    
    
    func willSessionsFitWithinSleepSchedule() -> Bool
    {
        
        let remainingSessions = self.getUpcomingSessions();
        
        for session in remainingSessions
        {
            let sessionDate = session.sessionDate as! Date;
            let wakeSleepTimes = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: sessionDate);
            
            if sessionDate.compare(wakeSleepTimes!.wake!) != .orderedDescending || sessionDate.compare(wakeSleepTimes!.bed!) != .orderedAscending
            {
                return false;
            }
            
        }
        
        return true;
    }
    
    // remove upcoming tests, but keep finished or missed ones
    
    func clearUpcomingSessions()
    {        
        if let tests = self.testSessions
        {
            var sessionsToRemove:Array<TestSession> = Array();
            let now = Date();
            
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if test.finishedSession == true || test.missedSession == true
                {
                    continue;
                }
                
                if let sessionTime = test.sessionDate
                {
                    if sessionTime.compare(now) == .orderedDescending
                    {
                        sessionsToRemove.append(test);
                    }
                }
            }
            
            for test in sessionsToRemove
            {
//                DNLog("removing test \(test.sessionID)");
                DNDataManager.backgroundContext.delete(test);
            }
        }
        
        DNDataManager.save();
    }
    
    
    func incompleteSessionCount() -> Int
    {
        var count:Int = 0;
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if test.finishedSession == false
                {
                    count += 1;
                }
            }
        }
        
        return count;
    }
    
    // search through TestSessions, find any whose expiration data is in the past, and hasn't ever been started,
    // and mark it as missed.
    
    func markMissedSessions()
    {
        if let tests = self.testSessions
        {
            for i in 0..<tests.count
            {
                let test = tests[i] as! TestSession;
                
                if test.finishedSession == false && test.expirationDate?.compare(Date()) != .orderedDescending
                {
                    test.markMissed();
                }
            }
        }
        
        DNDataManager.save();
    }
    
    
    func totalMissedSessionCount() -> Int
    {
        var missed:Int = 0;
        for test in self.getPastSessions()
        {
            if test.missedSession
            {
                missed += 1;
            }
        }
        return missed;

    }
    
    // returns the longest count of consecutive missed tests
    
    func consecutiveMissedSessionCount() -> Int
    {
        var consecutive:Int = 0;
        var maxMissed:Int = 0;
        for test in self.getPastSessions()
        {
            if test.missedSession
            {
                consecutive += 1;
            }
            else
            {
                if consecutive > maxMissed
                {
                    maxMissed = consecutive;
                }
                consecutive = 0;
            }
        }
        
        //check one last time to make sure we have the max count of consecutive missed tests.
        
        if consecutive > maxMissed
        {
            maxMissed = consecutive;
        }
        
        return maxMissed;
    }
    
    func hasScheduledTestSessions() -> Bool
    {
        return (self.testSessions?.count ?? 0) > 0;
    }
    
    
    //MARK: notification-related methods
    
    
    // creates notifications for upcoming TestSessions
    func scheduleSessionNotifications()
    {
        let tests = self.getUpcomingSessions()
        for test in tests
        {
            
            let title = NSLocalizedString("It's time to take a quick test!", comment: "Notification text for test session");
            let body = title;
            
            let newNotification = NotificationEntry.scheduleNotification(date: test.sessionDate! as Date, title: title, body: body, identifierPrefix: "TestSession");
            
            newNotification.arcID = self.arcID;
            newNotification.sessionID = test.sessionID;
        }
        
        self.hasScheduledNotifications = true;
        
        DNDataManager.save();
    }
    
    // clear notifications for upcoming TestSessions
    func clearSessionNotifications()
    {
        NotificationEntry.clearNotifications(withIdentifierPrefix: "TestSession");
        self.hasScheduledNotifications = false;
        DNDataManager.save();
    }
    
    
    // creates a reminder notification for the start of the given Arc.
    
    func scheduleDateRemdinderNotification()
    {
        if let d = self.userStartDate as Date?
        {
            var fireDate:Date = d.addingDays(days: -7 * 4); // 4 weeks prior to start date
            
            if let wakeSleep = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: fireDate)
            {
                var midDay = wakeSleep.wake!.addingTimeInterval(wakeSleep.bed!.timeIntervalSince(wakeSleep.wake!) / 2.0);
                if midDay.compare(fireDate) == .orderedAscending
                {
                    midDay = midDay.addingDays(days: 1);
                }
                fireDate = midDay;
            }
            
            var body = NSLocalizedString("Your next test will be on <DATE>", comment: "Notification text for one-month reminder");
            
            let formattedDate = DateFormatter.localizedString(from: d, dateStyle: .short, timeStyle: .none);
            
            body = body.replacingOccurrences(of: "<DATE>", with: formattedDate);
            
            let newNotification = NotificationEntry.scheduleNotification(date: fireDate, title: "", body: body, identifierPrefix: "DateReminder-\(self.arcID)");
            newNotification.arcID = self.arcID;
            DNDataManager.save();
        }
    }
    
    func hasScheduledDateReminder() -> Bool
    {
        let maybeNotifications = NotificationEntry.getNotifications(withIdentifierPrefix: "DateReminder-\(self.arcID)", onlyPending: false);
        return maybeNotifications.count > 0;
    }
    
    func clearDateReminderNotification()
    {
        NotificationEntry.clearNotifications(withIdentifierPrefix: "DateReminder-\(self.arcID)");
    }
    
    // Start Date confirmations
    // creates a notification reminding the participant to confirm their start date
    
    
    func scheduleConfirmationReminders()
    {
        if let d = self.arcStartDate as Date?
        {
            
            for week in 1...3
            {
                let previousDate = d.addingDays(days: -7 * week);
                
                if let wakeSleep = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: previousDate)
                {
                    var midDay = wakeSleep.wake!.addingTimeInterval(wakeSleep.bed!.timeIntervalSince(wakeSleep.wake!) / 2.0);
                    if midDay.compare(previousDate) == .orderedAscending
                    {
                        midDay = midDay.addingDays(days: 1);
                    }
                    self.scheduleConfirmationReminder(week: week, fireDate: midDay);
                }
                else
                {
                    self.scheduleConfirmationReminder(week: week, fireDate: previousDate);
                }
            }
        }
    }
    
    func scheduleConfirmationReminder(week:Int, fireDate:Date = Date().addingMinutes(minutes: 1))
    {
        let body = NSLocalizedString("Please confirm your next test date.", comment: "Notification for Arc date confirmation");
        
        let newNotification = NotificationEntry.scheduleNotification(date: fireDate, title: "", body: body, identifierPrefix: "DateConfirmation-\(self.arcID)-\(week)");
        newNotification.arcID = self.arcID;
        DNDataManager.save();
    }
    
    func hasScheduledConfirmationReminder(forWeek week:Int) -> Bool
    {
        let maybeNotifications = NotificationEntry.getNotifications(withIdentifierPrefix: "DateConfirmation-\(self.arcID)-\(week)", onlyPending: false);
        return maybeNotifications.count > 0;
    }
    
    func hasScheduledConfirmationReminders() -> Bool
    {
        let maybeNotifications = NotificationEntry.getNotifications(withIdentifierPrefix: "DateConfirmation-\(self.arcID)", onlyPending: false);
        return maybeNotifications.count > 0;
    }
    
    
    func clearConfirmationReminder(forWeek week:Int)
    {
        NotificationEntry.clearNotifications(withIdentifierPrefix: "DateConfirmation-\(self.arcID)-\(week)");
    }
    
    func clearConfirmationReminders()
    {
        NotificationEntry.clearNotifications(withIdentifierPrefix: "DateConfirmation-\(self.arcID)");
    }
    
    
    // missed test notifications
    
    func scheduleMissedTestsNotification(fireDate:Date = Date().addingMinutes(minutes: 1))
    {
        let body = NSLocalizedString("You've missed your tests. If you're unable to finish this week, please contact your site coordinator.", comment: "Notification for missed test sessions");
        
        let newNotification = NotificationEntry.scheduleNotification(date: fireDate, title: "", body: body, identifierPrefix: "MissedTests-\(self.arcID)");
        newNotification.arcID = self.arcID;
        DNDataManager.save();
    }
    
    func hasScheduledMissedTestsNotification() -> Bool
    {
        let maybeNotifications = NotificationEntry.getNotifications(withIdentifierPrefix: "MissedTests-\(self.arcID)", onlyPending: false);
        return maybeNotifications.count > 0;
    }
    
    
    //MARK: helpers
    
    private func findBiggestGap(times:Array<Date>) -> Int
    {
        var largestGap:TimeInterval = 0;
        var largestIndex:Int = 0;
        for i in 0...(times.count - 2)
        {
            let gap = times[i + 1].timeIntervalSince(times[i]);
            if gap > largestGap
            {
                largestGap = gap;
                largestIndex = i;
            }
        }
        return largestIndex;
    }
    
    func sortSessions()
    {
        
        if let sessionArray =  self.testSessions?.sorted(by: { (a, b) -> Bool in
            return (a as! TestSession).sessionDate!.compare((b as! TestSession).sessionDate! as Date) == .orderedAscending;
        }){
            self.testSessions = NSOrderedSet(array: sessionArray);
        }
    }
    
    func printTestSchedule()
    {
        DNLog("Printing test session schedule");
        
        
        if let sessions = self.testSessions
        {
            
            DNLog("total sessions: \(sessions.count)");
            DNLog("incomplete sessions: \(self.incompleteSessionCount())");
            
            
            for i in 0..<sessions.count
            {
                let session = sessions[i] as! TestSession;
                
                DNLog("session \(session.sessionID), date: \(DateFormatter.localizedString(from: session.sessionDate! as Date, dateStyle: .short, timeStyle: .short)) | time since beginning of Arc: \(session.sessionDate!.timeIntervalSince(self.userStartDate! as Date)) | days since today: \((session.sessionDate! as Date).daysSince(date: Date())) | day index: \(session.sessionDayIndex)", quiet: true);
            }
        }
    }
    
    func printNotificationSchedule()
    {
        DNLog("Printing notification schedule");
        
        let notifications = NotificationEntry.getNotifications(withIdentifierPrefix: nil, onlyPending: true);
        
        DNLog("total pending notifications: \(notifications.count)");
        for notification in notifications
        {
            DNLog("\(notification.notificationIdentifier!) | '\(notification.title!)'\(notification.body!)' date: \(DateFormatter.localizedString(from: notification.scheduledAt! as Date, dateStyle: .short, timeStyle: .short)) | arc id: \(notification.arcID) | session id: \(notification.sessionID)",quiet: true );
        }
        
        
    }
    
    
}
