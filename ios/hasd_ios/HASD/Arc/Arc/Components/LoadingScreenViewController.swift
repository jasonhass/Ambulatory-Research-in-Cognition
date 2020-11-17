//
// LoadingScreenViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

class LoadingScreenViewController: UIViewController {
	@IBOutlet weak var progress:UIProgressView!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

	open func didProgress(_ items:Int, _ total:Int) {
		let prog = Float(items) / Float(total)
		//print(items, total, prog)
			self.progress.progress = prog

		
		
		if items >= total {
			
			Arc.shared.nextAvailableState()
			
		}
	}
	

}
