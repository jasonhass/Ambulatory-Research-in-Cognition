//
// WelcomeViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit
import Arc

open class WelcomeViewController: UIViewController {

    
    @IBOutlet weak var titleImage:UIImageView!
    @IBOutlet weak var titleText:UILabel!
    @IBOutlet weak var aboutButton:UIButton!
    @IBOutlet weak var privacyButton:UIButton!
    @IBOutlet weak var signInButton:UIButton!


    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let attributes:[NSAttributedString.Key:Any] = [
            .foregroundColor : UIColor(named: "Primary") as Any,
            .font : UIFont(name: "Roboto-Medium", size: 18.0) as Any,
            .underlineStyle: NSUnderlineStyle.single.rawValue
        ]
        let aboutTitle = NSAttributedString(string: "About This App", attributes: attributes)
        let privacyTitle = NSAttributedString(string: "Privacy Policy", attributes: attributes)
        aboutButton.setAttributedTitle(aboutTitle, for: .normal)
        privacyButton.setAttributedTitle(privacyTitle, for: .normal)
        titleImage.image = Arc.shared.WELCOME_LOGO
        titleText.text = Arc.shared.WELCOME_TEXT
    }
    
    @IBAction open func signInPressed(_ sender: UIButton) {
        Arc.shared.appNavigation.navigate(state: Arc.shared.appNavigation.defaultAuth(), direction: .fade)
    }
    
    @IBAction open func aboutPressed(_ sender: UIButton) {
        if let about = Arc.shared.appNavigation.viewForState(state: Arc.shared.appNavigation.defaultAbout()) as? AboutViewController {
            about.returnVC = self.parent
            Arc.shared.appNavigation.navigate(vc: about, direction: .toRight)
        }
    }
    
    @IBAction func privacyPressed(_ sender: UIButton) {
        Arc.shared.appNavigation.defaultPrivacy()
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
