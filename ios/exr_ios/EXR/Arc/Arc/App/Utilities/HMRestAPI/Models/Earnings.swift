//
// Earnings.swift
//



import Foundation

public struct EarningOverview : Codable {
    
    public struct Response : Codable {
        public var success:Bool
        public var earnings:Earnings?
        
        public struct Earnings : Codable {
            public var total_earnings:String
            public var cycle:Int
			public var day:Int
            public var cycle_earnings:String
            
            public var goals:Array<Goal>
			public var new_achievements:Array<Achievement>
            public struct Goal : Codable {
                public var name:String
                public var value:String
                public var progress:Int
                public var amount_earned:String
                public var completed:Bool
				public var completed_on:TimeInterval?
                public var progress_components:Array<Int>
            }
			
			public struct Achievement : Codable {
				public var name:String
				public var amount_earned:String
			
			}
        }
    }
	
	var response:Response?
	var errors:[String:[String]]
}


public struct EarningDetail : Codable {
    
    public struct Response : Codable {
        public var success:Bool
        public var earnings:Earnings?

        public struct Earnings : Codable {
            public var total_earnings:String
            public var cycles:[Cycle]?
            
            public struct Cycle : Codable {
                public var cycle:Int
                public var total:String
                public var start_date:TimeInterval
                public var end_date:TimeInterval
                public var details:[Detail]
                
                public struct Detail : Codable {
                    public var name:String
                    public var value:String
                    public var count_completed:Int
                    public var amount_earned:String
                }
            }
        }
    }
	var response:Response?
	var errors:[String:[String]]
}

public struct EarningRequestData:Codable {
    
    var cycle:Int?   //index of the cycle to retrieve
    var day:Int?     //index of the day of the cycle
    
    init(cycle:Int?, day:Int?) {
        self.cycle = cycle
        self.day = day
    }
    
}
