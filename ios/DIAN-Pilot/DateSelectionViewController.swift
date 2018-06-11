/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
        TestVisit.createAllVisits(startingID: DNDataManager.sharedInstance.visitCount, startDate: self.selectedDate);
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
            
            cell.weekdayLabel.text = weekNames[indexPath.item].localized()
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
