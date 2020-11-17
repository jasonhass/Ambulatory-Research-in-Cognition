//
// EXITests.swift
//



import XCTest
import CoreData
@testable import Arc
@testable import EXR

enum EXTestError : Error {
	case dayImproperNumberOfTests(day:Int)
	case improperStart(session:Session)
	case idSpacingError(Int, prev:Session, next:Session)
	case idMismatch(trueId:Int, actualId:Int)
	case startDateMisMatch(trueDate:String, actualDate:String, study:StudyPeriod)
	case endDateMisMatch(trueDate:String, actualDate:String, study:StudyPeriod)
	case outofBounds(start:Date, end:Date, session:TestScheduleRequestData.Entry)
	var localizedDescription: String {
		switch self {
		case let .dayImproperNumberOfTests(day):
			return "Day: \(day) has the wrong number of tests"
		case let .idSpacingError(distance, id, nextId):
			return "Distance of \(distance) from \(id.sessionID)(in study: \(id.study?.studyID ?? -1)) to \(nextId.sessionID)(in study: \(nextId.study?.studyID ?? -1))"
		
		case let .improperStart(session):
			return "First session started on wrongId \(session.sessionID) in study \(session.study?.studyID ?? -1)"
		case let .idMismatch(trueId, actualId):
			return "Id \(actualId) is not the expected value \(trueId)"
		case let .startDateMisMatch(trueDate, actual, study):
			return "\(study.studyID): Start date \(trueDate) does not fit provided date \(actual), must be within a day"
		case let .endDateMisMatch(trueDate, actual, study):
			return "\(study.studyID): End date \(trueDate) does not fit provided date \(actual), must be within a day"
		case let .outofBounds(start, end, session):
			return "Session \(session.session_id) Date \(Date(timeIntervalSince1970: session.session_date).localizedFormat()) is outside of \(start.localizedFormat()) - \(end.localizedFormat())"
		}
	
	}
	
}

