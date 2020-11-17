//
// EXAuthViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import Arc
public class EXAuthViewController : BasicSurveyViewController {
    var controller:AuthController = Arc.shared.authController
    var initialValue:String = ""
	
    public override init(file: String, surveyId:String? = nil, showHelp:Bool? = true) {
		super.init(file: file)
		
		shouldNavigateToNextState = false
		
	}
	
	required public init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	
	
    func helpHandler() {
        //        print("Navigate to help")
    }
	open override func didPresentQuestion(input: SurveyInput?, questionId: String) {
		
        let input = input
        if questionId == "auth_arc" {
            
            if let view = input as? SegmentedTextView {
                view.set(length: 6)
            }
            input?.setValue(AnyResponse(type: .segmentedText,
                                       value: initialValue))
            
        } else if questionId == "auth_rater" {
            
            
            if let view = input as? SegmentedTextView {
                view.set(length: 6)
            }
            if let pass = controller.getPassword() {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: pass))
            }
        } else if questionId == "auth_confirm" {
            //Try next will trigger the next button if not nil
            //We don't want to fire this for the final step (#9016)

            if let view = input as? SegmentedTextView {
				view.shouldTryNext = false
                view.set(length: 6)
            }
            if let pass = controller.getUserName() {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: pass))
            }
        }
        
    }
    
	public override func isValid(value: QuestionResponse?, questionId: String, didFinish: @escaping ((Bool) -> ())){
		
		
		guard (value?.value as? String) != nil else {
            //assertionFailure("Should be a string value")
            didFinish(false)
			return
        }
        
        didFinish(true)
    }
    
    //Override this to write to other controllers
    override open func valueSelected(value:QuestionResponse, index:String) {
        //All questions are of type string in this controller
        if let input:SurveyInput = self.topViewController as? SurveyInput {
            
            input.setError(message:nil)
        }
		guard let value = value.value as? String else {
            assertionFailure("Should be a string value")
            return
        }
        
        if index == "auth_arc"
        {
            initialValue = value

        }
        else if index == "auth_rater"
        {
            _ = controller.set(password: value)

          

        } else if index == "auth_confirm" {
            if initialValue != value {
                if let input:SurveyInput = self.topViewController as? SurveyInput {
                    input.setError(message:"ARC ID does not match.".localized(ACTranslationKey.login_error1))
                    return
                }
            } else {
                if let input:SurveyInput = self.topViewController as? SurveyInput {

                    input.setError(message:nil)
                }
                _ = controller.set(username: value)
            }
        }
        
        guard let _ = controller.getUserName(), let _ = controller.getPassword() else { return; }
        
        if let top = self.topViewController as? CustomViewController<InfoView> {
            top.customView.nextButton?.showSpinner(color: UIColor(white: 1.0, alpha: 0.8), backgroundColor:UIColor(named:"Primary") )
        }
        controller.authenticate { (id, error) in
            OperationQueue.main.addOperation {
                if let value = id {
                    if let input:SurveyInput = self.topViewController as? SurveyInput {
                        input.setError(message: nil)
                    }
                    Arc.shared.participantId = Int(value)
					
					
					if let top = self.topViewController as? CustomViewController<InfoView> {
						top.customView.nextButton?.hideSpinner()
					}
					
					Arc.shared.nextAvailableState()
							
					
				
					
                } else {
                    if let input:SurveyInput = self.topViewController as? SurveyInput {
                        input.setError(message:error)
                        if let top = self.topViewController as? CustomViewController<InfoView> {
                            top.customView.nextButton?.hideSpinner()
                        }
                    }
                }
            }
        }
    }
}
