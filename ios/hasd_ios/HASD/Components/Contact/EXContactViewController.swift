//
// EXContactViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit
import MessageUI
import Arc
import ArcUIKit
import HMMarkup
open class EXContactViewController: UIViewController, MFMailComposeViewControllerDelegate {
	var returnState:State = Arc.shared.appNavigation.previousState() ?? Arc.shared.appNavigation.defaultState()
    
	var returnVC:UIViewController?
    
    var contactInfo: ContactInfoResponse.Body.ContactInfo?
    
    @IBOutlet weak var backButton: HMMarkupButton!
    
    @IBOutlet weak var phoneLabel: HMMarkupLabel!
    @IBOutlet weak var callButton: ACButton!
    
    @IBOutlet weak var emailLabel: HMMarkupLabel!
    @IBOutlet weak var emailButton: ACButton!
    
    
	override open func viewDidLoad() {
		super.viewDidLoad()

        self.loadContactInfo();
	}
    
    func loadContactInfo()
    {
		if self.phoneLabel.text == "555-555-5555" {
				   self.phoneLabel.text = "".localized(ACTranslationKey.contact_call2)
			   }
			   
			   if self.emailLabel.text == "sample@email.com" {
				   self.emailLabel.text = "".localized(ACTranslationKey.contact_email2)
			   }
		
        if let json = CoreDataStack.currentDefaults()?.object(forKey: "contact_info") as? Data, let contact_info = try? JSONDecoder().decode(ContactInfoResponse.Body.ContactInfo.self, from: json)
        {
            
            self.contactInfo = contact_info;
			self.phoneLabel.text = contact_info.phone;
			self.emailLabel.text = contact_info.email;
        }
        else
        {
			HMAPI.getContactInfo.execute(data: nil, completion: { (res, obj, err) in
                guard err == nil && obj?.errors.isEmpty ?? true else {
                    return;
                }
                guard let contact_info = obj?.response?.contact_info else {
                    return;
                }
                DispatchQueue.main.async{
                    self.contactInfo = contact_info;
                    self.phoneLabel.text = contact_info.phone;
                    self.emailLabel.text = contact_info.email;
                    if let json = try? JSONEncoder().encode(contact_info)
                    {
                        CoreDataStack.currentDefaults()?.set(json, forKey: "contact_info");
                    }
                    
                }
            });
        }
        
       
    }
	
	@IBAction func goBackPressed(_ sender: Any) {
        
        if let nav = self.navigationController, nav.viewControllers.count > 1
        {
            nav.popViewController(animated: true);
        }
		else if let vc = returnVC {
			Arc.shared.appNavigation.navigate(vc: vc, direction: .toLeft)
		} else {
			Arc.shared.appNavigation.navigate(state: returnState, direction: .toLeft)
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
	public func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
		controller.dismiss(animated: true)
	}
	
	//MARK: - Actions
	@IBAction func callPressed(_ sender: Any) {
        if let phone = self.contactInfo?.phone
        {
            let str = "tel://\(phone)";
            UIApplication.shared.open(URL(string: str)!, options: [:], completionHandler: nil)
        }
			
	}
	
    @IBAction func emailPressed(_ sender: Any) {
        if let email = self.contactInfo?.email,  MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self
            mail.setToRecipients([email])
            
            present(mail, animated: true)
        } else {
            // show failure alert
        }
    }
    
    
}
