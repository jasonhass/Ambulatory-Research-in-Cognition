//
// EXPhase.swift
//



import Foundation
import Arc
enum EXPhase : String,  Phase {
	typealias RawValue = String
	
	
	case none, baseline, active
	
	
	static func from(startDate:Date, currentDate:Date) -> EXPhase {
		return .none
	}
	static func from(weeks:Int) -> EXPhase {
		switch weeks {
		case 0:
			return .baseline
		default:
            return .active

		}
		
	}
    
    static func from(days:Int) -> EXPhase {
        switch days {
		case 0 ... 7:
			return .baseline
        default:
            return .active
            
        }
        
    }
    
	static func from(studyId:Int) -> EXPhase {
		switch studyId {
		case 0:
			return .baseline
		default:
			return .active
		}
		
	}
	func daysPerStudy() -> Int {
		switch self {
		case .baseline:
			return 8
		case .active:
			return 7
		default:
			return 0
		}
	}
	func totalSessionCount() ->  Int {
		switch self {
		case .baseline:
			//the first day contains 1 test instead of 4
			//So subtracting 3 sessions will account for that difference
			return daysPerStudy() * sessionsPerDay() - 3
		case .active:
			return daysPerStudy() * sessionsPerDay()
		default:
			return 0
		}
	}
	func sessionsPerDay(currentDay:Int? = nil) -> Int {
		switch self {
		case .baseline:
			if let day = currentDay, day == 0 {
				return 1
			}
			return 4
		case .active:
			return 4
		default:
			return 0
		}
	}
    
    static func startingSessionId(forStudyId studyId:Int) -> Int
    {
        var sessionId:Int = 0;
        
        if studyId == 0
        {
            return 0;
        }
        
        for index in 0..<studyId
        {
            let phase = EXPhase.from(studyId: index);
			
            sessionId += phase.totalSessionCount();
			
        }
        
        return sessionId;
    }
    
	func statesForSession(week:Int, day:Int, session:Int) -> [State] {
		switch self {
		case .baseline:
			if day == 0 && session == 0 {
				return [EXState.chronotype, EXState.wake, EXState.context] + EXState.tests.shuffled()
			} else {
				return [EXState.context] + EXState.tests.shuffled()
			}
			
		case .active:
            
            //Use inter arc weeks to determine if we're starting on a new study period
            //Study periods in this app are 7 days long.
			
            return [EXState.context] + EXState.tests.shuffled()

		
		default:
			return []
		}
		
	}
	
	func statesFor(session: Session) -> [State] {
		return [EXState.context] + EXState.tests.shuffled()
	}
}
