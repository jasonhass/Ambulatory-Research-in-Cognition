//
// HASDStudyController.swift
//




import Foundation
import Arc

class HASDStudyController : StudyController {
    // create test sessions
    // creates and schedules sessions from self.userStartDate
	
    private enum RemainingDayLength {
        case veryShort, short, medium, regular, long
		
		
        static func from(timeInterval:TimeInterval) -> RemainingDayLength {
            switch timeInterval {
//            case  0 ..< TimeInterval.Unit.hour * 2:
//                return .veryShort
//            case TimeInterval.Unit.hour * 2 ..< TimeInterval.Unit.hour * 4:
//                return .short
//
//            case TimeInterval.Unit.hour * 4 ..< TimeInterval.Unit.hour * 8:
//                return .medium
//
//            case TimeInterval.Unit.hour * 8 ..< TimeInterval.Unit.hour * 18:
//                return .regular
//
//            case TimeInterval.Unit.hour * 18 ... TimeInterval.Unit.hour * 24:
//                return .long
				
				//We are not performing remaining day length shortening for HASD
				//The baseline takes care of this.
				//Reinstallation is also not effected, because test that occur
			//before reinstallation are counted as missed if expired.
			default:
				return .regular
				
			}
		}
		var testPossible: Int {
			switch self {
			case .veryShort: return 0
			case .short: return 0
			case .medium: return 0
			case .regular, .long: return 4
			}
		}
	}
	open override func createStudyPeriod(forDate: Date, studyId:Int) -> StudyPeriod
	{
		
		HMLog("Creating StudyPeriod \(studyId) at date: \(DateFormatter.localizedString(from: forDate, dateStyle: .short, timeStyle: .none))");
		let phase = HASDPhase.from(studyId: studyId)
		let newStudyPeriod:StudyPeriod = new()
		newStudyPeriod.studyID = Int64(studyId);
		newStudyPeriod.startDate = forDate.startOfDay();
		newStudyPeriod.endDate = forDate.startOfDay().addingDays(days: phase.daysPerStudy() - 1).endOfDay();
		newStudyPeriod.userStartDate = newStudyPeriod.startDate;
		newStudyPeriod.userEndDate = newStudyPeriod.endDate;
		
		
		return newStudyPeriod;
	}
	open override func set(userStartDate date:Date, forStudyId studyId:Int) -> StudyPeriod? {
		guard let study = get(study: studyId) else {
			fatalError("Invalid studyId")
		}
		let phase = HASDPhase.from(studyId: studyId)
		study.userStartDate = date;
		study.userEndDate = date.startOfDay().addingDays(days: phase.daysPerStudy() - 1).endOfDay();
		
		save()
		return study
	}
	
	override open func create<T:Phase>(testSessionsWithSchedule schedule:TestScheduleRequestData, with PhaseType:T.Type) -> Bool where T.PhasePeriod == T{
		guard let firstTest = firstTest else {
			return false
		}
		
		MHController.dataContext.performAndWait {
			
			//Create all test sessions, this gives all studies their default start dates.
			//Take caution to update the user start dates if the first session of the study
			//does not match the study's start date, update the user start date.
			Arc.shared.studyController.beginningOfStudy = Date(timeIntervalSince1970: firstTest.session_date)
			createAllStudyPeriods(startingID: 0, startDate: Date(timeIntervalSince1970: firstTest.session_date))
			
			for sessionData in schedule.sessions
			{
				guard let sessionID = Int(sessionData.session_id) else {
					fatalError("Malformed Data in response")
				}
				
				
				var studyId = sessionID / 28
				var sessionOffSet = sessionID % 28
				
				if sessionID <= 28 {
					studyId = 0
					sessionOffSet = sessionID
				} else {
					studyId = (sessionID - 1) / 28
					sessionOffSet = (sessionID - 1) % 28
				}
				let phase = PhaseType.from(studyId: studyId)
				
				let date = Date(timeIntervalSince1970: sessionData.session_date)
				let session = self.schedule(sessionAt: date, studyId: studyId)
				if sessionOffSet == 0 && studyId != 0 {
					let study = get(study: studyId)
					if session.sessionDate?.days(from: study!.startDate!) != 0 {
						_ = set(userStartDate: session.sessionDate!, forStudyId: studyId)
					}
				}
				session.day = sessionData.day
				session.sessionDayIndex = sessionData.day
				session.week = sessionData.week
				session.sessionID = Int64(sessionID)
				session.session = sessionData.session
				
				let states = phase.statesForSession(week: Int(session.week),
													day: Int(session.day),
													session: Int(session.session))
				
				for state in states {
					let surveyType = state.surveyTypeForState()
					session.createSurveyFor(surveyType: surveyType)
				}
			}
			
			let studies = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID})
			for i in 0 ..< studies.count {
				let study = studies[i]
				
				_ = Arc.shared.studyController.mark(confirmed: Int(study.studyID))
				Arc.shared.notificationController.clear(sessionNotifications: Int(study.studyID))
			}
			cleanupSessionsBeforeLatest()
			
