//
//  DNTestHomeViewController.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/29/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class NoTestViewController: DNViewController {


    
    override func viewDidLoad() {
        super.viewDidLoad()

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func editAvailabilityPressed(_ sender: Any)
    {
        AppDelegate.go(state: .setupTime)
    }
}
