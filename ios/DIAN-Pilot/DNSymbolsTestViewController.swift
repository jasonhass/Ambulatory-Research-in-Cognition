/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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


