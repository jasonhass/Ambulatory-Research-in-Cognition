//
//  DateSelectionViewController.swift
//  ARC
//
//  Created by Michael Votaw on 5/15/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

struct CalendarItem
{
    var date:Date = Date();
    var selectable:Bool = true;
    var dayOfMonth:Int = 1;
    
}

class DateSelectionViewController: DNViewController, UICollectionViewDelegate, UICollectionViewDataSource {
    @IBOutlet weak var previousMonthButton: UIButton!

    @IBOutlet weak var monthLabel: UILabel!
    @IBOutlet weak var nextMonthButton: UIButton!
    
    @IBOutlet weak var monthCollectionView: UICollectionView!
    
    var currentMonth: Date = Date();
    var calendar:Calendar = Calendar(identifier: .gregorian);
    var calendarDates:Dictionary<Int,CalendarItem> = Dictionary();
    var formatter = DateFormatter();
    var selectedDate:Date = Date();
    var selectedIndex:IndexPath?;
    
    var minDate:Date = Date();
    var weekNames:Array<String> = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.monthCollectionView.register(UINib(nibName: "DayOfMonthCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "DayOfMonthCollectionViewCell");
        self.monthCollectionView.register(UINib(nibName: "WeekDayCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "WeekDayCollectionViewCell");
        self.monthCollectionView.allowsSelection = true;
        self.figureOutDays();
    }

    func figureOutDays()
    {
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: currentMonth);
        components.day = 1;
        calendarDates.removeAll();
        
        
        //strip the hours/min/seconds from minDate
        var minDateComponents = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: minDate);
        minDate = calendar.date(from: minDateComponents)!;
        
        selectedIndex = nil;
        if let startOfMonthDate = calendar.date(from: components)
        {
            let startOfMonthComponents = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: startOfMonthDate);
            
            let dayRange = calendar.range(of: .day, in: .month, for: startOfMonthDate)!
            
            let selectedDayComponents = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: selectedDate);
            
            
            
            for i in dayRange.lowerBound..<dayRange.upperBound
            {
                let w:Int = (startOfMonthComponents.weekday! - 1) + (i - 1);
                components.day = i;
                let d = calendar.date(from: components);
                var item = CalendarItem();
                item.dayOfMonth = Int(i);
                item.date = d!;
                item.selectable = d!.compare(minDate) != .orderedAscending
                calendarDates[w] = item;
                if selectedDayComponents.year == components.year && selectedDayComponents.month == components.month && selectedDayComponents.day == i
                {
                    selectedIndex = IndexPath(item:w, section: 1);
                }
            }
            
            
        }
        
        self.monthLabel.text = "\(formatter.monthSymbols[components.month! - 1]) \(components.year!)".capitalized;
        
        self.monthCollectionView.reloadData();
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func previousMonthTapped(_ sender: Any)
    {
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: currentMonth);
        components.day = 1;
        components.month = components.month! - 1;
        
        if let startOfMonthDate = calendar.date(from: components)
        {
            self.currentMonth = startOfMonthDate;
            self.figureOutDays();
        }
    }
    
    
    @IBAction func nextMonthTapped(_ sender: Any)
    {
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: currentMonth);
        components.day = 1;
        components.month = components.month! + 1;
        
        if let startOfMonthDate = calendar.date(from: components)
        {
            self.currentMonth = startOfMonthDate;
            self.figureOutDays();
        }
    }
    
    @IBAction func confirmPressed(_ sender: Any)
    {
        TestArc.createAllArcs(startingID: DNDataManager.sharedInstance.arcCount, startDate: self.selectedDate);
        AppDelegate.go(state: .setupTime);
    }
    
    
    
    //MARK: - Collection View Delegate
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 2;
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if section == 0
        {
            return 7;
        }
        
        return 42;
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if indexPath.section == 0
        {
            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "WeekDayCollectionViewCell", for: indexPath) as! WeekDayCollectionViewCell;
            
            cell.weekdayLabel.text = NSLocalizedString(weekNames[indexPath.item], comment: "")
            return cell;
            
        }
        
        //else it's a day of month cell
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "DayOfMonthCollectionViewCell", for: indexPath) as! DayOfMonthCollectionViewCell;
        
        cell.backgroundColor = UIColor.clear;
        if let d = calendarDates[indexPath.item]
        {
            cell.dateLabel.text = "\(d.dayOfMonth)";
            cell.isSelectable = d.selectable;
        }
        else
        {
            cell.dateLabel.text = "";
            cell.isSelectable = false;
        }
        
        if let s = selectedIndex, s == indexPath
        {
            collectionView.selectItem(at: s, animated: false, scrollPosition: .top);
            cell.isSelected = true;
            cell.updateSelected();
        }
        return cell;
    }
    
    
    func collectionView(_ collectionView: UICollectionView, shouldSelectItemAt indexPath: IndexPath) -> Bool {
        if indexPath.section == 1 && calendarDates[indexPath.item] != nil && calendarDates[indexPath.item]!.selectable
        {
            return true;
        }
        
        return false;
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath)
    {
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: currentMonth);

        if let d = calendarDates[indexPath.item]
        {
            self.selectedDate = d.date;
            figureOutDays();
        }
    }
    
    
}
