//
// EXHomeViewController.swift
//



import UIKit
import Arc
import ArcUIKit
open class EXHomeViewController: ACHomeViewController {
	@IBAction func changeAvailabilityPressed(_ sender: Any) {
		app.appNavigation.navigate(state: EXState.rescheduleAvailability, direction: .toRight)
		
	}
	
	@IBAction func contactStudyCoordinatorPressed(_ sender: Any) {
		app.appNavigation.navigate(state: EXState.contact, direction: .toRight)
		
	}
}
