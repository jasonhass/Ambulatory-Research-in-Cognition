//
// StudyProgress.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

public struct StudyProgress : Codable {
    public var response: Response?

    public struct Response : Codable {
        public var success:Bool
        public var study_progress:Progress?
        
        public struct Progress : Codable {
            public var cycle_count:Int
            public var current_cycle:Int
            public var joined_date:TimeInterval
            public var finish_date:TimeInterval
        }
    }
}

public struct CycleProgress : Codable {
    public var response: Response?
    
    public struct Response : Codable {
        public var success:Bool
        public var cycle_progress:Progress?
        
        public struct Progress : Codable {
            public var cycle:Int
            public var start_date:TimeInterval?
            public var end_date:TimeInterval
            public var day_count:Int
            public var current_day:Int?
            public var days:[Day]
            
            public struct Day : Codable {
                public var start_date:TimeInterval?
                public var end_date:TimeInterval?
                public var day:Int
                public var cycle:Int
                public var sessions:[Session]
                
                public struct Session : Codable {
                    public var session_index:Int
                    public var session_date:TimeInterval
                    public var status:String
                    public var percent_complete:Int
                }
            }
        }
    }
}

// creating dictionary for the session and session status
public extension CycleProgress.Response{
    var asDictionary: [Int:String] {
        guard let progress = cycle_progress else { return [:]}
        var result: [Int:String] = [:]
        
        for day in progress.days {
            for session in day.sessions
            {
                result[session.session_index] = session.status
            }
        }
        return result
    }
}

public struct DayProgress : Codable {
    
    public struct Response : Codable {
        public var success:Bool
        public var day_progress:Progress
        
        public struct Progress : Codable {
            public var start_date:TimeInterval
            public var end_date:TimeInterval
            public var day:Int
            public var cycle:Int
            public var sessions:[Session]
            
            public struct Session : Codable {
                public var session_index:Int
                public var session_date:TimeInterval
                public var status:String
                public var percent_complete:Int
            }
        }        
    }
}

public struct CycleProgressRequestData:Codable {
    
    var cycle:Int?   //index of the cycle to retrieve
    
    init(cycle:Int?) {
        self.cycle = cycle
    }
}

public struct DayProgressRequestData:Codable {
    
    var cycle:Int?   //index of the cycle to retrieve
    var day:Int?     //index of the day of the cycle
    
    init(cycle:Int?, day:Int?) {
        self.cycle = cycle
        self.day = day
    }
    
}
