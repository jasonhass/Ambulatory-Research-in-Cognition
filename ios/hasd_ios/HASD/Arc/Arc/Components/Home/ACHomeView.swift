//
// ACHomeView.swift
//



import UIKit
import ArcUIKit
import HMMarkup
public protocol ACHomeViewDelegate : class{
	func beginPressed()
}
public class ACHomeView: ACTemplateView {
	public weak var delegate:ACHomeViewDelegate?
	public var notificationsOff:Bool = false
	public var heading:String? {
		get {
			return headingLabel.text
		}
		set {
			headingLabel.text = newValue

		}
	}
	public var message:String? {
		get {
			return messageLabel.text
		}
		set {
			messageLabel.text = newValue
			

		}
	}
	
	public var version:String? {
		get {
			return versionLabel.text
		}
		set {
			versionLabel.text = newValue
			
		}
	}
	
	var headingLabel: UILabel!
	var messageLabel: UILabel!
	
	public var debugButton: UIButton!
	public var surveyButton: UIButton!
    public var changeAvailabilityButton: UIButton!
    public var enableNotificationsButton: UIButton!
	
	var relSeparatorWidth:CGFloat = 0.15
	var versionLabel: UILabel!
	public var separator:ACHorizontalBar!
	public var tutorialTarget:UIView?
//	var tutorialAnimation:Animate = Animate().
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
	public override init() {
		super.init()
        debugButton.isHidden = (Arc.environment?.isDebug ?? false) == false
		separator.setNeedsDisplay()
	}
	public override func didMoveToWindow() {
		separator.relativeWidth = 0.15
		

	}
	public func highlightTutorialTargets() -> HintView? {
		
		tutorialTarget?.overlay()
		return window?.hint { [unowned self] in
			$0.content = "".localized(ACTranslationKey.popup_begin)
            $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                    secondaryColor: UIColor(named:"HintFill")!,
                                                    textColor: .black,
                                                    cornerRadius: 8.0,
                                                    arrowEnabled: true,
                                                    arrowAbove: true))
            $0.updateHintContainerMargins()
            $0.updateTitleStackMargins()
			$0.layout {
				$0.centerX == self.tutorialTarget!.centerXAnchor
				$0.top == self.tutorialTarget!.bottomAnchor + 2
				

			}
			$0.targetView = self.tutorialTarget
		}
	}
	
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
	}
	override public func content(_ view: UIView) {
		let relWidth = relSeparatorWidth
		
			self.tutorialTarget = view.stack { [weak self] in
				$0.spacing = 20
				$0.axis = .vertical
				$0.distribution = .fill
				self?.headingLabel = $0.acLabel {
					Roboto.Style.headingMedium($0)
				}
				self?.separator = $0.acHorizontalBar {
					$0.relativeWidth = relWidth
					$0.color = UIColor(named: "HorizontalSeparator")
					$0.layout {
						$0.height == 2 ~ 999
                        }
				}
				self?.messageLabel = $0.acLabel {
					Roboto.Style.body($0)
				}
				self?.surveyButton = $0.acButton {
					$0.setTitle("BEGIN".localized(ACTranslationKey.button_begin), for: .normal)
					$0.addAction {
						[weak self] in
                        MHController.dataContext.performAndWait {
                            Arc.shared.notificationController.scheduleMissedTestNotification()
                        }
						self?.delegate?.beginPressed()
						self?.window?.removeHighlight()
						self?.window?.clearOverlay()
					}
				}
                
			}
			
			
			self.debugButton = view.acButton {
				$0.setTitle("DEBUG", for: .normal)
				$0.addAction { [weak self] in
					Arc.shared.debugSchedule()
					self?.window?.removeHighlight()
					self?.window?.clearOverlay()
				}

			}
			view.view {
				$0.setContentHuggingPriority(.defaultLow, for: .vertical)
			}
		
			
			
	
	}
    
    override open func footer(_ view: UIView) {
        let attributes:[NSAttributedString.Key:Any] = [
            .foregroundColor : UIColor(named: "Primary") as Any,
            .font : UIFont(name: "Roboto-Bold", size: 16.0) as Any,
            .underlineStyle: NSUnderlineStyle.single.rawValue
        ]
		let notificationTitle = "Turn On Notifications".localized(ACTranslationKey.onboarding_notifications_header2)
		.replacingOccurrences(of: ".", with: "")
        let changeAvailabilityTitle = NSAttributedString(string: "Change Availability".localized(ACTranslationKey.resources_availability), attributes: attributes)
        let enableNotificationsTitle = NSAttributedString(string: notificationTitle, attributes: attributes)
		self.enableNotificationsButton = view.button {
			$0.isHidden = true
			$0.titleLabel?.numberOfLines = 0
			$0.titleLabel?.textAlignment = .center

            $0.setAttributedTitle(enableNotificationsTitle, for: .normal)
			
            $0.setTitleColor(UIColor(named: "Primary"), for: .normal)
            
            $0.addAction {
				let message = "We’ll now open your Settings App. Once there, tap Notifications, and turn on the Allow Notifications Switch.".localized("popup_opensettings")
				Arc.shared.displayAlert(message: message,
					options: [.default("OK".localized(ACTranslationKey.button_okay), {
						guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
							return
						}
						
						if UIApplication.shared.canOpenURL(settingsUrl) {
							UIApplication.shared.open(settingsUrl, completionHandler: { (success) in
								print("Settings opened: \(success)") // Prints true
							})
						}
					}),
							  
							  .cancel("Not Now".localized("button_notnow"), {})
					]
				)
            }
        }
		
        self.changeAvailabilityButton = view.button {
            $0.setAttributedTitle(changeAvailabilityTitle, for: .normal)
			$0.setTitle("Change Availability".localized(ACTranslationKey.resources_availability),
                        for: .normal)
            $0.setTitleColor(UIColor(named: "Primary"), for: .normal)
            
            $0.addAction {
                Arc.shared.appNavigation.navigate(state: ACState.changeStudyStart, direction: .toRight)
            }
        }
    }
    
	public func setState(surveyStatus:SurveyAvailabilityStatus) {
		surveyButton.isHidden = true
		changeAvailabilityButton.isHidden = true
		// Do any additional setup after loading the view.
		switch surveyStatus {
		case .available:
			//heading = "Hello!".localized(ACTranslationKey.home_header)
			//message = "You have a new test available.".localized(ACTranslationKey.home_body)
            heading = "You have a new test available.".localized(ACTranslationKey.home_header1)
            separator.isHidden = true
			surveyButton.isHidden = false
			
		case .laterToday:
			heading = "There are no tests to take right now.".localized(ACTranslationKey.home_header2)
//			if notificationsOff {
//				message = "*Please turn on notifications to receive a message later today* when it’s time to take your next test."
//			} else {
				message = "You will receive a notification later today when it's time to take your next test.".localized(ACTranslationKey.home_body2)
//			}
			
			
		case .laterThisCycle(let date):
			heading = "There are no tests available right now.".localized(ACTranslationKey.home_header4)
			
			message = "Your next tests will be available starting *{DATE1}* .".localized(ACTranslationKey.home_body4)
				.replacingOccurrences(of: "{DATE1}", with: date)
			
	
		case .later(let date, let endDate):
			heading = "There are no tests available right now.".localized(ACTranslationKey.home_header4)
			
			message = "Your next testing cycle will be *{DATE1}* through *{DATE2}*.".localized(ACTranslationKey.home_body4)
				.replacingOccurrences(of: "{DATE1}", with: date)
				.replacingOccurrences(of: "{DATE2}", with: endDate)
			
            changeAvailabilityButton.isHidden = false
            
		case .tomorrow:
            let schedule = Arc.shared.scheduleController.get(confirmedSchedule: Arc.shared.participantId!)
            let s = schedule!.entries.first
            let start = s!.availabilityStart
            let end = s!.availabilityEnd
            
			heading = "You're done with today's tests.".localized(ACTranslationKey.home_header3)
			message = "We'll notify you tomorrow with your next test.".localized(ACTranslationKey.home_body3)
                .replacingOccurrences(of: "{TIME1}", with: start!)
                .replacingOccurrences(of: "{TIME2}", with: end!)
			
		case .startingTomorrow(let date, let endDate):
			heading = "Your next testing cycle starts tomorrow and runs through \(date).\n".localized(ACTranslationKey.home_header5)
				.replacingOccurrences(of: "{DATE}", with: date)
			
			message = "".localized(ACTranslationKey.home_body5)
                .replacingOccurrences(of: "{DATE 1}", with: date)
                .replacingOccurrences(of: "{DATE1}", with: date)
                .replacingOccurrences(of: "{DATE2}", with: endDate)
            
		case .finished:
			heading = "You've finished the study!".localized(ACTranslationKey.home_header6)
			message = "There are no more tests to take.".localized(ACTranslationKey.home_body6)
            
        case .postBaseline:
            let schedule = Arc.shared.scheduleController.get(confirmedSchedule: Arc.shared.participantId!)
            let s = schedule!.entries.first
            let start = s!.availabilityStart
            let end = s!.availabilityEnd
            
            heading = "*Your first testing cycle begins tomorrow.*".localized(ACTranslationKey.home_header7)
            message = "There will be 4 test sessions per day for 7 days.\n\nWe'll notify you *between {TIME1} and {TIME2}* when it's time to take a test.".localized(ACTranslationKey.home_body7)
                .replacingOccurrences(of: "{TIME1}", with: start!)
                .replacingOccurrences(of: "{TIME2}", with: end!)
		}
		
		
	}
}
