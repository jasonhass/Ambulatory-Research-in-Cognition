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



open class PricesQuestionViewController: UIViewController {
    //@IBOutlet var buttons: [UIButton]!
    
    @IBOutlet weak var questionLabel: UILabel!
    //@IBOutlet weak var priceLabel: UILabel!
    
    // Buttons
    @IBOutlet weak var buttonStack: UIStackView!
    private var buttons:[ChoiceView] = []
    let topButton:ChoiceView = .get()
    let bottomButton:ChoiceView = .get()
    
    var controller = Arc.shared.pricesTestController
    var responseId:String = ""
    var questionIndex = 0
    var questions:Set<Int> = []
    var presentedQuestions:Set<Int> = []
    override open func viewDidLoad() {
        super.viewDidLoad();

        
        
    }
    override open func viewWillAppear(_ animated:Bool) {
        super.viewDidAppear(animated)
        buildButtonStackView()
        if isBeingPresented {

            let test:PriceTestResponse = try! controller.get(id: responseId)
            questions = Set(0 ..< test.sections.count)
//            for i in 0..<buttons.count
//            {
//                let b = buttons[i];
//                b.tag = i;
//                b.removeTarget(nil, action: nil, for: .touchUpInside);
//                b.addTarget(self, action: #selector(didSelect(_:)), for: .touchDown);
//            }
        }
    }
//    @IBAction func didSelect(_ sender: UIButton) {
//        guard let id = buttons.index(of: sender) else {
//            return
//        }
//
//        _ = controller.mark(timeTouched: responseId, index: questionIndex)
//        let p = controller.set(choice: id, id: responseId, index: questionIndex)
//        print(p.toString())
//        selectQuestion()
//
//
//    }
    
    func didSelect(id:Int) {
//        guard let id = buttons.index(of: sender) else {
//            return
//        }
        
        _ = controller.mark(timeTouched: responseId, index: questionIndex)
        let p = controller.set(choice: id, id: responseId, index: questionIndex)
//        print(p.toString())
        selectQuestion()
    }
    
    func selectQuestion() {
        if let value = questions.subtracting(presentedQuestions).randomElement() {
            presentQuestion(index: value, id: responseId)
        } else {
			_  = controller.mark(filled: responseId)
			Arc.shared.nextAvailableState()
        }
    }
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
            let priceIndex = buttons.index(of: b)!
            
            let string = controller.get(option: priceIndex, forQuestion: index, id: id)!
            
            b.set(message: "$\(string)") //setTitle("\(string)", for: .normal)
            b.isHidden = false
            
            //b.set(selected: false)
        }
            
            
            
        
    }
    
    func buildButtonStackView() {
        //topButton.set(message: top)
        //bottomButton.set(message: bottom)
        
        topButton.needsImmediateResponse = true
        bottomButton.needsImmediateResponse = true
        
        topButton.button.titleLabel?.numberOfLines = 1
        bottomButton.button.titleLabel?.numberOfLines = 1
        
        topButton.set(state: .radio)
        bottomButton.set(state: .radio)
        
        topButton.tapped = {
            [weak self] view in
            //self?.topButton.set(selected: true)
            if self?.topButton.getSelected() == false {
                self?.didSelect(id: 0)
            }
            self?.topButton.updateState()
        }
        
        bottomButton.tapped = {
            [weak self] view in
            //self?.bottomButton.set(selected: true)
            if self?.bottomButton.getSelected() == false {
                self?.didSelect(id: 1)
            }
            self?.bottomButton.updateState()
        }
        
        buttons.append(topButton)
        buttons.append(bottomButton)
        buttonStack.addArrangedSubview(topButton)
        buttonStack.addArrangedSubview(bottomButton)
    }
    
}

