//
//  TextAlertViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/18/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class TextAlertViewController: DNAlertViewController {

    @IBOutlet weak var textLabel: UILabel!
    private var message:String?
    @IBOutlet weak var okayButton: UIButton!
    private var confirmString:String?
    var timein:TimeInterval?;
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        if (message != nil) {
            setText(string: message!)
        }

        if confirmString != nil
        {
            okayButton.setTitle(confirmString, for: .normal);
        }
        
        if let t = timein
        {
            okayButton.isEnabled = false;
            
            Timer.scheduledTimer(timeInterval: t, target: self, selector: #selector(self.enableOkayButton), userInfo: nil, repeats: false);
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
    
    
    
    public func setConfirmText(string:String)
    {
        confirmString = string;
        if okayButton != nil
        {
            okayButton.setTitle(confirmString, for: .normal);
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirm(_ sender: AnyObject) {
        self.confirmationButtonPresssed()
    }
    
    func enableOkayButton()
    {
        self.okayButton.isEnabled = true;
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
