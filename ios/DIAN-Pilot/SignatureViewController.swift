//
//  SignatureViewController.swift
//  ARC
//
//  Created by Philip Hayes on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class SignatureViewController: DNViewController {
    @IBOutlet weak var signatureView:SignatureView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
        
        DNDataManager.sharedInstance.currentTestSession = TestArc.getCurrentArc()?.getAvailableTestSession();
        DNDataManager.sharedInstance.isTesting = true
        DNDataManager.sharedInstance.currentTestSession?.startTime = Date() as NSDate;
        
//        DNDataManager.sharedInstance.takeDeviceSample();
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
        //TODO: save signature
        if signatureView.path.isEmpty {
            return
        }
        if let arc = TestArc.getCurrentArc(), let session = DNDataManager.sharedInstance.currentTestSession, let img = signatureView.save()
        {
            session.startSignature = UIImagePNGRepresentation(img) as NSData?;
            DNDataManager.save();
            if arc.hasTakenChronotypeSurvey() == false
            {
                AppDelegate.go(state: .surveyChronotype);
            }
            else if arc.hasTakenWakeSurvey(forDay: Date()) == false
            {
                AppDelegate.go(state: .surveyWake);
            }
            else
            {
                AppDelegate.go(state: .surveyContext);
            }
        }
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
