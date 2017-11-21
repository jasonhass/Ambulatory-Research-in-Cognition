//
//  WakeSurveyViewController.swift
//  ARC
//
//  Created by Philip Hayes on 5/9/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit
import CoreData
class WakeSurveyViewController: DNViewController,SurveyStackViewDelegate {
    
    private enum QuestionKey:Int {
        case none = 0, bedTime, sleepTime, numWakes, wakeTime, outOfBed, sleepQuality
        
    
    }
    
    @IBOutlet var surveyQuestionViews:[SurveyStackView]!
    @IBOutlet var doneButton:UIButton!
    var wake:WakeSurvey?
    var currentContext:NSManagedObjectContext?
    var bedTime:Date?;
    var sleepTimeHours:Int = 0;
    var sleepTimeMinutes:Int = 0;
    var wakeTime:Date?;
    var outOfBed:Date?;
    
    
    func validate() -> Bool{
        var isValid = true
        //Check if contextSurvey Exists
        guard wake != nil else {
            return false
        }
        //Run loop over available questionViews
        main: for v in surveyQuestionViews {
            
            //Check the question key on the view
            guard let key = QuestionKey(rawValue: v.questionKey) else {
                continue
            }
            
            //Use a switch to check the values validity
            switch key {
            case .bedTime:
                if wake?.bedTime == nil {
                    isValid = false
                    break main
                }
                
            case .sleepTime:
                if wake?.sleepTime == nil {
                    isValid = false
                    break main
                }
            case .numWakes:
                if (wake?.numWakes)! < 0 || (wake?.numWakes)! == 99 {
                    isValid = false
                    break main
                }
                
            case .wakeTime:
                if wake?.wakeTime == nil {
                    isValid = false
                    break main
                }
                
            case .outOfBed:
                if wake?.getUpTime == nil {
                    isValid = false
                    break main
                }
            
            default:
                continue main
            }
        }
        if isValid {
            doneButton.isEnabled = true
        } else {
            doneButton.isEnabled = false
        }
        return isValid
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let session = DNDataManager.sharedInstance.currentTestSession;
        currentContext = DNDataManager.backgroundContext
        if session?.wakeSurvey == nil {
            let c:WakeSurvey = NSManagedObject.createIn(context: currentContext!)
            session?.wakeSurvey = c
            wake = c
        } else {
            wake = session?.wakeSurvey

        }
        

        
        for v in surveyQuestionViews {
            
            v.delegate = self
            
            if let buttonStack = v as? SurveyStackButtonView {
                buttonStack.hideValue()
            }
            
            if let buttonStack = v as? SurveyStackPickerView {
                buttonStack.hideValue()
            }
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
        
        self.wake?.cpuLoad = "\(Int(LoadMonitor.cpu_usage()))%";
        self.wake?.deviceMemory = "\(Int(LoadMonitor.memory_usage()))/\(LoadMonitor.totalMemory())MB";
        DNDataManager.save();
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    
    func didOutputValue<T>(view: SurveyStackView, value: T) {
        print(value, T.self)
        guard let key = QuestionKey.init(rawValue: view.questionKey) else {
            return
        }
        switch  key {
        case .bedTime:
            let datePicker:DatePickerAlertViewController = GetAlertController()
            datePicker.datePickerMode = .time
            datePicker.defaultDate = bedTime;
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.bedTime = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }
                        self.wake?.bedTime = time
                        
                    }
                }
                
            })
            
            break
        case .sleepTime:
            let sleepTimePicker:PickerAlertViewController = GetAlertController()
            
            var hoursList:Array<String> = Array();
            for i in 0...24
            {
                hoursList.append("\(i)");
            }
            
            var minutesList:Array<String> = Array();
            
            for i in 0..<12
            {
                minutesList.append("\(i * 5)");
            }
            
            let hoursLabel:Array<String> = [DateComponentsFormatter.localizedString(forUnit: .hour, style: .full)];
            let minutesLabel:Array<String> = [DateComponentsFormatter.localizedString(forUnit: .minute, style: .short)];
            
            sleepTimePicker.columns = [hoursList, hoursLabel, minutesList, minutesLabel];
            sleepTimePicker.defaultRows = [self.sleepTimeHours, 0, Int(self.sleepTimeMinutes / 5), 0];
            
            self.present(sleepTimePicker, animated: false, completion: {
                
                sleepTimePicker.onConfirm = { v in
                    if let t = v as? Array<String>, let h = Int(t[0]), let m = Int(t[2]) {
                        self.sleepTimeMinutes = m;
                        self.sleepTimeHours = h;
                        
                        let s:TimeInterval = TimeInterval(h * 60 * 60) + TimeInterval(m * 60);
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: "\(s.localizedInterval())")
                            
                        }
                        self.wake?.sleepTime = "\(Int(s / 60.0)) min";
                        
                    }
                }
                
            })
            break
        case .numWakes:
            let picker:PickerAlertViewController = GetAlertController()
            
            var wakeCountList:Array<String> = Array();
            for i in 0...20
            {
                wakeCountList.append("\(i)");
            }
            
            picker.columns = [wakeCountList];
            
            picker.defaultRows = [Int(self.wake?.numWakes ?? 0)];
            if self.wake != nil && self.wake!.numWakes == 99
            {
                picker.defaultRows = [0];
            }
            
            self.present(picker, animated: false, completion: {
                picker.onConfirm = { v in
                    
                    if let t = v as? Array<String>, let c = Int(t[0]) {
                        OperationQueue.main.addOperation {
                            self.ConfigurePickerStack(view: view as! SurveyStackPickerView, title: "\(c)")
                            
                        }
                        self.wake?.numWakes = Int64(c)
                        
                        
                    }
                }
            })
            
            break
        case .wakeTime:
            let datePicker:DatePickerAlertViewController = GetAlertController()
            datePicker.datePickerMode = .time
            datePicker.defaultDate = wakeTime;
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.wakeTime = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }
                        self.wake?.wakeTime = time
                        
                    }
                }
                
            })
            break
        case .outOfBed:
            let datePicker:DatePickerAlertViewController = GetAlertController()
            datePicker.datePickerMode = .time
            datePicker.defaultDate = outOfBed;
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.outOfBed = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }
                        self.wake?.getUpTime = time
                        
                    }
                }
                
            })
            break
        case .sleepQuality:
            if let v = value as? Float {
                self.wake?.sleepQuality = v
            }
            
            break
            
        default:
            return
        }
    }
    
    @IBAction func surveyCompletedPressed(sender:UIButton){
        DNDataManager.save()
        
        
        if let session = DNDataManager.sharedInstance.currentTestSession
        {
            session.hasTakenWake = true;
            DNDataManager.save();
        }
        AppDelegate.go(state: .surveyContext);
    }
    func StringFromTime(date:Date) -> String{
        
        return DateFormatter.localizedString(from: date, dateStyle: .none, timeStyle: .short);
        
    }
    func ConfigureButtonStack(view:SurveyStackButtonView, title:String){
        
        view.setDisplayLabel(title: title)
        view.hideButton()
        view.showValue()
        if validate() {
            
        }
    }
    func ConfigurePickerStack(view:SurveyStackPickerView, title:String){
        
        view.setDisplayLabel(title: title)
        view.hideButton()
        view.showValue()
        if validate() {
            
        }
    }

}
