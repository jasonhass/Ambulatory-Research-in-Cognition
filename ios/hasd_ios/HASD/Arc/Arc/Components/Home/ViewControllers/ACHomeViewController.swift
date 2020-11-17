//
// ACHomeViewController.swift
//



import UIKit
import ArcUIKit
open class ACHomeViewController: CustomViewController<ACHomeView> {
    @IBOutlet weak var heading: UILabel!
    @IBOutlet weak var message: UILabel!
    
    @IBOutlet weak var debugButton: UIButton!
    
    @IBOutlet weak var versionLabel: UILabel!
    
    var session:Int?
    var study:Int?
    
    var thisWeek:ThisWeekExpressible = Arc.shared.studyController
	var thisStudy:ThisStudyExpressible = Arc.shared.studyController

    override open func viewDidLoad() {
        super.viewDidLoad()
        //        versionLabel.text = "v\(Arc.shared.versionString)"
        //        configureState()
		app.notificationController.authenticateNotifications { (value, error) in
			HMLog("Backup handler called.")
		}
//        let _ = Arc.shared.appNavigation.viewForState(state: Arc.shared.appNavigation.defaultState())
    }
    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        configureState()
		app.notificationController.isAuthorized { [weak self] in
			if $0 {
				self?.customView.enableNotificationsButton.isHidden = true
			} else {
				self?.customView.enableNotificationsButton.isHidden = false
			}
			self?.customView.notificationsOff = !$0
			self?.configureState()

		}
        if app.participantId != nil {
            Arc.shared.uploadTestData()
        }
    }
    
    open override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
		//If this button is hidden then we've already seen or missed the first test at least.
		if customView.surveyButton.isHidden {
            set(flag: .first_test_begin)

		}
        if !get(flag: .first_test_begin) {
            currentHint = customView.highlightTutorialTargets()
            set(flag: .first_test_begin)
            
        }
		
        if get(flag: .baseline_completed) && !get(flag: .baseline_onboarding) {
            if  thisStudy.studyState == .baseline {
                showBaselineOboarding()
            }
            else if get(flag: .paid_test_completed) {
                showPaidOnboarding()
            }
        }
    }

    open override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        currentHint?.removeFromSuperview()

    }
    
    // Shown after the baseline test
    func showBaselineOboarding() {
        // view.window?.overlayView(withShapes: [])
        // view.isUserInteractionEnabled = false
        currentHint = view.window?.hint {
            $0.layout {
                $0.centerX == view.centerXAnchor
                $0.centerY == view.bottomAnchor - 60
                $0.width == 232
                $0.height == 73
            }
            
            $0.content = nil
            $0.buttonTitle = "".localized(ACTranslationKey.popup_tour)
            $0.hideBar()
            $0.onTap = {[unowned self] in
                self.set(flag: .baseline_onboarding)
                self.currentHint?.removeFromSuperview()
                self.view.isUserInteractionEnabled = true
                
                NotificationCenter.default.post(name: .ACHomeStartOnboarding, object: nil)
            }
        }
    }
    
    // Shown after paid tests if the user has not previously completed the onboarding flow
    func showPaidOnboarding() {
        // view.window?.overlayView(withShapes: [])
        // view.isUserInteractionEnabled = false
        currentHint = view.window?.hint {
            $0.layout {
                $0.centerX == view.centerXAnchor
                $0.centerY == view.bottomAnchor - 100
                $0.width == 232
                //$0.height == 73
            }
            $0.content = "".localized(ACTranslationKey.popup_nicejob)
            $0.buttonTitle = "".localized(ACTranslationKey.popup_next)
            $0.onTap = {[unowned self] in
                self.set(flag: .baseline_onboarding)
                self.currentHint?.removeFromSuperview()
                self.view.isUserInteractionEnabled = true
                
                NotificationCenter.default.post(name: .ACHomeStartOnboarding, object: nil)
            }
        }
    }
    
    func configureState() {
        
        var upcoming:Session?
        // var completedStudy:Bool = false
        if let study = app.currentStudy {
            
            upcoming = app.studyController.get(upcomingSessions: Int(study)).first
            // completedStudy = (app.studyController.get(upcomingSessions: Int(study)).count == 0) && (app.studyController.getUpcomingStudyPeriod() == nil)
            self.study = study
            
            
            
            if let session = app.availableTestSession {
                self.session = session
            }
        }
        if upcoming == nil, let nextCycle = app.studyController.getUpcomingStudyPeriod(){
			
			let sessions = nextCycle.sessions ?? []
			if sessions.count > 0 {
				upcoming = sessions.firstObject as? Session
			}
			
        }
        
        let surveyStatus = Arc.shared.getSurveyStatus()
        customView.setState(surveyStatus: surveyStatus)
        customView.surveyButton.addTarget(self, action: #selector(beginPressed(_:)), for: .primaryActionTriggered)
        customView.debugButton.addTarget(self, action: #selector(debug(_:)), for: .primaryActionTriggered)
        
    }
    @objc func beginPressed(_ sender: Any) {
        currentHint?.removeFromSuperview()
        configureState()
        
        guard let session = session else {
            return
        }
        if let study = study {
            
            app.currentTestSession = session
            app.availableTestSession = nil
            app.appController.lastFlaggedMissedTestCount = 0
            
            app.studyController.mark(started: session, studyId: study)
            app.notificationController.clearNotifications(sessionId: session,
                                                          studyId: study)
            app.notificationController.clearNotifications(withIdentifierPrefix: "MissedTests-\(study)")
            app.appController.lastClosedDate = nil;
            
            app.nextAvailableState()
        } else {
            
        }
    }
    
    @IBAction func debug(_ sender: Any) {
        currentHint?.removeFromSuperview()
        
    }
    
}
