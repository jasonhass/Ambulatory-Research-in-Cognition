//
// StartDateShiftViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit


open class StartDateShiftViewController: BasicSurveyViewController {
    enum QuestionId : String {
        case user_schedule_1, user_schedule_2
    }
    
    var selectedDate:Int = 7
    var dates:[Date] = []
    let longFormat = ACDateStyle.longWeekdayMonthDay.rawValue
    let mediumFormat = ACDateStyle.mediumWeekDayMonthDay.rawValue
    let upComingStudy = Arc.shared.studyController.getUpcomingStudyPeriod()

    
    open override func viewDidLoad() {
        super.viewDidLoad()
        
//        guard let upcoming = upComingStudy, let userDate = upcoming.userStartDate else {
//            return
//        }
//        print(userDate.localizedString())
//        let date = upComingStudy?.startDate ?? Date()
//        for i in -7 ... 7 {
//            let d = date.startOfDay().addingDays(days: i)
//            guard Date().endOfDay().timeIntervalSince1970 < d.timeIntervalSince1970 else {
//                continue
//            }
//
//            dates.append(d)
//        }
//        for i in 0 ..< dates.count {
//            let possibleDate = dates[i]
//            if possibleDate.compare(userDate) == .orderedSame{
//                selectedDate = i
//
//            }
//        }
		
		
		
    }
	open override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
		setBackButton()

	}
	func setBackButton() {
		let title = "BACK".localized(ACTranslationKey.button_back)
		let selector = #selector(StartDateShiftViewController.cancelPressed)
		let b = UIBarButtonItem.primaryBackButton(title: title, target:self, selector: selector)
		
		if let vc = topViewController {
			vc.navigationItem.leftBarButtonItem = b
		}
	}
	@objc func cancelPressed()
	{
		Arc.shared.nextAvailableState(runPeriodicBackgroundTask: false, direction: .toLeft)
	}
	open override func templateForQuestion(id questionId: String) -> Dictionary<String, String> {
		let _ = super.templateForQuestion(id: questionId)
        guard let index = QuestionId(rawValue: questionId) else {return [:]}
		
		if index == .user_schedule_2 {
            return ["start":  dates[selectedDate].localizedFormat(template:longFormat),
                    "DATE1":  dates[selectedDate].localizedFormat(template:longFormat),
                    "end":  dates[selectedDate].addingDays(days: 6).localizedFormat(template:longFormat),
                    "DATE2":  dates[selectedDate].addingDays(days: 6).localizedFormat(template:longFormat)]
        }
        return [:]
    }
    
    override open func didPresentQuestion(input: SurveyInput?, questionId: String) {
        super.didPresentQuestion(input: input, questionId: questionId)
        guard let index = QuestionId(rawValue: questionId) else {return}
        enableNextButton()
        switch index {
        case .user_schedule_1:
			
            guard let picker = input as? ACPickerView else {
                fatalError("Wrong input type, needs ACPickerView")
            }
            
            let question = Arc.shared.surveyController.get(question: questionId)
            enableNextButton(title: question.nextButtonTitle ?? "".localized(ACTranslationKey.button_next))
            
            loadDates()
            
            picker.set(dates.map({ (dateItem) -> String in
                //TODO: Refactor for reusability as needed
                return "\(dateItem.localizedFormat(template:mediumFormat)) - \(dateItem.addingDays(days: 6).localizedFormat(template:mediumFormat))"
            }))
            picker.setValue(AnyResponse(type: .picker, value: selectedDate))
            break
        case .user_schedule_2:
            let start = dates[selectedDate].startOfDay().addingDays(days: -3)
            let end = dates[selectedDate].startOfDay().addingDays(days: 10)

            let selectedStart = dates[selectedDate]
            let selectedEnd = selectedStart.startOfDay().addingDays(days: 6)
            
            var store = ACCalendarStore(range: start ... end)
            store.selectedDateRange = (selectedStart ... selectedEnd)
            
            input?.setValue(AnyResponse(type: .calendar, value: store))
			enableNextButton(title: "Done".localized(ACTranslationKey.button_done))
            
            let vc:CustomViewController<InfoView> = getTopViewController()!
            let message = "".localized(ACTranslationKey.availability_change_week_confirm)
                .replacingOccurrences(of: "{DATE1}", with: selectedStart.localizedFormat(template:longFormat))
                .replacingOccurrences(of: "{DATE2}", with: selectedEnd.localizedFormat(template:longFormat))
            vc.customView.setHeading(message)
            
            break
        
        }
    }
        
    override open func valueSelected(value: QuestionResponse, index: String) {
        //super.onValueSelected(value: value, index: index)
        guard let index = QuestionId(rawValue: index) else {return}

        
        switch index {
        case .user_schedule_1:
            guard let selectedIndex = value.value as? Int else {fatalError("Expected Int")}
            selectedDate = selectedIndex
            break
        case .user_schedule_2:
            
            guard let study = upComingStudy else {
                return
            }
            MHController.dataContext.performAndWait {

                study.userStartDate = dates[selectedDate]
                let new = Arc.shared.studyController.set(userStartDate: dates[selectedDate], forStudyId: Int(study.studyID))
                print(new?.userStartDate?.localizedString() as Any)
                
               let id = Int(study.studyID)
                
                Arc.shared.studyController.clear(upcomingSessions: id)
                Arc.shared.studyController.createTestSessions(studyId: id, isRescheduling: true)
                _ = Arc.shared.studyController.mark(confirmed: id)
                Arc.shared.notificationController.clear(sessionNotifications: id)
                Arc.shared.notificationController.schedule(upcomingSessionNotificationsWithLimit: 32)
                _ = Arc.shared.notificationController.scheduleDateConfirmationsForUpcomingStudy(force: true)

                Arc.shared.scheduleController.upload(confirmedSchedule: id);

                Arc.shared.studyController.save()
                Arc.shared.nextAvailableState()
            }
            break
            
        }
    }
    
    fileprivate func loadDates() {
        guard let upcoming = upComingStudy, let userDate = upcoming.userStartDate else {
            return
        }
        
        dates = []
        
        print(userDate.localizedString())
        let date = upComingStudy?.startDate ?? Date()
        for i in -7 ... 7 {
            let d = date.startOfDay().addingDays(days: i)
            guard Date().endOfDay().timeIntervalSince1970 < d.timeIntervalSince1970 else {
                continue
            }
            
            dates.append(d)
        }
        for i in 0 ..< dates.count {
            let possibleDate = dates[i]
            if possibleDate.compare(userDate) == .orderedSame{
                selectedDate = i
                
            }
        }
    }
    
}
