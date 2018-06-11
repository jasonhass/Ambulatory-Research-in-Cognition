    /*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit


class TimeSetupViewController: DNViewController, UITextFieldDelegate, UIPickerViewDelegate {

    @IBOutlet var wakeupField: UITextField!
    @IBOutlet var bedField: UITextField!
    
    @IBOutlet var pickerContainer: UIView!
    @IBOutlet var timePicker: UIDatePicker!
    @IBOutlet var okayButton: UIButton!
    
    @IBOutlet weak var copyTimeButton: UIButton!
    @IBOutlet weak var nextButton: UIButton!
    
    @IBOutlet var copyDaysView: UIView!
    @IBOutlet var copyDaysDoneButton: UIButton!
    @IBOutlet var copyDaysButtons: [wkCopyButton]!
    @IBOutlet var copyDaysLabel: UILabel!
    
    @IBOutlet var weekdays: [WeekdayButton]!
    
    var currentTextField: UITextField?;
    
    var lastSelectedDay:WeekdayButton?
    var df = DateFormatter();
    
    var times:Dictionary<Int, dayTime> = Dictionary();
    var daysOfWeek:Array<String> = ["Sunday".localized(),
                                    "Monday".localized(),
                                    "Tuesday".localized(),
                                    "Wednesday".localized(),
                                    "Thursday".localized(),
                                    "Friday".localized(),
                                    "Saturday".localized()]
    var dayStrings = [
        "setup_b_popup_header_su",
        "setup_b_popup_header_mo",
        "setup_b_popup_header_tu",
        "setup_b_popup_header_we",
        "setup_b_popup_header_th",
        "setup_b_popup_header_fr",
        "setup_b_popup_header_sa"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.nextButton.isEnabled = false;
        df.dateStyle = .none;
        df.timeStyle = .short;
        
        self.okayButton.addTarget(self, action: #selector(self.timeSelected), for: .touchUpInside);
        
        self.wakeupField.inputView = pickerContainer;
        self.bedField.inputView = pickerContainer;
        
        for w in weekdays
        {
            times[w.tag] = DNDataManager.sharedInstance.getTimes(dayOfWeek: w.tag);
            
            
            if w.tag == 0
            {
                w.isSelected = true;
                lastSelectedDay = w;
                showCurrentTimes();
            }
        }
        
        refreshWeekdayButtons();
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func nextPressed(_ sender: Any)
    {
        if self.areTimesSet() == false
        {
            return;
        }
        
        
        // once the user has changed their wake/sleep schedule,
        // we need to re-create any upcoming Test Sessions,
        // on either the current or upcoming Arc.
        // 
        
        if let t = TestVisit.getCurrentVisit()
        {
            if t.getUpcomingSessions().count == 0 || t.willSessionsFitWithinSleepSchedule() == false
            {
                t.clearUpcomingSessions();
                t.createTestSessions();
                t.clearSessionNotifications();
                t.scheduleSessionNotifications();
            }
            
        }
        else if let t = TestVisit.getUpcomingVisit(includeToday:true)
        {
            if t.getUpcomingSessions().count == 0 || t.willSessionsFitWithinSleepSchedule() == false
            {
                t.clearUpcomingSessions();
                t.createTestSessions();
                t.clearSessionNotifications();
                t.scheduleSessionNotifications();
            }
        }
        
        var wakeSleepTimes = Array<dayTime>();
        
        for i in 0..<7
        {
            wakeSleepTimes.append(DNDataManager.sharedInstance.getTimes(dayOfWeek: i)!);
        }
        
        DNRestAPI.shared.sendWakeSleepSchedule(wakeSleepTimes: wakeSleepTimes);
        DNDataManager.sharedInstance.initialTimeSetupComplete()

        AppDelegate.chooseDisplay();
    }
    
    
    
    @IBAction func timeChanged(_ sender: Any)
    {
        checkBedOkayButton();
    }
    
    
    func checkBedOkayButton()
    {
        if let l = lastSelectedDay
        {
            if currentTextField == bedField
            {
                if let times = getTimes(dayOfWeek: l.tag)
                {
                    
                    if let wake = times.wake?.timeIntervalSinceStartOfDay()
                    {
                        let bed = timePicker.date.timeIntervalSinceStartOfDay();
                        
                        if (bed > wake && (bed - wake < 14400)) || (wake > bed && (wake - bed < 14400))
                        {
                            okayButton.isEnabled = false;
                            return;
                        }
                    }
                }
            }
        }
        
        okayButton.isEnabled = true;
    }
    
    
    func timeSelected()
    {
        if let l = lastSelectedDay
        {
            self.currentTextField?.text = df.string(from: timePicker.date);
            self.currentTextField?.resignFirstResponder();
            
            if currentTextField! == wakeupField
            {
                setTimes(wake: timePicker.date, bed: nil, dayOfWeek: l.tag);
            }
            else
            {
                setTimes(wake: nil, bed: timePicker.date, dayOfWeek: l.tag);
            }
            
            if times[l.tag]?.wake != nil && times[l.tag]?.bed != nil
            {
                l.isTimeSet = true;
            }
        }
        if (wakeupField.text?.isEmpty)! || (bedField.text?.isEmpty)! {
            copyTimeButton.isHidden = true
        } else {
            copyTimeButton.isHidden = false
        }
        refreshWeekdayButtons()
    }
    
    func refreshWeekdayButtons()
    {
        for b in weekdays
        {
            if let t = times[b.tag]
            {
                if t.wake != nil && t.bed != nil
                {
                    b.isTimeSet = true;
                }
                else
                {
                    b.isTimeSet = false;
                }
            }
            b.setNeedsDisplay();
        }
        
        if areTimesSet()
        {
            self.nextButton.isEnabled = true;
        }
    }
    
    func setTimes(wake:Date?, bed:Date?, dayOfWeek:Int)
    {
        var currentTimes:dayTime = dayTime();
        if let t = times[dayOfWeek]
        {
            currentTimes = t;
        }
        
        if let w = wake
        {
            currentTimes.wake = w;
        }
        
        if let b = bed
        {
            currentTimes.bed = b;
        }
        
        times[dayOfWeek] = currentTimes;
        
        if currentTimes.wake != nil && currentTimes.bed != nil
        {
            DNDataManager.sharedInstance.setTimes(wake: currentTimes.wake!, bed: currentTimes.bed!, dayOfWeek: dayOfWeek);
        }
    }
    
    func areTimesSet() -> Bool
    {
        for w in weekdays
        {
            if let t = DNDataManager.sharedInstance.getTimes(dayOfWeek: w.tag)
            {
                if t.wake == nil || t.bed == nil
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        
        return true;
    }
    
    func getTimes(dayOfWeek:Int) -> dayTime?
    {
        return times[dayOfWeek];
    }
    
    
    
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        
        self.currentTextField?.resignFirstResponder();
        self.currentTextField = textField;
        
        timePicker.date = Date().startOfDay().addingHours(hours: 12);
        
        if let l = lastSelectedDay
        {
            if let times = getTimes(dayOfWeek: l.tag)
            {
                if self.currentTextField == wakeupField && times.wake != nil
                {
                    timePicker.date = times.wake!;
                }
                else if times.bed != nil
                {
                    timePicker.date = times.bed!;
                }
            }
        }
        
        checkBedOkayButton();
        
        return true;
    }


    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder();
        return false;
    }
    
    func showCurrentTimes()
    {
        if let l = lastSelectedDay, let t = times[l.tag]
        {
            if t.wake != nil
            {
                wakeupField.text = df.string(from: t.wake!);
            }
            else
            {
                wakeupField.text = "";
            }
            
            if t.bed != nil
            {
                bedField.text = df.string(from: t.bed!);
            }
            else
            {
                bedField.text = "";
            }
        }
        else
        {
            wakeupField.text = "";
            bedField.text = "";
        }
        if (wakeupField.text?.isEmpty)! || (bedField.text?.isEmpty)! {
            copyTimeButton.isHidden = true
        } else {
            copyTimeButton.isHidden = false
        }
    }
    
    @IBAction func weekdaySelected(_ sender: WeekdayButton)
    {
        if sender == lastSelectedDay
        {
            return;
        }
        sender.isSelected = true;
        lastSelectedDay?.isSelected = false;
        lastSelectedDay = sender;
        
        currentTextField?.resignFirstResponder();
        currentTextField = nil;
        
        showCurrentTimes();
    }
    
    
    @IBAction func copyTimes(_ sender: Any)
    {
        setupCopyDaysView();
    }
    
    
    func setupCopyDaysView()
    {
        if let l = lastSelectedDay
        {
            copyDaysLabel.text = String(format: "".localized(key: dayStrings[l.tag]), daysOfWeek[l.tag]);
            
            var offset:Int = 0;
            for i in 0...5
            {
                if i == l.tag
                {
                    offset += 1;
                }
                
                let currentButton = copyDaysButtons[i];
                currentButton.tag = (i + offset);
                currentButton.isSelected = false;
                print("\(i + offset): \(daysOfWeek[i + offset])");
                currentButton.setTitle(daysOfWeek[currentButton.tag], for: .normal);
                currentButton.layoutSubviews()
                print(currentButton.title(for: .normal))
            }
            copyDaysView.frame = self.view.bounds;
            self.view.addSubview(copyDaysView);
        }
    }
    
    @IBAction func copyDaySelected(_ sender: wkCopyButton)
    {
        sender.isSelected = !sender.isSelected;
        copyDaysDoneButton.isEnabled = false;
        for b in copyDaysButtons
        {
            if b.isSelected
            {
                copyDaysDoneButton.isEnabled = true;
                break;
            }
        }
    }
    
    
    @IBAction func copyDaysDonePressed(_ sender: Any)
    {
        if let l = lastSelectedDay, let t = times[l.tag]
        {
            for b in copyDaysButtons
            {
                if b.isSelected
                {
                    setTimes(wake: t.wake, bed: t.bed, dayOfWeek: b.tag);
                }
            }
        }
        
        refreshWeekdayButtons();
        copyDaysView.removeFromSuperview();
    }
}
