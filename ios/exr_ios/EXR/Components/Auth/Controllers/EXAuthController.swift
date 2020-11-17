//
// EXAuthController.swift
//



import Foundation
import Arc

open class EXAuthController : AuthController {
    override open func getAuthIssue(from code:Int?) -> String {
        if let code = code {
            if code == 401 {
				return "Invalid  Rater ID or ARC ID".localized(ACTranslationKey.login_error1)
            }
            if code == 409 {
                return "Already enrolled on another device".localized(ACTranslationKey.login_error2)
            }
        }
        return "Sorry, our app is currently experiencing issues. Please try again later.".localized(ACTranslationKey.login_error3)
    }
	
	open override func authenticate(completion: @escaping ((Int64?, String?) -> ())) {
		super.authenticate { (id, error) in
			
			if error != nil {
				return completion(nil, error)
			}
			guard let participantId = id else {
				return completion(id, error)
			}
			
			
			if let _ = Arc.shared.studyController.latestTest {
				Arc.shared.appController.commitment = .committed
				Arc.shared.authController.pullData(phaseType: EXPhase.self, completion: {_ in 
					completion(participantId, nil)
				})
			} else {
				completion(id, nil)

			}
		}
	}
}
