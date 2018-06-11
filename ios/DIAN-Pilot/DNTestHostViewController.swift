/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
import Zip
import CoreData


class DNTestHostViewController: DNViewController, DNTestViewControllerDelegate {
    @IBOutlet weak var progressLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var instructionContainer: UIView!
    @IBOutlet weak var startButton: UIButton!
    @IBOutlet weak var lineView: UIView!
    
    
    var currentTest:Int = -1
    var page:Int = 0
    var testDone = false
    var authenticated:Bool = false
    var exitAuthentication:Bool = false
    var tests:[DNTest] = []
    var currentTask:URLSessionDataTask?
        
    var currentArc:TestVisit?;
    var currentSession:TestSession?;
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.currentArc = TestVisit.getCurrentVisit();
        self.currentSession = DNDataManager.sharedInstance.currentTestSession
        
        loadTests()
        self.startTests();
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.view.layoutSubviews()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //MARK: - test flow
    
    func startTests()
    {
        DNDataManager.save();
        currentTest = 0;
        
        displayTestInstructions();
    }
    
    func endTests()
    {
        
        var nextState:AppState = .surveyInterrupted;
        
        if (self.currentSession?.isLastSession())!
        {
            nextState = .surveyFinalTest;
        }
        
        DNDataManager.save();
        
        AppDelegate.go(state: nextState);
    }
    
    func nextTest()
    {
        let vc:TextAlertViewController = DNAlertViewController.GetAlertController()
        
        DNDataManager.save();
        
        vc.setText(string: "You finished this test, ready for the next one?".localized());
        vc.setConfirmText(string: "Next".localized());
        vc.onConfirm = {[weak self] ret in
            if let weakSelf = self {
                DispatchQueue.main.async {
                    weakSelf.displayTestInstructions();
                }
            }
        }
        self.presentedViewController?.view.isHidden = true;
        self.startButton.isHidden = true;
        
        self.dismiss(animated: false) {
        }
        
        self.addChildViewController(vc);
        self.view.addSubview(vc.view);
    }
    


    @IBAction func nextPressed(_ sender: UIButton) {

        page += 1
        //Get the information for the current test
        let testDesc = tests[currentTest].getTestDescription()
       
        if page >= testDesc.pages.count
        {
            self.displayTest(index: currentTest);
        }
        else
        {
            self.displayTestInstructions();
        }
    }

    //Load test
    func loadTests(){
        
        if(DNRestAPI.shared.dontRandomize)
        {
            tests = [DNGridTest(), DNSymbolsTest(), DNPricesTest(setId: Int(max(0,currentSession!.sessionID)) + Int(max(0, currentSession!.testVisit!.visitID)) * SESSIONS_PER_DAY * DAYS_PER_ARC)];
        }
        else
        {
            tests = [DNSymbolsTest(), DNGridTest(), DNPricesTest(setId: Int(max(0,currentSession!.sessionID)) + Int(max(0, currentSession!.testVisit!.visitID)) * SESSIONS_PER_DAY * DAYS_PER_ARC) ]
            tests.shuffle();
        }
    }
    
    
    func clearInstructions()
    {
        if let v = instructionContainer.subviews.first {
            
            v.removeFromSuperview()
            
        }
        for c in self.childViewControllers
        {
            c.removeFromParentViewController();
        }
        
        
        lineView.isHidden = true;
        progressLabel.text = "";
        titleLabel.text = "";
        
        page = 0;
    }
    
    func displayTestInstructions()
    {
        let testDesc = tests[currentTest].getTestDescription()
        //Configure button state
        if page >= testDesc.pages.count - 1 {
            startButton.setTitle("Start".localized(), for: .normal)
        } else {
            startButton.setTitle("Next".localized(), for: .normal)
        }
        
        lineView.isHidden = false;
        progressLabel.text = String(format:"\("TEST".localized()) %@ %@ %@", "\(currentTest + 1)", "/" ,"\(tests.count)");
        
        titleLabel.text = testDesc.title;
        if let v = instructionContainer.subviews.first {
            
            v.removeFromSuperview()
            
        }
        
        for c in self.childViewControllers
        {
            c.view.removeFromSuperview();
            c.removeFromParentViewController();
        }
        
        self.startButton.isHidden = false;
        let sb = UIStoryboard(name: testDesc.storyBoardName, bundle: nil)
        let instructionsVC = sb.instantiateViewController(withIdentifier: testDesc.pages[page])
        instructionsVC.view.frame = instructionContainer.bounds
        instructionContainer.addSubview(instructionsVC.view)
        instructionsVC.view.layoutSubviews()
        self.addChildViewController(instructionsVC);
    }
    
    
    //Display test
    func displayTest(index:Int) {
        //Set up label for title
        progressLabel.text = "" //"TEST \(currentTest + 1) OF \(tests.count)"

        self.clearInstructions();
        //Used to pick a test in the tests array
        currentTest =  index

        //This gets the information to populate the test host and get the correct test
        let testDesc = tests[index].getTestDescription()
        
        titleLabel.text = ""
        
        let sb = UIStoryboard(name: testDesc.storyBoardName, bundle: nil)
        
        guard let test = sb.instantiateInitialViewController() as? DNTestViewController else {
            
            return
            
        }
        //Set the view controllers test
        
        test.delegate = self;
        test.setTest(test: tests[currentTest])
        
        self.present(test, animated: false, completion: {
            //self.tests[self.currentTest].startTest()
        })
    }

    //Test notifies when complete
    
    func testDidFinish(viewController: DNTestViewController)
    {
        currentTest += 1;
        
        if currentTest >= tests.count
        {
            self.endTests();
        }
        else
        {
            self.nextTest();
        }
        
    }
    
}
