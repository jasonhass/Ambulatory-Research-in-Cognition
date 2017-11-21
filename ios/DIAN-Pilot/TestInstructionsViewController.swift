//
//  SurveyInstructionsViewController.swift
//  DIAN-Pilot
//
//  Created by Geoff Strom on 11/21/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class TestInstructionsViewController: DNViewController {

    @IBAction func pressedNext(_ sender: AnyObject) {
        AppDelegate.go(state: .testSession)
    }

}
