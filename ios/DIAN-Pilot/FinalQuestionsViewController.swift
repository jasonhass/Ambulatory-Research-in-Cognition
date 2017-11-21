//
//  FinalQuestionsViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/17/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class FinalQuestionsViewController: DNViewController {

    @IBOutlet weak var interruptedYes: SurveyButton!
    @IBOutlet weak var interruptedNo: SurveyButton!
    
    @IBOutlet weak var upgradeYes: SurveyButton?
    @IBOutlet weak var upgradeNo: SurveyButton?
    
    @IBOutlet weak var nextButton: UIButton!
    override func viewDidLoad() {
        nextButton.isEnabled = false;
    }
    
    
    
    @IBAction func interruptedYesTapped(_ sender: UIButton)
    {
        DNDataManager.sharedInstance.currentTestSession?.interrupted = true;
        interruptedNo.isSelected = false;
        interruptedYes.isSelected = true;
        if upgradeNo == nil || (upgradeNo!.isSelected || upgradeYes!.isSelected)
        {
            nextButton.isEnabled = true;
        }

    }
    
    
    @IBAction func interruptedNoTapped(_ sender: UIButton)
    {
        DNDataManager.sharedInstance.currentTestSession?.interrupted = false;
        interruptedNo.isSelected = true;
        interruptedYes.isSelected = false;
        
        if upgradeNo == nil || (upgradeNo!.isSelected || upgradeYes!.isSelected)
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    @IBAction func upgradeYesTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = true;
        upgradeNo?.isSelected = false;
        upgradeYes?.isSelected = true;
        if interruptedNo.isSelected || interruptedYes.isSelected
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    @IBAction func upgradeNoTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = false;
        upgradeNo?.isSelected = true;
        upgradeYes?.isSelected = false;
        
        if interruptedNo.isSelected || interruptedYes.isSelected
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    
    @IBAction func nextPressed(_ sender: Any)
    {
        DNDataManager.save();
        AppDelegate.go(state: .endingVerification);
    }
    
    
    
    
}
