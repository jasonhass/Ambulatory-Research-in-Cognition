//
// ACScheduleViewController.swift
//



import Foundation
public class ACScheduleViewController : BasicSurveyViewController {
    
    public var participantId:Int?
    
	open override func templateForQuestion(id questionId:String) -> Dictionary<String, String> {
        return [:]
    }
    
    // enum values:
    // wake_time, sleep_time: Monday wake/sleep
    // schedule_3: "Do you usually wake up/go to bed..."
    // schedule_sub_1, schedule_sub_2: Tuesday
    // schedule_sub_3, schedule_sub_4: Wednesday
    // schedule_sub_5, schedule_sub_6: Thursday
    // schedule_sub_7, schedule_sub_8: Friday
    // schedule_4, schedule_5: Saturday
    // schedule_6, schedule_7: Sunday
    
	enum QuestionIndex : String, CaseIterable {
		case start, wake_time, sleep_time, sleep_confirm
		
		var day:Int? {
			switch self {
			case .wake_time, .sleep_time:
				return 1
            default:
                return nil
			}
		}
		static var wakeTimeQuestion:Array<QuestionIndex> {
			return [.wake_time,]

			
		}
		static var sleepTimeQuestion:Array<QuestionIndex> {
			return [.sleep_time,]
		
		
        }
    }

    var wakeTime:DayTime?
    var sleepTime:DayTime?
    
	struct DayTime {
		var time:String
		var day:Int
	}
    
    
	public var isChangingSchedule = false
	var error:String?
    
