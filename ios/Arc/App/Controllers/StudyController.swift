//
// StudyController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
import CoreData
open class StudyController : MHController {
	open var DAYS_PER_STUDY:Int = 7;
	open var SESSIONS_PER_DAY:Int =  4; // sessions per day of arc
	open var MIN_SPACING:TimeInterval = 7200; // min spacing betweeen tests, 2 hours
	open var TEST_TIMEOUT:TimeInterval = 300; // 5 minute timeout if the application is closed
	open var TEST_START_ALLOWANCE:TimeInterval = -300; // 5 minute window before actual start time

	///For testing replace these with their mock counterparts
	///TODO: Investigate necessity of cross controller integration
    open var ArcStartDays:  Dictionary<Int, Int> = [1: 0,
                                                    ];
	public var progressHandler:((Int, Int)->())?

	open var studyCount:Int
	{
		get {
			return (defaults.value(forKey:"visitCount") as? Int) ?? 0;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey: "visitCount");
			defaults.synchronize();
		}
	}
    open var beginningOfStudy:Date
        {
        get {
            if let date = (defaults.value(forKey:"beginningOfStudy") as? Date)
            {
                return date;
            }
            else
            {
                let date = Date();
                defaults.setValue(date, forKey:"beginningOfStudy");
                return date;
            }
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "beginningOfStudy");
            defaults.synchronize();
        }
    }
	open var firstTest:SessionInfoResponse.TestState?
		{
		get {
			guard let data = defaults.value(forKey: "firstTest") as? Data else {
				return nil
			}
			return data.decode();

		
		}
		set (newVal)
		{
			if let encoded = newVal.encode() {
				defaults.setValue(encoded, forKey: "firstTest");
				defaults.synchronize();

			} else {
				defaults.removeObject(forKey: "firstTest");
				defaults.synchronize();
			}
		}
	}
	
	open var latestTest:SessionInfoResponse.TestState?
		{
		get {
			guard let data = defaults.value(forKey: "latestTest") as? Data else {
				return nil
			}
			return data.decode();
		}
		set (newVal)
		{
			if let encoded = newVal.encode() {
				defaults.setValue(encoded, forKey: "latestTest");
				defaults.synchronize();
				
			} else {
				defaults.removeObject(forKey: "latestTest");
				defaults.synchronize();
			}
		}
	}
	// Creates a new Arc, sets the visitStartDate and visitEndDate, increments DNDataManager's visitCount
	
	@discardableResult
	open func createStudyPeriod(forDate: Date) -> StudyPeriod
	{
		
		HMLog("Creating StudyPeriod \(self.studyCount) at date: \(DateFormatter.localizedString(from: forDate, dateStyle: .short, timeStyle: .none))");
		let newStudyPeriod:StudyPeriod = new()
		newStudyPeriod.studyID = Int64(studyCount);
		newStudyPeriod.startDate = forDate.startOfDay();
		newStudyPeriod.endDate = forDate.startOfDay().addingDays(days: DAYS_PER_STUDY).endOfDay();
		newStudyPeriod.userStartDate = newStudyPeriod.startDate;
		newStudyPeriod.userEndDate = newStudyPeriod.endDate;
		studyCount = studyCount + 1;
        
		return newStudyPeriod;
	}
	@discardableResult
	open func schedule(sessionAt date:Date, studyId:Int) -> Session
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid studyId")
		}
		let newSession:Session = Arc.shared.sessionController.create(sessionAt: date)
		newSession.study = study;
		newSession.sessionDate = date;
		newSession.expirationDate = date.addingHours(hours: 2);
		
		return newSession;
	}
	open func get(study studyId:Int) -> StudyPeriod? {
		
		let study:[StudyPeriod]? = fetch(predicate: NSPredicate(format: "studyID == %i", studyId), sort: nil)
		
		return study?.first
		
	}
	open func get(session:Int, inStudy studyId:Int) -> Session {
		let study = get(study: studyId)
		return study?.sessions?.first(where: { (test) -> Bool in
			let s = test as! Session
			return s.sessionID == Int(session)
			
		}) as! Session
	}
    open func get(session sessionId:Int) -> Session? {
        
        let study:[Session]? = fetch(predicate: NSPredicate(format: "sessionID == %i", sessionId), sort: nil)
        
        return study?.first
        
    }
    
    open func getUpcomingSessions(withLimit limit:Int) -> [Session]  {
        let now = NSDate();
        
        let predicate = NSPredicate(format: "sessionDate>=%@", now);
        let sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
        
        
        let results:[Session] =  fetch(predicate:predicate, sort:sortDescriptors, limit: limit) ?? []
        
        return results
    }
	open func getAllStudyPeriods() -> [StudyPeriod]
	{
		
		
		let results:[StudyPeriod] = fetch() ?? []
		
		return results;
		
	}
	
	open func getPastStudyPeriods() -> [StudyPeriod]
	{
		
		let now = NSDate();
		let predicate = NSPredicate(format: "userEndDate<=%@",now, now);
		let sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
		
		
		let results:[StudyPeriod] =  fetch(predicate:predicate, sort:sortDescriptors) ?? []
		
		return results;
		
	}
	/** get current arc
	Finds an Arc whose start date and end date fall around the current date ( startDate <= now <= endDate) And has at least one upcoming (or currently available) Session
	*/
	open func getCurrentStudyPeriod() -> StudyPeriod?
	{
		
		let now = NSDate();
		let predicate = NSPredicate(format: "userStartDate<=%@ AND userEndDate>=%@",now, now);
		let sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
		
		
		let results:[StudyPeriod] = fetch(predicate:predicate, sort:sortDescriptors) ?? []
		
		if(results.count > 0)
		{
			let firstResult = results[0] as StudyPeriod
			let id = Int(firstResult.studyID)
			let hasUpcomingSessions = get(upcomingSessions: id).count > 0
			let hasAvailableTestSession = get(availableTestSession: id) != nil
			let hasCurrentTestSession = get(currentTestSession: id) != nil
			
			if hasUpcomingSessions || hasAvailableTestSession || hasCurrentTestSession
			{
				return firstResult;
			}
		}
		
		
		
		return nil;
	}
	// Is there a currently running test session?
	// Checks all of the sessions for one whose sessionDate falls within the current timeframe, has been started, and hasn't been completed or missed or something.
	// Returns the "current" test.
	// Unlike getAvailableTestSession, this will only return a Session if it has been started already (startTime != nil)
	
	//NOTE that if you're trying to get a currently running Test Session, you should probably use DNDataManager's currentTestSession value.
	
    open func get(numberOfTestTakenOfType surveyType:SurveyType, inStudy studyId:Int, week:Int? = nil, day:Int? = nil, session:Int? = nil) -> Int {
        guard let study = get(study: studyId) else {
            fatalError("Invalid study ID")
        }
        guard let tests = study.sessions else {
            return 0
        }
        var total = 0
        for obj in tests {
            
            
            guard let testSession = obj as? Session else {
                continue
            }
            guard let file = testSession.getSurveyFor(surveyType: surveyType) else {
                continue
                
            }
            guard file.isFilledOut else {
                continue
            }
            let isFinished = testSession.finishedSession
            
            let isMissed = testSession.missedSession
            
            let isStarted = testSession.startTime != nil
            
            let isCurrentTest = Arc.shared.currentTestSession == Int(testSession.sessionID)
            
            let isExpired = testSession.expirationDate?.compare(Date()) != .orderedDescending && !isStarted
            
            //If the app is terminated forcibly during a test it will take up until the expiration for it to get marked missed.
            let isRecentlyAbandoned = isStarted && !isCurrentTest && !isFinished
            
            
            guard !isMissed && !isExpired else {
                //The test was found but the session was missed
                continue
            }
            
            guard !isRecentlyAbandoned else {
                continue
            }
            
            //The test was NOT missed and WAS started
            guard isStarted else {
                continue
            }
            
            var weekMatches = true
            
            var dayMatches = true
            
            var sessionMatches = true
            
            if let week = week {
                
                weekMatches = testSession.week == Int64(week)
                
            }
            
            if let day = day {
                
                dayMatches = testSession.day == Int64(day)
                
            }
            
            if let session = session {
                
                sessionMatches = testSession.session == Int64(session)
                
            }
            
            //The test is either the current test being taken and meets requirements
            //Or there was a test found matching these requirements in the past
            if (weekMatches && dayMatches && sessionMatches) {
                total += 1
            }
            
        }
        return total
    }
    
	open func get(currentTestSession studyId:Int) -> Session?
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		if let tests = study.sessions
		{
			let now = Date();
			
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
	// static: get upcoming arc
	// Finds the most recent upcoming Arc, not including any currently running ( userStartDate >= now)
	// includeToday will return any Arcs that would start at the beginning of the current day (this is useful for finding newly created Arcs
	// that we haven't scheduled tests for yet)
	open func getUpcomingStudyPeriod(includeToday:Bool = false) -> StudyPeriod?
	{
		
		var now = NSDate();
		if includeToday
		{
			now = (now as Date).startOfDay() as NSDate;
		}
		let predicate = NSPredicate(format: "userStartDate>=%@", now);
		let sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
		
		
		let results:[StudyPeriod] =  fetch(predicate:predicate, sort:sortDescriptors) ?? []
		
		if(results.count > 0)
		{
			let firstResult = results[0] as StudyPeriod;
			return firstResult;
		}
		
		
		return nil;
	}
	
	open func getMostRecentStudyPeriod() -> StudyPeriod?
	{
		
		let sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:false)];
		
		
		let results:[StudyPeriod] =  fetch(predicate:nil, sort:sortDescriptors) ?? []
		
		if(results.count > 0)
		{
			let firstResult = results[0] as StudyPeriod;
			return firstResult;
		}
		return nil
	}
	
	// Creates all future Arcs starting from startingID (which references the IDs in the InterArcWeeks dictionary).
	// Also creates the first Date Reminder notification for 4 weeks before the start of the Arc.
	// The first Arc is created on startDate, and each subsequent Arc is created N weeks from that.
	
	open func createAllStudyPeriods(startingID:Int, startDate:Date)
	{
		var nextStartDate:Date = startDate;
		var nextId:Int = startingID;
		
		while ArcStartDays[nextId] != nil
		{
			createStudyPeriod(forDate: nextStartDate);
			
			nextId += 1;
			if let days = ArcStartDays[nextId]
			{
				nextStartDate = startDate.addingDays(days: days);
			}
			else
			{
                break;
			}
		}
        save();
	}
	
	// gets the weeks until the next visit, if there is another visit after the current one.
	
	open func getWeeksUntilNextVisit(study:StudyPeriod) -> Int?
	{
		if let days = ArcStartDays[Int(study.studyID) + 1]
		{
			return days;
		}
		
		return nil;
	}
	
	// create test sessions
	// creates and schedules sessions from self.userStartDate
	
	open func create(testSessions studyId: Int, days:Int = -1, startingSessionId:Int64 = 0)
	{
		//Removing use of statics
		var days = days
		if days == -1 {
			days = self.DAYS_PER_STUDY
		}
		guard let participantID = Arc.shared.participantId else {
			HMLog("No participant registered")
			return
		}
		guard let currentStudy = get(study: studyId) else {
			HMLog("No available study.")
			return
		}
		
		guard let startDate = currentStudy.userStartDate else {
			HMLog("HEY DUDE YOU NEED TO SET A START DATE");
			return;
		}
		
//		let calendar = Calendar(identifier: .gregorian);
		let currentStudyId = Int(currentStudy.studyID)
		for i in 0..<days
		{
			
			// Get "current" date, wake sleep times for date, and any existing tests.
			
			let currentDate = startDate.addingDays(days: i);
			
			let sessionsPerDay = SESSIONS_PER_DAY
			
			let day = WeekDay.getDayOfWeek(currentDate)
			let schedule = Arc.shared.scheduleController.get(entriesForDay: day, forParticipant: participantID)
			
			let existingTests = get(sessionsFromDayIndex: i, studyId: currentStudyId) 
			
			// if the current day already has all of its sessions for the day, then we don't need to do anything
			if existingTests.count >= sessionsPerDay
			{
				continue;
			}
			
			// starting from the time of the last test, build new tests at intervals
			let formatter = DateFormatter()
			formatter.defaultDate = currentDate
			formatter.dateFormat = "h:mm a"
			
			var start = formatter.date(from:  (schedule?.first?.availabilityStart)!)!;
			
			var end = formatter.date(from:  (schedule?.last?.availabilityEnd)!)!;
			if end <= start {
				end = end.addingDays(days: 1)
			}
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
			var createdSessions:Array<Session> = Array();
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
				let session = self.schedule(sessionAt: time, studyId: Int(currentStudy.studyID))
				session.sessionDayIndex = Int64(i);
				
				createdSessions.append(session);
			}
			
            //If we're scheduling a set of test sessions for today, make the first one start now
            if existingTests.count == 0 && start.daysSince(date: Date()) == 0
            {
                createdSessions[0].sessionDate = Date();
                createdSessions[0].expirationDate = Date().addingHours(hours: 2);
            }
		}
		
		//Now that we've scheduled all of the tests, let's sort them and set the sessionID accordingly
        let originalDate = beginningOfStudy;
		sort(sessions: Int(currentStudy.studyID))
		if let createdSessions = currentStudy.sessions
		{
			var session:Int64 = 0
			for i in 0..<createdSessions.count
			{
				
				
				let aSession = createdSessions[i] as! Session;
                let weeks = Int64((aSession.sessionDate?.daysSince(date: originalDate) ?? 0) / 7)
                let days = Int64((aSession.sessionDate?.daysSince(date: originalDate) ?? 0) % 7)

				let formatter = DateFormatter()
				formatter.defaultDate = aSession.sessionDate
				formatter.dateFormat = "h:mm a"
				
				
				
				
				if session >= SESSIONS_PER_DAY {
					session = 0
				}
				aSession.sessionID = Int64(i) + startingSessionId;
				aSession.week = weeks
				aSession.day = days
				aSession.session = session
				
				
				session += 1


			}
		}
		save();
		HMLog("Completed Schedule")
		
	}
	
	// remove upcoming tests, but keep finished or missed ones
	
	open func clear(upcomingSessions studyId:Int)
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		if let tests = study.sessions
		{
			var sessionsToRemove:Array<Session> = Array();
			let now = Date();
			
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
				delete(test);
			}
		}
		
		save();
	}
	
	
	open func incompleteSessionCount(studyId:Int) -> Int
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var count:Int = 0;
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
				if test.finishedSession == false
				{
					count += 1;
				}
			}
		}
		
		return count;
	}
	
	// search through sessions, find any whose expiration data is in the past, and hasn't ever been started,
	// and mark it as missed.
	open func mark(confirmed studyId:Int) -> StudyPeriod? {
		let study = get(study: studyId)
		study?.hasConfirmedDate = true
		save()
		return study
	}
	open func markMissedSessions(studyId:Int)
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
				if test.finishedSession == false && test.expirationDate?.compare(Date()) != .orderedDescending
				{
					mark(missed: Int(test.sessionID), studyId: studyId)
				}
			}
		}
		
	}
	
	open func mark(started sessionId:Int, studyId:Int)
	{
		let session = get(session: sessionId, inStudy: studyId)
		session.startTime = Date();
		save();
	}
	open func mark(missed sessionId:Int, studyId:Int)
	{
		let session = get(session: sessionId, inStudy: studyId)
		session.missedSession = true;
		save()
	}
	open func mark(interrupted:Bool,  sessionId:Int, studyId:Int)
	{
		let session = get(session: sessionId, inStudy: studyId)
		session.interrupted = interrupted;
		save();
	}
	open func mark(finished sessionId:Int, studyId:Int)
	{
		let session = get(session: sessionId, inStudy: studyId)
		session.missedSession = false;
		session.finishedSession = true;
		save();
	}
	open func totalMissedSessionCount(studyId:Int) -> Int
	{
		
		var missed:Int = 0;
		for test in get(pastSessions: studyId)
		{
			if test.missedSession
			{
				missed += 1;
			}
		}
		return missed;
		
	}
	open func get(lastSessionId studyId:Int) -> Int64 {
		var id:Int64 = 0
		for session in get(allSessionsForStudy: studyId)
		{
			if session.sessionID >= id {
				id = session.sessionID
			}
		}
		return id
	}
	// returns the longest count of consecutive missed tests
	
	open func get(consecutiveMissedSessionCount studyId:Int) -> Int
	{
		
		var consecutive:Int = 0;
		var maxMissed:Int = 0;
		for test in get(pastSessions: studyId)
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
	
	open func has(scheduledTestSessions studyId:Int) -> Bool
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		return (study.sessions?.count ?? 0) > 0;
	}
	
	open func has(takenAllTests studyId:Int) -> Bool
	{
		
		var hasFinished:Bool = true;
		
		for session in get(allSessionsForStudy: studyId)
		{
			if !session.missedSession && session.startTime == nil && !session.finishedSession
			{
				hasFinished = false;
			}
		}
		
		return hasFinished;
		
	}
	
	open func get(upcomingSessions studyId:Int) -> [Session]
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var upcomingTests:Array<Session> = Array();
		
		let now:Date = Date();
		
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
	
	open func get(pastSessions studyId:Int) -> [Session]
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var pastTests:Array<Session> = Array();
		
		let now:Date = Date();
		
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
	
	open func get(sessionsOnDay date: Date, studyId:Int) -> Array<Session>
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var sessions:Array<Session> = Array();
		
		let startOfDay = date.startOfDay();
		let endOfDay = date.endOfDay()
		
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
	
	open func delete(sessionsUpTo sessionId:Int, inStudy studyId: Int) {
		guard let study = get(study: studyId), let sessions = study.sessions else {
			fatalError("Invalid study ID")
		}
		var deleted:[Session] = []
		for s in sessions {
			let session = s as! Session
			if session.sessionID <= Int64(sessionId){
				deleted.append(session)
			}
		}
		for session in deleted {
			session.study = nil
			study.removeFromSessions(session)
		}
		save()
	}
	
	// Returns an array of Sessions from the given index (0-6). This is not correlated with days of the week,
	// but with the 'day' number in the Arc.
	// Each Session is marked with a sessionDayIndex, which indicates which "day" of the Arc it's intended for.
	// If a participant has an odd wake/sleep cycle (like if they work night shifts), then one "day" of the Arc may span
	// over two days.
	
	open func get(sessionsFromDayIndex index:Int, studyId:Int) -> Array<Session>
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var sessions:Array<Session> = Array();
		
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
				if test.sessionDayIndex == Int64(index)
				{
					sessions.append(test);
				}
			}
		}
		
		sessions.sort { (a, b) -> Bool in
			
			return a.sessionDate!.compare(b.sessionDate!) == .orderedAscending;
		}
		
		return sessions;
	}
	
	open func get(allSessionsForStudy studyId:Int) -> Array<Session>
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		var sessions:Array<Session> = Array();
		
		if let tests = study.sessions
		{
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				sessions.append(test);
			}
		}
		
		return sessions;
	}
	
	open func sort(sessions studyId:Int)
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		if let sessionArray =  study.sessions?.sorted(by: { (a, b) -> Bool in
			return (a as! Session).sessionDate!.compare((b as! Session).sessionDate! as Date) == .orderedAscending;
		}){
			study.sessions = NSOrderedSet(array: sessionArray);
		}
	}
	//MARK: session-related methods
	
	// can user take a test right now?
	// Is there a test available at this moment that hasn't already been started?
	// Checks all of the sessions for one whose sessionDate falls within the current timeframe, and hasn't been started or missed or something.
	// Unlike getCurrentTestSession, this will only return a Session if it has not been started yet (startTime == nil)
	open func get(availableTestSession studyId:Int) -> Session?
	{
		guard let study = get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		
		if let tests = study.sessions
		{
			let now = Date();
			
			for i in 0..<tests.count
			{
				let test = tests[i] as! Session;
				
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
	}
	// clears all useful data from the Session. It only keeps data related to start date, which Arc it's part of,
	// and whether or not it was finished or missed.
	
	open func clearData(sessionId:Int, studyId:Int)
	{
		let session = get(session: sessionId, inStudy: studyId)
		let relationships = session.entity.relationshipsByName;
		
		// delete all of the relationships
		for (name, _) in relationships
		{
			if name == "study"
			{
				continue;
			}
			
			if let v = session.value(forKey: name) as? NSManagedObject
			{
				delete(v);
			}
		}
		
		// and now clear out any data we don't absolutely need to keep the app running
		session.completeTime = nil;
		session.endSignature = nil;
		session.startSignature = nil;
		session.startTime = nil;
		session.willUpgradePhone = false;
		session.interrupted = false;
		
		save();
		
		// and now, delete any notifications
		
		Arc.shared.notificationController.clearPastNotifications();
		
	}
}
