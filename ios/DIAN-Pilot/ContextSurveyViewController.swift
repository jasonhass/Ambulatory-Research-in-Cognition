//
//  ContextSurveyViewController.swift
//  ARC
//
//  Created by Philip Hayes on 5/9/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit
import CoreData

class ContextSurveyViewController: DNViewController, SurveyStackViewDelegate {
    
    private enum QuestionKey:Int {
        case none = 0, whoIsWith, location, mood, alertness, recentActivity
        
        
    }
    var json:JSON?
    @IBOutlet var surveyQuestionViews:[SurveyStackView]!
    @IBOutlet weak var doneButton: UIButton!
    private var context:ContextSurvey? = nil
    var currentContext:NSManagedObjectContext?

    private var selectedIndexes:Dictionary<QuestionKey,Array<Int>> = Dictionary();
    
    
    func validate() -> Bool{
        var isValid = true
        //Check if contextSurvey Exists
        guard context != nil else {
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
            case .whoIsWith:
                if context?.whoIsWith == nil {
                    isValid = false
                    break main
                }
                
            case .location:
                if context?.location == nil {
                    isValid = false
                    break main
                }
            case .recentActivity:
                if context?.recentActivity == nil {
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
        
        currentContext = DNDataManager.backgroundContext
        let session = DNDataManager.sharedInstance.currentTestSession
        
        if session?.contextSurvey == nil {
            let c:ContextSurvey = NSManagedObject.createIn(context: currentContext!)

            session?.contextSurvey = c
            context = c
        } else {
            context = session?.contextSurvey

        }
        for v in surveyQuestionViews {
            v.delegate = self
            
            if let buttonStack = v as? SurveyStackButtonView {
                buttonStack.hideValue()
            }
        }
        
        let file = Bundle.main.path(forResource: "survey_questions", ofType: ".json")
        let data = NSData(contentsOfFile: file!)
        json = JSON(data: data! as Data)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
        
        self.context?.cpuLoad = "\(Int(LoadMonitor.cpu_usage()))%";
        self.context?.deviceMemory = "\(Int(LoadMonitor.memory_usage()))/\(LoadMonitor.totalMemory())MB";
        DNDataManager.save();
        
//        DNDataManager.sharedInstance.takeDeviceSample();
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    func didOutputValue<T>(view: SurveyStackView, value: T) {
        
        guard let key = QuestionKey.init(rawValue: view.questionKey) else {
            return
        }
        switch  key {
        case .whoIsWith:
            let vc = self.storyboard!.instantiateViewController(withIdentifier: "surveySelect") as! SurveySelectViewController
            let items = json?["questions"][0]["questions"]
            vc.isMultiple = true
            vc.exclusiveSelectables = [0];
            vc.setChoices(values: items?.arrayObject as! Array<String>)
            if let s = self.selectedIndexes[.whoIsWith]
            {
                vc.setSelected(s);
            }
            vc.view.frame = self.view.frame;
            self.view.addSubview(vc.view);
            self.addChildViewController(vc);
            
            vc.onConfirm = {selected, ret in
                if let v = ret, v.characters.count > 1{
                    self.context?.whoIsWith = v
                    self.selectedIndexes[.whoIsWith] = selected;
                    OperationQueue.main.addOperation {

                        self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: v)
                    }
                }
            }
                
            break
        case .location:
            let vc = self.storyboard!.instantiateViewController(withIdentifier: "surveySelect") as! SurveySelectViewController
            let items = json?["questions"][1]["questions"]
            vc.setChoices(values: items?.arrayObject as! Array<String>)
            if let s = self.selectedIndexes[.location]
            {
                vc.setSelected(s);
            }
            vc.view.frame = self.view.frame;
            vc.isMultiple = false;
            self.view.addSubview(vc.view);
            self.addChildViewController(vc);
            vc.onConfirm = {selected, value in
                if let v = value , v.characters.count > 1{
                    self.context?.location = v
                    self.selectedIndexes[.location] = selected;
                    OperationQueue.main.addOperation {

                        self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: v)
                    }
                }
            }
            
            break
        case .mood:
            if let v = value as? Float {
                self.context?.mood = v
                if self.validate() {
                    
                }
            }
            
            break
        case .alertness:
            if let v = value as? Float {
                self.context?.alertness = v
                if self.validate() {
                    
                }
            }
            break
        case .recentActivity:
            let vc = self.storyboard!.instantiateViewController(withIdentifier: "surveySelect") as! SurveySelectViewController
            let items = json?["questions"][4]["questions"]
            
            vc.setChoices(values: items?.arrayObject as! Array<String>)
            if let s = self.selectedIndexes[.recentActivity]
            {
                vc.setSelected(s);
            }
            vc.view.frame = self.view.frame;
            self.view.addSubview(vc.view);
            self.addChildViewController(vc);
            
            vc.onConfirm = {selected, value in
                if let v = value, v.characters.count > 1{
                    
                    self.context?.recentActivity = v
                    self.selectedIndexes[.recentActivity] = selected;
                    OperationQueue.main.addOperation {

                        self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: v)
                    }
                }
            }
            
            break
            
        default:
            return
        }
    }
    @IBAction func surveyCompletedPressed(sender:UIButton){
        DNDataManager.save()
        AppDelegate.go(state: .testInstructions);
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
