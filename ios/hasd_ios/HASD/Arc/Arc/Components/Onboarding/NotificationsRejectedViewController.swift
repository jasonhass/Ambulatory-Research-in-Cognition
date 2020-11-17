//
// NotificationsRejectedViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import ArcUIKit
public class NotificationsRejectedViewController : CustomViewController<InfoView>, SurveyInput {
	public var useDarkStatusBar:Bool = false
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return useDarkStatusBar ? .default : .lightContent
    }
	
	public var orientation: UIStackView.Alignment = .top

	
	public var surveyInputDelegate: SurveyInputDelegate?
	
	
	
	public func getValue() -> QuestionResponse? {
		return nil
	}
	
	public func setValue(_ value: QuestionResponse?) {
		
	}
	public override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		
		
	}
	public func updateState() {
		customView.nextButton?.setTitle("".localized(ACTranslationKey.button_settings), for: .normal)
		Arc.shared.notificationController.authenticateNotifications { [weak self] (granted, error) in
			OperationQueue.main.addOperation {
				Arc.shared.appController.isNotificationAuthorized = granted
				if !granted {
					self?.customView.nextButton?.setTitle("".localized(ACTranslationKey.button_settings), for: .normal)

				} else {
					self?.customView.nextButton?.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
					self?.surveyInputDelegate?.didChangeValue()
					self?.surveyInputDelegate?.tryNextPressed()
				}
				
				
				
			}
			
		}
	}
	public override func viewDidLoad() {
		super.viewDidLoad()
		
        useDarkStatusBar = false
        setNeedsStatusBarAppearanceUpdate()
		customView.addSpacer()

        customView.backgroundView.image = UIImage(named: "availability_bg", in: Bundle(for: self.classForCoder), compatibleWith: nil)
		customView.infoContent.alignment = .center
		customView.backgroundColor = UIColor(named:"Primary")!
		customView.setTextColor(UIColor(named: "Secondary Text"))
        
		
		
		customView.setButtonColor(style:.secondary)
		
		customView.nextButton?.setTitle("".localized(ACTranslationKey.button_settings), for: .normal)
		customView.nextButton?.addAction {  [weak self] in
			Arc.shared.notificationController.authenticateNotifications { [weak self] (granted, error) in
				OperationQueue.main.addOperation {
					Arc.shared.appController.isNotificationAuthorized = granted
					if !granted {
						guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
							return
						}
						
						if UIApplication.shared.canOpenURL(settingsUrl) {
							UIApplication.shared.open(settingsUrl, completionHandler: { (success) in
								print("Settings opened: \(success)") // Prints true
							})
						}
					} else {
						self?.surveyInputDelegate?.didChangeValue()
						self?.surveyInputDelegate?.tryNextPressed()
					}
					
					
					
				}
				
			}
			
			
		}
		customView.setHeading("".localized(ACTranslationKey.onboarding_notifications_header2))
		customView.infoContent.headingLabel?.textAlignment = .center
		
		customView.setContentLabel("".localized(ACTranslationKey.onboarding_notifications_body2_ios)
			.replacingOccurrences(of: "{APP NAME}", with: "EXR"))
		
		customView.getContentLabel().textAlignment = .center
		
		
		
		customView.addSpacer()
		
		let button1 = ACButton()
		
		button1.primaryColor = .clear
		button1.secondaryColor = .clear
		button1.topColor = .clear
		button1.bottomColor = .clear
		button1.setTitle("Proceed Without Notifications".localized(ACTranslationKey.button_proceed_without_notifications), for: .normal)
		button1.titleLabel?.textAlignment = .center

		Roboto.PostProcess.link(button1)
		
		button1.addAction {  [weak self] in
			self?.surveyInputDelegate?.tryNextPressed()
		}
		customView.setAdditionalFooterContent(button1)
		

	}
	
	
}
