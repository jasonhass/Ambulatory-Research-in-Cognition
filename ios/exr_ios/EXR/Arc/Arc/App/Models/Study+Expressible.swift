//
// Study+Expressible.swift
//



import Foundation
extension StudyController : ThisStudyExpressible {
	public var nextTestCycle: String {
		if studyState == .inactive, let study = getUpcomingStudyPeriod() {
			guard let start = study.userStartDate?.localizedFormat(template: ACDateStyle.mediumWeekDayMonthDay.rawValue) else {
				return ""
			}
			guard let end = study.userEndDate?.localizedFormat(template: ACDateStyle.mediumWeekDayMonthDay.rawValue) else {
				return ""
			}
            return "\(start) - \(end)"
		}
		
		return ""
	}
	public var week: Int {
		let previousStudiesCount = getPastStudyPeriods().count
		let currentStudy = (getCurrentStudyPeriod(includeCompleted: true) != nil) ? 1 : 0
		return previousStudiesCount + currentStudy
	}
	public var studyState:StudyState {
		if let study = getCurrentStudyPeriod() {
			guard let start = study.userStartDate else {return .unknown}
		
			
			
			if study.studyID == 0 {
				if day == 0{
					return .baseline
				}
				return .activeBaseline
			}
			
			return .active
		} else if let _ = getUpcomingStudyPeriod() {
			
			
			return .inactive
		} else {
			return .complete
		}
		
		// return .complete

	}
	public var totalWeeks: Int {
		let totalStudies = getAllStudyPeriods().count
		
		return totalStudies
	}
	
	public var joinedDate: String {
		return beginningOfStudy.localizedFormat(template:ACDateStyle.longWeekdayMonthDayYear.rawValue)
	}
	
	public var finishDate: String {
		guard let lastCycle = getAllStudyPeriods().last else {
			return ""
		}
		guard let lasSession = lastCycle.sessions?.lastObject as? Session else {
			return ""
		}
		return lasSession.expirationDate!.localizedFormat(template:ACDateStyle.longWeekdayMonthDayYear.rawValue)
	}
	
	public var timeBetweenTestingWeeks: String {
		var components = DateComponents()
		
		var calendar = Calendar.current
		
		calendar.locale = Locale(identifier: Arc.shared.appController.locale.string)
		
		components.calendar = calendar

		components.month = 6
		
		return DateComponentsFormatter.localizedString(from: components, unitsStyle: DateComponentsFormatter.UnitsStyle.full) ?? ""
	}
	
	
}


extension StudyController : ThisWeekExpressible {
	public var daysArray: [String] {
		var w:StudyPeriod?
		if let p = getCurrentStudyPeriod() {
			w = p
		}
		if let p = getPastStudyPeriods().last {
			w = p
		}
		guard let week = w else {
			return []
		}
		guard let start = week.userStartDate else {return []}
		guard let end = week.userEndDate else {return []}
		let formatter = DateFormatter()
		formatter.dateFormat = "EEEEE"
		formatter.locale = Arc.shared.appController.locale.getLocale()
		var values:[String] = []
		for i in 0 ... end.daysSince(date: start) {
			let date = start.addingDays(days: i)
			values.append(formatter.string(from: date))
			
		}
		
		return values
	
	}
	
	public var day: Int {
		guard let session = Arc.shared.studyController.get(sessionsOnDay: Date()).first else {
			return -1
		}
		
		return Int(session.day)
	}

	
	public var totalDays: Int {
		switch getCurrentStudyPeriod() {
		case .some(let week):
			guard let start = week.userStartDate else {return 0}
			guard let end = week.userEndDate else {return 0}

			return end.daysSince(date: start)
			
		default:
			return 0
		}

	}
	
	public var startDate: String {
		switch getCurrentStudyPeriod() {
		case .some(let week):
			guard var start = week.userStartDate else {return ""}
			if studyState == .baseline || studyState == .activeBaseline {
				start = start.addingDays(days: 1)
			}
			return start.localizedFormat(template:ACDateStyle.longWeekdayMonthDay.rawValue)
		default:
			return ""
		}
	}
	
	public var endDate: String {
		switch getCurrentStudyPeriod() {
		case .some(let week):
			guard let end = week.userEndDate else {return ""}
			
			return end.localizedFormat(template:ACDateStyle.longWeekdayMonthDay.rawValue)
		default:
			return ""
		}
	}

}
