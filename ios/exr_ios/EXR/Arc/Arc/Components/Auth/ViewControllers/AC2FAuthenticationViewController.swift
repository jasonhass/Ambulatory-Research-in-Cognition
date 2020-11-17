//
// AC2FAuthenticationViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import HMMarkup
import ArcUIKit
public struct ConfirmationCode {
	public struct Request : Codable {
		var participant_id:String
	}
	
}
fileprivate let confirmationCode:HMAPIRequest<ConfirmationCode.Request, HMResponse> = .post("/request-confirmation-code")

public class AC2FAuthenticationViewController: BasicSurveyViewController {
	public var authCode:String?
	public var initialValue:String?
	public var validParticipantId:String?
	public var code:String?
	let controller = Arc.shared.authController
	
	fileprivate func addResendCodeButton() {
		if let vc:CustomViewController<InfoView> = getTopViewController() {
			let button = HMMarkupButton()
			
			//Unhide the spacer to prevent the did receive code button from sitting at the bottom of the view. 
			vc.customView.spacerView.isHidden = false
			button.setTitle("Didn't receive code?".localized(ACTranslationKey.login_problems_2FA), for: .normal)
			button.setTitleColor(UIColor(named:"Primary"), for: .normal)
			Roboto.Style.bodyBold(button.titleLabel!)
			Roboto.PostProcess.link(button)
			button.contentHorizontalAlignment = .leading
			
			button.addAction {[weak self] in
                let vc:ResendCodeViewController = ResendCodeViewController(id: self?.initialValue ?? "100000")
                self?.addController(vc)
			}
			vc.customView.setAdditionalContent(button)
		}
	}
	
	public override func didPresentQuestion(input: SurveyInput?, questionId: String) {
		
		let view = input as? (SegmentedTextView)
		
		//If the view is a segmented text view and not nil
		//set the length to 6
		view?.set(length: 6)
			
	
		
		if questionId == "2FA" {
			
			//This will prevent the input from triggering a next action when valid. 
			view?.shouldTryNext = false
			view?.hideHelpButton = true
			addResendCodeButton()
            
            let vc:CustomViewController<InfoView> = getTopViewController()!
            // let label = vc.customView.getContentLabel()
            // vc.customView.setContentLabel(label.text!.replacingOccurrences(of: "{digits}", with: "5555"))
            vc.customView.setContentLabel("")
        }
		super.didPresentQuestion(input: input, questionId: questionId)

	}
	
	public override func isValid(value: QuestionResponse?, questionId: String, didFinish:@escaping ((Bool) -> Void))
	{
		super.isValid(value: value, questionId: questionId)
		{ [weak self] valid in
			var valid = valid

			guard let value:String = value?.getValue() else
			{
				valid = false
				didFinish(valid)
				return
			}
			
			if questionId == "auth_confirm"
			{
				if self?.initialValue != value
				{
					self?.set(error:"ARC ID does not match.".localized(ACTranslationKey.login_error1))

					valid = false
					didFinish(valid)
					return
				}
				
				OperationQueue().addOperation
					{
					switch Await(TwoFactorAuth.verifyParticipant).execute(value)
					{
					case .error(let e):
						valid = false
                        
                        let error = (e as? VerifyError)?.localizedDescription ?? "Sorry, our app is currently experiencing issues. Please try again later.".localized(ACTranslationKey.login_error3)

                        DispatchQueue.main.async {
                            _ = self?.popViewController(animated: true)
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                self?.set(error: error)
                            }
                        }
                        
						break
					case .success(let id):
						self?.validParticipantId = id
					}
				}
                didFinish(valid)
			}
			else
			{
				
				didFinish(valid)

			}
		}

	}
	
	
	public override func nextPressed(input: SurveyInput?, value: QuestionResponse?) {
		super.nextPressed(input: input, value: value)
		
		
	}
	public override func valueSelected(value: QuestionResponse, index: String) {
		//All questions are of type string in this controller
		
		self.set(error: nil)

		guard let value:String = value.getValue() else {
			assertionFailure("Should be a string value")
			return
		}
		
		if index == "auth_arc"
		{
			//Clear credentials
			controller.clear()
			initialValue = value
			
		}
		else if index == "2FA"
		{
			
			_ = controller.set(password: value)
			guard let _ = controller.getUserName(), let _ = controller.getPassword() else { return; }
			
			addSpinner(color: .white, backGroundColor: UIColor(named:"Primary"))

			controller.authenticate { (id, error) in
				OperationQueue.main.addOperation {
					if let value = id {
						self.set(error: nil)
						Arc.shared.participantId = Int(value)
						
						self.hideSpinner()
						
						Arc.shared.nextAvailableState()

					} else {
						self.set(error: error)
						self.hideSpinner()
					}
				}
			}

			
		} else if index == "auth_confirm" {
			
			_ = controller.set(username: value)
			
		}
		
		
	}
	
	
	
}

public enum VerifyError : Error {
	case invalidId
    case enrolledId
	case nullValueSupplied
	
	var localizedDescription:String {
		get {
			switch self {
			case .invalidId:
				return "Invalid Participant Id.".localized(ACTranslationKey.login_error1)
            case .enrolledId:
                return "This ARC ID is already enrolled on another device.".localized(ACTranslationKey.login_error2)
			case .nullValueSupplied:
				return "Empty value supplied.".localized(ACTranslationKey.login_error1)
			
			}
		}
	}
}


//A small library containing two factor auth utilities
public struct TwoFactorAuth {
	static func verifyParticipant(id:String, didFinish:@escaping (ACResult<String>)->()){
		
		if (Arc.environment?.blockApiRequests ?? false) == true
		{
			didFinish(ACResult.success(id));
			return;
		}
		let req = ConfirmationCode.Request(participant_id: id)
		print(req.toString())
		confirmationCode.execute(data: req, params: nil) { (urlResponse, hmResponse, fault) in
			if hmResponse?.response?.success ?? false == true {
				didFinish(ACResult.success(id))
			} else {
                var e:VerifyError = VerifyError.invalidId
                if let r = urlResponse as? HTTPURLResponse, r.statusCode == 409 {
                    e = VerifyError.enrolledId
                }
				didFinish(ACResult.error(e))
			}
		}
	}
}
