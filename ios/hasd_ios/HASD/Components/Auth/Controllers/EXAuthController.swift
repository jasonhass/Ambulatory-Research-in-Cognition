//
// EXAuthController.swift
//




import Foundation
import Arc

open class HASDAuthController : AuthController {
    override open func getAuthIssue(from code:Int?) -> String {
        if let code = code {
            if code == 401 {
                return "Invalid  Rater ID or ARC ID".localized("error1")
            }
            if code == 409 {
                return "Already enrolled on another device".localized("error2")
            }
        }
        return "Sorry, our app is currently experiencing issues. Please try again later.".localized("error3")
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
                Arc.shared.authController.pullData(phaseType: HASDPhase.self, completion: {_ in 
					completion(participantId, nil)
				})
			} else {
				completion(id, nil)

			}
		}
	}
}
