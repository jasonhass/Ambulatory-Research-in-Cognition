//
//  UserSetupViewController.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/21/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class UserSetupViewController: DNViewController, UITextFieldDelegate {
    @IBOutlet weak var nextButton: UIButton!

    @IBOutlet var participantId: UITextField!
    @IBOutlet var confirmId: UITextField!
    @IBOutlet var raterId: UITextField!
    @IBOutlet weak var existingParticipantCheckbox: Checkbox!
    @IBOutlet weak var existingParticipantLabel: UILabel!
    
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    override func viewDidLoad() {
        super.viewDidLoad()

        let toolbar = UIToolbar();
        let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(self.closeTextField));
        let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil);
        NotificationCenter.default.addObserver(self, selector: #selector(UserSetupViewController.closeTextField), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        toolbar.items = [space, doneButton];
        toolbar.sizeToFit();
        
        self.participantId.inputAccessoryView = toolbar;
        self.confirmId.inputAccessoryView = toolbar;
        self.raterId.inputAccessoryView = toolbar;
        
        self.participantId.delegate = self;
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
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func nextPressed(_ sender: Any) {
        
        var invalid:Bool = false;
        if participantId.text == nil || participantId.text! == ""
        {
            participantId.setErrorBorder();
            invalid = true;
        }
        
        if confirmId.text == nil || confirmId.text! == ""
        {
            confirmId.setErrorBorder();
            invalid = true;
        }
        
        if confirmId.text != nil && participantId.text != nil && confirmId.text! != participantId.text!
        {
            confirmId.setErrorBorder();
            participantId.setErrorBorder();
            invalid = true;
        }
        
        if raterId.text == nil || raterId.text == ""
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
            DNRestAPI.shared.registerParticipant(participantId: participantId.text!, raterId: raterId.text!, onCompletion: { (error) in
                
                if error != nil
                {
                    
                    
                    DispatchQueue.main.async {
                        self.raterId.setErrorBorder();
                        self.participantId.setErrorBorder();
                        self.confirmId.setErrorBorder();
                        self.spinner.stopAnimating();
                        self.nextButton.isEnabled = true;
                        self.view.isUserInteractionEnabled = true;
                    }
                }
                else
                {
                    if self.existingParticipantCheckbox.isSelected
                    {
                        DNRestAPI.shared.getCompletedArcCount(onCompletion: { (count, errors) in
                            if errors.count > 0
                            {
                                //TODO: what do we do here? Do we just assume it's zero?
                                DNDataManager.sharedInstance.arcCount = 0;
                            }
                            else
                            {
                                DNDataManager.sharedInstance.arcCount = count;
                            }
                            
                            DispatchQueue.main.async {
                                self.finishSetup(participantId: self.participantId.text!);
                            }
                        })
                    }
                    else
                    {
                        DispatchQueue.main.async {
                            self.finishSetup(participantId: self.participantId.text!);
                        }
                    }
                }
            })
            
            
        }
    }
    
    @IBAction func checkboxTapped(_ sender: UIButton)
    {
        existingParticipantCheckbox.isSelected =  !existingParticipantCheckbox.isSelected;
    }
    func closeTextField()
    {
        participantId.resignFirstResponder();
        confirmId.resignFirstResponder();
        raterId.resignFirstResponder();
        
        if raterId.text != nil && raterId.text != "" && participantId.text != nil && confirmId.text != nil && participantId.text != "" &&  participantId.text == confirmId.text
        {
            nextButton.isEnabled = true
        } else {
            nextButton.isEnabled = false
        }
    }

    
    func finishSetup(participantId:String)
    {
        DNDataManager.sharedInstance.participantId =  participantId;
        
        if existingParticipantCheckbox.isSelected
        {
            AppDelegate.go(state: .setupArcDate);
        }
        else
        {
            TestArc.createArc(forDate: Date());
            AppDelegate.go(state: .setupTime);
        }
    }
    
    
    //MARK: - UITextFieldDelegate
    
    func textFieldDidBeginEditing(_ textField: UITextField)
    {
//        textField.clearErrorBorder();
    }
    
    func textFieldDidEndEditing(_ textField: UITextField)
    {
        if textField == participantId || textField == confirmId
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
        participantId.clearErrorBorder();
        confirmId.clearErrorBorder();
        
        if confirmId.text != nil && participantId.text != nil && confirmId.text! != participantId.text!
        {
            confirmId.setErrorBorder();
            participantId.setErrorBorder();
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
