        /*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class ArcConfirmViewController: DNViewController, UIPickerViewDelegate, UIPickerViewDataSource
{
    
    @IBOutlet weak var pageTitle: UILabel!
    @IBOutlet weak var pageHR: UIView!
    
    @IBOutlet weak var questionsLabel: DNLabel!
    
    
    @IBOutlet weak var adjustHeight: NSLayoutConstraint!
    @IBOutlet weak var confirmHeight: NSLayoutConstraint!
    @IBOutlet weak var startLabel: UILabel!
    @IBOutlet weak var adjustDateButton: DNButton!
    @IBOutlet weak var confirmButton: UIButton!
    
    @IBOutlet weak var noTestsText: UILabel!
    @IBOutlet weak var confirmText: UILabel!
    
    @IBOutlet var dateConfirmView: UIView!
    @IBOutlet weak var dateConfirmPicker: UIPickerView!
    
    var weekPickerChoices:Array<(date:Date, label:String)> = Array();
    
    var upcomingVisit:TestVisit?
    
    override func viewDidLoad() {
        super.viewDidLoad();
        
        //if we don't already have an upcoming Arc setup, we need to create one.
        
        if let visit = TestVisit.getUpcomingVisit()
        {
            upcomingVisit = visit;
        }
        else
        {
            var nextDate:Date = Date();
            
            if let previousArc = TestVisit.getMostRecentVisit()
            {
                nextDate = (previousArc.visitStartDate! as Date).addingWeeks(weeks: previousArc.getWeeksUntilNextVisit() ?? 0);
            }
            
            let newVisit = TestVisit.createVisit(forDate: nextDate);
            newVisit.clearUpcomingSessions();
            newVisit.createTestSessions();
            
            newVisit.clearSessionNotifications();
            newVisit.scheduleSessionNotifications();
            upcomingVisit = newVisit;
        }
        
        
        if let visit = upcomingVisit
        {
            // are we one day away?
            if (visit.userStartDate! as Date).daysSince(date: Date()) <= 1
            {
                noTestsText.isHidden = false;
                confirmText.isHidden = true;
                adjustHeight.constant = 0;
                adjustDateButton.isHidden = true;
                confirmHeight.constant = 0;
                confirmButton.isHidden = true;
                pageTitle.isHidden = true;
                pageHR.isHidden = true;
                if visit.hasScheduledNotifications == false
                {
                    visit.scheduleSessionNotifications();
                }
            }
            else
            {
                if visit.hasConfirmedDate
                {
                    noTestsText.isHidden = false;
                    confirmText.isHidden = true;
                    confirmButton.isHidden = true;
                    confirmHeight.constant = 0;
                    pageTitle.isHidden = true;
                    pageHR.isHidden = true;
                    adjustDateButton.translationKey = "notest_adjust";
                    
                }
                else
                {
                    noTestsText.isHidden = true;
                    confirmText.isHidden = false;
                    questionsLabel.isHidden = true;
                    adjustDateButton.translationKey = "adjust_start_date";
                    if visit.hasScheduledDateReminder() == false
                    {
                        visit.scheduleDateRemdinderNotification();
                    }
                    
                    if visit.hasScheduledConfirmationReminders() == false
                    {
                        visit.scheduleConfirmationReminders();
                    }
                }
            }
        }
        
        updateStartLabel();
    }
    
    func updateStartLabel()
    {
        if let visit = upcomingVisit
        {
            let startDate = DateFormatter.localizedString(from: visit.userStartDate! as Date, dateStyle: .short, timeStyle: .none);
            startLabel.text = String(format: "Your next test will begin on {DATE}".localized(key: "confirm_body1").replacingOccurrences(of: "{DATE}", with: startDate))
        }
    }
    
    
    @IBAction func adjustDateTapped(_ sender: Any)
    {
     
        weekPickerChoices.removeAll();
        var select:Int = 0;
        if let visit = upcomingVisit
        {
            // get possible week choices, +/- 2 weeks.
            let start = visit.visitStartDate! as Date;
            
            for i:Int in -2...2
            {
                let d = start.addingDays(days: i * 7);
                
                // we don't want to include past weeks (or even today)
                if d.compare(Date()) == .orderedDescending
                {
                    let label = DateFormatter.localizedString(from: d, dateStyle: .short, timeStyle: .none);
                    weekPickerChoices.append(  (date:d, label: label));
                    
                    if i == 0
                    {
                        select = weekPickerChoices.count - 1;
                    }
                }
            }
            
            
            // show picker.
            dateConfirmView.frame = self.view.frame;
            self.view.addSubview(dateConfirmView);
            
            dateConfirmPicker.reloadAllComponents();
            dateConfirmPicker.selectRow(select, inComponent: 0, animated: false);
        }
    }
    
    @IBAction func closePicker(_ sender: Any)
    {
        
        dateConfirmView.removeFromSuperview();
        
        let selectedIndex = dateConfirmPicker.selectedRow(inComponent: 0);
        
        let selectedDate = weekPickerChoices[selectedIndex].date;
        
        if let visit = upcomingVisit
        {
            visit.setStartDate(date: selectedDate);
            visit.clearSessionNotifications();
            visit.clearUpcomingSessions();
            visit.createTestSessions();
            visit.scheduleSessionNotifications();
        }
     
        updateStartLabel();
    }
    
    @IBAction func confirmTapped(_ sender: Any)
    {
        if let visit = upcomingVisit
        {
            visit.hasConfirmedDate = true;
            DNDataManager.save();
            AppDelegate.go(state: .surveyUpgradePhone);
        }
    }

    
    //MARK: - UIPickerViewDelegate
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return weekPickerChoices.count;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return weekPickerChoices[row].label;
    }
}
