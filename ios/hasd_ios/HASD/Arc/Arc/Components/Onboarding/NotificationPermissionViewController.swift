//
// NotificationPermissionViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public class NotificationPermissionViewController: CustomViewController<NotificationPermissionView>, SurveyInput {
	
	public weak var surveyInputDelegate: SurveyInputDelegate?

	public var orientation: UIStackView.Alignment = .center
	
	
	
	var _didAllow:Bool = false
	
	
	override public func viewDidLoad() {
        super.viewDidLoad()
		navigationItem.leftBarButtonItem = UIBarButtonItem(customView: UIView())
		navigationItem.rightBarButtonItem = UIBarButtonItem(customView: UIView())

        // Do any additional setup after loading the view.
    }
    

	override public func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		Arc.shared.notificationController.authenticateNotifications { [weak self] (granted, error) in
			OperationQueue.main.addOperation {
				self?._didAllow = granted
				Arc.shared.appController.isNotificationAuthorized = granted
				self?.surveyInputDelegate?.didChangeValue()
				self?.surveyInputDelegate?.tryNextPressed()
				
			}
			
		}
		
	}
	
	
	public func getValue() -> QuestionResponse? {
		return AnyResponse(type: .choice, value: Int(_didAllow ? 1 : 0), textValue: String(_didAllow))
	}
	
	public func setValue(_ value: QuestionResponse?) {
		
	}

}
