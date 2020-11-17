//
// AppTransformer.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import XCTest

public struct AppProcedure {
	var name:String
	var transforms:[AppTransformation]
	
	public func apply(to app:XCUIApplication) {
		for t in transforms {
			t.apply(to: app)
		}
	}
}



public struct AppTransformation {
	public let closure : (XCUIApplication) -> Void
	
	public func apply(to app:XCUIApplication) {
			closure(app)
	}
}

public extension AppTransformation {
	

	static var pressNext:Self {
		return AppTransformation { app in
			let button = app.buttons["next_button"]
			if button.exists {
				button.tap()
			}
			
		}
	}
}
