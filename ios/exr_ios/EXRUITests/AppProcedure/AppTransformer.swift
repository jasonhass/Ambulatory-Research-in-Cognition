//
// AppTransformer.swift
//



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
