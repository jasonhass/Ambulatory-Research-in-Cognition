//
//  SymbolsTestViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/14/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class DNSymbolsTestViewController: DNTestViewController {
    
    @IBOutlet weak var option1: UIView!
    @IBOutlet weak var option2: UIView!
    @IBOutlet weak var option3: UIView!

    @IBOutlet weak var choice1: UIView!
    @IBOutlet weak var choice2: UIView!
    private var currentTrialData:DNSymbolInputData!
    private var test:DNSymbolsTest?

    override func getTest<T : DNTest>() -> T? {
        //If our test matches what the outside context wants, pass it back
        return self.test as? T
    }

    override func setTest<T : DNTest>(test: T) {
        self.test = test as? DNSymbolsTest
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        //Create the test optionally with a duration
        
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)


    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        //Trigger the begining of the test
    
        if isBeingPresented {
            self.test?.startTest()
        }
        
        layoutOptionsAndChoices()
    }
    private func layoutOptionsAndChoices(){

        guard let choicesAndOptions = test?.currentTestSet else {
            return
        }

        //Set each of the options
        configureOption(view: option1, symbolSet: choicesAndOptions.options[0])
        configureOption(view: option2, symbolSet: choicesAndOptions.options[1])
        configureOption(view: option3, symbolSet: choicesAndOptions.options[2])

        //Set each of the tappable choices
        configureOption(view: choice1, symbolSet: choicesAndOptions.choices[0])
        configureOption(view: choice2, symbolSet: choicesAndOptions.choices[1])
        let date = NSDate()

        currentTrialData = DNSymbolInputData()
        currentTrialData.referenceTime = date
        test?.testSetAppearanceTime.append(date)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func configureOption(view:UIView, symbolSet:DNSymbolSet){
        guard let top = view.viewWithTag(1) as? UIImageView else {
            print(view.subviews)
            return
        }
        guard let bottom = view.viewWithTag(2)as? UIImageView else {
            print(view.subviews)

            return
        }
        top.image = test?.getSymbol(index: symbolSet.symbols[0])
        bottom.image = test?.getSymbol(index: symbolSet.symbols[1])

    }
    @IBAction func optionSelected(_ sender: SymbolSelectButton) {
        currentTrialData.timeTouched = sender.touchTime! as NSDate
        currentTrialData.choice = sender.tag
        currentTrialData.touchLocation = sender.touchLocation!
        
        test?.touchLocations.append(sender.touchLocation!);
        test?.touchTimes.append(sender.touchTime!);
        test?.selectValue(option: currentTrialData as AnyObject?)
        
        if (test?.currentQuestion)! >= (test?.numQuestions)! {
            test?.endTest()
            super.endTest()

        }
        else
        {
            test?.nextStep()
            layoutOptionsAndChoices();
        }

    }
    


}


