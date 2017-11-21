//
//  TestStartViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class TestStartViewController: DNViewController {

    
    override func viewDidLoad() {
        AppDelegate.go(state: .beginningVerification);
    }
    
    @IBAction func startPressed(_ sender: Any)
    {
        
    }
    
    
}
