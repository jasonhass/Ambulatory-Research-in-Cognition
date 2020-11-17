//
// EXAppNavigationController.swift
//




import UIKit
import Arc
public class EXAppNavigationController : AppNavigationController {
	
	
    let app = Arc.shared
    
	public func defaultHelpState() -> UIViewController {
		let contact = EXState.contact.viewForState() as! EXContactNavigationController
		
		contact.prev = UIApplication.shared.keyWindow?.rootViewController
		
		return contact
		
	}
    
    public func defaultAuth() -> State {
        return EXState.auth
    }
    public func defaultAbout() -> State {
        return EXState.about
    }
    public func defaultContact() -> State {
        return EXState.contact
    }
    public func defaultRescheduleAvailability() -> State {
        return EXState.rescheduleAvailability
    }
    public func defaultPrivacy() {
        UIApplication.shared.open(URL(string: Arc.shared.APP_PRIVACY_POLICY_URL)!,
                                  options: [:],
                                  completionHandler: nil)
//        Arc.shared.displayAlert(message: "The Privacy Policy will open in your web browser",
//                                options: [.default("OK".localized(ACTranslationKey.okay), {
//                                    
//                                }), .cancel("Not Now", { })])
    }
	public func defaultState() -> State {
		return EXState.home
	}
	public func previousState() -> State? {
		return _previousState
	}


	var didShowTestIntro = false
	var didEndSession = false
	var showedProgress = false
	var showedCycleProgress = false
	var state:State = EXState.home
	private var _previousState:State?
	public func viewForState(state:State) -> UIViewController {

		self._previousState = self.state
		self.state = state
		let vc = state.viewForState()


		return vc
	}

	public func navigate(state:State, direction: UIWindow.TransitionOptions.Direction = .toRight) {
		var duration = 0.5
		if let tkState = state as? EXState, EXState.tests.contains(tkState) {
			duration = 0
		}
		Arc.currentState = state
		navigate(vc: viewForState(state: state), direction: direction, duration: duration)
	}
	public func navigate(vc:UIViewController, direction: UIWindow.TransitionOptions.Direction = .toRight, duration:Double = 0.5) {
		guard let window = UIApplication.shared.keyWindow else {
			assertionFailure("No Keywindow")

			return
		}

		guard let oldView = window.rootViewController else {
			window.rootViewController = vc
			window.makeKeyAndVisible()
			return
		}
		var options = UIWindow.TransitionOptions(direction: direction, style: .easeInOut)
		options.duration = duration
		if duration == 0 {
			options.style = .linear
		}
		let view = UIView()
        var color = window.getBackgroundColor();
        view.backgroundColor = color;
        
        if let nav = oldView as? UINavigationController {
            view.backgroundColor = nav.topViewController?.view.backgroundColor ?? .white

        }
		options.background = UIWindow.TransitionOptions.Background.customView(view)
     
        // We need to check if there are any presented view controllers, and close them first before we remove olView.
        
        if let presentedView = oldView.presentedViewController
        {
            presentedView.dismiss(animated: false) {
                if options.duration == 0
                {
                    window.makeKeyAndVisible()
                    window.rootViewController = vc
                }
                else
                {
                    window.setRootViewController(vc, options:options)
                }
            }
        }
        else
        {
            if options.duration == 0
            {
                window.makeKeyAndVisible()
                window.rootViewController = vc
            }
            else
            {
                window.setRootViewController(vc, options:options)
            }
        }
        
        
		
	}
	public func navigate(vc: UIViewController, direction: UIWindow.TransitionOptions.Direction) {
		var duration = 0.5
		if vc is GridTestViewController || vc is PricesTestViewController || vc is SymbolsTestViewController {
			duration = 0
		}
		navigate(vc: vc, direction: direction, duration: duration)
	}

