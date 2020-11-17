//
// BasicSurveyViewController.swift
//



import UIKit
import HMMarkup
import ArcUIKit

open class BasicSurveyViewController: UINavigationController, SurveyInputDelegate {
	public var app:Arc {
		return Arc.shared
	}
	public var helpButton: UIBarButtonItem?
	public var isShowingHelpButton = false{
		didSet {
			if shouldShowHelpButton {
				displayHelpButton(shouldShowHelpButton)
			} else {
				displayHelpButton(false)
				
			}
		}
	}
	public var autoAdvancePageIndex:Bool = true
    public var backButton: UIBarButtonItem?
    public var isShowingBackButton = false{
        didSet {
            if shouldShowBackButton {
                displayBackButton(shouldShowBackButton)
            } else {
                displayBackButton(false)
            }
        }
    }
    var useDarkStatusBar:Bool = false
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return useDarkStatusBar ? .default : .lightContent
    }
    
	public var shouldShowHelpButton = true
	public var shouldShowBackButton = true
	public var survey:Survey
	public var questions = Array<Survey.Question>()
	var subQuestions:[Survey.Question]?
	public var currentIndex:Int = 0
	var answeredQuestions:[Survey.Question] = []
	public var surveyId:String
	public var shouldNavigateToNextState:Bool = true
    public var currentViewControllerAlwaysHidesBarButtons = false
    public init(file:String, surveyId:String? = nil, showHelp:Bool? = true) {
		
        shouldShowHelpButton = showHelp ?? true
        
		let newSurvey = Arc.shared.surveyController.load(survey: file)
		survey = newSurvey
		questions = survey.questions
		
		subQuestions = survey.subQuestions
//		print(file)
//		dump(survey)
		var newId:String?
		//If we have a current study running
		if let i = Arc.shared.studyController.getCurrentStudyPeriod()?.studyID  {
			
			let studyId = Int(i)
			//And there is a session running
			if let sessionId = Arc.shared.currentTestSession  {
				let session = Arc.shared.studyController.get(session: sessionId, inStudy: studyId)
				
				//find a matching surveyResponse for the type of the new survey
				if	let surveyType = newSurvey.type,
					let data = session.surveyFor(surveyType: surveyType){
					Arc.shared.surveyController.mark(startDate: data.id!)
					//We're going to use this id now.
					newId = data.id!
					
				}
				
			}
			
			
		}
		if newId == nil {
			newId = surveyId ?? Arc.shared.surveyController.create(type:newSurvey.type);

		}
		
		self.surveyId = newId!
		

		super.init(nibName: nil, bundle: nil)
	}
	
	open func displayHelpButton(_ shouldShow:Bool) {
		if shouldShow {
			
			var rightButton:UIBarButtonItem? = helpButton
			if helpButton == nil {
				
				let helpButton = UIButton(type: .custom)
				helpButton.frame = CGRect(x: 0, y: 0, width: 60, height: 10)
				helpButton.setTitle("HELP".localized(ACTranslationKey.button_help), for: .normal)
				helpButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
				helpButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: -16)
				helpButton.setTitleColor(UIColor(named: "Primary"), for: .normal)
				helpButton.addTarget(self, action: #selector(self.onHelp), for: .touchUpInside)
				
				rightButton = UIBarButtonItem(customView: helpButton)
			}
			topViewController?.navigationItem.rightBarButtonItem = rightButton
			
			
		} else {
			self.navigationItem.rightBarButtonItem = nil
			helpButton = nil
		}
	}
    open func displayBackButton(_ shouldShow:Bool) {
        if shouldShow {
            
            var leftButton:UIBarButtonItem? = backButton
            if backButton == nil {
                if self.viewControllers.count > 1 {
                let backButton = UIButton(type: .custom)
                backButton.frame = CGRect(x: 0, y: 0, width: 60, height: 10)
                backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_blue"), for: .normal)
                backButton.setTitle("BACK".localized(ACTranslationKey.button_back), for: .normal)
                backButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
                backButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: -12)
                backButton.setTitleColor(UIColor(named: "Primary"), for: .normal)
                backButton.addTarget(self, action: #selector(self.backPressed), for: .touchUpInside)
                
                leftButton = UIBarButtonItem(customView: backButton)
                }
            }
            topViewController?.navigationItem.leftBarButtonItem = leftButton
            
            
        } else {
            self.navigationItem.leftBarButtonItem = nil
            backButton = nil
            topViewController?.navigationItem.backBarButtonItem = nil
            topViewController?.navigationItem.hidesBackButton = true
        }
    }
    @objc open func backPressed()
    {
        popViewController(animated: true)
    }
	@objc open func onHelp() {
		//Supply project specific handler to prevent white screen
		let helpState = Arc.shared.appNavigation.defaultHelpState()
		Arc.shared.appNavigation.navigate(vc: helpState, direction: .toRight)
		
	}
	public func setCompleted() {
		onCompleted()
		//If this survey is being stored in core data
		//mark it as filled out.
		_ = Arc.shared.surveyController.mark(filled: surveyId)
		
		//Subclasses may perform conditional async operations
		//that determine if the app should proceed.
		if shouldNavigateToNextState {
			Arc.shared.nextAvailableState()
		}
	}
	public func onCompleted(){
		
	}
	required public init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	func getTopViewController<T:UIView>() -> CustomViewController<T>? {
		return topViewController as? CustomViewController<T>
	}
	func getCurrentQuestion() -> String {
		
		return questions[currentIndex].questionId
	}
	
	public func addSpinner(color:UIColor? = UIColor(white: 1.0, alpha: 0.8), backGroundColor:UIColor? = UIColor(named:"Primary")) {
		OperationQueue.main.addOperation {[weak self] in

			if let vc:CustomViewController<InfoView> = self?.getTopViewController(){
				
				
				vc.customView.nextButton?.showSpinner(color: color, backgroundColor: backGroundColor)
			}
		}
	}
	
	public func hideSpinner() {
		OperationQueue.main.addOperation { [weak self] in

			if let vc:CustomViewController<InfoView> = self?.getTopViewController(){
				vc.customView.nextButton?.hideSpinner()
			}
		}
	}
	public func set(error:String?) {
		OperationQueue.main.addOperation { [weak self] in
			
			if let vc:CustomViewController<InfoView> = self?.getTopViewController(){
				vc.customView.setError(message: error)
				
			} else if let input = self?.getInput() {
				input.setError(message: error)
			}
		}
	}

	func getInput() -> SurveyInput? {
		if let vc:CustomViewController<InfoView> = getTopViewController() {
			return vc.customView.inputItem
		}
		if let input:SurveyInput = topViewController as? SurveyInput {
			return input
		}
		return nil
	}
	
	override open func viewDidLoad() {
        super.viewDidLoad()
		guard questions.count > 0 else {
			assertionFailure("Cannot display survey with 0 questions.")
			return
		}
		let question = questions[0]
		if question.style == .viewController {
			addController(customViewController(forQuestion: question))

		} else {
			addController()

		}
		Arc.shared.surveyController.mark(startDate: self.surveyId)

    }
	open func customViewController(forQuestion question:Survey.Question) -> UIViewController? {
		return nil
	}
	func addController(_ vc:UIViewController? = nil) {
		if let vc = vc {
			pushViewController(vc, animated: true)
		} else {
			pushViewController(CustomViewController<InfoView>(), animated: true)
		}
	}
	func display(question:Survey.Question) {
		//Reset this every time we view a new vc
		currentViewControllerAlwaysHidesBarButtons = false
		let style = question.style ?? .none
		switch style {
        
		case .instruction, .test:
			instructionStyle(question)
		case .none:
			questionStyle(question)
		case .viewController:
			viewControllerStyle(question)
		case .impasse:
			questionStyle(question)
        case .onboarding:
            questionStyle(question)
		case .grids:
			instructionStyle(question, presentableVc: GridTestTutorialViewController())
			
		case .prices:
            if Arc.environment?.priceTestType == .simplified {
                instructionStyle(question, presentableVc: SimplifiedPricesTestTutorialViewController())
            } else {
                instructionStyle(question, presentableVc: PricesTestTutorialViewController())
            }
		case .symbols:
			instructionStyle(question, presentableVc: SymbolsTutorialViewController())
			
		}
		
	}
	
	func viewControllerStyle(_ question: Survey.Question) {
		if var input = topViewController as? SurveyInput {
			input.surveyInputDelegate = self
		}
		if ["NotificationAccessRejected"].contains(question.state) {
			currentViewControllerAlwaysHidesBarButtons = true
		}
		

	}
	func instructionStyle(_ question:Survey.Question, presentableVc:UIViewController? = nil) {
		// Do any additional setup after loading the view.
		let vc:CustomViewController<InfoView> = getTopViewController()!
        useDarkStatusBar = false
        setNeedsStatusBarAppearanceUpdate()
        let bg_image = UIImage(named: "availability_bg", in: Bundle(for: BasicSurveyViewController.self), compatibleWith: nil)
        vc.customView.backgroundView.image = bg_image
		vc.customView.infoContent.alignment = .center
		vc.customView.backgroundColor = UIColor(named:"Primary")!
		vc.customView.setTextColor(UIColor(named: "Secondary Text"))
		
		vc.customView.setButtonColor(style:.secondary)
		
		vc.customView.setMediumHeading(question.prompt)
		vc.customView.setSubHeading(question.subTitle)
		vc.customView.setContentLabel(question.detail)
		vc.customView.infoContent.headingLabel?.textAlignment = .center
		vc.customView.infoContent.contentLabel?.textAlignment = .center
		vc.customView.infoContent.subheadingLabel?.textAlignment = .center
		vc.customView.infoContent.alignment = .center
		vc.customView.spacerView.isHidden = false
		vc.customView.topSpacer.isHidden = false
		vc.customView.infoContent.layout {
			$0.centerY == vc.customView.infoContent.superview!.centerYAnchor - 40 ~ 999
		}
		if let presentable = presentableVc {
			let button = HMMarkupButton()
			button.setTitle("View a Tutorial".localized(ACTranslationKey.testing_tutorial_link), for: .normal)
			Roboto.Style.bodyBold(button.titleLabel!, color:.white)
			Roboto.PostProcess.link(button)
			button.addAction {[weak self] in
				self?.present(presentable, animated: true) {
					
				}
			}
			vc.customView.setAdditionalFooterContent(button)
			
		}
		if let input = question.type?.create(inputWithQuestion: question) as? (UIView & SurveyInput) {
			vc.customView.setInput(input)
		} else {
			vc.customView.inputContainer.isHidden = true
		}
		vc.customView.inputDelegate = self
		
		vc.customView.nextButton?.addTarget(self, action: #selector(nextButtonPressed(sender:)), for: .primaryActionTriggered)
        vc.customView.nextButton?.setTitle(question.nextButtonTitle ?? "NEXT".localized(ACTranslationKey.button_next), for: .normal)
		didPresentQuestion(input: vc.customView.inputItem, questionId: question.questionId)
		currentViewControllerAlwaysHidesBarButtons = true
	}
	func questionStyle(_ question:Survey.Question) {
		// Do any additional setup after loading the view.
		let vc:CustomViewController<InfoView> = getTopViewController()!
        
        useDarkStatusBar = true
        setNeedsStatusBarAppearanceUpdate()
        
        //if shouldShowHelpButton {
            //displayHelpButton(shouldShowHelpButton)
        //}
        //if shouldShowBackButton{
            displayBackButton(shouldShowBackButton)
        //}
		vc.customView.infoContent.alignment = .leading
		
		vc.customView.setTextColor(UIColor(named: "Primary Text"))
		
		vc.customView.setButtonColor(style:.primary)
		
		vc.customView.setHeading(question.prompt)
        if let style = question.style, style == .onboarding{
            vc.customView.setSeparatorWidth(0.15)

            vc.customView.nextButton?.isEnabled = true;
            shouldShowBackButton = false
            displayBackButton(shouldShowBackButton)
            shouldShowHelpButton = true
            displayHelpButton(shouldShowHelpButton)
        }
        if let style = question.style, style == .impasse
        {
            vc.customView.setSeparatorWidth(0.15)
			shouldShowBackButton = true
            vc.customView.nextButton?.isEnabled = false;
            vc.customView.nextButton?.isHidden = true;
            shouldShowHelpButton = true
            displayHelpButton(shouldShowHelpButton)
        }
        else
        {
            vc.customView.nextButton?.addTarget(self, action: #selector(nextButtonPressed(sender:)), for: .primaryActionTriggered)
            shouldShowHelpButton = false
            displayHelpButton(shouldShowHelpButton)
        }
        vc.customView.setPrompt(question.subTitle)
		vc.customView.setContentLabel(question.detail)
		
		if let input = question.type?.create(inputWithQuestion: question) as? (UIView & SurveyInput) {
			vc.customView.setInput(input)
		}
		vc.customView.inputDelegate = self
		

        
		
        disableNextButton(title: question.altNextButtonTitle ?? "NEXT".localized(ACTranslationKey.button_next))
        
		didPresentQuestion(input: vc.customView.inputItem, questionId: question.questionId)


	}
	open func valueSelected(value:QuestionResponse, index:String) {
		guard value.type != .none else {
			return
		}
		let question = Arc.shared.surveyController.get(question: index)
		
		let _ = Arc.shared.surveyController.set(response: value,
												questionId: question.questionId,
												question: question.prompt.localized(question.prompt),
												forSurveyId: self.surveyId)
	}
	
	
	
	open func templateForQuestion(id: String) -> Dictionary<String, String> {return [:]}
	
	open func didPresentQuestion(input: SurveyInput?, questionId:String) {
		let question = Arc.shared.surveyController.get(question: questionId)
        if let value = Arc.shared.surveyController.getResponse(forQuestion: questionId, fromSurveyResponse: surveyId), value.value != nil{
			input?.setValue(value)
			enableNextButton(title: question.nextButtonTitle ?? "".localized(ACTranslationKey.button_next))
		}
		
		let _ = Arc.shared.surveyController.mark(displayTime: questionId,
												 question: question.prompt.localized(question.prompt),
												 forSurveyResponse: surveyId)
		
	}
	
	open func didFinishSetup() {}
	
	open func tryNextPressed() {
		nextButtonPressed(sender: self)
	}
	
    
    public func enableNextButton()
    {
        
        guard let input:CustomViewController<InfoView> = self.getTopViewController() else {
            return
        }
        
        input.customView.enableNextButton()
    }
    
    public func enableNextButton(title:String = "NEXT".localized(ACTranslationKey.button_next)) {
        guard let input:CustomViewController<InfoView> = self.getTopViewController() else {
            return
        }
        
        input.customView.enableNextButton(title: title)
    }
    
    public func disableNextButton(title:String = "NEXT".localized(ACTranslationKey.button_next))
    {
        guard let input:CustomViewController<InfoView>  = self.getTopViewController() else {
            return
        }
        
        input.customView.disableNextButton(title: title)
    }
    
	@objc public func nextButtonPressed(sender:Any) {
		

		if let item = getInput() {
			nextPressed(input: item, value: item.getValue())
		} else {
			nextPressed(input: nil, value: nil)
		}

	}
	
	
	open func isValid(value:QuestionResponse?, questionId: String, didFinish:@escaping ((Bool)->Void)) {
		if let input = getInput(){
			input.setError(message: nil)
			if value?.value == nil {
				didFinish(false)
			} else {
				didFinish(true)
			}
		} else {
			didFinish(true)

		}
		
	}
	open func didChangeValue() {
		
		let question = questions[currentIndex]
		
		let _ = Arc.shared.surveyController.mark(responseTime: question.questionId,
												 question: question.prompt.localized(question.prompt),
												 forSurveyResponse: self.surveyId)
        
		if getInput()?.getValue() != nil {
			enableNextButton(title: question.nextButtonTitle ?? "".localized(ACTranslationKey.button_next))
		} else {
			disableNextButton(title: question.altNextButtonTitle ?? "".localized(ACTranslationKey.button_next))
		}
	}
	public func nextPressed(input: SurveyInput?, value: QuestionResponse?) {
		isValid(value: value, questionId: questions[currentIndex].questionId) { [weak self] valid in
			
			OperationQueue.main.addOperation {
				guard valid else {return}
				guard let wSelf = self else {return}
				if let value = value {
					
					wSelf.valueSelected(value: value, index: wSelf.questions[wSelf.currentIndex].questionId)
					
				} else {
					wSelf.valueSelected(value: AnyResponse(type: .none, value: nil), index: wSelf.questions[wSelf.currentIndex].questionId)
				}
				var nextQuestion = wSelf.questions.index(wSelf.currentIndex, offsetBy: 1, limitedBy: wSelf.questions.count - 1)
				
				guard wSelf.currentIndex < wSelf.questions.count else {
					//Move on to the next step
					return
				}
				guard let value = value, value.value != nil else {
					if let nextQ = nextQuestion  {
						let question = wSelf.questions[nextQ]
						wSelf.addController(wSelf.customViewController(forQuestion: question))
					} else {
						//No questions were added, move on to the next step
						wSelf.setCompleted()
					}
					return
				}
				
				
				let question = wSelf.questions[wSelf.currentIndex]
				//Check to see if any questions were added as a result of a choice
				wSelf.updateNextQuestion(question: question, answer: value)
				nextQuestion = wSelf.questions.index(wSelf.currentIndex, offsetBy: 1, limitedBy: wSelf.questions.count - 1)
				
				if nextQuestion != nil {
					
					wSelf.addController(wSelf.customViewController(forQuestion: wSelf.questions[nextQuestion!]))
				} else {
					//No questions were added, move on to the next step
					wSelf.setCompleted()
				}
			}
		}

	}
	
	open override func popViewController(animated: Bool) -> UIViewController? {
		let vc = super.popViewController(animated: animated)

		currentIndex = viewControllers.count - 1
		print("Controller index:", currentIndex)
		set(error: nil)
		while answeredQuestions.count >= viewControllers.count && answeredQuestions.count > 0 {
			_ = answeredQuestions.popLast()
		}
		return vc

	}
    
    open override func popToRootViewController(animated: Bool) -> [UIViewController]? {
        let vcs = super.popToRootViewController(animated: animated);
        
        currentIndex = viewControllers.count - 1
        print("Controller index:", currentIndex)
		set(error: nil)

        while answeredQuestions.count >= viewControllers.count && answeredQuestions.count > 0 {
            _ = answeredQuestions.popLast()
        }
        
        return vcs;
    }
    
	/// This override will control what to display as the questions unfold
	///
	open override func pushViewController(_ viewController: UIViewController, animated: Bool) {
		super.pushViewController(viewController, animated: animated)
		if autoAdvancePageIndex {
			currentIndex = viewControllers.count - 1
		}
		print(currentIndex)
		guard currentIndex < questions.count else {
			return
		}
		if autoAdvancePageIndex {

			display(question: questions[currentIndex])
		}
		if currentViewControllerAlwaysHidesBarButtons {
				displayHelpButton(false)
				displayBackButton(false)
		} else {
			if shouldShowHelpButton {
				displayHelpButton(shouldShowHelpButton)
			}
			if shouldShowBackButton{
				displayBackButton(shouldShowBackButton)
			}
		}
		
	}
	
	public func updateNextQuestion(question:Survey.Question, answer:QuestionResponse){
		self.answeredQuestions.append(question)

		if let routes = question.routes?.reversed() {
			for route in routes {
				//Make sure to insert new subquestions only (if they go back)
				
				questions = questions.filter({ (q) -> Bool in
					return q.questionId != route.nextQuestionId
				})
				switch answer.value {
				case let value as String:
					if value == route.value as? String {
						let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
						questions.insert(question, at: currentIndex + 1)
					}
				case let number as Int:
					
					if number == route.value as? Int {
						let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
						questions.insert(question, at: currentIndex + 1)
					} else if let validAnswers = (route.value as? [Int]), validAnswers.contains(number) {
						let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
						questions.insert(question, at: currentIndex + 1)
					}
					
					
				case let intArray as [Int]:
						for value in intArray {
						if let validAnswers = (route.value as? [Int]), validAnswers.contains(value) {
							let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
							questions.insert(question, at: currentIndex + 1)
						} else if value == route.value as? Int {
							let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
							questions.insert(question, at: currentIndex + 1)
						}
					}
				case .none:
					break
				case .some(_):
					break
				}
				
			}
		}
	}
}
