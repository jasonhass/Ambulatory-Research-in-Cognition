//
// ACAuthViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
open class ACAuthViewController: BasicSurveyViewController {

	var controller:AuthController = Arc.shared.authController
	var initialValue:String?
	
	
	
	override open func viewDidLoad() {
		super.viewDidLoad()
		// Do any additional setup after loading the view.
		shouldNavigateToNextState = false
		shouldShowHelpButton = true
	}
	
	func helpHandler() {
//		print("Navigate to help")
	}
	
	
	open override func didPresentQuestion(input: SurveyInput?, questionId: String)
	{
        let input = input
		
		if let view = input as? (SegmentedTextView) {
			view.set(length: 6)
			
		}
		
		if questionId == "auth_1" {

            if let initialValue = initialValue {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: initialValue))
            }
		}
		
		if questionId == "auth_2" {

            if let initialValue = controller.getUserName() {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: initialValue))
            }
		}
		
		if questionId == "auth_3" {
			//Try next will trigger the next button if not nil
			//We don't want to fire this for the final step (#9016)
            if let view = input as? SegmentedTextView {
				view.shouldTryNext = false
                
            }
            if let pass = controller.getPassword() {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: pass))
            }
		}
		
		if questionId == "auth_arc_id_no_rater" {
			if let initialValue = initialValue {
				input?.setValue(AnyResponse(type: .segmentedText,
											value: initialValue))
			}
		}
		
		if questionId == "auth_arc_id_confirm_no_rater" {
			if let initialValue = initialValue {
				input?.setValue(AnyResponse(type: .segmentedText,
											value: initialValue))
			}
			if let view = input as? SegmentedTextView {
				view.shouldTryNext = false
				
			}
			if let pass = controller.getPassword() {
				input?.setValue(AnyResponse(type: .segmentedText,
											value: pass))
			}
		}
		super.didPresentQuestion(input: input, questionId: questionId)
	}
	
	open override func isValid(value: QuestionResponse?, questionId: String, didFinish:@escaping ((Bool) -> Void))
	{
		super.isValid(value: value, questionId: questionId)
		{ [weak self] valid in
			var valid = valid

			guard let value = value?.value as? String else {
				assertionFailure("Should be a string value")
				didFinish(false)
				return
			}
			if questionId == "auth_2"{
				if value != self?.initialValue {
					
					self?.set(error:"Subject ID doesn’t match")
					didFinish(false)
					return
					
				}
			}
			if questionId == "auth_arc_id_confirm_no_rater"{
				if value != self?.initialValue {
					
					self?.set(error:"Subject ID doesn’t match")
					didFinish(false)
					return
					
				}
			}
			didFinish(true)
			return
			
		}
	}
	
	//Override this to write to other controllers
	override open func valueSelected(value:QuestionResponse, index:String) {
		//All questions are of type string in this controller
		guard let value = value.value as? String else {
			assertionFailure("Should be a string value")
			return
		}
		self.set(error: nil)

		if index == "auth_1" {
			initialValue = value
		}
		if index == "auth_arc_id_no_rater" {
			initialValue = value
			_ = controller.set(username: value)
		}
		if index == "auth_2" {
			if value == initialValue {
				_ = controller.set(username: value)
			}
		}
		
		if index == "auth_3" || index == "auth_arc_id_confirm_no_rater" {
			_ = controller.set(password: value)

			if initialValue != controller.getUserName() {
				
				self.set(error: "Subject ID does not match.")

			} else {
				let top:CustomViewController<InfoView>? = getTopViewController()
				
				top?.customView.nextButton?.showSpinner(color: UIColor(white: 1.0, alpha: 0.8), backgroundColor:UIColor(named:"Primary"))
				
				controller.authenticate { (id, error) in
					OperationQueue.main.addOperation {
						if let value = id {
							self.set(error:nil)
							
							
							Arc.shared.participantId = Int(value)
						
							top?.customView.nextButton?.hideSpinner()

							Arc.shared.nextAvailableState()

						} else {
							self.set(error:error)
							
							top?.customView.nextButton?.hideSpinner()
						}
					}
				}
			}
		}
		
	}
	
	
	


}
