//
// ResendCodeViewController.swift
//



import UIKit
import HMMarkup
import ArcUIKit
class ResendCodeViewController: CustomViewController<InfoView>, SurveyInput{
	
	
	var orientation: UIStackView.Alignment = .top

	
	var surveyInputDelegate: SurveyInputDelegate?
	
	var participantId:String
	init(id:String) {
		participantId = id
		super.init()
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	fileprivate func addNeedMoreHelpButton() {
		//Unhide the spacer to prevent the button from sitting at the bottom of the view.
		customView.spacerView.isHidden = false
		let button = HMMarkupButton()
		
		button.setTitle("I need more help".localized(ACTranslationKey.login_2FA_morehelp_linked), for: .normal)
		button.setTitleColor(UIColor(named:"Primary"), for: .normal)
		Roboto.Style.bodyBold(button.titleLabel!)
		Roboto.PostProcess.link(button)
		button.contentHorizontalAlignment = .leading
		
        button.addAction {[weak self] in
            //self?.nextPressed(input: self?.getInput(), value: AnyResponse(type: .text, value: "0"))
            self?.pressedNeedMoreHelp()
        }
        customView.infoContent.addArrangedSubview(button)
	}
	
	fileprivate func addSendNewCodeButton() {
		let sendCodeButton = ACButton()
		sendCodeButton.setTitle("SEND NEW CODE".localized(ACTranslationKey.button_sendnewcode), for: .normal)
		sendCodeButton.addTarget(self, action: #selector(sendCode), for: .touchUpInside)
        customView.infoContent.addArrangedSubview(sendCodeButton)
	}
	
	override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
		customView.setTextColor(UIColor(named:"Primary Text"))
		customView.setHeading("login_resend_header")
		customView.setContentLabel("login_resend_subheader")
		customView.nextButton?.isHidden = true
		
	
	  
		
		addSendNewCodeButton()
		
		addNeedMoreHelpButton()
    }
	@objc func backPressed()
	  {
		navigationController?.popViewController(animated: true)
	  }
    func pressedNeedMoreHelp() {
        let vc:AC2FAHelpViewController = .get()
        self.navigationController?.pushViewController(vc, animated: true)
    }
	override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
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
	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		
		
	}
    @objc func sendCode(_ sender: ACButton) {
		
        sender.showSpinner()
        sender.setTitle("", for: .normal)
        
		let nav = navigationController
		let id = participantId
		MHController.dataContext.perform {
			
			let result:ACResult<String> = Await(TwoFactorAuth.verifyParticipant).execute(id)
			switch result {
			case .error(_):
				break;
				
			case .success(_):
				OperationQueue.main.addOperation {
					nav?.popViewController(animated: true)
				}
			}
            OperationQueue.main.addOperation {
                sender.hideSpinner()
            }
		}
	}
	func getValue() -> QuestionResponse? {
		return nil
	}
	
	func setValue(_ value: QuestionResponse?) {
		
	}

}
