//
// WeekDay.swift
//



import Foundation
public enum WeekDay : Int64, Equatable, Comparable, Strideable, CaseIterable {
	case sunday, monday, tuesday, wednesday, thursday, friday, saturday, none
	
    static public func from(rawValue:Int64) -> WeekDay {
        
        let val = rawValue % 7
        
        switch val {
        case 0:     return sunday
        case 1, -6: return monday
        case 2, -5: return tuesday
        case 3, -4: return wednesday
        case 4, -3: return thursday
        case 5, -2: return friday
        case 6, -1: return saturday
        default:
            fatalError("invalid input value")
        }
    }
    
    static public func fromString(day:String) -> WeekDay {
		let day = day.lowercased()
		switch day {
		case "sunday", "sun", "1":
			return .sunday
		case "monday", "mon", "2":
			return .monday
		case "tuesday", "tue", "3":
			return .tuesday
		case "wednesday", "wed", "4":
			return .wednesday
		case "thursday", "thur", "5":
			return .thursday
		case "friday", "fri", "6":
			return .friday
		case "saturday", "sat", "7":
			return .saturday
		default:
			return .none
		}
	}
	public func toString() -> String {
		switch self {
		case .sunday:
			return "sunday"
		case .monday:
			return "monday"
		case .tuesday:
			return "tuesday"
		case .wednesday:
			return "wednesday"
		case .thursday:
			return "thursday"
		case .friday:
			return "friday"
		case .saturday:
			return "saturday"
		
		case .none:
			return ""
		}
	}
	static public func getDayOfWeek(_ today:Date) -> WeekDay {
		
		let myCalendar = Calendar(identifier: .gregorian)
		let weekDay = myCalendar.component(.weekday, from: today)
		return fromString(day: "\(weekDay)")
	}
	static public func < (lhs: WeekDay, rhs: WeekDay) -> Bool {
		return lhs.rawValue < rhs.rawValue
	}
	public func distance(to other: WeekDay) -> Int64 {
		return (other.rawValue - rawValue) % 7
	}
	public func advanced(by n: Int64) -> WeekDay {
        return WeekDay.from(rawValue: (rawValue + n) % 7)
	}
}
