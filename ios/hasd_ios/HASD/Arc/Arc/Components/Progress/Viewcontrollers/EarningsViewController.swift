//
// EarningsViewController.swift
//



import UIKit
import ArcUIKit
public class EarningsViewController: CustomViewController<ACEarningsView> {
	var thisStudy:ThisStudyExpressible = Arc.shared.studyController
	var thisWeek:ThisWeekExpressible = Arc.shared.studyController

	var lastUpdated:TimeInterval?
	var earningsData:EarningOverview?
	var dateFormatter = DateFormatter()
	var goalDateFormatter = DateFormatter()

	var isPostTest:Bool = false
	var timeout:Timer?
    // TODO
    // This enum was copypasta'd from ACEarningsDetailView
    // Don't do that
    enum GoalDisplayName : String {
        case testSession = "test-session"
        case fourOfFour = "4-out-of-4"
        case twoADay = "2-a-day"
        case totalSessions =  "21-sessions"
        
        func getName() ->String {
            switch self {
                
                
            case .testSession:
                return "Completed Test Session".localized(ACTranslationKey.progress_earnings_status1)
            case .fourOfFour:
                return "Completed 4 Out of 4 Goal".localized(ACTranslationKey.progress_earnings_status3)
            case .twoADay:
                return "Completed 2-A-Day Goal".localized(ACTranslationKey.progress_earnings_status4)
            case .totalSessions:
                return "Completed 21 Sessions Goal".localized(ACTranslationKey.progress_earnings_status2)
                
            }
        }
    }
	
	public init(isPostTest:Bool) {
		super.init()
		self.isPostTest = isPostTest
	}
	
