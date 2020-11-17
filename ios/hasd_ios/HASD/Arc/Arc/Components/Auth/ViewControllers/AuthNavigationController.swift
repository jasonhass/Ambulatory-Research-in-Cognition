//
// AuthNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import Arc

open class AuthNavigationController: SurveyNavigationViewController {
	var controller:AuthController = Arc.shared.authController
	var initialValue:String?
    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
		shouldNavigateToNextState = false
		shouldShowHelpButton = true
    }
	override open func loadSurvey(template:String) {
		survey = Arc.shared.surveyController.load(survey: template)
		
		
		//Shuffle the questions
		questions = survey?.questions ?? []
		
		
		
	}
	
	override open func onQuestionDisplayed(input:SurveyInput, index:String) {
		if index == "auth_1" {
            if let initialValue = controller.getUserName() {
                input.setValue(AnyResponse(type: .segmentedText,
                                           value: initialValue))
            }
		} else if index == "auth_2" {
            if let initialValue = controller.getUserName() {
                input.setValue(AnyResponse(type: .segmentedText,
                                           value: initialValue))
            }
		} else if index == "auth_3" {
            if let pass = controller.getPassword() {
                input.setValue(AnyResponse(type: .password,
                                           value: pass))
            }
		}
		
	}
    
    override open func isValid(value:QuestionResponse, index: String) -> Bool {
        guard let value = value.value as? String else {
            assertionFailure("Should be a string value")
            return false
        }
        if index == "auth_2", let input:SurveyInput = self.topViewController as? SurveyInput {
            if value != initialValue {
                input.setError(message:"mHealth IDs do not match.")
                return false
            } else {
                input.setError(message: nil)
            }
        }
        return true
    }
	
	//Override this to write to other controllers
	override open func onValueSelected(value:QuestionResponse, index:String) {
		//All questions are of type string in this controller
		guard let value = value.value as? String else {
			assertionFailure("Should be a string value")
			return
		}
		
		if index == "auth_1" {
			initialValue = value
		} else if index == "auth_2" {
			if value == initialValue {
				_ = controller.set(username: value)
			}
		} else if index == "auth_3" {
			_ = controller.set(password: value)
			
			if initialValue != controller.getUserName() {
				if let input:SurveyInput = self.topViewController as? SurveyInput {
					input.setError(message:"mHealth IDs do not match.")
				}
			} else {
				if let top = self.topViewController as? SurveyViewController {
					top.nextButton.showSpinner(color: UIColor(white: 1.0, alpha: 0.8), backgroundColor:UIColor(named:"Primary") )
				}
				controller.authenticate { (id, error) in
					OperationQueue.main.addOperation {
						if let value = id {
							if let input:SurveyInput = self.topViewController as? SurveyInput {
								input.setError(message: nil)
							}
							Arc.shared.participantId = Int(value)
							
							if let top = self.topViewController as? SurveyViewController {
								top.nextButton.hideSpinner()
							}
							
							Arc.shared.nextAvailableState()

						} else {
							if let input:SurveyInput = self.topViewController as? SurveyInput {
								input.setError(message:error)
								if let top = self.topViewController as? SurveyViewController {
									top.nextButton.hideSpinner()
								}
							}
						}

					}
				}
			}
		}
		
	}
	
	


}
