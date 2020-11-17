//
// EXHomeViewController.swift
//




import UIKit
import Arc
import ArcUIKit
open class HASDHomeViewController: ACHomeViewController {
	@IBAction func changeAvailabilityPressed(_ sender: Any) {
		app.appNavigation.navigate(state: HDState.rescheduleAvailability, direction: .toRight)
		
	}
	
	@IBAction func contactStudyCoordinatorPressed(_ sender: Any) {
		app.appNavigation.navigate(state: HDState.contact, direction: .toRight)
		
	}
}
