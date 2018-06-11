//
//  DIAN_PilotTests.swift
//  DIAN-PilotTests
/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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

    func testRegistration() {
        
    }
    func testVisitCreation()
    {
        print("Creating Arc for today:");
        
        let now = Date();
        
        let visit = TestVisit.createVisit(forDate: Date());
        
        
        
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
        
        visit.createTestSessions();
        
        for i in 0...6
        {
            let wakeSleep = DNDataManager.sharedInstance.getTimes(dayOfWeek: i);
            print("wake/sleep schedule for \(i):  \(DateFormatter.localizedString(from: wakeSleep!.wake!, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: wakeSleep!.bed!, dateStyle: .none, timeStyle: .short))");
            
            let tests = visit.getSessionsOnDay(date: Date().addingDays(days: i));
            
            for test in tests
            {
                let afterWake = (test.sessionDate! as Date).timeIntervalSinceStartOfDay() >= wakeSleep!.wake!.timeIntervalSinceStartOfDay();
                
                let beforeSleep = (test.sessionDate! as Date).timeIntervalSinceStartOfDay() <= wakeSleep!.bed!.timeIntervalSinceStartOfDay();
                
                XCTAssertTrue(afterWake && beforeSleep, "TestSession outside of wake/sleep bounds");
            }
        }
        
        visit.printTestSchedule();
    }
    
    func testAvailableSession()
    {
        let visit = TestVisit.createVisit(forDate: Date());
        
        visit.scheduleSession(atDate: Date().addingTimeInterval(-120));
        
        XCTAssertTrue(visit.getAvailableTestSession() != nil, "getAvailableTestSession failed");
    }
    
    func testCurrentSession()
    {
        let visit = TestVisit.createVisit(forDate: Date());
        
        visit.scheduleSession(atDate: Date().addingTimeInterval(-120));
        
        if let session = visit.getAvailableTestSession()
        {
            session.startTime = Date() as NSDate;
            DNDataManager.save();
            XCTAssertTrue(visit.getCurrentTestSession() != nil, "getCurrentTestSession failed");
        }
    }
    
    func testPastSessions()
    {
        
        for i in -8...3
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: i));
            visit.createTestSessions();
            let pastSessions = visit.getPastSessions();
            print("pastSessions count: \(pastSessions.count)");
        }
    }
    
    func testUpcomingSessions()
    {
        for i in -8...3
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: i));
            visit.createTestSessions();
            let upcomingSessions = visit.getUpcomingSessions();
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

        
        
        let visit = TestVisit.createVisit(forDate: Date());
        visit.createTestSessions();
        
        
        
        for i in 0...6
        {
            let tests = visit.getSessionsOnDay(date: Date().addingDays(days: i));
            
            XCTAssertTrue(tests.count == SESSIONS_PER_DAY, "getSessionsOnDay failed. Should be \(SESSIONS_PER_DAY) tests.");
        }
    }
    
    func testClearUpComing()
    {
        for i in -8...3
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: i));
            visit.createTestSessions();
            visit.clearUpcomingSessions();
            let upcomingSessions = visit.getUpcomingSessions();
            print("getUpcomingSessions count: \(upcomingSessions.count)");
        }
        
    }
    
    func testIncompleteSessionCount()
    {
        for i in -8...3
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: i));
            visit.createTestSessions();
            visit.clearUpcomingSessions();
            let incompleteCount = visit.incompleteSessionCount();
            print("incompleteSessionCount count: \(incompleteCount)");
        }
        
    }
    
    func testMissedSesions()
    {
        for i in -3...8
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: i));
            visit.createTestSessions();
            visit.markMissedSessions();
            let missedCount = visit.totalMissedSessionCount();
            print("totalMissedSessionCount count: \(missedCount)");
        }
        
    }
    
    
    func testConsecutiveMissedSessionCount()
    {
        for m in 0...4
        {
            let visit = TestVisit.createVisit(forDate: Date().addingDays(days: -7));
            
            visit.createTestSessions();
            
            
            var missedString:String = "";
            
            if let tests = visit.testSessions
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
            
            let missed = visit.consecutiveMissedSessionCount();
            
            print("consecutive missed count: \(missed)");
        }
        
    }
    
    
    func testNightShiftSchedule()
    {
        let now = Date();
        let visit = TestVisit.createVisit(forDate: now);
     
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
        
        visit.createTestSessions();
        visit.printTestSchedule();
        
        for i in 0...6
        {
            let wakeSleep = DNDataManager.sharedInstance.getWakeSleepTimes(forDate: now.addingDays(days: i))
            print("wake/sleep schedule for \(i):  \(DateFormatter.localizedString(from: wakeSleep!.wake!, dateStyle: .none, timeStyle: .short)) / \(DateFormatter.localizedString(from: wakeSleep!.bed!, dateStyle: .none, timeStyle: .short))");
            
            let tests = visit.getSessionsFromDayIndex(index:Int(i));
            
            for test in tests
            {
                let afterWake = (test.sessionDate! as Date).timeIntervalSince1970 >= wakeSleep!.wake!.timeIntervalSince1970;
                
                let beforeSleep = (test.sessionDate! as Date).timeIntervalSince1970 <= wakeSleep!.bed!.timeIntervalSince1970
                
                XCTAssertTrue(afterWake && beforeSleep, "TestSession outside of wake/sleep bounds");
            }
        }
        
        
        
        
    }
    
}
