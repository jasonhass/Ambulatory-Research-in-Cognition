//
//  ArcConfirmViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/17/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class ArcConfirmViewController: DNViewController, UIPickerViewDelegate, UIPickerViewDataSource
{
    
    @IBOutlet weak var pageTitle: UILabel!
    @IBOutlet weak var pageHR: UIView!
    
    
    
    @IBOutlet weak var adjustHeight: NSLayoutConstraint!
    @IBOutlet weak var confirmHeight: NSLayoutConstraint!
    @IBOutlet weak var startLabel: UILabel!
    @IBOutlet weak var adjustDateButton: UIButton!
    @IBOutlet weak var confirmButton: UIButton!
    
    @IBOutlet weak var noTestsText: UILabel!
    @IBOutlet weak var confirmText: UILabel!
    
    @IBOutlet var dateConfirmView: UIView!
    @IBOutlet weak var dateConfirmPicker: UIPickerView!
    
    var weekPickerChoices:Array<(date:Date, label:String)> = Array();
    
    var upcomingArc:TestArc?
    
    override func viewDidLoad() {
        super.viewDidLoad();
        
        //if we don't already have an upcoming Arc setup, we need to create one.
        
        if let arc = TestArc.getUpcomingArc()
        {
            upcomingArc = arc;
        }
        else
        {
            var nextDate:Date = Date();
            
            if let previousArc = TestArc.getMostRecentArc()
            {
                nextDate = (previousArc.arcStartDate! as Date).addingWeeks(weeks: 12);
            }
            
            let newArc = TestArc.createArc(forDate: nextDate);
            newArc.clearUpcomingSessions();
            newArc.createTestSessions();
            
            newArc.clearSessionNotifications();
            newArc.scheduleSessionNotifications();
            upcomingArc = newArc;
        }
        
        
        if let arc = upcomingArc
        {
            // are we one day away?
            if (arc.userStartDate! as Date).daysSince(date: Date()) <= 1
            {
                noTestsText.isHidden = false;
                confirmText.isHidden = true;
                adjustHeight.constant = 0;
                adjustDateButton.isHidden = true;
                confirmHeight.constant = 0;
                confirmButton.isHidden = true;
                if arc.hasScheduledNotifications == false
                {
                    arc.scheduleSessionNotifications();
                }
            }
            else
            {
                if arc.hasConfirmedDate
                {
                    noTestsText.isHidden = false;
                    confirmText.isHidden = true;
                    confirmButton.isHidden = true;
                    confirmHeight.constant = 0;
                    pageTitle.isHidden = true;
                    pageHR.isHidden = true;
                }
                else
                {
                    noTestsText.isHidden = true;
                    confirmText.isHidden = false;
                    
                    if arc.hasScheduledDateReminder() == false
                    {
                        arc.scheduleDateRemdinderNotification();
                    }
                    
                    if arc.hasScheduledConfirmationReminders() == false
                    {
                        arc.scheduleConfirmationReminders();
                    }
                }
            }
        }
        
        updateStartLabel();
    }
    
    func updateStartLabel()
    {
        if let arc = upcomingArc
        {
            let startDate = DateFormatter.localizedString(from: arc.userStartDate! as Date, dateStyle: .short, timeStyle: .none);
            startLabel.text = String(format: NSLocalizedString("Your next test will begin on %@", comment: ""), startDate);
        }
    }
    
    
    @IBAction func adjustDateTapped(_ sender: Any)
    {
     
        weekPickerChoices.removeAll();
        var select:Int = 0;
        if let arc = upcomingArc
        {
            // get possible week choices, +/- 2 weeks.
            let start = arc.arcStartDate! as Date;
            
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
        
        if let arc = upcomingArc
        {
            arc.setStartDate(date: selectedDate);
            arc.clearSessionNotifications();
            arc.clearUpcomingSessions();
            arc.createTestSessions();
            arc.scheduleSessionNotifications();
        }
     
        updateStartLabel();
    }
    
    @IBAction func confirmTapped(_ sender: Any)
    {
        if let arc = upcomingArc
        {
            arc.hasConfirmedDate = true;
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
