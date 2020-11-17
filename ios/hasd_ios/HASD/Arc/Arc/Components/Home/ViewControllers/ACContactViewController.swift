//
// ACContactViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import MessageUI
open class ACContactViewController: UIViewController, MFMailComposeViewControllerDelegate {
	var returnState:State = Arc.shared.appNavigation.previousState() ?? Arc.shared.appNavigation.defaultState()
	var returnVC:UIViewController?
	@IBOutlet weak var aboutButton:UIButton!
	@IBOutlet weak var privacyButton:UIButton!
	
	override open func viewDidLoad() {
		super.viewDidLoad()
	}
	
	
	
	
}
