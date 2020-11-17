//
// ArcTests.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import XCTest
@testable import Arc
class ArcTests: XCTestCase {
	let environment = ACEnvironment()
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
		Arc.configureWithEnvironment(environment: environment)
    }
	
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testStudyCreationEqualsArcStartDays() {
		MHController.dataContext.performAndWait {
			let studyController = Arc.shared.studyController
			
			var studies = studyController.getAllStudyPeriods()
			XCTAssert(studies.isEmpty, "State is not reset, configure Mock Data")
			let count = Arc.environment?.arcStartDays?.count
			
			studyController.createAllStudyPeriods(startingID: 0, startDate: Date())
			studies = studyController.getAllStudyPeriods()
			XCTAssert(count == studies.count, "Mismatched count, creating all studies should result in study counts equaling environment start days.")
			dump(studies.compactMap{$0.startDate})
		}
		
		
    }
	
	
//    func testPerformanceExample() {
//        // This is an example of a performance test case.
//        self.measure {
//            // Put the code you want to measure the time of here.
//        }
//    }

}
