//
// EarningsController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

public protocol EarningsControllerDelegate {
	func didUpdateEarnings()
}
open class EarningsController {
	static public var overviewKey = "EarningsOverview"
	static public var detailKey = "EarningsDetail"
	static public var studySummaryKey = "StudySummary"

	static let shared = EarningsController()
	lazy var thisWeek:ThisWeekExpressible = {Arc.shared.studyController}()
	lazy var thisStudy:ThisStudyExpressible = {Arc.shared.studyController}()
	
	init() {
		NotificationCenter.default.addObserver(self, selector: #selector(sessionsUpdated(notification:)), name: .ACSessionUploadComplete, object: nil)
		
		NotificationCenter.default.addObserver(self, selector: #selector(updateEarnings), name: .ACStartEarningsRefresh, object: nil)
	}
	@objc public func sessionsUpdated(notification:Notification) {
		let uploads = notification.object as? Set<Int64>
		assert(uploads != nil, "Wrong type supplied")
		if uploads?.isEmpty == true {
			
			updateEarnings()
			
		}
	}

	@objc private func updateEarnings() {
		
		OperationQueue().addOperation {
			
			//Perform request and fire notifications notifying the system of updates
			
			if let overview = Await(fetchEarnings).execute(()) {
				Arc.shared.appController.lastFetched[EarningsController.overviewKey] = Date().timeIntervalSince1970
				Arc.shared.appController.store(value: overview, forKey: EarningsController.overviewKey)
				
				NotificationCenter.default.post(name: .ACEarningsUpdated, object: overview)
				
			}
	
		}
		OperationQueue().addOperation {
			
			if let detail = Await(fetchEarningDetails).execute(()) {
				Arc.shared.appController.lastFetched[EarningsController.detailKey] = Date().timeIntervalSince1970
				Arc.shared.appController.store(value: detail, forKey: EarningsController.detailKey)
				
				NotificationCenter.default.post(name: .ACEarningDetailsUpdated, object: detail)
				
				
			}
		}
		OperationQueue().addOperation {
			
			if let summary = Await(fetchStudySummary).execute(()) {
				Arc.shared.appController.lastFetched[EarningsController.studySummaryKey] = Date().timeIntervalSince1970
				Arc.shared.appController.store(value: summary, forKey: EarningsController.studySummaryKey)
				
				NotificationCenter.default.post(name: .ACStudySummaryUpdated, object: summary)
				
				
			}
		}
	}
}

fileprivate func fetchEarnings(request:Void,  didFinish:@escaping (EarningOverview?)->()) {

	
	HMAPI.getEarningOverview.execute(data: nil) { (urlResponse, data, err) in
		if let err = err {
			HMLog(err.localizedDescription)
			didFinish(nil)
			return
		}
		didFinish(data)
	}
}

fileprivate func fetchStudySummary(request:Void,  didFinish:@escaping (StudySummary?)->()) {

	
	HMAPI.getStudySummary.execute(data: nil) { (urlResponse, data, err) in
		if let err = err {
			HMLog(err.localizedDescription)
			didFinish(nil)
			return
		}
		didFinish(data)
	}
}
fileprivate func fetchEarningDetails(request:Void,  didFinish:@escaping (EarningDetail?)->()) {
	
	
	HMAPI.getEarningDetail.execute(data: nil) { (urlResponse, data, err) in
		if let err = err {
			HMLog(err.localizedDescription)
			didFinish(nil)
			return
		}
		didFinish(data)
	}
}
