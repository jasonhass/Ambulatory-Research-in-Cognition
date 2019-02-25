//
// AboutViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class AboutViewController: UIViewController {

    open var returnState:State = Arc.shared.appNavigation.previousState() ?? Arc.shared.appNavigation.defaultState()
    open var returnVC:UIViewController?
    
    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction open func goBackPressed(_ sender: Any) {
		if let nav = navigationController {
			nav.popViewController(animated: true)
		} else {
			if let vc = returnVC {
				Arc.shared.appNavigation.navigate(vc: vc, direction: .toLeft)
			} else {
				Arc.shared.appNavigation.navigate(state: returnState, direction: .toLeft)
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
