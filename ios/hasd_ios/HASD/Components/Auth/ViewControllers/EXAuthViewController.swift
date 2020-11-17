//
// EXAuthViewController.swift
//




import UIKit
import Arc
public class HASDAuthViewController : BasicSurveyViewController {
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
		
//		if index == "auth_1" {
//
//				self.initialValue = nil
//				controller.clear()
//
//		} else if index == "auth_2" {
//				controller.set(password: "")
//		} else if index == "auth_3" {
//
//		}
		
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
            if let userName = controller.getUserName() {
                input?.setValue(AnyResponse(type: .segmentedText,
                                           value: userName))
            }
        }
        
    }
    
	public override func isValid(value: QuestionResponse?, questionId: String, didFinish: @escaping ((Bool) -> ())){
		
		
		guard let value = value?.value as? String else {
            //assertionFailure("Should be a string value")
            didFinish(false)
			return
        }
		if questionId == "auth_confirm" {
			if initialValue != value {
				set(error:"Map ID does not match.".localized(ACTranslationKey.login_error4))
				didFinish(false)

				return
			   
			}
		}
        didFinish(true)
    }
    
    //Override this to write to other controllers
    override open func valueSelected(value:QuestionResponse, index:String) {
        //All questions are of type string in this controller
            
		set(error:nil)
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
			initialValue = ""
			controller.clear()
            _ = controller.set(password: value)

          

        } else if index == "auth_confirm" {
            if initialValue != value {
				set(error:"Map ID does not match.".localized(ACTranslationKey.login_error4))
				return
               
            } else {

				set(error:nil)
				_ = controller.set(username: value)
            }
			
			guard let _ = controller.getUserName(), let _ = controller.getPassword() else { return; }
			
			addSpinner()
			
			controller.authenticate { (id, error) in
				OperationQueue.main.addOperation {
					if let value = id {
						
						self.set(error:nil)
						
						Arc.shared.participantId = Int(value)
						
						self.hideSpinner()
						
						Arc.shared.nextAvailableState()

					} else {
						
						self.set(error:error)
						
						self.hideSpinner()
					
					}
				}
			}
        }
        
    }
}
