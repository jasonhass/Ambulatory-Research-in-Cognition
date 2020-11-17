//
// WelcomeViewController.swift
//



import UIKit

open class WelcomeViewController: UIViewController {

    
    @IBOutlet weak var titleImage:UIImageView!
    @IBOutlet weak var titleText:UILabel!
    @IBOutlet weak var aboutButton:UIButton!
    @IBOutlet weak var privacyButton:UIButton!
    @IBOutlet weak var signInButton:UIButton!

    @IBOutlet weak var versionLabel: UILabel!
    
    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let attributes:[NSAttributedString.Key:Any] = [
            .foregroundColor : UIColor(named: "Primary") as Any,
            .font : UIFont(name: "Roboto-Medium", size: 18.0) as Any,
            .underlineStyle: NSUnderlineStyle.single.rawValue
        ]
        let aboutTitle = NSAttributedString(string: "About This App".localized(ACTranslationKey.about_linked), attributes: attributes)
        let privacyTitle = NSAttributedString(string: "Privacy Policy".localized(ACTranslationKey.privacy_linked), attributes: attributes)
        aboutButton.setAttributedTitle(aboutTitle, for: .normal)
		
        privacyButton.setAttributedTitle(privacyTitle, for: .normal)
        titleImage.image = Arc.shared.WELCOME_LOGO
        titleText.text = Arc.shared.WELCOME_TEXT
        versionLabel.text = "v\(Arc.shared.versionString)"
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
