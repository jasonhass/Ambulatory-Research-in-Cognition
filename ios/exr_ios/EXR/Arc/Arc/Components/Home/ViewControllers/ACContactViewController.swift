//
// ACContactViewController.swift
//



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
