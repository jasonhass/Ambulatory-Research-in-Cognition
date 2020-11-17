//
// 2FAAuth.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import XCTest
public extension AppProcedure {
	static var Auth2FAScreens : Self {
			return AppProcedure(name: "Auth 2fa",
								transforms: [
									.enterCode(keys: "1", "1", "1", "1", "1", "1"),
									.enterCode(keys: "1", "1", "1", "1", "1", "1"),
									.enterCode(keys: "1", "1", "1", "1", "1", "1"),
									.pressNext
									
								
				]
			)
	}
}


public extension AppTransformation {
	
	static func enterCode(keys:String ...) -> Self {
		return AppTransformation { app in
			for key in keys {
				let key = app.keys[key]
				key.tap()
			}
			dump(app.buttons)
			let doneButton = app.toolbars["Toolbar"].buttons["done_button"]
			
			doneButton.tap()
			
		}
	}
	
}
