//
// EXHomeViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





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
