//
//  BaselineWaitViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class BaselineWaitViewController: DNViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func siteCoordinatorTapped(_ sender: Any)
    {
        //TODO: go through setup again
        
        AppDelegate.go(state: .setupUser, userInfo: ["existingUser":true]);
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
