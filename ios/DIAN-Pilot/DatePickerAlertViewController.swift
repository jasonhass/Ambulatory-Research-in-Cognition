//
//  DatePickerAlertViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/18/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class DatePickerAlertViewController: DNAlertViewController {
    
    
    
    @IBOutlet weak var datePicker: UIDatePicker!
    var datePickerMode:UIDatePickerMode = .time;
    var defaultDate:Date?;
    var defaultDuration:TimeInterval?;
    override func viewDidLoad() {
        super.viewDidLoad()
        datePicker.datePickerMode = self.datePickerMode;
        datePicker.minuteInterval = 5;
        if datePickerMode == .countDownTimer
        {
            datePicker.countDownDuration = defaultDuration ?? 5 * 60;
        }
        else
        {
            datePicker.date = defaultDate ?? datePicker.date.roundedTo(minutes: datePicker.minuteInterval);
        }
        
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        self.view.backgroundColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 0.7043309564)

        datePicker.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
        
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func datePicked(_ sender: UIDatePicker) {
        
        switch sender.datePickerMode
        {
            case .time:
            self.retValue = sender.date as AnyObject?
            case .date:
            self.retValue = sender.date as AnyObject?
            case .dateAndTime:
            self.retValue = sender.date as AnyObject?
            case .countDownTimer:
            self.retValue = sender.countDownDuration as AnyObject?
        }
    }
    
    @IBAction func confirm(_ sender: AnyObject) {
        
        
        switch datePicker.datePickerMode
        {
        case .time:
            self.retValue = datePicker.date as AnyObject?
        case .date:
            self.retValue = datePicker.date as AnyObject?
        case .dateAndTime:
            self.retValue = datePicker.date as AnyObject?
        case .countDownTimer:
            self.retValue = datePicker.countDownDuration as AnyObject?
        }
        
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


