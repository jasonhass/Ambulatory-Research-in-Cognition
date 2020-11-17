//
// SurveyNavigationViewController.swift
//



import UIKit
@available(*, deprecated, message: "Use BasicSurveyViewController instead. If this is a subclass update its conformance to BasicSurveyViewController.")
open class SurveyNavigationViewController: UINavigationController, UINavigationControllerDelegate, SurveyInput, SurveyInputDelegate {
	

    public var orientation: UIStackView.Alignment = .center
    
	open weak var surveyInputDelegate: SurveyInputDelegate?

	var app = Arc.shared
    public var survey:Survey! = nil
    public var surveyId:String?
    public var participantId:Int?
	
    public var questions:Array<Survey.Question> = []
	public var shouldShowIntro:Bool = true
	public var shouldShowOutro:Bool = true

	public var answeredQuestions:[String] = []
	public var surveyType:SurveyType?
    public var instructionIndex = 0
	public var currentQuestion:String?
    private var displayed = false
	public var shouldNavigateToNextState = true
	public var shouldShowHelpButton = false
	public var shouldShowBackButton = true
	public var helpPressed:(()->())?
	public var currentIndex:Int = 0

	public var isShowingHelpButton = false{
		didSet {
			if shouldShowHelpButton {
				displayHelpButton(shouldShowHelpButton)
			} else {
				displayHelpButton(false)
				
			}
		}
	}
	public var helpButton: UIBarButtonItem?
    
    
    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        delegate = self
    }
    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if displayed == false {
			displayed = true

			next(nil)
        }
    }
    public func getValue() -> QuestionResponse? {
        guard let input = topViewController as? SurveyInput else {
            return nil
        }
        return input.getValue()
    }
    
    public func setValue(_ value: QuestionResponse?) {
        guard let input = topViewController as? SurveyInput else {
            return
        }
        input.setValue(value)
    }
    public func setError(message: String?) {
        guard let input = topViewController as? SurveyInput else {
            return
        }
        input.setError(message: message)
    }
	public func didChangeValue() {
		
	}
	public func nextPressed(input: SurveyInput?, value: QuestionResponse?) {
		
		let instructionCount = survey.instructions?.count ?? 0
		
		if currentIndex < instructionCount {
			
			next(nil)
		}
		else if let current = currentQuestion {
			
			//Check if its nil OR if the input is informational. 
			if (input == nil || input!.isInformational())  {
				valueSelected(value: AnyResponse(type: QuestionType.none, value: nil), index: current)
			}
			else if let selection = value,
				!selection.isEmpty(),
				isValid(value: selection, index: current)
			{
				valueSelected(value: selection,index: current)

			}
			
			next(currentQuestion)
		} else {
			next(nil)
		}

	}
	

	public func templateForQuestion(id: String) -> Dictionary<String, String> {
		return [:]
	}
	
	public func didPresentQuestion(input: SurveyInput?) {
		assert(currentQuestion != nil, "No question to present, check code path.")
		guard let currentQuestion = currentQuestion else {
			return
		}
		if let surveyId = self.surveyId {
			
			let question = Arc.shared.surveyController.get(question: currentQuestion)
			
			Arc.shared.surveyController.mark(displayTime: currentQuestion,
											 question: question.prompt,
											 forSurveyResponse: surveyId)
		}
		if let input = input {
			questionDisplayed(input: input, index: currentQuestion)
		}
	}
	
	public func didFinishSetup() {
		guard let currentQuestion = currentQuestion else {
			return
		}
		onFinishSetup(index: currentQuestion)
	}
	
	public func tryNextPressed() {
		
	}
	open func questionDisplayed(input:SurveyInput, index:String) {
		
		let question = Arc.shared.surveyController.get(question: index)
		//If we set a value for this test then load it
		if let id = surveyId {
			let questionId = question.questionId
			if let value = Arc.shared.surveyController.getResponse(forQuestion: questionId, fromSurveyResponse: id) {
				
				
				if value.isEmpty(){
					input.setValue(nil)
				} else {
					input.setValue(value)
				}
			}
		}
		
		
	}
	
	open func onFinishSetup(index:String) {
	
	}
	open func valueChanged(index:String) {
		
		let question = Arc.shared.surveyController.get(question: index)
        guard self.surveyId != nil else {
			return
		}
		let _ = Arc.shared.surveyController.mark(responseTime: question.questionId,
												 question: question.prompt,
												 forSurveyResponse: self.surveyId!)
		
	}
	//Override this to write to other controllers
	
	open func valueSelected(value:QuestionResponse, index:String) {
		
		let question = Arc.shared.surveyController.get(question: index)
		
		let _ = Arc.shared.surveyController.set(response: value,
												questionId: question.questionId,
												question: question.prompt,
												forSurveyId: self.surveyId!)
		
		
		
		//        dump(response.toString())
		
	}
    public func enableNextButton()
    {
        guard let input = topViewController as? SurveyViewController else {
            return
        }
        input.enableNextButton()
    }
    
    public func disableNextButton()
    {
        guard let input = topViewController as? SurveyViewController else {
            return
        }
        input.disableNextButton()
    }
    //Override this to prepare a controller
    open func loadSurvey(template:String) {
        self.loadSurvey(template: template, surveyId: nil)
    }
    
    //Override this to prepare a controller
    open func loadSurvey(template:String, surveyId:String? = nil) {
		let app = Arc.shared
        survey = app.surveyController.load(survey: template)
		surveyType = survey.type
        questions = survey.questions
		currentQuestion = survey.questions.first?.questionId
        guard let i = app.studyController.getCurrentStudyPeriod()?.studyID else {
            self.surveyId = createSurvey(surveyId: surveyId)

            return
        }
        
		let studyId = Int(i)
		guard let sessionId = app.currentTestSession else {
            
           self.surveyId = createSurvey(surveyId: surveyId)
			return
		}
        
		let session = app.studyController.get(session: sessionId, inStudy: studyId)

		if	let surveyType = surveyType,
			let data = session.surveyFor(surveyType: surveyType){
			Arc.shared.surveyController.mark(startDate: data.id!)

			self.surveyId = data.id
			
		}
    }
    private func createSurvey(surveyId:String?) -> String{
        var response:String = ""
        if let surveyId = surveyId {
            response = Arc.shared.surveyController.create(surveyResponse: surveyId, type: surveyType)
        } else {
            response = Arc.shared.surveyController.create(type: surveyType)
            
        }
        
        Arc.shared.surveyController.mark(startDate: response)
        return response
    }
	open func shuffleQuestions() {
		questions = questions.shuffled()
	}
    private func displayIntro(index:Int) -> Bool {
		if !shouldShowIntro {
			return false
		}
		
        if let instructions = survey?.instructions,
            index < instructions.count
             {
            let instruction = instructions[index]
			let vc:IntroViewController = IntroViewController()
			vc.loadViewIfNeeded()
            
            self.pushViewController(vc, animated: true)
            
            vc.set(heading:     instruction.title,
                   subheading:  instruction.subtitle,
				   content:     instruction.preface,
				   template: templateForInstruction(instruction: index))
				vc.nextButtonTitle = instruction.nextButtonTitle
                vc.nextButtonImage = instruction.nextButtonImage

               vc.inputDelegate = self
			self.isShowingHelpButton = false
            return true
        }
        return false
    }
	
	private func displayOutro(index:Int) -> Bool {
		if !shouldShowIntro {
			return false
		}
		if let instructions = survey?.postSurvey,
               index < instructions.count,
				index >= 0
		{
			let instruction = instructions[index]
			let vc:IntroViewController = IntroViewController()
			vc.loadViewIfNeeded()
            vc.instructionIndex = index
			if !shouldShowBackButton {
				vc.shouldHideBackButton = true
			}
			self.pushViewController(vc, animated: true)
			
			vc.set(heading:     instruction.title,
				   subheading:  instruction.subtitle,
				   content:     instruction.preface,
				   template: templateForPostSurvey(instruction: index))
			vc.nextButtonTitle = instruction.nextButtonTitle
            vc.nextButtonImage = instruction.nextButtonImage

			vc.inputDelegate = self
			self.isShowingHelpButton = false

			return true
		}
		return false
	}
    
    
    private func displayInstruction() {
        
    }
    //TODO: Refactor survey question display, keep in mind old versions of surveys
    open func displayQuestion(index:String) -> Bool {
        let question = Arc.shared.surveyController.get(question: index)
		currentQuestion = index

        if let style = question.style, style == .instruction {
            
			let vc:IntroViewController = IntroViewController()
			vc.loadViewIfNeeded()
            vc.isIntersitial = true

            if !shouldShowBackButton {
                vc.shouldHideBackButton = true
            }
            self.pushViewController(vc, animated: true)
            
            vc.set(heading:     question.prompt,
                   subheading:  question.detail,
				   content:     question.content,
				   template: templateForQuestion(id: index))
            vc.nextButtonTitle = question.nextButtonTitle
            vc.nextButtonImage = question.nextButtonImage
           	vc.inputDelegate = self
            self.isShowingHelpButton = false
            
            return true
        }
		let vc:SurveyViewController = SurveyViewController()
		if let helpPressed = helpPressed {
			vc.helpPressed = helpPressed
		}
		vc.surveyInputDelegate = self
		vc.surveyId = surveyId
	
		self.pushViewController(vc, animated: true)
		
		vc.set(questionIndex: index)

		self.isShowingHelpButton = true

        //didPresentQuestion(input: vc.input!)
        
		return true
		
    }
	
    open func isValid(value:QuestionResponse, index: String) -> Bool {
        //Override if needing validation
        return true
    }
	
	
    
    open func templateForInstruction(instruction:Int) -> Dictionary<String, String> {
        return [:]
    }

    open func templateForPostSurvey(instruction:Int) -> Dictionary<String, String> {
        return [:]
    }
    
	private func next(_ questionId: String?) {
		let instructionCount = survey.instructions?.count ?? 0
		let questionCount = questions.count
		let outroIndex = self.viewControllers.count - instructionCount - questionCount
		
		
		let introDisplayed = displayIntro(index: currentIndex)
		//If an introductory slide was displayed, then return and wait for user input
		guard !introDisplayed else {
			return
		}
		currentQuestion = figureOutNextQuestion(id: questionId)
		//If any questions remain...
		if let question = currentQuestion{
			//Display a question...
			guard !displayQuestion(index: question) else {
			
				//If a question was displayed return
				return
			}
		}
		
		//If we display any exiting slides return
		guard !displayOutro(index: outroIndex) else {
			return
		}
		
	
		
		//If this survey is being stored in core data
		//mark it as filled out.
		if let id = surveyId {
			_ = Arc.shared.surveyController.mark(filled: id)
		}
		
		//Subclasses may perform conditional async operations
		//that determine if the app should proceed.
		if shouldNavigateToNextState {
			Arc.shared.nextAvailableState()
		}
    }
	
    
	open func figureOutNextQuestion(id:String?) -> String? {
		
		if let instructionCount = survey.instructions?.count {
			guard currentIndex >= instructionCount else {
				return nil
			}
		}
		
		guard let id = id else {
			if answeredQuestions.count < questions.count {
				return questions[answeredQuestions.count].questionId
			} else {
				return nil
			}
		}
		self.answeredQuestions.append(id)
		
		
		if let surveyId = surveyId {
			
            let question = Arc.shared.surveyController.get(question: id)

			let answer = Arc.shared.surveyController.getResponse(forQuestion: id, fromSurveyResponse: surveyId)
            
			if let routes = question.routes?.reversed() {
				for route in routes {
					//Make sure to insert new subquestions only (if they go back)
					_ = Arc.shared.surveyController.delete(response: route.nextQuestionId, fromSurveyResponse: surveyId)

					questions = questions.filter({ (q) -> Bool in
						return q.questionId != route.nextQuestionId
					})
					switch answer?.value {
					case let value as String:
						if value == route.value as? String {
							let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
							questions.insert(question, at: self.answeredQuestions.count)
						}
					case let number as Int:
						
						if number == route.value as? Int {
							let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
							questions.insert(question, at: self.answeredQuestions.count)
						} else if let validAnswers = (route.value as? [Int]), validAnswers.contains(number) {
							let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
							questions.insert(question, at: self.answeredQuestions.count)
						}
							
					
					case let intArray as [Int]:
						for value in intArray {
							if let validAnswers = (route.value as? [Int]), validAnswers.contains(value) {
								let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
								questions.insert(question, at: self.answeredQuestions.count)
							} else if value == route.value as? Int {
								let question = Arc.shared.surveyController.get(question: route.nextQuestionId)
								questions.insert(question, at: self.answeredQuestions.count)
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
		if answeredQuestions.count < questions.count {
			return questions[answeredQuestions.count].questionId
		} else {
			return nil
		}
		
	}
    
	open override func popViewController(animated: Bool) -> UIViewController? {
		let vc = super.popViewController(animated: animated)
		currentIndex = viewControllers.count
		print(currentIndex)
		currentQuestion = figureOutNextQuestion(id: nil)

		return vc
		
	}
	open override func pushViewController(_ viewController: UIViewController, animated: Bool) {
		super.pushViewController(viewController, animated: animated)
		
		currentIndex = viewControllers.count
		print(currentIndex)
		
	}
	open func navigationController(_ navigationController: UINavigationController, didShow viewController: UIViewController, animated: Bool) {
        HMLog("Changed stuff \(viewController)")
		
		//Pop off questions that got popped off the viewcontroller stack
		let surveyControllerCount = navigationController.viewControllers.filter { (vc) -> Bool in
            if let vc = vc as? IntroViewController {
                return vc.isIntersitial
            }
			return vc is SurveyViewController
		}
		while answeredQuestions.count >= surveyControllerCount.count && answeredQuestions.count > 0 {
			_ = answeredQuestions.popLast()
		}
	}
	
	open func displayHelpButton(_ shouldShow:Bool) {
		if shouldShow {
			
            var rightButton:UIBarButtonItem? = nil
			if helpButton == nil {
                
                let helpButton = UIButton(type: .custom)
                helpButton.frame = CGRect(x: 0, y: 0, width: 60, height: 10)
                helpButton.setTitle("HELP".localized(ACTranslationKey.button_help), for: .normal)
                helpButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
                helpButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: -16)
                helpButton.setTitleColor(UIColor(named: "Primary"), for: .normal)
                helpButton.addTarget(self, action: #selector(self.contactStudyCoordinatorPressed), for: .touchUpInside)
                
                rightButton = UIBarButtonItem(customView: helpButton)
            }
			topViewController?.navigationItem.rightBarButtonItem = rightButton


		} else {
			self.navigationItem.rightBarButtonItem = nil
			helpButton = nil
		}
	}
	@objc open func contactStudyCoordinatorPressed() {
		//Supply project specific handler to prevent white screen
		if let helpPressed = helpPressed {
			helpPressed()
		} else {
			app.appNavigation.navigate(vc: app.appNavigation.defaultHelpState(), direction: .toRight)
		}
		
	}
}
