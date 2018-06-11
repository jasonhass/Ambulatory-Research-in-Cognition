/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class UserSetupViewController: DNViewController, UITextFieldDelegate {
    @IBOutlet weak var nextButton: UIButton!

    @IBOutlet var arcId: UITextField!
    @IBOutlet var confirmId: UITextField!
    @IBOutlet var raterId: UITextField!
    @IBOutlet weak var existingParticipantCheckbox: Checkbox!
    @IBOutlet weak var existingParticipantLabel: UILabel!
    @IBOutlet weak var raterIdField: BorderTextField!
    
    @IBOutlet weak var raterIdLabel: DNLabel!
    
    @IBOutlet weak var exrLabel: DNLabel!
    
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    override func viewDidLoad() {
        super.viewDidLoad()

        let toolbar = UIToolbar();
        let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(self.closeTextField));
        let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil);
        NotificationCenter.default.addObserver(self, selector: #selector(UserSetupViewController.closeTextField), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        toolbar.items = [space, doneButton];
        toolbar.sizeToFit();
        
        self.arcId.inputAccessoryView = toolbar;
        self.confirmId.inputAccessoryView = toolbar;
        self.raterId.inputAccessoryView = toolbar;
        
        self.arcId.delegate = self;
        self.confirmId.delegate = self;
        self.raterId.delegate = self;
        
        nextButton.isEnabled = false
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated);
        
        if let ui = self.userInfo
        {
            if let isExisting = ui["existingUser"] as? Bool
            {
                existingParticipantCheckbox.isSelected = isExisting;
                existingParticipantCheckbox.isHidden = true;
                existingParticipantLabel.isHidden = true;
            }
        }
//        #if DBG
            existingParticipantCheckbox.isHidden = true;
            existingParticipantLabel.isHidden = true;

//        #endif
        
        #if EXR
            exrLabel.isHidden = false;
        #endif
        
        
        if DNRestAPI.shared.requireRegistration == false
        {
            raterIdField.isHidden = true;
            raterIdLabel.isHidden = true;
        }
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func nextPressed(_ sender: Any) {
        
        var invalid:Bool = false;
        if arcId.text == nil || arcId.text! == ""
        {
            arcId.setErrorBorder();
            invalid = true;
        }
        
        if confirmId.text == nil || confirmId.text! == ""
        {
            confirmId.setErrorBorder();
            invalid = true;
        }
        
        if confirmId.text != nil && arcId.text != nil && confirmId.text! != arcId.text!
        {
            confirmId.setErrorBorder();
            arcId.setErrorBorder();
            invalid = true;
        }
        
        if DNRestAPI.shared.requireRegistration == true && ( raterId.text == nil || raterId.text == "")
        {
            raterId.setErrorBorder();
            invalid = true;
        }
        
        
        if invalid == false
        {
            NotificationCenter.default.removeObserver(self)
            self.view.isUserInteractionEnabled = false;
            nextButton.isEnabled = false;
            spinner.startAnimating();
            #if EXR
                DNRestAPI.shared.requestVerificationCode(arcId: arcId.text!, onCompletion: { (error) in
                    if error != nil
                    {
                        
                        
                        DispatchQueue.main.async {
                            self.raterId.setErrorBorder();
                            self.arcId.setErrorBorder();
                            self.confirmId.setErrorBorder();
                            self.spinner.stopAnimating();
                            self.nextButton.isEnabled = true;
                            self.view.isUserInteractionEnabled = true;
                        }
                    }
                    else
                    {
                        
                        DispatchQueue.main.async {
                            
                            self.finishSetup(arcId: self.arcId.text!);
                        }
                        
                    }
                })
            #else
            
                DNRestAPI.shared.registerParticipant(arcId: arcId.text!, raterId: raterId.text!, onCompletion: { (error) in
                if error != nil
                {
                    
                    
                    DispatchQueue.main.async {
                        self.raterId.setErrorBorder();
                        self.arcId.setErrorBorder();
                        self.confirmId.setErrorBorder();
                        self.spinner.stopAnimating();
                        self.nextButton.isEnabled = true;
                        self.view.isUserInteractionEnabled = true;
                    }
                }
                else
                {

                    DispatchQueue.main.async {

                        self.finishSetup(arcId: self.arcId.text!);
                    }

                }
            })
            #endif

        }
    }
    
    @IBAction func checkboxTapped(_ sender: UIButton)
    {
        existingParticipantCheckbox.isSelected =  !existingParticipantCheckbox.isSelected;
    }
    func closeTextField()
    {
        arcId.resignFirstResponder();
        confirmId.resignFirstResponder();
        raterId.resignFirstResponder();
        
        if (DNRestAPI.shared.requireRegistration == false || (raterId.text != nil && raterId.text != ""))
            && arcId.text != nil && confirmId.text != nil
            && arcId.text != "" &&  arcId.text == confirmId.text
        {
            nextButton.isEnabled = true
        } else {
            nextButton.isEnabled = false
        }
    }

    
    func finishSetup(arcId:String)
    {
        DNDataManager.sharedInstance.arcId =  arcId;
        ///EXR-CHANGES
//        #if DBG
        #if EXR
            AppDelegate.go(state: .twoFactorEnterCode);

        #else

        if DNDataManager.sharedInstance.visitCount > 0
        {
            AppDelegate.go(state: .setupArcDate);
        }
        else
        {
            TestVisit.createVisit(forDate: Date());
            AppDelegate.go(state: .setupTime);
        }
        #endif
    }
    
    
    //MARK: - UITextFieldDelegate
    
    func textFieldDidBeginEditing(_ textField: UITextField)
    {
//        textField.clearErrorBorder();
    }
    
    func textFieldDidEndEditing(_ textField: UITextField)
    {
        if textField == arcId || textField == confirmId
        {
            validateParticipantID();
        }
        else    //else it's the rater ID
        {
            validateRaterID();
        }
    }
    
    func validateParticipantID()
    {
        arcId.clearErrorBorder();
        confirmId.clearErrorBorder();
        
        if confirmId.text != nil && arcId.text != nil && confirmId.text! != arcId.text!
        {
            confirmId.setErrorBorder();
            arcId.setErrorBorder();
        }
    }
    
    func validateRaterID()
    {
        if raterId.text != nil && raterId.text != ""
        {
            raterId.clearErrorBorder();
        }
    }
}
