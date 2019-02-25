//
// PricesTestViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

public class PricesTestViewController: ArcViewController {
    
    @IBOutlet weak var itemNameLabel: UILabel!
    @IBOutlet weak var itemPriceLabel: UILabel!
    @IBOutlet weak var goodPriceLabel: UILabel!
    
    @IBOutlet weak var buttonStack: UIStackView!
    private var questionDisplay:PricesQuestionViewController?

    var controller = Arc.shared.pricesTestController
    var test:PriceTest?
    var responseID = ""
//    private var questionDisplay:DNPricesQuestionViewController?
//    private var test:DNPricesTest?
    private var itemIndex = 0
    private var questionIndex = 0
    private var flippedPrices:Set<Int>! = nil
    var displayTimer:Timer?;
    
    // Buttons
    private var views:[ChoiceView] = []
    let topButton:ChoiceView = .get()
    let bottomButton:ChoiceView = .get()
    
//    override func getTest<T : DNTest>() -> T? {
//        //If our test matches what the outside context wants, pass it back
//        return self.test as? T
//    }
//
//    override func setTest<T : DNTest>(test: T) {
//        self.test = test as? DNPricesTest
//
//    }
    
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        //topButton.shouldAutoToggle = false;
        //bottomButton.shouldAutoToggle = false;
        ACState.testCount += 1

        buildButtonStackView()
		let app = Arc.shared
		let studyId = Int(app.studyController.getCurrentStudyPeriod()?.studyID ?? -1)
		let sessionId = app.currentTestSession ?? -1
		let session = app.studyController.get(session: sessionId, inStudy: studyId)
		if let data = session.surveyFor(surveyType: .priceTest){
			
			responseID = data.id! //A crash here means that the session is malformed
			
		} else {
		
        	test = controller.loadTest(index: 0, file: "priceSets-en-US")
        	responseID = controller.createResponse(withTest: test!)
		}
        // Do any additional setup after loading the view.
    }
    
    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
            _ = controller.start(test: responseID)
		_  = controller.mark(filled: responseID)

            displayItem()
        
    }
	public override func viewDidDisappear(_ animated: Bool) {
		super.viewDidDisappear(animated)
		displayTimer?.invalidate()
	}
    override open func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func nextItem()
    {
        itemIndex += 1;
		
        displayItem();
    }
    
    func displayItem() {
        
        displayTimer?.invalidate();
        
        topButton._isSelected = false;
        topButton.isUserInteractionEnabled = true;
        bottomButton._isSelected = false;
        bottomButton.isUserInteractionEnabled = true;
        
        if flippedPrices == nil {
            let count = controller.get(testCount: responseID)
            flippedPrices = Set<Int>.uniqueSet(numberOfItems: count / 2, maxValue: count)
            
        }
        if itemIndex < controller.get(testCount: responseID) {
            guard let item = controller.get(question: itemIndex, id: responseID) else {
                return
            }
            let topLabel = (flippedPrices.contains(itemIndex)) ? itemNameLabel : itemPriceLabel
            let bottomLabel = (topLabel == itemNameLabel) ? itemPriceLabel : itemNameLabel
            
            topLabel?.text = item.item
            
            let correctPrice = "$" + item.price
            bottomLabel?.text = correctPrice
            
            bottomLabel?.resizeFontForSingleWords();
            topLabel?.resizeFontForSingleWords();
            
            _ = controller.mark(stimulusDisplayTime: responseID, index: itemIndex)
            
            displayTimer = Timer.scheduledTimer(timeInterval: 3.0, target: self, selector: #selector(nextItem), userInfo: nil, repeats: false)
        } else {
			
			let onConfirm = {[weak self] in
				if let weakSelf = self {
					
					//Present controller
					weakSelf.questionDisplay = .get()
					weakSelf.questionDisplay?.responseId = weakSelf.responseID
					
					weakSelf.present(weakSelf.questionDisplay!, animated: false, completion: { [weak self] in
						guard let weakself = self else {
							return
						}
						weakself.questionDisplay?.selectQuestion()
					})
				}
			}
			
			Arc.shared.displayAlert(message: "You will now start the test.\nYou will see an item and two prices. Please select the price that matches the item you studied.", options: [
				
				.delayed(name: "BEGIN", delayTime: 3.0, onConfirm),
				
				.wait(waitTime: 12.0, onConfirm)
				
			])
			
            
            self.topButton.isHidden = true;
            self.bottomButton.isHidden = true;
            self.goodPriceLabel.isHidden = true;
            self.itemNameLabel.text = ""
            self.itemPriceLabel.text = ""
            //Suffle before input
            
        }
        
    }
    
    func buildButtonStackView() {
        topButton.set(message: "Yes")
        bottomButton.set(message: "No")
        
        topButton.needsImmediateResponse = true
        bottomButton.needsImmediateResponse = true
        
        topButton.button.titleLabel?.numberOfLines = 1
        bottomButton.button.titleLabel?.numberOfLines = 1
        
        topButton.set(state: .radio)
        bottomButton.set(state: .radio)
        
        topButton.tapped = {
            [weak self] view in
            self?.yesPressed()
        }
        
        bottomButton.tapped = {
            [weak self] view in
            self?.noPressed()
        }
        
        views.append(topButton)
        views.append(bottomButton)
        buttonStack.addArrangedSubview(topButton)
        buttonStack.addArrangedSubview(bottomButton)
    }
    
    func yesPressed() {
        if itemIndex < controller.get(testCount: responseID) {
            
            topButton.set(selected: true)
            bottomButton.set(selected: false)
            
            let p = controller.set(goodPrice: 1, id: responseID, index: itemIndex)
//            print(p.toString())
        }
    }
    
    func noPressed() {
        if itemIndex < controller.get(testCount: responseID) {
            
            topButton.set(selected: false)
            bottomButton.set(selected: true)
            
            let p = controller.set(goodPrice: 0, id: responseID, index: itemIndex)
//            print(p.toString())
        }
    }
    
    //MARK: - DNPriceQuestionDelegate
    
//    func didAnswerWithValue(data: DNPriceInputData) {
//        test?.testData.items[questionIndex].inputData = data;
//        questionIndex += 1
//
//        if questionIndex < test!.itemCount {
//            questionDisplay?.presentQuestion(item: (test?.testData.items[questionIndex])!,variation: (self.test is DNPricesTestVariationA) ? 1 : 0)
//        } else {
//            self.questionDisplay?.dismiss(animated: false, completion: nil)
//
//            test?.endTest()
//
//            endTest()
//            self.itemNameLabel.text = ""
//            self.itemPriceLabel.text = ""
//
//        }
//
//    }
    
}



