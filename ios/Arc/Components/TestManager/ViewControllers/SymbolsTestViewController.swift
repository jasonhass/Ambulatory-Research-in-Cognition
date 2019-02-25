//
// SymbolsTestViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit



public class SymbolsTestViewController: UIViewController {
    private var symbols:[Int:UIImage] = [0: #imageLiteral(resourceName: "symbol_0"),
                                         1: #imageLiteral(resourceName: "symbol_1"),
                                         2: #imageLiteral(resourceName: "symbol_2"),
                                         3: #imageLiteral(resourceName: "symbol_3"),
                                         4: #imageLiteral(resourceName: "symbol_4"),
                                         5: #imageLiteral(resourceName: "symbol_5"),
                                         6: #imageLiteral(resourceName: "symbol_6"),
                                         7: #imageLiteral(resourceName: "symbol_7")]
    
    var controller = Arc.shared.symbolsTestController
    var responseID = ""
    var questionIndex = 0
    @IBOutlet weak var option1: UIView!
    @IBOutlet weak var option2: UIView!
    @IBOutlet weak var option3: UIView!
    
    @IBOutlet weak var choice1: UIView!
    @IBOutlet weak var choice2: UIView!
    
//    private var currentTrialData:DNSymbolInputData!
    private var test:SymbolsTest?
    
    
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        //Create the test optionally with a duration
        ACState.testCount += 1

		let app = Arc.shared
		let studyId = Int(app.studyController.getCurrentStudyPeriod()?.studyID ?? -1)
		
		if let sessionId = app.currentTestSession {
			let session = app.studyController.get(session: sessionId, inStudy: studyId)
			let data = session.surveyFor(surveyType: .symbolsTest)
			
			responseID = data!.id! //A crash here means that the session is malformed
			test = controller.generateTest(numSections: 12, numSymbols: 8)
			responseID = controller.createResponse(withTest: test!, id: responseID)
		} else {
		
        	test = controller.generateTest(numSections: 12, numSymbols: 8)
        	responseID = controller.createResponse(withTest: test!)
		}
        
        let gradient = CAGradientLayer()
        gradient.frame = choice1.bounds
        gradient.colors = [UIColor.white.cgColor, UIColor(red: 244.0/255.0, green: 244.0/255.0, blue: 244.0/255.0, alpha: 1.0).cgColor]
        choice1.layer.insertSublayer(gradient, at: 0)
        
        let gradient2 = CAGradientLayer()
        gradient2.frame = choice2.bounds
        gradient2.colors = [UIColor.white.cgColor, UIColor(red: 244.0/255.0, green: 244.0/255.0, blue: 244.0/255.0, alpha: 1.0).cgColor]
        choice2.layer.insertSublayer(gradient2, at: 0)
    }
    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        
        
    }
    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        //Trigger the begining of the test

        _ = controller.start(test: responseID)
		_  = controller.mark(filled: responseID)

        layoutOptionsAndChoices()
    }
    private func layoutOptionsAndChoices(){
        
        let choicesAndOptions = test!.sections[questionIndex]
		
        
        
        //Set each of the options
        configureOption(view: option1, symbolSet: choicesAndOptions.options[0])
        configureOption(view: option2, symbolSet: choicesAndOptions.options[1])
        configureOption(view: option3, symbolSet: choicesAndOptions.options[2])
		
        //Set each of the tappable choices
        configureOption(view: choice1, symbolSet: choicesAndOptions.choices[0])
        configureOption(view: choice2, symbolSet: choicesAndOptions.choices[1])
		
        
        _ = controller.mark(appearanceTimeForQuestion: questionIndex, id: responseID)
    }
    
    override open func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    func getSymbol(index:Int) -> UIImage {
        let img = self.symbols[index]!
        return img.copy() as! UIImage;
    }
    
    func configureOption(view:UIView, symbolSet:SymbolsTest.SymbolSet){
        guard let top = view.viewWithTag(1) as? UIImageView else {
//            print(view.subviews)
            return
        }
        guard let bottom = view.viewWithTag(2)as? UIImageView else {
//            print(view.subviews)
            
            return
        }
        top.image = getSymbol(index: symbolSet.symbols[0])
        bottom.image = getSymbol(index: symbolSet.symbols[1])
		
    }
    @IBAction func optionSelected(_ sender: SymbolSelectButton) {
        _ = controller.mark(timeTouched: questionIndex, date:sender.touchTime!, id: responseID)
        let p = controller.set(choice: sender.tag, forQueston: questionIndex, id: responseID)
//        currentTrialData.touchLocation = sender.touchLocation! //this doesn't appear in the data for current tests
//        print(p!.toString())
//        test?.touchLocations.append(sender.touchLocation!);
//        test?.touchTimes.append(sender.touchTime!);
//        test?.selectValue(option: currentTrialData as AnyObject?)
        
        if questionIndex >= controller.get(questionCount: responseID) - 1 {
			_  = controller.mark(filled: responseID)
			Arc.shared.nextAvailableState()
            
        }
        else
        {
            questionIndex += 1
            layoutOptionsAndChoices();
        }
        
    }
    
}
