//
// StudySummary.swift
//


/*
{
  "response": {
    "success": true,
    "summary": {
      "total_earnings": "$0.00",
      "tests_taken": 0,
      "days_tested": 0,
      "goals_met": 0
    }
  },
  "errors": {}
}

*/
import Foundation
public struct StudySummary : Codable {
	static var test = try! JSONDecoder().decode(StudySummary.self, from: """
	{
	  "response": {
		"success": true,
		"summary": {
		  "total_earnings": "$80.00",
		  "tests_taken": 212,
		  "days_tested": 60,
		  "goals_met": 9000
		}
	  },
	  "errors": {}
	}
	""".data(using: .utf8)!)
	
	public struct Response : Codable {
		   public var success:Bool
		   public var summary:Summary?
		   
		   public struct Summary : Codable {
			public var total_earnings:String
			public var tests_taken:Int
			public var days_tested:Int
			public var goals_met:Int
		   }
	   }
	
	var response:Response
}