	required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
	}
	
	override public func viewDidLoad() {
		
        super.viewDidLoad()
        self.view.backgroundColor = .primaryInfo
		customView.bottomScrollIndicatorView.isHidden = true
		
		//When in post test mode perform modifications
		lastUpdated = app.appController.lastFetched[EarningsController.overviewKey]

		if isPostTest {
			timeout = Timer.scheduledTimer(withTimeInterval: 15.0, repeats: false) { [weak self] (timer) in
				
				if let lastKnownUpdate = self?.lastUpdated,
					let updated = Arc.shared.appController.lastFetched[EarningsController.overviewKey],
						 lastKnownUpdate == updated,
						fabs(updated - Date().timeIntervalSince1970) > 10 * 60{
					self?.errorState()

				
				}
				
			}
			customView.nextButton?.addTarget(self, action: #selector(self.nextPressed), for: .touchUpInside)
			
            configureForPostTest()
			
		} else {
			configureForTab()
		}
        
		dateFormatter.locale = app.appController.locale.getLocale()
		dateFormatter.dateFormat = "MMM dd 'at' hh:mm a"
		goalDateFormatter.locale = app.appController.locale.getLocale()
		goalDateFormatter.dateFormat = "MM-dd-yy"
		NotificationCenter.default.addObserver(self, selector: #selector(updateEarnings(notification:)), name: .ACEarningsUpdated, object: nil)
		
		customView.viewDetailsButton.addAction {
			[weak self] in
            guard self != nil else {
				return
			}
			self?.navigationController?.pushViewController(EarningsDetailViewController(), animated: true)
		}
        
        self.navigationController?.isNavigationBarHidden = true
		
        // Do any additional setup after loading the view.
		
		
    }
	fileprivate func errorState() {
		customView.earningsSection.isHidden = true
		customView.bonusGoalsHeader.isHidden = true
		customView.bonusGoalContent.isHidden = true
		customView.bonusGoalsSection.isHidden = true
		customView.errorLabel.isHidden = false
		customView.hideSpinner()
		customView.earningsParentStack.fadeIn()
	}
	fileprivate func configureForTab() {
//		customView.root.refreshControl = UIRefreshControl()
//		customView.root.addSubview(customView.root.refreshControl!)
//		customView.root.refreshControl?.addTarget(self, action: #selector(refresh(sender:)), for: UIControl.Event.valueChanged)
//
//		customView.root.alwaysBounceVertical = true
		
		customView.button.addTarget(self, action: #selector(self.viewFaqPressed), for: .touchUpInside)
	}
	fileprivate func configureForPostTest() {
		
		customView.earningsSection.isHidden = false
		customView.bonusGoalsHeader.isHidden = false
		customView.bonusGoalContent.isHidden = false
		customView.bonusGoalsSection.isHidden = false
		customView.errorLabel.isHidden = true
		customView.backgroundView.image = UIImage(named: "finished_bg", in: Bundle(for: self.classForCoder), compatibleWith: nil)
		customView.backgroundColor = UIColor(named: "Primary Info")
		customView.button.isHidden = true
		customView.gradientView?.isHidden = false
		customView.earningsSection.backgroundColor = .clear
		customView.headerLabel.textAlignment = .center
		customView.headerLabel.text = "".localized(ACTranslationKey.progress_earnings_header)
		
		customView.viewDetailsButton.isHidden = true
		customView.separator.isHidden = true
		customView.earningsBodyLabel.isHidden = true
		customView.lastSyncedLabel.isHidden = true
		
		customView.bonusGoalsSection.backgroundColor = .clear
		customView.bonusGoalsHeader.textAlignment = .center
		customView.bonusGoalsSeparator.isHidden = true
		customView.bonusGoalsHeader.textColor = .white
		customView.bonusGoalsBodyLabel.textColor = .white
		
		customView.bonusGoalsBodyLabel.textAlignment = .center
		Roboto.Style.body(customView.bonusGoalsBodyLabel, color:.white)
		
		customView.earningsParentStack.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 88, right: 0)
		
		
	}
    @objc func nextPressed() {
		if thisStudy.studyState == .inactive {
			Arc.shared.appNavigation.navigate(vc: ACPostCycleFinishViewController(), direction: .toRight)
		} else {
			Arc.shared.nextAvailableState()

		}
    }
    
    @objc func viewFaqPressed() {
        let vc:FAQViewController = .get()
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
	@objc func refresh(sender:AnyObject)
	{
		
		//my refresh code here..
		print("refreshing")
		NotificationCenter.default.post(name: .ACStartEarningsRefresh, object: nil)
	}
	public override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
		self.navigationController?.isNavigationBarHidden = true
		
		if isPostTest && fabs((lastUpdated ?? Date().timeIntervalSince1970) - Date().timeIntervalSince1970) > 10 * 60 {
            self.navigationController?.isNavigationBarHidden = false
			customView.showSpinner(color: ACColor.highlight, backgroundColor: ACColor.primaryInfo, message:"progress_endoftest_syncing")
			customView.earningsParentStack.alpha = 0
			

		}
		
	}
	public override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
//		if isPostTest && !HMRestAPI.shared.isWaitingForTask(named: ["earning-details", "earning-overview"]){
//			customView.hideSpinner()
//			customView.earningsParentStack.fadeIn()
//
//		}
		lastUpdated = app.appController.lastFetched[EarningsController.overviewKey]
		earningsData = Arc.shared.appController.read(key: EarningsController.overviewKey)
		setGoals()
	}
    public override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
	@objc public func updateEarnings(notification:Notification) {
		OperationQueue.main.addOperation { [weak self] in

			guard let weakSelf = self else {
				return
			}
			weakSelf.timeout?.invalidate()
			weakSelf.customView.earningsParentStack.fadeIn()

			
			

			weakSelf.lastUpdated = weakSelf.app.appController.lastFetched[EarningsController.overviewKey]
			weakSelf.earningsData = Arc.shared.appController.read(key: EarningsController.overviewKey)
			weakSelf.setGoals()
			weakSelf.customView.hideSpinner()
			weakSelf.customView.root.refreshControl?.endRefreshing()
			
		}
		
	}
	
	fileprivate func updateBodyText() {
		if let last = lastUpdated {
			let date = Date(timeIntervalSince1970: last)
			
			var time = ""
			if #available(iOS 13.0, *) {
				//TODO: Update for iOS 13, right now, this is commented out because
				//it does not build in xcode 10.
				let dateFormatter:RelativeDateTimeFormatter = RelativeDateTimeFormatter()
				dateFormatter.locale = Arc.shared.appController.locale.getLocale()

				time = dateFormatter.localizedString(for: date, relativeTo:  Date())
			} else {
				// Fallback on earlier versions
				
				time = dateFormatter.string(from: date)
			}
			
			if date.addingMinutes(minutes: 1).minutes(from: Date()) < 1 {
				customView.lastSyncedLabel.text = "\("".localized(ACTranslationKey.earnings_sync)) \(time)"
			}
		}
		customView.bonusGoalsBodyLabel.text = "".localized(ACTranslationKey.earnings_bonus_body)
		
		switch thisStudy.studyState {
		case .baseline:
			customView.earningsBodyLabel.text = "".localized(ACTranslationKey.earnings_body0)
			
			break
		default:
			customView.earningsBodyLabel.text = "".localized(ACTranslationKey.earnings_body1)
			
			
			break
		}
	}
	fileprivate func setGoalRewardText(value:String, goalView:GoalView, isComplete:Bool, date:TimeInterval?) {
		
		if let dateInterval = date {
			let timeSince = (Date().timeIntervalSince1970 - dateInterval)
			if timeSince > 60.0 * 60.0 * 24.0 {
				let dateString = goalDateFormatter.string(from: Date(timeIntervalSince1970: dateInterval))
				goalView.set(isUnlocked: isComplete, date: dateString)
			} else {
				goalView.set(isUnlocked: isComplete, date: nil)

			}
		} else {
			goalView.set(isUnlocked: isComplete, date: nil)

		}
			goalView.set(
				goalRewardText: ""
					.localized((isComplete) ? ACTranslationKey.earnings_bonus_complete : ACTranslationKey.earnings_bonus_incomplete)
					.replacingOccurrences(of: "{AMOUNT}", with: value))
	}
	
	fileprivate func fourofFourGoal(_ fourOfFourGoal:EarningOverview.Response.Earnings.Goal) {
		let components = fourOfFourGoal.progress_components
		
		for component in components.enumerated() {
			let index = component.offset
			let value = component.element
			customView.fourofFourGoal.set(progress:Double(value)/100.0, for: index)
		}
		
		customView.fourofFourGoal.set(
			titleText: ""
				.localized(ACTranslationKey.earnings_4of4_header))

		customView.fourofFourGoal.set(bodyText: "".localized(ACTranslationKey.earnings_4of4_body)
			.replacingOccurrences(of: "{AMOUNT}", with: fourOfFourGoal.value))
        
		

		setGoalRewardText(value: fourOfFourGoal.value, goalView: customView.fourofFourGoal, isComplete: fourOfFourGoal.completed, date: fourOfFourGoal.completed_on)
	}
	
	fileprivate func twoADayGoal(_ twoADay:EarningOverview.Response.Earnings.Goal) {
		let components = twoADay.progress_components.suffix(7)
		for component in components.enumerated() {
			let index = component.offset
			let value = component.element
			customView.twoADayGoal.set(progress:Double(min(2, value))/2.0, forIndex: index)
		}
		customView.twoADayGoal.set(bodyText: "".localized(ACTranslationKey.earnings_2aday_body)
			.replacingOccurrences(of: "{AMOUNT}", with: twoADay.value))
       

			setGoalRewardText(value: twoADay.value, goalView: customView.twoADayGoal, isComplete: twoADay.completed, date: twoADay.completed_on)
	}
	
	fileprivate func totalSessionsGoal(_ totalSessions:EarningOverview.Response.Earnings.Goal) {
		for component in totalSessions.progress_components.enumerated() {
			let value = component.element
			customView.totalSessionsGoal.set(total: 21.0)
			customView.totalSessionsGoal.set(current: value)
			
		}
		
		customView.totalSessionsGoal.set(bodyText: "".localized(ACTranslationKey.earnings_21tests_body)
			.replacingOccurrences(of: "{AMOUNT}", with: totalSessions.value))
       

			setGoalRewardText(value: totalSessions.value, goalView: customView.totalSessionsGoal, isComplete: totalSessions.completed, date: totalSessions.completed_on)
	}
	
	public func setGoals() {
		
		
		updateBodyText()
		
		customView.twoADayGoal.add(tiles: thisWeek.daysArray.suffix(7))
		guard let earnings = earningsData?.response?.earnings else {
			return
		}
		if isPostTest {
			customView.clearRewards()
			for a in earnings.new_achievements {
                let name = GoalDisplayName(rawValue: a.name)?.getName() ?? "Goal Name"
				customView.add(reward: (name, a.amount_earned))
			}
		}
		
		customView.thisWeeksEarningsLabel.text = earnings.total_earnings
		customView.thisStudysEarningsLabel.text = earnings.cycle_earnings
		
		for goal in earnings.goals {
			switch goal.name {
			case "4-out-of-4":
				fourofFourGoal(goal)
			case "2-a-day":
				twoADayGoal(goal)
			case "21-sessions":
				totalSessionsGoal(goal)
			default:
				break
			}
		}
	}
	
	
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

