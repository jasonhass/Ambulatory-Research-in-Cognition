//
// EXIUITests.swift
//



import XCTest

class EXIUITests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false

        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        XCUIApplication().launch()

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }
	func testApp(){
		
		let app = XCUIApplication()
		
		AppProcedure.languageSelectScreen(language: .choice_6).apply(to: app)
		AppProcedure.welcomeScreen.apply(to: app)
		AppProcedure.Auth2FAScreens.apply(to: app)

//
//		let signInButton = app.buttons["sign_in_button"]
//		signInButton.tap()
//
//		enterPassCode(keys: ["3","3","2","0","0","8"])
//
//
//		enterPassCode(keys: ["3","3","2","0","0","8"])
		
	}
	func enterPassCode(keys:[String]) {
		let app = XCUIApplication()
		
		for key in keys {
			let key = app.keys[key]
			key.tap()
		}
		let doneButton = app.toolbars["Toolbar"].buttons["DONE"]
		doneButton.tap()
	}
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

	

}
