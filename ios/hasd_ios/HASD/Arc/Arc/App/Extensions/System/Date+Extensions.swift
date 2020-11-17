//
// Date+Extensions.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public enum ACDateStyle:String {
	 case longWeekdayMonthDayYear = "EEEE, MMMM dd YYYY"
    case longWeekdayMonthDay = "EEEE, MMMM dd"
    case mediumWeekDayMonthDay = "EEE, MMM dd"
}

public extension Date {
    
    func localizedString(dateStyle:DateFormatter.Style = .short, timeStyle:DateFormatter.Style = .short) -> String
	{
		return DateFormatter.localizedString(from: self, dateStyle: dateStyle, timeStyle: timeStyle);
	}
    func localizedFormat(template:String = "MM/dd/yyyy h:mm a", options:Int = 0, locale:Locale? = nil ) -> String
    {
        let df = DateFormatter();
        df.locale = locale ?? Locale(identifier: Arc.shared.appController.locale.string)
        df.setLocalizedDateFormatFromTemplate(DateFormatter.dateFormat(fromTemplate: template, options: 0, locale: locale) ?? template)
//        df.doesRelativeDateFormatting = true
       let v = df.string(from: self);
        return v
    }
    func localFormat(locale:Locale? = nil) -> String {
        let df = DateFormatter();
        df.locale = locale ?? Locale(identifier: Arc.shared.appController.locale.string)
        df.dateStyle = .none
        df.timeStyle = .short
        //        df.doesRelativeDateFormatting = true
        let v = df.string(from: self);
        return v
    }
    func filenameSafeString() -> String
	{
		let df = DateFormatter();
		df.dateFormat = "yyyy-MM-dd-HH-mm-ss";
		return df.string(from: self);
	}
	
    static func random(in range: ClosedRange<Date>) -> Date {
        if range.lowerBound.timeIntervalSince1970 > range.upperBound.timeIntervalSince1970 {
            let val = Double.random(in: range.upperBound.timeIntervalSince1970...range.lowerBound.timeIntervalSince1970)
            return Date(timeIntervalSince1970: val)

        } else if range.lowerBound.timeIntervalSince1970 == range.upperBound.timeIntervalSince1970 {
            return range.lowerBound
        } else {
        
            let val = Double.random(in: range.lowerBound.timeIntervalSince1970...range.upperBound.timeIntervalSince1970)
            return Date(timeIntervalSince1970: val)

        }
    }
	
	static func time(year:Int?, month:Int?, day:Int?, hour:Int?, minute:Int?) -> Date {
		var dateComponents = DateComponents()
		
		dateComponents.year = year
		dateComponents.month = month
		dateComponents.day = day
		dateComponents.hour = hour
		dateComponents.minute = minute
		
		// Create date from components
		let userCalendar = Calendar.current // user calendar
		
		return userCalendar.date(from: dateComponents)!
	}
    /// Returns the amount of years from another date
    func years(from date: Date) -> Int {
        return Calendar.current.dateComponents([.year], from: date, to: self).year ?? 0
    }
    /// Returns the amount of months from another date
    func months(from date: Date) -> Int {
        return Calendar.current.dateComponents([.month], from: date, to: self).month ?? 0
    }
    /// Returns the amount of weeks from another date
    func weeks(from date: Date) -> Int {
        return Calendar.current.dateComponents([.weekOfMonth], from: date, to: self).weekOfMonth ?? 0
    }
    /// Returns the amount of days from another date
    func days(from date: Date) -> Int {
        return Calendar.current.dateComponents([.day], from: date, to: self).day ?? 0
    }
    /// Returns the amount of hours from another date
    func hours(from date: Date) -> Int {
        return Calendar.current.dateComponents([.hour], from: date, to: self).hour ?? 0
    }
    /// Returns the amount of minutes from another date
    func minutes(from date: Date) -> Int {
        return Calendar.current.dateComponents([.minute], from: date, to: self).minute ?? 0
    }
    /// Returns the amount of seconds from another date
    func seconds(from date: Date) -> Int {
        return Calendar.current.dateComponents([.second], from: date, to: self).second ?? 0
    }
    /// Returns the amount of nanoseconds from another date
    func nanoseconds(from date: Date) -> Int {
        return Calendar.current.dateComponents([.nanosecond], from: date, to: self).nanosecond ?? 0
    }
    /// Returns the a custom time interval description from another date
    func offset(from date: Date) -> String {
        if years(from: date)   > 0 { return "\(years(from: date))y"   }
        if months(from: date)  > 0 { return "\(months(from: date))M"  }
        if weeks(from: date)   > 0 { return "\(weeks(from: date))w"   }
        if days(from: date)    > 0 { return "\(days(from: date))d"    }
        if hours(from: date)   > 0 { return "\(hours(from: date))h"   }
        if minutes(from: date) > 0 { return "\(minutes(from: date))m" }
        if seconds(from: date) > 0 { return "\(seconds(from: date))s" }
        if nanoseconds(from: date) > 0 { return "\(nanoseconds(from: date))ns" }
        return ""
    }
    func dayOfMonth(calendar:Calendar =  Calendar(identifier: .gregorian)) -> Int {
        
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: self);
        return components.day ?? -1
    }
    
	
	/// Returns a tuple where the first index is the weekDay and the second is the day of that month
	/// - Parameter gregorian: The calendar used to provide values
    func weekdayOfMonth(calendar:Calendar =  Calendar(identifier: .gregorian)) -> (Int,Int) {
        
        var components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: self);
        return (weekDay: components.weekday ?? -1, day: components.day ?? -1)
        
    }
	// returns a count of "days" until or since the given date.
	// This ignores the actual time of the Dates, and instead gives you a general count of days.
	// So for instance, a date of 01/02/2017 0:00:01 still counts as 1 day since 01/01/2017 23:59:59
	
    func daysSince(date:Date) -> Int
	{
        return Calendar.current.dateComponents([.day], from: date.startOfDay(), to: self.startOfDay()).day ?? 0

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
    func isToday() -> Bool {
        let today = Date()
        if self > today.startOfDay() && self < today.endOfDay() {
            return true
        }
        return false
    }
    func isTomorrow() -> Bool {
        let today = Date()
        if self > today.endOfDay() && self < today.addingDays(days: 1).endOfDay() {
            return true
        }
        return false
    }
    func addingYears(years:Int) -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.month = components.year! + years;
        return calendar.date(from: components)!;
    }
    func addingMonths(months:Int) -> Date
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        var components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self);
        components.month = components.month! + months;
        return calendar.date(from: components)!;
    }
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

