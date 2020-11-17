//
// ACSignatureNavigationController.swift
//



import Foundation
open class ACSignatureNavigationController: SurveyNavigationViewController {
    public var sessionId:Int64 = -1
    public var tag:Int32 = -1
    open override func viewDidLoad() {
        super.viewDidLoad()
        guard let session = Arc.shared.currentTestSession else {return}
        
        sessionId = Int64(session)
		let backButton = UIButton(type: .custom)
			backButton.frame = CGRect(x: 0, y: 0, width: 60, height: 10)
			backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_blue"), for: .normal)
			backButton.setTitle("BACK".localized(ACTranslationKey.button_back), for: .normal)
			backButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
			backButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: -12)
			backButton.setTitleColor(UIColor(named: "Primary"), for: .normal)
			backButton.addTarget(self, action: #selector(self.backPressed), for: .touchUpInside)
			
			let leftButton = UIBarButtonItem(customView: backButton)
			
		
		navigationItem.leftBarButtonItem = leftButton
    }
	@objc func backPressed()
	{
	  navigationController?.popViewController(animated: true)
	}
    open override func valueSelected(value: QuestionResponse, index: String) {
        //Do things here
        guard let image = value.value as? UIImage else {
            return
        }
        
        if Arc.shared.appController.save(signature: image, sessionId: sessionId, tag: tag) {
            print("saved")
        } else {
            print("Not saved")
        }
    }

}
