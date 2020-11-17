//
// EXContactNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

public class EXContactNavigationController: UINavigationController {
	var prev:UIViewController?
	weak var vc:EXContactViewController! = nil
	
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
	override public func viewDidLoad() {
		super.viewDidLoad()
		let contact:EXContactViewController = .get()
		vc = contact

		pushViewController(contact, animated: true)
		navigationBar.isHidden = true
		// Do any additional setup after loading the view.
	}
	override public func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		vc.returnVC = prev
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
