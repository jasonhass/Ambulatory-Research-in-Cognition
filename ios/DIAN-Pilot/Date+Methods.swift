//
//  Date+methods.swift
//  ARC
//
//  Created by Michael Votaw on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation

extension Date
{
    
    func localizedString(dateStyle:DateFormatter.Style = .short, timeStyle:DateFormatter.Style = .short) -> String
    {
        return DateFormatter.localizedString(from: self, dateStyle: dateStyle, timeStyle: timeStyle);
    }
    
    func filenameSafeString() -> String
    {
        let df = DateFormatter();
        df.dateFormat = "yyyy-MM-dd-HH-mm-ss";
        return df.string(from: self);
    }
    
    
    // returns a count of "days" until or since the given date.
    // This ignores the actual time of the Dates, and instead gives you a general count of days.
    // So for instance, a date of 01/02/2017 0:00:01 still counts as 1 day since 01/01/2017 23:59:59
    
    func daysSince(date:Date) -> Int
    {
        let diff = self.startOfDay().timeIntervalSince(date.startOfDay());
        
        return Int( floor(diff / (24 * 60 * 60)));
    }
    
    // creates a Date object with the same year, month, and day components of the given date, with a time of 00:00:00
    func startOfDay() -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.hour = 0;
        components.minute = 0;
        components.second = 0;

        return calendar.date(from: components)!;
    }
    
    
    // creates a Date object with the same year, month, and day components of the given date, with a time of 23:59:59
    
    func endOfDay() -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.hour = 23;
        components.minute = 59;
        components.second = 59;
        
        return calendar.date(from: components)!;
    }
    
    // creates a Date by adding the given number of days. Pass in a negative value to subtract days.
    // Maintains same hour/minute/second values.
    
    func addingDays(days:Int) -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.day = components.day! + days;
        return calendar.date(from: components)!;
    }
    
    func addingHours(hours:Int) -> Date
    {
        return self.addingTimeInterval(TimeInterval(hours * 60 * 60));
    }
    
    func addingMinutes(minutes:Int) -> Date
    {
        return self.addingTimeInterval(TimeInterval(minutes * 60));
    }
    
    func addingWeeks(weeks:Int) -> Date
    {
        return self.addingTimeInterval(TimeInterval(weeks * 7 * 24 * 60 * 60));
    }
    
    // given two Date objects, it takes the year,month,day components from the first, and the hour, minute,second components from the second.
    
    
    static func combine(day:Date, time:Date) -> Date
    {
        let dayComponents = NSCalendar.current.dateComponents([.day, .month, .year], from: day);
        let timeComponents = NSCalendar.current.dateComponents([ .second, .minute, .hour, .timeZone], from: time);
        
        var bothComponents = DateComponents();
        bothComponents.day = dayComponents.day;
        bothComponents.month = dayComponents.month;
        bothComponents.year = dayComponents.year;
        bothComponents.second = timeComponents.second;
        bothComponents.minute = timeComponents.minute;
        bothComponents.hour = timeComponents.hour;
        bothComponents.timeZone = timeComponents.timeZone
        
        let newDate = NSCalendar.current.date(from: bothComponents);
        return newDate!;
    }
    func JSONDate() -> Double{
        return self.timeIntervalSince1970

    }
    
    
    // returns only the time component of the Date as a TimeInterval.
    // So if the Date was 1/1/2017 8:15:00 AM, this would return 29700
    func timeIntervalSinceStartOfDay() -> TimeInterval
    {
        return self.timeIntervalSince(self.startOfDay());
    }
    
    func roundedTo(minutes:Int = 5) -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.minute = (components.minute! / minutes) * minutes;
        let newDate = NSCalendar.current.date(from: components);
        return newDate!;
    }
}
