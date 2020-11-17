//
// WelcomeScreen.swift
//



import Foundation
import XCTest
public extension AppProcedure {
	static var welcomeScreen : Self {
			return AppProcedure(name: "Welcome Sign in",
								transforms: [
						
									.pressSignIn
								]
			)
	}
}


public extension AppTransformation {
	
	static var pressSignIn : Self {
		return AppTransformation { app in
			let button = app.buttons["signin_button"]
			if button.exists {
				button.tap()
				
			}
			
		}
	}
	
}
