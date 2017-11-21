//
//  SignatureViewController.swift
//  ARC
//
//  Created by Philip Hayes on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class EndSignatureViewController: DNViewController {
    @IBOutlet weak var signatureView:SignatureView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
            
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func undo(sender:UIButton){
        self.signatureView.clear()
    }
    
    
    override func pressedNext(sender: UIButton)
    {
        if signatureView.path.isEmpty {
            return
        }
        if let session = DNDataManager.sharedInstance.currentTestSession, let img = signatureView.save()
        {
            
            session.completeTime = Date() as NSDate;
            session.endSignature = UIImagePNGRepresentation(img) as NSData?;
            session.markFinished();
            DNDataManager.save();
            DNDataManager.sharedInstance.currentTestSession = nil;
            DNDataManager.sharedInstance.isTesting = false;
            
        }
        
        AppDelegate.chooseDisplay(runPeriodicBackgroundTask: true);
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
