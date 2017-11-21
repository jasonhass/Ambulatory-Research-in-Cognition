//
//  ConfirmPhoneViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/17/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class ConfirmPhoneViewController: DNViewController {

    
    @IBOutlet weak var yesButton: SurveyButton!
    @IBOutlet weak var noButton: SurveyButton!
    @IBOutlet weak var doneButton: UIButton!
    
    override func viewDidLoad() {
        doneButton.isEnabled = false;
    }
    
    @IBAction func yesTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = true;
        noButton.isSelected = false;
        doneButton.isEnabled = true;
    }
    
    
    @IBAction func noTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = false;
        yesButton.isSelected = false;
        doneButton.isEnabled = true;
    }
    
    @IBAction func nextPressed(_ sender: Any)
    {
        DNDataManager.save();
        AppDelegate.chooseDisplay();
    }
    
    
}