			Arc.shared.notificationController.schedule(upcomingSessionNotificationsWithLimit: 32)
			_ = Arc.shared.notificationController.scheduleDateConfirmationsForUpcomingStudy()
			
			save();
		}
		
		
		return true
	}
	
	
	override open func createTestSessions(studyId: Int, isRescheduling:Bool = false, now:Date = Date())
	{
		
		let phase = HASDPhase.from(studyId: studyId)
		
		let days = phase.daysPerStudy();
		
		guard let participantID = Arc.shared.participantId, let currentStudy = get(study: studyId), let studyStartDate = currentStudy.userStartDate else {
			fatalError("Cannot schedule test sessions. One or more of the following are missing: participant Id, study, study start date");
		}
		
		let beginningOfStudy = Arc.shared.studyController.beginningOfStudy;
		
		let formatter = DateFormatter()
		formatter.dateFormat = "h:mm a"
		
		let startingSessionId = HASDPhase.startingSessionId(forStudyId: studyId);
		
		for dayIndex in 0..<days
		{
			
			let sessionsToCreate = phase.sessionsPerDay(currentDay: dayIndex);
			let sessionDayStartId = (0 ..< dayIndex).reduce(0) {$0 + phase.sessionsPerDay(currentDay: $1)}
			let currentDate:Date = studyStartDate.addingDays(days: dayIndex);
			let dayOfWeek:WeekDay = WeekDay.getDayOfWeek(currentDate)
			let existingTests:Array<Session> = get(sessionsFromDayIndex: dayIndex, studyId: studyId)
			
			formatter.defaultDate = currentDate
			formatter.isLenient = true
			
			guard let schedule:Array<ScheduleEntry> = Arc.shared.scheduleController.get(entriesForDay: dayOfWeek, forParticipant: participantID), schedule.count > 0 else {
				fatalError("Cannot schedule test sessions:Availability Schedule has not been set.");
			}
			
			let start:Date = formatter.date(from:  schedule.first!.availabilityStart!)!;
			var end:Date = formatter.date(from:  schedule.last!.availabilityEnd!)!;
			
			if end <= start {
				end = end.addingDays(days: 1)
			}
			
			// If we're rescheduling, there are several situations that we want to pass on:
			// - If the dates that we're trying to schedule are in the past
			// - If the start < now < end
			// - If the existing test sessions fall around now (the first sessions' start date < now < last session's expiration date)
			
			if isRescheduling
			{
				if end < now || (start < now && now < end)
				{
					continue
				}
				
				if existingTests.count > 0,
					let sessionStart = existingTests.first?.sessionDate,
					let sessionEnd = existingTests.last?.expirationDate,
					sessionStart < now,
					now < sessionEnd
				{
					continue
				}
			}
			
			
			// If there are any existing test sessions, let's delete them, it's easier than trying to re-use them.
			// We're no longer trying to reschedule part of a day, so we don't need to worry about keeping track of what's
			// already been scheduled.
			
			for t in existingTests
			{
				delete(t)
			}
			
			var times:Array<Date> = self.spaceOutTests(sessionsToCreate: sessionsToCreate, start: start, end: end);
			
			// The first study is treated special. It only has one test session, and it should be scheduled for beginningOfStudy
			// If this first test is being scheduled to start today, let's make sure that it actually gets set to now.
			// beginningOfStudy SHOULD be pretty close to now, but we have no way of really knowing for sure.
			
			if studyId == 0 && dayIndex == 0
			{
				if start <= now && now <= end
				{
					times[0] = Date()
				}
				else
				{
					times[0] = beginningOfStudy
				}
			}
			
			
			// Finally, let's create the Sessions, and set their attributes
			for t in 0..<times.count
			{
				let time = times[t];
				
				let session:Session = self.schedule(sessionAt: time, studyId: studyId);
				
				session.sessionDayIndex = Int64(dayIndex);
				//This must take into account that sessions per day can change
				//a cumulative count will produce more accurate results.
				
				let currentSessionId = startingSessionId + sessionDayStartId + t;
				session.sessionID = Int64(currentSessionId);
				session.week = Int64(studyStartDate.weeks(from: beginningOfStudy));
				session.day = session.sessionDayIndex
				
				session.session = Int64(t);
				self.addTestStatesForSession(session, phase: phase)
			}
		}
		
		save();
		HMLog("Completed Schedule")
		
	}
	
	// Space out tests evenly, and, if time allows, randomly disperse the tests.
	private func spaceOutTests(sessionsToCreate:Int, start:Date, end:Date) -> Array<Date>
	{
		var times:Array<Date> = [];
		// setup the initial spacing of the tests, spacing them evenly throughout the time given
		let dayLength = end.timeIntervalSince(start);
		let timeSlice = 1.0 / Double(sessionsToCreate)
		for i in 1...sessionsToCreate
		{
			if start.timeIntervalSince1970 > end.timeIntervalSince1970 {
				times.append((end ... start)[timeSlice * Double(i) - (0.5 * timeSlice)]);
			} else {
				times.append((start ... end)[timeSlice * Double(i) - (0.5 * timeSlice)]);
			}
			
		}
		
		// if we have more than 7 hours, then we want to try to make the spacing more random.
		if dayLength >= 7 * 60 * 60
		{
			times = self.randomizeTimes(times: times, start: start, end: end);
		}
		
		return times;
	}
	
	// Add the necessary test sections to the session, based on the given phase.
	
	private func addTestStatesForSession(_ session: Session, phase: HASDPhase)
	{
		let states = phase.statesForSession(week: Int(session.week),
											day: Int(session.day),
											session: Int(session.session))
		
		var sessionTests:Array<HMCodable> = Array();
		for state in states {
			let surveyType = state.surveyTypeForState()
			
			// All of this looks very repetitive, but because of how the response structs
			// are defined, we can't easily pass them to createNewJSONData
			// (JSONEncoder().encode() doesn't like being passed a generic protocol, it wants to
			// know what the object type is)
			switch surveyType
			{
			case .edna, .ema, .context, .mindfulness, .chronotype, .wake:
				let response = session.createSurveyTest(surveyType: surveyType);
				let jsonData = self.createNewJSONData(id: response.id!, obj: response)
				sessionTests.append(response);
				session.addToSessionData(jsonData);
			case .gridTest:
				let response = session.createGridTest();
				let jsonData = self.createNewJSONData(id: response.id!, obj: response)
				sessionTests.append(response);
				session.addToSessionData(jsonData);
			case .priceTest:
				let response = session.createPriceTest();
				let jsonData = self.createNewJSONData(id: response.id!, obj: response)
				sessionTests.append(response);
				session.addToSessionData(jsonData);
			case .symbolsTest:
				let response = session.createSymbolsTest();
				let jsonData = self.createNewJSONData(id: response.id!, obj: response)
				sessionTests.append(response);
				session.addToSessionData(jsonData);
			default:
				fatalError("Unknown survey type: \(surveyType)");
			}
		}
	}
	
}
