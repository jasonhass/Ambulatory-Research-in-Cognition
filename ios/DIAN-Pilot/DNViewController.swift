//
//  ViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/14/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class DNViewController: UIViewController {
 
    
    var didDismiss:(Void) -> Void = {};
    var userInfo:Dictionary<String, Any>?;
    
    deinit {
//        print("deinit: \(self.classForCoder)");
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func pressedNext(sender:UIButton){
       
        
    }

}

