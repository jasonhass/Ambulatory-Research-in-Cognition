/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
import CoreData
class ChronotypeSurveyViewController: DNViewController, SurveyStackViewDelegate, UITextFieldDelegate  {
    
    private enum QuestionKey:Int {
        case none = 0, shiftWorker, workDays, workWake, workSleep, workFreeWake, workFreeSleep
        
        
    }
    
    @IBOutlet weak var workDaysField: BorderTextField?
    
    @IBOutlet var surveyQuestionViews:[SurveyStackView]!
    private var chronotype:ChronotypeSurvey? = nil
    @IBOutlet var doneButton:UIButton!
    var currentContext:NSManagedObjectContext?
    
    @IBOutlet weak var workDaysView: SurveyStackInputView!
    
    private var selectedDates:Dictionary<QuestionKey,Date> = Dictionary();
    private var wasShiftWorker:Bool?;
    
    var workDays:Int = 0;
    
    @discardableResult func validate() -> Bool{
        var isValid = true
        //Check if contextSurvey Exists
        guard chronotype != nil else {
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
            case .shiftWorker:
                if wasShiftWorker == nil {
                    isValid = false
                    break main
                }
                
            case .workDays:
                if (chronotype?.numWorkDays)! < 0 || (chronotype?.numWorkDays)! > 7 {
                    isValid = false
                    break main
                }
            case .workWake:
                if chronotype?.workWakeTime == nil {
                    isValid = false
                    break main
                }
                
            case .workSleep:
                if chronotype?.workSleepTime == nil {
                    isValid = false
                    break main
                }
                
            case .workFreeWake:
                if chronotype?.workFreeWakeTime == nil {
                    isValid = false
                    break main
                }
                
            case .workFreeSleep:
                if chronotype?.workFreeSleepTime == nil {
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
        if let c = session?.chronotypeSurvey {
            chronotype = c

        } else {
            let c:ChronotypeSurvey = NSManagedObject.createIn(context: currentContext!)
            session?.chronotypeSurvey = c
            DNDataManager.save();
            chronotype = c
        }
        //TODO: Get session and apply chronotype to it OR Get existing chronotype from session
        // Do any additional setup after loading the view.
        for v in surveyQuestionViews {
            v.delegate = self
            print(v.questionKey)
            if let buttonStack = v as? SurveyStackButtonView {
                buttonStack.hideValue()
            }
        }
        
        workDaysField?.delegate = self;
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        

        self.chronotype?.cpuLoad = "\(Int(LoadMonitor.cpu_usage()))%";
        self.chronotype?.deviceMemory = "\(Int(LoadMonitor.memory_usage()))/\(LoadMonitor.totalMemory())MB";
        DNDataManager.save();
        
        
//        DNDataManager.sharedInstance.takeDeviceSample();
        
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
        case .shiftWorker:
            if let v = value as? Bool {
                self.wasShiftWorker = v;
                chronotype?.wasShiftWorker = v
                if self.validate() {
                    
                }
            }
            else
            {
                self.wasShiftWorker = nil;
                chronotype?.wasShiftWorker = false;
                self.validate();
            }
            
            break
        case .workDays:
            if let v = Int(value as! String) {
                chronotype?.numWorkDays = Int64(v)
                if self.validate() {
                    
                }
            }
            break
        case .workWake:
            let datePicker:DatePickerAlertViewController = GetAlertController()
            datePicker.datePickerMode = .time
            if let d = selectedDates[.workWake]
            {
                datePicker.defaultDate = d;
            }
            
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.chronotype?.workWakeTime = time
                        self.selectedDates[.workWake] = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }

                       
                    }
                }
            })
            
            break
        case .workSleep:
            let datePicker:DatePickerAlertViewController = GetAlertController()
            datePicker.datePickerMode = .time
            if let d = selectedDates[.workSleep]
            {
                datePicker.defaultDate = d;
            }
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.chronotype?.workSleepTime = time
                        self.selectedDates[.workSleep] = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }

                    }
                }

            })
                        break
        case .workFreeWake:
            let datePicker:DatePickerAlertViewController = GetAlertController()
                datePicker.datePickerMode = .time
            if let d = selectedDates[.workFreeWake]
            {
                datePicker.defaultDate = d;
            }
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.chronotype?.workFreeWakeTime = time
                        self.selectedDates[.workFreeWake] = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)
                            
                        }

                    }
                }

            })
                        break
        case .workFreeSleep:
            let datePicker:DatePickerAlertViewController = GetAlertController()
                datePicker.datePickerMode = .time
            if let d = selectedDates[.workFreeSleep]
            {
                datePicker.defaultDate = d;
            }
            self.present(datePicker, animated: false, completion: {
                
                datePicker.onConfirm = { v in
                    if let t = v as? Date {
                        let time = self.StringFromTime(date: t)
                        self.chronotype?.workFreeSleepTime = time
                        self.selectedDates[.workFreeSleep] = t;
                        OperationQueue.main.addOperation {
                            self.ConfigureButtonStack(view: view as! SurveyStackButtonView, title: time)

                        }
                    }
                }
            })
            
            break
            
        default:
            return
        }
    }
    @IBAction func surveyCompletedPressed(sender:UIButton){
        
        DNDataManager.save()
        
        if let session = DNDataManager.sharedInstance.currentTestSession
        {
            session.hasTakenChronotype = true;
            DNDataManager.save();
            if session.testVisit!.hasTakenWakeSurvey(forDay: Date()) == false
            {
                AppDelegate.go(state: .surveyWake);
            }
            else
            {
                AppDelegate.go(state: .surveyContext);
            }
        }
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
    
    func closeTextField()
    {
        workDaysField?.resignFirstResponder();
    }
    
    
    //MARK: - UITextFieldDelegate
    
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool
    {
        
        let picker:PickerAlertViewController = GetAlertController()
        
        var workDaysList:Array<String> = Array();
        for i in 0...7
        {
            workDaysList.append("\(i)");
        }
        
        picker.columns = [workDaysList];
        
        
        picker.defaultRows = [workDays];
        self.present(picker, animated: false, completion: {
            picker.onConfirm = { v in
                self.workDaysField?.resignFirstResponder();
                if let t = v as? Array<String>, let c = Int(t[0]) {
                    self.workDaysField?.text = "\(c)";
                    self.workDays = c;
                    self.didOutputValue(view: self.workDaysView, value:self.workDaysField?.text ?? "");
                }
            }
        });
        
        return false;
    }
    
}
