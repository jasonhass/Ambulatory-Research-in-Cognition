//
// AC2FAHelpViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import HMMarkup
import ArcUIKit

class AC2FAHelpViewController: UIViewController {

    @IBOutlet weak var backButton: HMMarkupButton!
    @IBOutlet weak var headerLabel: ACLabel!
    @IBOutlet weak var answerLabel: HMMarkupLabel!
 
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.setNavigationBarHidden(true, animated: true)

        self.backButton.addAction {
            self.navigationController?.popViewController(animated: true);
        }
        
        self.headerLabel.text = "faq_tech_q5"
        self.answerLabel.text = "faq_tech_a5"
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
}
