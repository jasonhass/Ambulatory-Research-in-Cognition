//
// OnboardingSurveyViewController.swift
//



import UIKit

open class OnboardingSurveyViewController: BasicSurveyViewController {
	var defaultBackValue = true
	var defaultHelpValue = true
	override open func viewDidLoad() {
        super.viewDidLoad()
		defaultBackValue = shouldShowBackButton
        // Do any additional setup after loading the view.
    }
	open override var preferredStatusBarStyle: UIStatusBarStyle {
		return .lightContent
	}
	
	open override func customViewController(forQuestion question: Survey.Question) -> UIViewController? {
		if question.state == "NotificationAccess" {
			currentViewControllerAlwaysHidesBarButtons = true

			return NotificationPermissionViewController()
		}
		if question.state == "NotificationAccessRejected" {
			currentViewControllerAlwaysHidesBarButtons = true

			return NotificationsRejectedViewController()
		}
		if question.state == "rebuked" {
			return RebukedCommitmentViewController()
		}
		return nil
	}
	
	open override func valueSelected(value: QuestionResponse, index: String) {
		super.valueSelected(value: value, index: index)
		if index == "commitment" {
			if let value:Int = value.getValue() {
				if value == 0 {
					app.appController.commitment = .committed
				} else if value == 1 {
					app.appController.commitment = .rebuked
				}
			
			}
		}
	}
	open override func isValid(value: QuestionResponse?, questionId: String, didFinish: @escaping ((Bool) -> ())) {
	
		super.isValid(value: value, questionId: questionId) {valid in
			
			
			if questionId == "commitment" {
				if value?.value == nil {
					didFinish(false)
					return
				} else {
					didFinish(true)
					return
				}
			}
			else if questionId == "allow_from_settings" {
                
//                let vc:CustomViewController<InfoView> = self.getTopViewController()!
//                let message = "".localized(ACTranslationKey.allow_from_settings)
//                    .replacingOccurrences(of: "[app name]", with: "EXR")
//                vc.customView.setHeading(message)
//
				if Await(checkNotificationStatus).execute(()) {
					didFinish(true)
				} else {
					//We let them go in either case. 
					didFinish(true)

				}
			}
			else {
				didFinish(true)
			}
		}
		
	}
	
	
	
}
fileprivate func checkNotificationStatus(void:Void, didFinish: @escaping (Bool)->()) {
	//A long running request
	Arc.shared.notificationController.authenticateNotifications { (granted, error) in
		OperationQueue().addOperation {
			didFinish(granted)
		}
	}
}
