//
// WelcomeScreen.swift
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
