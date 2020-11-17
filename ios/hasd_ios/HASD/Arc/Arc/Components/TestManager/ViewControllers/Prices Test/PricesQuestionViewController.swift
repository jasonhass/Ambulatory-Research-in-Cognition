//
// PricesQuestionViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import UIKit


open class PricesQuestionViewController: ArcViewController, TestProgressViewControllerDelegate {
	
	
    //@IBOutlet var buttons: [UIButton]!
    
    @IBOutlet weak var questionLabel: UILabel!
    @IBOutlet weak var questionLabel2: UILabel!
	public var questionAlignment:NSTextAlignment = .left
    //@IBOutlet weak var priceLabel: UILabel!
    
    // Buttons
    @IBOutlet weak var buttonStack: UIStackView!
	@IBOutlet weak var questionDisplay: UIStackView!
	private var buttons:[ChoiceView] = []
	public var shouldAutoProceed = true
    let topButton:ChoiceView = .get()
    let bottomButton:ChoiceView = .get()
	weak var delegate:PricesTestDelegate?

    var controller = Arc.shared.pricesTestController
    var responseId:String = ""
    var questionIndex = 0
    var questions:Set<Int> = []
    var presentedQuestions:Set<Int> = []
    var isTutorial:Bool = false
    override open func viewDidLoad() {
        super.viewDidLoad();

        
        
    }
    override open func viewWillAppear(_ animated:Bool) {
        super.viewDidAppear(animated)
        buildButtonStackView()
		questionLabel.textAlignment = questionAlignment
		questionLabel2.textAlignment = questionAlignment
        if isBeingPresented {

			
			prepareQuestions()
        }
    }
	public func prepareQuestions() {
		let test:PriceTestResponse = try! controller.get(id: responseId)
		questions = Set(0 ..< test.sections.count)
	}
	public func getCorrectOption() -> Int {
		
		return controller.get(correctOptionforQuestion: questionIndex, id: responseId) ?? -1
	}
    public func didSelect(id:Int) {
		
        _ = controller.mark(timeTouched: responseId, index: questionIndex)
        _ = controller.set(choice: id, id: responseId, index: questionIndex)
		if shouldAutoProceed {
        	selectQuestion()
		}
		delegate?.didSelectPrice(id)
    }
	
	/// Select question will check to see what prices were shown to the user
	/// and then pick a random value that has NOT been presented to the user.
	/// if it cannot find a value it will mark the test as complete and defer
	/// to the application for navigation.
    public func selectQuestion() {
		var value:Int? = nil
		if isTutorial {
			value = questions.subtracting(presentedQuestions).sorted().first

		} else {
			value = questions.subtracting(presentedQuestions).randomElement()

		}
        if let v = value {
            presentQuestion(index: v, id: responseId)
        } else {
			if delegate?.shouldEndTest() ?? true {

				_  = controller.mark(filled: responseId)

				let nextMessage = (ACState.testCount == 3) ? "Well done!".localized(ACTranslationKey.testing_done) : "Loading next test...".localized(ACTranslationKey.testing_loading)
				
				let vc = TestProgressViewController(title: "Prices Test Complete!".localized(ACTranslationKey.prices_complete), subTitle: nextMessage, count: ACState.testTaken - 1)
				vc.delegate = self
				self.addChild(vc)
				self.view.anchor(view: vc.view)
				vc.set(count: ACState.testTaken)
				vc.waitAndExit(time: 3.0)
				

			
			
			}
        }
    }
	
	///Reset the state of the buttons, his is more prevalent to tutorials as in the
	///actual test the app proceeds automatically and resets state.
	public func deselectButtons() {
		topButton.set(selected: false)
		bottomButton.set(selected: false)

	}
	
	/// Present question will track question indicies passed into this function.
	/// It will then fetch the data for that question and configure the view.
	/// Once the user makes a selection it will immediately present the next question.
	/// - Parameter index: The index of a question to be presented
	/// - Parameter id: the id of the survey to present the question from
    func presentQuestion(index:Int, id:String){
        presentedQuestions.insert(index)
        questionIndex = index
        responseId = id
		
		
        _ = controller.mark(questionDisplayTime: id, index: index)
        let item = controller.get(question: index, id: id)
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        
        questionLabel.text = String(describing: item!.item)
        
        buttons.forEach { (b) in
            b.isHidden = true
        }
        for b in buttons {
            let priceIndex = buttons.firstIndex(of: b)!
            
            let string = controller.get(option: priceIndex, forQuestion: index, id: id)!
            
            if isTutorial {
                b.set(message: string)
            } else {
                b.set(message: "\("".localized(ACTranslationKey.money_prefix))\(string)\("".localized(ACTranslationKey.money_suffix))")
            }
            b.isHidden = false
            
            b.set(selected: false, shouldUpdateColors: false)
        }
            
    }
    
    public func buildButtonStackView() {
        //topButton.set(message: top)
        //bottomButton.set(message: bottom)
        
        topButton.needsImmediateResponse = true
        bottomButton.needsImmediateResponse = true
        
        topButton.button.titleLabel?.numberOfLines = 1
        bottomButton.button.titleLabel?.numberOfLines = 1
        
        topButton.set(state: .button)
        bottomButton.set(state: .button)
        
        topButton.tapped = {
            [weak self] view in
            if self?.topButton.getSelected() == false {
                self?.topButton.set(selected: true)
                self?.didSelect(id: 0)
            }
        }
        
        bottomButton.tapped = {
            [weak self] view in
            if self?.bottomButton.getSelected() == false {
                self?.bottomButton.set(selected: true)
                self?.didSelect(id: 1)
            }
        }
        
        buttons.append(topButton)
        buttons.append(bottomButton)
        buttonStack.addArrangedSubview(topButton)
        buttonStack.addArrangedSubview(bottomButton)
    }
	public func testProgressDidComplete() {
		//If the delegate implements this method and returns false it will not proceed automatically.
		Arc.shared.nextAvailableState()
	}
}

