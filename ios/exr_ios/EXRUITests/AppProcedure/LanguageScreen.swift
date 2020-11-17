//
// LanguageScreen.swift
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
	static func languageSelectScreen(language: AppTransformation.Language) -> Self {
			return AppProcedure(name: "Language Select",
								transforms: [
									.select(language: .choice_6),
									.pressNext
								]
			)
	}
}


public extension AppTransformation {
	enum Language : String {
		case choice_0, choice_1, choice_2, choice_3, choice_4, choice_5, choice_6, choice_7, choice_8
	}
	static func select(language:Language) -> Self {
		return AppTransformation { app in
			let choice = language.rawValue
			let button = app.buttons[choice]
			if button.exists {
				button.tap()
				
			}
			
		}
	}
	
}