	public func nextAvailableState(runPeriodicBackgroundTask:Bool = false) -> State {
		let app = Arc.shared
        //Check region
        if app.appController.language == nil {
            return EXState.language
        }
		
		if let migrationInProgress = Arc.shared.appController.flags["migration_in_progress"] {
			if migrationInProgress {
				return EXState.migration
			} else {
				
				if Arc.shared.appController.flags["migrated_legacy"] == true {
					Arc.shared.appController.flags["migration_in_progress"] = nil
					//Check to see if we have a schedule set
					return EXState.changeSchedule
				}
			}
			
			if Arc.shared.appController.flags["migrated_legacy"] == false {
				return EXState.migrationFailed
			}
			
			
		}

		//Check for participant setup
		if app.participantId == nil {

			//If none set up go to auth
			guard let id = app.authController.checkAuth() else {
				return EXState.welcome
			}

			//set the id we can skip past this once set
			app.participantId = Int(id)
		}
        if app.appController.commitment == nil {
            return EXState.onboarding
        }
		if app.appController.commitment == .rebuked {
			return EXState.rebukedCommitment
		}
		
//		if !app.appController.isNotificationAuthorized {
//			app.appController.isNotificationAuthorized = true
//			return EXState.onboardingNotifications
//		}
		
		//2: Currently in a Test Session.
		// If the application is currently testing, and the test has not timed out,
		// just let them continue the test.



		if let study = app.currentStudy, let session = app.currentTestSession
		{
			app.availableTestSession = nil
			if let lastClosed = Arc.shared.appController.lastClosedDate
			{
				Arc.shared.appController.lastClosedDate = nil;
                // if last closed is greater than 5 minutes
				if lastClosed.timeIntervalSinceNow < (-1 * Arc.shared.studyController.TEST_TIMEOUT)
				{
					Arc.shared.currentTestSession = nil;

                    if !app.studyController.get(finished: session, studyId: study) {
                        Arc.shared.studyController.mark(missed:session, studyId: study)
                    }
					
				}
			}
		}
        
        if runPeriodicBackgroundTask
        {
            if app.participantId != nil {
                Arc.shared.periodicBackgroundTask(timeout: 20) {
                }
            }
        }
        
		setTestAvailability()

		//Check to see if we have a schedule set
		let hasSchedule = (app.scheduleController.get(confirmedSchedule: app.participantId!) != nil)
        
		//Send the user to the scheduler
		if !hasSchedule {
			return EXState.schedule
		}

		
		if let surveyState = nextAvailableSurveyState() {

			return surveyState
		}
		
		
		showedProgress = false

		return EXState.home
	}
	func setTestAvailability() {
		let app = Arc.shared
		if let study = app.studyController.getCurrentStudyPeriod() {

			app.currentStudy = Int(study.studyID)

			//If we have a current session store it here.
			if let id = app.studyController.get(availableTestSession: Int(study.studyID))?.sessionID {
				app.availableTestSession = Int(id)
			} else {
				app.availableTestSession = nil
			}

		} else {
			app.currentStudy = nil

			app.availableTestSession = nil
		}
	}
	public func nextAvailableSurveyState() -> State? {
		let app = Arc.shared

	
		

		//If no test is running then no survey is available.
		guard let session = app.currentTestSession else {
			return nil
		}
		guard let study = Arc.shared.studyController.get(session: session)?.study else {
			return nil
		}
        app.currentStudy = Int(study.studyID)
		//If we are running a test figure out what we need to do for it.
		guard let step = stepForActiveTest(test: session, study: Int(study.studyID)) else {

			return nil
		}
		return step

	}

	func stepForActiveTest(test:Int, study:Int) -> State?{
		//Check the session for prefilled data
		let test = Arc.shared.studyController.get(session: test, inStudy: study)
		//Get all surveys first.
		let week = Int(test.week)
		let day = Int(test.day)
		let session = Int(test.session)
		let phase = EXPhase.from(studyId: study)

        //This is an array of all the items that should be checked for in proper order
        var itemsToCheckFor:Array<EXState> = phase.statesForSession(week: week, day: day, session: session) as! Array<EXState>
        
        MHController.dataContext.performAndWait {
            if !test.isAvailableForState(state: EXState.wake) && shouldTakeWakeSurvey(study, inWeek: week, onDay: day) {
                test.createSurveyFor(surveyType: .wake)
                itemsToCheckFor.insert(EXState.wake, at: 0)
            }
            if test.isAvailableForState(state: EXState.wake) && !itemsToCheckFor.contains(.wake){
                itemsToCheckFor.insert(EXState.wake, at: 0)
                
            }
            if !test.isAvailableForState(state: EXState.chronotype) && shouldTakeChronotypeSurvey(study) {
                test.createSurveyFor(surveyType: .chronotype)
                itemsToCheckFor.insert(EXState.chronotype, at: 0)
            }
            if test.isAvailableForState(state: EXState.chronotype) && !itemsToCheckFor.contains(.chronotype){
                itemsToCheckFor.insert(EXState.chronotype, at: 0)
                
            }
        }
        if app.appController.fetch(signature: test.sessionID, tag: 0) == nil {
            return EXState.signatureStart
        }
		for item in itemsToCheckFor {


			if test.isAvailableForState(state: item) {
				
			
				
				if EXState.tests.contains(item) {
					//TODO: Refactor, this is only here for tests introductions.
					if !didShowTestIntro {
						didShowTestIntro = true
						didEndSession = false

						return EXState.testIntro
					}
				}

				return item
			}
		}
        
        app.studyController.mark(finished: app.currentTestSession!, studyId: app.currentStudy!)
        
		if !didEndSession {
			didEndSession = true
			didShowTestIntro = false
            
			return applicableThankYou()
		}
		
		

		if !showedProgress {
			showedProgress = true

			return EXState.todayProgress
		}

		return nil
	}
    
    func shouldTakeWakeSurvey(_ studyId:Int, inWeek week: Int, onDay day: Int) -> Bool {
        return Arc.shared.studyController.get(numberOfTestTakenOfType: .wake,
                                              inStudy: studyId,
                                              week:week,
                                              day:day) == 0
        
    }
    func shouldTakeChronotypeSurvey(_ studyId:Int) -> Bool {
        return Arc.shared.studyController.get(numberOfTestTakenOfType: .chronotype, inStudy: studyId) == 0
    }
    
    func applicableThankYou() -> EXState {
        
        var upcoming:Session?
        var session:Int?
        if let study = app.currentStudy {
            
            upcoming = app.studyController.get(upcomingSessions: Int(study)).first
            if let s = app.availableTestSession {
                session = s
            }
        }
        if upcoming == nil, let nextCycle = app.studyController.getUpcomingStudyPeriod()?.sessions?.firstObject as? Session {
            upcoming = nextCycle
        }
        // Do any additional setup after loading the view.
        if let _ = session {
            return EXState.thankYouToday
        } else {
            if let upcoming = upcoming {
                let date = upcoming.sessionDate ?? Date()
                if date.isToday() {
                    return EXState.thankYouToday
                } else if date.isTomorrow() {
                    return EXState.thankYouTomorrow
                } else {
                    return EXState.thankYouCycle
                }
            } else {
                return EXState.thankYouComplete
            }
        }
    }
}