    public var shouldLimitWakeTime = false
    public var minWakeTime = 8
    public var maxWakeTime = 18
	public var shouldTestImmediately = true
	public var rescheduleToday = false
	private var todaysSessions:[Session] = []
    public override init(file: String, surveyId:String? = nil, showHelp:Bool? = true) {
        
        if Arc.shared.surveyController.get(surveyResponse: "availability")?.id == nil
        {
            _ = Arc.shared.surveyController.create(surveyResponse: "availability",type: SurveyType.schedule)
        }
        
        super.init(file: file, surveyId: "availability")
	
//        shouldShowHelpButton = true
		
		shouldNavigateToNextState = false
		
		questions = survey.questions
		
		for question in questions  {
            guard let v = Arc.shared.surveyController.getResponse(forQuestion: question.questionId, fromSurveyResponse: self.surveyId) else  {
				continue
			}
			guard let index = QuestionIndex(rawValue: question.questionId) else {
				continue
			}
			guard let day = index.day else {
				continue
			}
			guard let time = v.value as? String else {
				continue
			}
			
			if index == .wake_time
            {
                self.wakeTime = DayTime(time: time, day: day);
            }
            else if index == .sleep_time
            {
                self.sleepTime = DayTime(time: time, day: day);
            }
		}
		if self.isChangingSchedule {
			todaysSessions = Arc.shared.studyController.get(sessionsOnFollowingDay: Date())
		}
	}
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder);
    }
    
    public override func didPresentQuestion(input: SurveyInput?, questionId: String) {
        super.didPresentQuestion(input: input, questionId: questionId)
        guard let input = input else
        {
            return;
        }
        
		let qIndex = QuestionIndex(rawValue: questionId)!

        if qIndex == .wake_time, let wakeTime = self.wakeTime
        {
            input.setValue(AnyResponse(type: .time, value: wakeTime.time))
        }
        else if qIndex == .sleep_time, let sleepTime = self.sleepTime
        {
            input.setValue(AnyResponse(type: .time, value: sleepTime.time))
        }
	}
    
    public override func didFinishSetup() {
        
       didChangeValue()
//        setError(message:error)
    }
    
    public override func didChangeValue() {
        let questionId = self.getCurrentQuestion();
        
		guard let newValue = self.getInput()?.getValue() else
		{
			self.disableNextButton();
			return
		}
			
		isValid(value: newValue, questionId: questionId) { [weak self] valid in
			if valid {
				self?.enableNextButton();
			} else {
				self?.disableNextButton();
			}
		}
		
		
    }
	public override func isValid(value: QuestionResponse?, questionId: String, didFinish: @escaping ((Bool) -> Void)) {
     	set(error: nil)
        error = nil
        if questionId == QuestionIndex.sleep_confirm.rawValue
			|| questionId == QuestionIndex.wake_time.rawValue
			|| questionId == QuestionIndex.start.rawValue
        {
			didFinish(true)
            return;
        }
        
		guard let value:String = value?.getValue() else
		{
			didFinish(false)
			return;
		}
		
		
		guard let wakeTime = self.wakeTime else
		{
			didFinish(false)
			return;
			
		}
        
        let formatter = DateFormatter()
        formatter.defaultDate = Date();
        formatter.dateFormat = "h:mm a"
        
        if let wake = formatter.date(from: wakeTime.time),
            var sleep = formatter.date(from: value)
        {
            // If the sleep time is actually "before" the wake time (like they go to sleep at 1am and wake up at 11 am),
            // then we need to add a day to the sleep date, to make the math work right.
            // We can't just look at fabs(sleep.timeIntervalSince(wake)), because it won't take into account the change in day
            // properly. If they, for instance, set their sleep time to 1am, and their wake time to 4am, checking the absolute
            // value would only give us 3 hours, but the reality is that it's 21 hours.
            
            if wake.compare(sleep) == .orderedDescending
            {
                sleep = sleep.addingDays(days: 1);
            }
            
            if sleep.timeIntervalSince(wake) < Double(minWakeTime * 3600)
            {
                error = "Please set a minimum of {HOURS} hours of wake time.".localized(ACTranslationKey.availability_minimum_error).replacingOccurrences(of: "{HOURS}", with: "\(minWakeTime)")
				set(error: error)
				didFinish(false)
				return
            }
            
            if (sleep.timeIntervalSince(wake) > Double(maxWakeTime * 3600)) && shouldLimitWakeTime
            {
                error = "Please set a maximum of {HOURS} hours of availability.".localized(ACTranslationKey.availability_maximum_error).replacingOccurrences(of: "{HOURS}", with: "\(maxWakeTime)")
				set(error: error)

                return didFinish(false);
            }
        }
        
        return didFinish(true);
    
    }
	
	//Override this to write to other controllers
	override open func valueSelected(value:QuestionResponse, index:String) {
		super.valueSelected(value: value, index: index)
		
		let index = QuestionIndex(rawValue: index)!
        if index == .sleep_confirm
        {
            self.generateTestSessions();
            return;
        }
		if index == .start {
			return
		}
        let dayTime = DayTime(time: value.value as! String, day: 0);
        
        if index == .wake_time
        {
            self.wakeTime = dayTime;
        }
        else if index == .sleep_time
        {
            self.sleepTime = dayTime;
            self.saveAvailabilitySchedule();
        }
		
	}
    
    public override func customViewController(forQuestion question: Survey.Question) -> UIViewController?
    {
        if question.questionId == "sleep_confirm"
        {
            let vc:ScheduleEndViewController = .get();
            if isChangingSchedule {
                vc.setIsRescheduling()
            }
            return vc;
        }
        
        return nil;
        
    }
    
    open func didFinishScheduling() {
        
        
        //If we have a latest test then we shouldn't be going straight into anything.
        if Arc.shared.studyController.latestTest == nil && isChangingSchedule == false && shouldTestImmediately{
            _ = Arc.shared.startTestIfAvailable()
        }
        Arc.shared.nextAvailableState()
    }
    
    private func saveAvailabilitySchedule()
    {
        guard let wakeTime = self.wakeTime, let sleepTime = self.sleepTime else
        {
            return;
        }
		
		
        let _ = Arc.shared.scheduleController.delete(schedulesForParticipant: self.participantId!)
        
        for day in 0 ... 6 {
            let weekDay = WeekDay.init(rawValue: Int64(day))!
            let _ = Arc.shared.scheduleController.create(entry: wakeTime.time,
                                                         endTime: sleepTime.time,
                                                         weekDay: weekDay,
                                                         participantId: self.participantId!)
        }
        let _ = Arc.shared.scheduleController.get(confirmedSchedule: self.participantId!)
        
    }
    
    
    private func generateTestSessions()
    {
        
        //Probably see where the app wants to go next
        if let top = self.topViewController as? SurveyViewController {
            top.surveyView.nextButton?.showSpinner(color: UIColor(white: 1.0, alpha: 0.8), backgroundColor:UIColor(named:"Primary") )
        }
        
        self.view.showSpinner(color: nil, backgroundColor: UIColor(white: 0, alpha: 0.25));
        
        MHController.dataContext.perform {
            
            
            
            // If firstTest is set, that means we've probably recently re-installed the app, and are recreating a schedule.
            // So set beginningOfStudy to be the session_date of the first test.
            // Othwerwise, we'll just let beginningOfStudy's get handler set the date for us.
            
            if let firstTest = Arc.shared.studyController.firstTest {
                Arc.shared.studyController.beginningOfStudy = Date(timeIntervalSince1970: firstTest.session_date)
            }
            
            let date = Arc.shared.studyController.beginningOfStudy;
            
            // If we're changing a schedule, we first have to make sure to clear any existing notifications,
            // and any sessions after afterDate
            
            if self.isChangingSchedule {
                
				let studies = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID})
				//Using the sessions we grabbed before rescheduling figure out when to start scheduling
				
				
				for study in studies {
					Arc.shared.notificationController.clear(sessionNotifications: Int(study.studyID))
					
					
					
					Arc.shared.studyController.clear(sessionsAfterTodayInStudy:  Int(study.studyID), includeToday: self.rescheduleToday)
						
				}
            }
			
            // Otherwise, we need to make sure to initialize all of the study periods
            else
            {
                _ = Arc.shared.studyController.createAllStudyPeriods(startingID: 0, startDate: date)
            }
            
            var studies = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID})
            for i in 0 ..< studies.count{
                
                let study = studies[i]
                
                let sc = Arc.shared.studyController
                sc.createTestSessions(studyId: Int(study.studyID), isRescheduling: self.isChangingSchedule);
                
                
                _ = Arc.shared.studyController.mark(confirmed: Int(study.studyID))
                Arc.shared.notificationController.clear(sessionNotifications: Int(study.studyID))
            }
            
            // And now, delete any test sessions that have already passed.
            // studyController.latestTest is the most recent test that has passed, according to the server.
            
            for i in 0 ..< studies.count {
                if let latestTest = Arc.shared.studyController.latestTest, let session = Int(latestTest.session_id){
                    Arc.shared.studyController.delete(sessionsUpTo: session, inStudy: i)
                }
            }
            
            //Refetch the studies and upload
            
            studies = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID})
            
            Arc.shared.notificationController.schedule(upcomingSessionNotificationsWithLimit: 32)
            _ = Arc.shared.notificationController.scheduleDateConfirmationsForUpcomingStudy()
            Arc.shared.sessionController.uploadSchedule(studyPeriods: studies)
            
            if let study = studies.first
            {
                Arc.shared.scheduleController.upload(confirmedSchedule: Int(study.studyID));
            }
            
            MHController.dataContext.performAndWait {
                Arc.shared.notificationController.scheduleMissedTestNotification()
            }
            
            Arc.shared.studyController.save()
            
            DispatchQueue.main.async { [weak self] in
                self?.view.hideSpinner()
                if let top = self?.topViewController as? SurveyViewController {
                    top.surveyView.nextButton?.hideSpinner()
                    
                }
                self?.didFinishScheduling()
            }
        }
    }
	
}
