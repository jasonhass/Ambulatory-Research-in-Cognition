//
// LanguageScreen.swift
//



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
