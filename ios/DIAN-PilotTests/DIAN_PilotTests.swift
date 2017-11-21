//
//  DIAN_PilotTests.swift
//  DIAN-PilotTests
//
//  Created by Philip Hayes on 11/14/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import XCTest
import CoreData
import Foundation

class DIAN_PilotTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    
    func testArcCreation()
    {
        print("Creating Arc for today:");
        
        let now = Date();
        
        let arc = TestArc.createArc(forDate: Date());
        
        
        
        let wake = Date().startOfDay().addingTimeInterval(8 * 60 * 60);
        let sleep = Date().startOfDay().addingTimeInterval(22 * 60 * 60);
        
        print("Setting wake/sleep schedule to \(DateFormatter.localizedString(from: wake, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: sleep, dateStyle: .none, timeStyle: .short))");
        
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 0);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 1);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 2);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 3);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 4);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 5);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 6);
        
        arc.createTestSessions();
        
        for i in 0...6
        {
            let wakeSleep = DNDataManager.sharedInstance.getTimes(dayOfWeek: i);
            print("wake/sleep schedule for \(i):  \(DateFormatter.localizedString(from: wakeSleep!.wake!, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: wakeSleep!.bed!, dateStyle: .none, timeStyle: .short))");
            
            let tests = arc.getSessionsOnDay(date: Date().addingDays(days: i));
            
            for test in tests
            {
                let afterWake = (test.sessionDate! as Date).timeIntervalSinceStartOfDay() >= wakeSleep!.wake!.timeIntervalSinceStartOfDay();
                
                let beforeSleep = (test.sessionDate! as Date).timeIntervalSinceStartOfDay() <= wakeSleep!.bed!.timeIntervalSinceStartOfDay();
                
                XCTAssertTrue(afterWake && beforeSleep, "TestSession outside of wake/sleep bounds");
            }
        }
        
        arc.printTestSchedule();
    }
    
    func testAvailableSession()
    {
        let arc = TestArc.createArc(forDate: Date());
        
        arc.scheduleSession(atDate: Date().addingTimeInterval(-120));
        
        XCTAssertTrue(arc.getAvailableTestSession() != nil, "getAvailableTestSession failed");
    }
    
    func testCurrentSession()
    {
        let arc = TestArc.createArc(forDate: Date());
        
        arc.scheduleSession(atDate: Date().addingTimeInterval(-120));
        
        if let session = arc.getAvailableTestSession()
        {
            session.startTime = Date() as NSDate;
            DNDataManager.save();
            XCTAssertTrue(arc.getCurrentTestSession() != nil, "getCurrentTestSession failed");
        }
    }
    
    func testPastSessions()
    {
        
        for i in -8...3
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: i));
            arc.createTestSessions();
            let pastSessions = arc.getPastSessions();
            print("pastSessions count: \(pastSessions.count)");
        }
    }
    
    func testUpcomingSessions()
    {
        for i in -8...3
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: i));
            arc.createTestSessions();
            let upcomingSessions = arc.getUpcomingSessions();
            print("getUpcomingSessions count: \(upcomingSessions.count)");
        }
    }
    
    func testSessionsOnDay()
    {
        
        let wake = Date().startOfDay().addingTimeInterval(8 * 60 * 60);
        let sleep = Date().startOfDay().addingTimeInterval(22 * 60 * 60);
        
        print("Setting wake/sleep schedule to \(DateFormatter.localizedString(from: wake, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: sleep, dateStyle: .none, timeStyle: .short))");
        
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 0);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 1);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 2);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 3);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 4);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 5);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 6);

        
        
        let arc = TestArc.createArc(forDate: Date());
        arc.createTestSessions();
        
        
        
        for i in 0...6
        {
            let tests = arc.getSessionsOnDay(date: Date().addingDays(days: i));
            
            XCTAssertTrue(tests.count == SESSIONS_PER_DAY, "getSessionsOnDay failed. Should be \(SESSIONS_PER_DAY) tests.");
        }
    }
    
    func testClearUpComing()
    {
        for i in -8...3
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: i));
            arc.createTestSessions();
            arc.clearUpcomingSessions();
            let upcomingSessions = arc.getUpcomingSessions();
            print("getUpcomingSessions count: \(upcomingSessions.count)");
        }
        
    }
    
    func testIncompleteSessionCount()
    {
        for i in -8...3
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: i));
            arc.createTestSessions();
            arc.clearUpcomingSessions();
            let incompleteCount = arc.incompleteSessionCount();
            print("incompleteSessionCount count: \(incompleteCount)");
        }
        
    }
    
    func testMissedSesions()
    {
        for i in -3...8
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: i));
            arc.createTestSessions();
            arc.markMissedSessions();
            let missedCount = arc.totalMissedSessionCount();
            print("totalMissedSessionCount count: \(missedCount)");
        }
        
    }
    
    
    func testConsecutiveMissedSessionCount()
    {
        for m in 0...4
        {
            let arc = TestArc.createArc(forDate: Date().addingDays(days: -7));
            
            arc.createTestSessions();
            
            
            var missedString:String = "";
            
            if let tests = arc.testSessions
            {
                for i in 0..<tests.count
                {
                    let test = tests[i] as! TestSession;
                    
                    if arc4random() % 2 == 1
                    {
                        test.missedSession = true;
                        missedString.append("1");
                    }
                    else
                    {
                        missedString.append("0");
                    }
                }
            }
            
            print("missed sequence: \(missedString)");
            
            let missed = arc.consecutiveMissedSessionCount();
            
            print("consecutive missed count: \(missed)");
        }
        
    }
    
    
    func testNightShiftSchedule()
    {
        let now = Date();
        let arc = TestArc.createArc(forDate: now);
     
        let wake = Date().startOfDay().addingTimeInterval(14 * 60 * 60);
        let sleep = Date().startOfDay().addingTimeInterval(7 * 60 * 60);
        
        print("Setting wake/sleep schedule to \(DateFormatter.localizedString(from: wake, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: sleep, dateStyle: .none, timeStyle: .short))");
        
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 0);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 1);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 2);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 3);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 4);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 5);
        DNDataManager.sharedInstance.setTimes(wake: wake, bed: sleep, dayOfWeek: 6);
        
        arc.createTestSessions();
        arc.printTestSchedule();
        
        for i in 0...6
        {
            let wakeSleep = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: now.addingDays(days: i))
            print("wake/sleep schedule for \(i):  \(DateFormatter.localizedString(from: wakeSleep!.wake!, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: wakeSleep!.bed!, dateStyle: .none, timeStyle: .short))");
            
            let tests = arc.getSessionsFromDayIndex(index:Int(i));
            
            for test in tests
            {
                let afterWake = (test.sessionDate! as Date).timeIntervalSince1970 >= wakeSleep!.wake!.timeIntervalSince1970;
                
                let beforeSleep = (test.sessionDate! as Date).timeIntervalSince1970 <= wakeSleep!.bed!.timeIntervalSince1970
                
                XCTAssertTrue(afterWake && beforeSleep, "TestSession outside of wake/sleep bounds");
            }
        }
        
        
        
        
    }
    
}
