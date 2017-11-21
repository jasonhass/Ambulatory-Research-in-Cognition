//
//  QuestionAlertViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/28/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit
class QuestionAlertViewController: DNAlertViewController {

    @IBOutlet weak var textLabel: UILabel!
    private var message:String?
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        if (message != nil) {
            setText(string: message!)
        }
        textLabel.superview?.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))

    }
    public func setText(string:String){
        if textLabel == nil {
            message = string
        } else {
            textLabel.text = string
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func confirm(_ sender: UIButton) {
        self.retValue = sender.tag as AnyObject

        self.confirmationButtonPresssed()
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
