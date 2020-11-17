//
// TodaysProgress.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public struct TodaysProgess {
	public struct SessionData {
		public var started:Bool = false
		public var progress:Int = 0
		public var total:Int = 3
		
		public func getTotalProgress() -> Double {
			return Double(progress) / Double(total)
		}
	}
	
	
	
	public var sessionsCompleted:Int {
		var complete:Int = 0
		for session in sessionData {
			if session.progress == session.total {
				complete += 1
			}
		}
		return complete
	}
	
	
	public var sessionsStarted:Int {
		var started:Int = 0
		for session in sessionData {
			if session.started == true {
				started += 1
			}
		}
		return started
	}

	public var totalSessions:Int = 4
	public var sessionData:[SessionData] = []
	
	
	public init() {
		
	}
	
}