public extension ClosedRange where Bound == Date {
    func randomElement() -> Date? {
        return Date.random(in: self)
    }
    
    subscript(index:Double) -> Date {
        get {
            let min = lowerBound.timeIntervalSince1970
            let max = upperBound.timeIntervalSince1970
            
            return Date(timeIntervalSince1970:  min + (index * (max - min)))
        }
        set {
            
        }
    }
}

public extension TimeInterval {
    enum Unit : TimeInterval, RawRepresentable {
		case second = 1.0, minute = 60.0, hour = 3600.0, day = 86400.0, week = 604800.0
		
		static public func + (lhs:Unit, rhs:TimeInterval) -> TimeInterval {
			return lhs.rawValue + rhs
		}
		static public func * (lhs:Unit, rhs:TimeInterval) -> TimeInterval {
			return lhs.rawValue * rhs + 1.0
		}
		static public func - (lhs:Unit, rhs:TimeInterval) -> TimeInterval {
			return lhs.rawValue - rhs
		}
		static public func / (lhs:Unit, rhs:TimeInterval) -> TimeInterval {
			return lhs.rawValue / rhs
		}
	}
    func format(units:NSCalendar.Unit = [.day, .hour, .minute, .second, .nanosecond]) -> String? {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = units
        formatter.unitsStyle = .abbreviated
        formatter.maximumUnitCount = 1
        return formatter.string(from: self)
    }
    
    func localizedInterval() -> String
    {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.hour, .minute]
        formatter.unitsStyle = .short
        formatter.maximumUnitCount = 3
        return formatter.string(from: self)!
    }
}

public extension DateComponentsFormatter
{
    
    static func localizedString(forUnit unit:NSCalendar.Unit, style:DateComponentsFormatter.UnitsStyle) -> String
    {
        
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [unit]
        formatter.unitsStyle = style
        formatter.maximumUnitCount = 1;
        if let str = formatter.string(from: 0)
        {
            let localizedStr = str.replacingOccurrences(of: "0", with: "").trimmingCharacters(in: .whitespaces);
            return localizedStr;
        }
        
        return "";
    }
    
}

public extension DateFormatter {

    static var h24mm:DateFormatter = {
        let formatter = DateFormatter()
        formatter.defaultDate = Date()
        formatter.setLocalizedDateFormatFromTemplate("HH:mm")
        return formatter
    }()

    static var h12mm:DateFormatter = {
        let formatter = DateFormatter()
        formatter.defaultDate = Date()
        formatter.setLocalizedDateFormatFromTemplate("hh:mm a")
        return formatter
    }()
}
