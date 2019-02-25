//
// SurveyNavigationViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class SurveyNavigationViewController: UINavigationController, UINavigationControllerDelegate, SurveyInput {
    public var didChangeValue: (() -> ())?
    public var didFinishSetup: (() -> ())?

    public var orientation: UIStackView.Alignment = .center
    
    public var tryNext: (() -> ())?
    
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
            
			next(nextQuestion: questions.first?.questionId)
        }
        displayed = true
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
		let studyId = Int(app.studyController.getCurrentStudyPeriod()?.studyID ?? -1)
		guard let sessionId = app.currentTestSession else {
            
            var response:String = ""
            if let surveyId = surveyId {
                response = Arc.shared.surveyController.create(surveyResponse: surveyId, type: surveyType)
            } else {
                response = Arc.shared.surveyController.create(type: surveyType)

            }

			Arc.shared.surveyController.mark(startDate: response)
			self.surveyId = response
			questions = survey.questions
			return
		}
		let session = app.studyController.get(session: sessionId, inStudy: studyId)

		if	let surveyType = surveyType,
			let data = session.surveyFor(surveyType: surveyType){
			Arc.shared.surveyController.mark(startDate: data.id!)

			self.surveyId = data.id
			
		}
		questions = survey.questions
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
            let vc:IntroViewController = .get()
            
            self.pushViewController(vc, animated: true)
            
            vc.set(heading:     instruction.title,
                   subheading:  instruction.subtitle,
                   content:     instruction.preface)
				vc.nextButtonTitle = instruction.nextButtonTitle

                vc.nextPressed = {
                    [weak self] in
					self?.next(nextQuestion: self?.questions.first?.questionId)
                }
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
               index < instructions.count
		{
			let instruction = instructions[index]
			let vc:IntroViewController = .get()
			vc.templateHandler = templateForPostSurvey
            vc.instructionIndex = index
			if !shouldShowBackButton {
				vc.shouldHideBackButton = true
			}
			self.pushViewController(vc, animated: true)
			
			vc.set(heading:     instruction.title,
				   subheading:  instruction.subtitle,
				   content:     instruction.preface)
			vc.nextButtonTitle = instruction.nextButtonTitle
			vc.nextPressed = {
				[weak self] in

				self?.next(nextQuestion: nil)

				
			}
			self.isShowingHelpButton = false

			return true
		}
		return false
	}
    
    
    private func displayInstruction() {
        
    }
    //TODO: Refactor survey question display, keep in mind old versions of surveys
    private func displayQuestion(index:String) -> Bool {
        let question = Arc.shared.surveyController.get(question: index)
        if let style = question.style, style == .instruction {
            
            let vc:IntroViewController = .get()
            vc.isIntersitial = true
            vc.templateHandler = templateForPostSurvey

            if !shouldShowBackButton {
                vc.shouldHideBackButton = true
            }
            self.pushViewController(vc, animated: true)
            
            vc.set(heading:     question.prompt,
                   subheading:  question.detail,
                   content:     question.content)
            vc.nextButtonTitle = question.nextButtonTitle
            vc.nextPressed = {
                [weak self] in
                
                let nextQuestion = self?.figureOutNextQuestion(id: index)
                self?.onValueSelected(value: AnyResponse(type: QuestionType.none, value: nil), index: index)
                
                self?.next(nextQuestion: nextQuestion)
                
            }
            self.isShowingHelpButton = false
            
            return true
        }
		let vc:SurveyViewController = .get()
		if let helpPressed = helpPressed {
			vc.helpPressed = helpPressed
		}
		vc.templateHandler = templateForQuestion
		vc.surveyId = surveyId
	
		self.pushViewController(vc, animated: true)
		
		vc.set(questionIndex: index)
		
		//MARK: Next Pressed callback
		vc.nextPressed = {
			[weak self] input, value in
			
			//If the prompt being displayed is informational, move on.
			if input == nil {
				let nextQuestion = self?.figureOutNextQuestion(id: index)
                self?.onValueSelected(value: AnyResponse(type: QuestionType.none, value: nil), index: index)

				self?.next(nextQuestion: nextQuestion)
			} else {
			//else validate it
			
				if let selection = value, !selection.isEmpty(),
					let isValid = self?.isValid(value: selection, index: index), isValid == true {
					
					
					self?.onValueSelected(value: selection, index: index)
					
					let nextQuestion = self?.figureOutNextQuestion(id: index)
					
					self?.next(nextQuestion: nextQuestion)

				}
			}
		}
        vc.didChangeValue = {
            [weak self] in
            
            self?.onValueChanged(index: index)
        }
		//MARK: question Presented callback
		vc.questionPresented = {
			[weak self] input in
			if let surveyId = self?.surveyId {
				let question = Arc.shared.surveyController.get(question: index)

				Arc.shared.surveyController.mark(displayTime: index,
													 question: question.prompt,
													 forSurveyResponse: surveyId)
			}
			if let input = input {
				self?.onQuestionDisplayed(input: input, index: index)
			}

		}
        vc.didFinishSetup = {
            [weak self] in
            
            self?.onFinishSetup(index: index)
        }
		self.isShowingHelpButton = true

		return true
		
    }
	
    open func isValid(value:QuestionResponse, index: String) -> Bool {
        //Override if needing validation
        return true
    }
	
	open func templateForQuestion(questionId:String) -> Dictionary<String, String> {
		return [:]
	}
    
    open func templateForInstruction(instruction:Int) -> Dictionary<String, String> {
        return [:]
    }

    open func templateForPostSurvey(instruction:Int) -> Dictionary<String, String> {
        return [:]
    }
    
	private func next(nextQuestion: String?) {
		let instructionCount = survey.instructions?.count ?? 0
		let questionCount = questions.count
		let outroIndex = self.viewControllers.count - instructionCount - questionCount
        if displayIntro(index: self.viewControllers.count) {
        
        } else if let nextQuestion = nextQuestion {
			
			_ = displayQuestion(index: nextQuestion)
			
		} else if displayOutro(index: outroIndex){
		
		} else {
			
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
    }
    open func onQuestionDisplayed(input:SurveyInput, index:String) {
		
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
        input.didFinishSetup?()

    }
    
    open func onFinishSetup(index:String) {
        
    }
    open func onValueChanged(index:String) {
        
        let question = Arc.shared.surveyController.get(question: index)
        guard let surveyId = self.surveyId else {
            return
        }
        let _ = Arc.shared.surveyController.mark(responseTime: question.questionId,
                                                        question: question.prompt,
                                                        forSurveyResponse: self.surveyId!)

    }
    //Override this to write to other controllers
    open func onValueSelected(value:QuestionResponse, index:String) {
		
		let question = Arc.shared.surveyController.get(question: index)
            
        let _ = Arc.shared.surveyController.set(response: value,
                                                                questionId: question.questionId,
                                                                question: question.prompt,
                                                                forSurveyId: self.surveyId!)
		
		

//        dump(response.toString())
		
    }
    
	open func figureOutNextQuestion(id:String) -> String? {
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
                helpButton.setTitle("HELP", for: .normal)
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
