//
// State.swift
//



import UIKit
public protocol State {
	
	func viewForState() -> UIViewController
	func surveyTypeForState() -> SurveyType
}
