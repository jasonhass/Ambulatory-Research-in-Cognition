//
// 2FAAuth.swift
//



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