class EXITests: XCTestCase {
	let environment = EXEnvironment.unitTest
	var participantId = 111111
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
		Arc.configureWithEnvironment(environment: environment)
		
		
		Arc.shared.participantId = participantId
		
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
		CoreDataStack.shared.persistentContainer = CoreDataStack.shared.initializeMockStore()
		MHController.dataContext = CoreDataStack.shared.persistentContainer.newBackgroundContext()
		
    }
	
	func testScheduleStartsSequentialIds() {
		MHController.dataContext.performAndWait {
			let startDate =  time(year: 2020, month: 1, day: 1, hour: 8, minute: 0)
			let _ = generateSchedule(start: time(year: 2020, month: 1, day: 1, hour: 8, minute: 30),
											end: time(year: 2020, month: 1, day: 1, hour: 18, minute: 0))
			
			
			let studyController = Arc.shared.studyController
			studyController.createAllStudyPeriods(startingID: 0, startDate: startDate)
			let studies = studyController.getAllStudyPeriods()
			MHController.dataContext.performAndWait {
				Arc.shared.studyController.beginningOfStudy = startDate

				for period in studies {
					studyController.createTestSessions(studyId: Int(period.studyID),
													   isRescheduling: false, now: startDate)
					
					
				}
			}
			
			guard let firstSession = studyController.get(session: 0) else {
				XCTFail("Failed to schedule first session.")
				return
			}
			let values:[Session] = studyController.getUpcomingSessions(withLimit: 500, startDate: startDate as NSDate)
			
			var distance = 0
			let minmaxDistance = 1
			var lastId:Session?
			var errors:[EXTestError] = []
			for item in values.enumerated() {
				let value = item.element
				if let lastId = lastId {
					distance = Int(value.sessionID) - Int(lastId.sessionID)
					if distance != minmaxDistance {
						errors.append(.idSpacingError(distance, prev:lastId, next: value))
					}
				} else {
					if value.sessionID != 0 {
						errors.append(.improperStart(session: value))
					}
				}
				if Int(value.sessionID) != item.offset {
					errors.append(.idMismatch(trueId:item.offset, actualId:Int(value.sessionID)))
				}
				lastId = value

			}
			let output = values.map{"\((id:$0.sessionID, date:$0.sessionDate!.localizedFormat(), day:$0.day, studyId: $0.study!.studyID))"}
			for o in output {
				print(o)
			}
			if errors.count > 0 {
				let statements = errors.map {$0.localizedDescription}.joined(separator: ",\n")
				
				XCTFail(statements)
			}
		}
	}
	
	func testScheduleFourSessionsADay() {
		
		MHController.dataContext.performAndWait {
			let startDate =  time(year: 2020, month: 1, day: 1, hour: 8, minute: 0)
			let schedule = generateSchedule(start: time(year: 2020, month: 1, day: 1, hour: 18, minute: 30),
									 end: time(year: 2020, month: 1, day: 2, hour: 8, minute: 0))
			
			
			let studyController = Arc.shared.studyController
			studyController.createAllStudyPeriods(startingID: 0, startDate: startDate)
			let studies = studyController.getAllStudyPeriods().sorted {$0.studyID < $1.studyID}
			MHController.dataContext.performAndWait {
				for period in studies {
					studyController.createTestSessions(studyId: Int(period.studyID),
													   isRescheduling: false)
					
					
				}
			}
			
			
			let values:[Session] = studyController.getUpcomingSessions(withLimit: 500, startDate: startDate as NSDate).filter {$0.sessionID != 0}
			let sessionSchedule:TestScheduleRequestData = TestScheduleRequestData(withStudyPeriods: studies)
			
			var errors:[EXTestError] = []
			var days:[[TestScheduleRequestData.Entry]] = []
			var day:[TestScheduleRequestData.Entry] = []
			for item in sessionSchedule.sessions.enumerated(){
				let value = item.element
				guard value.session_id != "0" else {
					continue
				}
				
				//In EXR all days are the same range.
				let sessionDate = Date(timeIntervalSince1970: value.session_date)
				let range = Arc.shared.scheduleController.get(rangeForCurrentDay: sessionDate, participantId: participantId)
				let weekday = Arc.shared.scheduleController.get(scheduleForDay: sessionDate, participantID: participantId)
				
				print("""
					\(value.session_id) \(weekday.day.toString())->
					\(range.lowerBound.localizedFormat()) -
					\(range.upperBound.localizedFormat()):
					\(sessionDate.localizedFormat()) \((range.lowerBound ... range.upperBound).contains(sessionDate) ? "√" : "X" )
					""")
				
				if !(range.lowerBound ... range.upperBound).contains(sessionDate) {
					errors.append(.outofBounds(start: range.lowerBound, end: range.upperBound, session: value))
				}
				if day.count > 0 {
					if day.last?.day != value.day {
						days.append(day)
						if day.count != 4 {
							errors.append(.dayImproperNumberOfTests(day: Int(days.count)))
							print(day)
						}
						
						day = []
					}
				}
				day.append(value)
				
			}
			print(days.toString())
			
			if errors.count > 0 {
				let statements = errors.map {$0.localizedDescription}.joined(separator: ",\n")
				
				XCTFail(statements)
			}
		}
	}
	
	func testScheduleCreation() {
		MHController.dataContext.performAndWait {
			var errors:[EXTestError] = []
			/*
			Study duration = 5 years
			Cycle frequency = Every 6 months
			
			Consider "6 months" to be 26 weeks, or 182 days.

			*/
			// Specify date components
			let startDate = time(year: 2019, month: 6, day: 26, hour: 0, minute: 0)
			let cycleStartEndDates = [
				
			/*
			June 26 = Day 1 entry into the app
			June 26th (day 1) + 182 days => Dec 25th
			
			June 26 - July 3 2019 (Test Cycle 0 = Baseline + 8 days)

			*/
			(time(year: 2019, month: 6, day: 26, hour: 0, minute: 0),time(year: 2019, month: 7, day:3, hour: 23, minute: 0)),
			
			//Dec 25 - 31 2019 (Test Cycle 1 = 7 days)
			(time(year: 2019, month: 12, day: 25, hour: 0, minute: 0),time(year: 2019, month: 12, day: 31, hour: 0, minute: 0)),
			
			//June 24 - 30 2020  (Test Cycle 2 = 7 days)
			(time(year: 2020, month: 6, day: 24, hour: 0, minute: 0),time(year: 2020, month: 6, day: 30, hour: 0, minute: 0)),
			
			
			//Dec 23 - 29 2020  (Test Cycle 3 = 7 days)
			(time(year: 2020, month: 12, day: 23, hour: 0, minute: 0),time(year: 2020, month: 12, day: 29, hour: 0, minute: 0)),
			
			//June 23 - 29 2020  (Test Cycle 4 = 7 days)
			(time(year: 2021, month: 6, day: 23, hour: 0, minute: 0),time(year: 2021, month: 6, day: 29, hour: 0, minute: 0)),
			//Dec 22 - 28 2021 (Test Cycle 5 = 7 days)
			(time(year: 2021, month: 12, day: 22, hour: 0, minute: 0),time(year: 2021, month: 12, day: 28, hour: 0, minute: 0)),
			
			//June 22 - 28 2022  (Test Cycle 6 = 7 days)
			(time(year: 2022, month: 6, day: 22, hour: 0, minute: 0),time(year: 2022, month: 6, day: 28, hour: 0, minute: 0)),
			
			//Dec 21 - 27 2022  (Test Cycle 7 = 7 days)
			(time(year: 2022, month: 12, day: 21, hour: 0, minute: 0),time(year: 2022, month: 12, day: 27, hour: 0, minute: 0)),
			
			//June 21 - 27 2023  (Test Cycle 8 = 7 days)
			(time(year: 2023, month: 6, day: 21, hour: 0, minute: 0),time(year: 2023, month: 6, day: 27, hour: 0, minute: 0)),
			
			//Dec 20 - 26 2023  (Test Cycle 9 = 7 days)
			(time(year: 2023, month: 12, day: 20, hour: 0, minute: 0),time(year: 2023, month: 12, day: 26, hour: 0, minute: 0)),
			]

			let schedule = generateSchedule(start: time(year: 2020, month: 1, day: 1, hour: 8, minute: 30),
											end: time(year: 2020, month: 1, day: 1, hour: 18, minute: 0))
			
			let studyController = Arc.shared.studyController
			studyController.createAllStudyPeriods(startingID: 0, startDate: startDate)
			let studies = studyController.getAllStudyPeriods().sorted {$0.studyID < $1.studyID}

			for period in studies {
				studyController.createTestSessions(studyId: Int(period.studyID),
												   isRescheduling: false)
				
			}
			let output:[String] = schedule.compactMap {
				guard
					let start = $0.availabilityStart,
					let end = $0.availabilityEnd else
				{
					return nil
				}
				
				
				return "\($0.day.toString()): \(start) - \(end)\n"
			}
			print(output.joined())
			XCTAssert(studies.count == 10, "Must have 10 (ten) study cycles.")
			
			
			for value in studies.enumerated() {
				let study = value.element
				let index = value.offset
				if study.userStartDate?.days(from: cycleStartEndDates[index].0) != 0 {
					errors.append(.startDateMisMatch(trueDate: cycleStartEndDates[index].0.localizedFormat(), actualDate: study.userStartDate!.localizedFormat(), study: study))
				}
				
				if study.userEndDate?.days(from: cycleStartEndDates[index].1) != 0 {
					errors.append(.endDateMisMatch(trueDate: cycleStartEndDates[index].1.localizedFormat(), actualDate: study.userEndDate!.localizedFormat(), study: study))
				}
			}
			if errors.count > 0 {
				let statements = errors.map {$0.localizedDescription}.joined(separator: ",\n")
				
				XCTFail(statements)
			}
		}
	}
	func testSessionCreation() {
		MHController.dataContext.performAndWait {
			let startDate = time(year: 2019, month: 6, day: 26, hour: 0, minute: 0)
			let _ = generateSchedule(start: time(year: 2020, month: 1, day: 1, hour: 8, minute: 30),
											end: time(year: 2020, month: 1, day: 1, hour: 18, minute: 0))
			
			let studyController = Arc.shared.studyController
			studyController.createAllStudyPeriods(startingID: 0, startDate: startDate)
			let studies = studyController.getAllStudyPeriods()
			MHController.dataContext.performAndWait {
				for period in studies {
					studyController.createTestSessions(studyId: Int(period.studyID),
													   isRescheduling: false)
					
					
				}
			}
			
			
			let values:[String] = studyController.getUpcomingSessions(withLimit: 500).compactMap {
				if let date = $0.sessionDate {
					return "\(date.localizedFormat())\n"
				}
				return nil
				
				
			}
			print(values.joined())
		}
		
		
	}
	func time(year:Int, month:Int, day:Int, hour:Int, minute:Int) -> Date {
		var dateComponents = DateComponents()
		dateComponents.year = year
		dateComponents.month = month
		dateComponents.day = day
		dateComponents.hour = hour
		dateComponents.minute = minute
		
		// Create date from components
		let userCalendar = Calendar.current // user calendar
		
		return userCalendar.date(from: dateComponents)!
	}
	func generateSchedule(start:Date, end:Date) -> [ScheduleEntry] {
		let scheduleController = Arc.shared.scheduleController
		var entries:[ScheduleEntry] = []
		for day in WeekDay.allCases {
			dump(day)
			let formatter = DateFormatter()
			formatter.dateFormat = "h:mm a"
			formatter.isLenient = true
			
			if let entry = scheduleController.create(entry: formatter.string(from: start),
													 endTime: formatter.string(from: end),
													 weekDay: day,
													 participantId: Arc.shared.participantId!) {
				
				entries.append(entry)
			}
			
		}
		XCTAssert(!entries.isEmpty, "Entries cannot be empty")
		return entries
	}
	func generateRandomSchedule() -> [ScheduleEntry] {
		let scheduleController = Arc.shared.scheduleController
		var entries:[ScheduleEntry] = []
		for day in WeekDay.allCases {
			dump(day)
			let formatter = DateFormatter()
			formatter.dateFormat = "h:mm a"
			formatter.isLenient = true
			guard let start = (Date().startOfDay() ... Date().endOfDay()).randomElement() else {
				XCTFail("Failed to produce start time.")
				return []
			}
			guard let end = (Date().startOfDay() ... Date().endOfDay()).randomElement() else {
				XCTFail("Failed to produce end time")
				return []
			}
			
			if let entry = scheduleController.create(entry: formatter.string(from: start),
													 endTime: formatter.string(from: end),
													 weekDay: day,
													 participantId: Arc.shared.participantId!) {
				
				entries.append(entry)
			}
			
		}
		XCTAssert(!entries.isEmpty, "Entries cannot be empty")
		return entries
	}
//    func testPerformanceExample() {
//        // This is an example of a performance test case.
//        self.measure {
//            // Put the code you want to measure the time of here.
//        }
//    }

}
