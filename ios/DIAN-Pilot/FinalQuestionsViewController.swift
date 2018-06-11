/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class FinalQuestionsViewController: DNViewController {

    @IBOutlet weak var interruptedYes: SurveyButton!
    @IBOutlet weak var interruptedNo: SurveyButton!
    
    @IBOutlet weak var upgradeYes: SurveyButton?
    @IBOutlet weak var upgradeNo: SurveyButton?
    
    @IBOutlet weak var nextButton: UIButton!
    override func viewDidLoad() {
        nextButton.isEnabled = false;
    }
    
    
    
    @IBAction func interruptedYesTapped(_ sender: UIButton)
    {
        DNDataManager.sharedInstance.currentTestSession?.interrupted = true;
        interruptedNo.isSelected = false;
        interruptedYes.isSelected = true;
        if upgradeNo == nil || (upgradeNo!.isSelected || upgradeYes!.isSelected)
        {
            nextButton.isEnabled = true;
        }

    }
    
    
    @IBAction func interruptedNoTapped(_ sender: UIButton)
    {
        DNDataManager.sharedInstance.currentTestSession?.interrupted = false;
        interruptedNo.isSelected = true;
        interruptedYes.isSelected = false;
        
        if upgradeNo == nil || (upgradeNo!.isSelected || upgradeYes!.isSelected)
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    @IBAction func upgradeYesTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = true;
        upgradeNo?.isSelected = false;
        upgradeYes?.isSelected = true;
        if interruptedNo.isSelected || interruptedYes.isSelected
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    @IBAction func upgradeNoTapped(_ sender: Any)
    {
        DNDataManager.sharedInstance.currentTestSession?.willUpgradePhone = false;
        upgradeNo?.isSelected = true;
        upgradeYes?.isSelected = false;
        
        if interruptedNo.isSelected || interruptedYes.isSelected
        {
            nextButton.isEnabled = true;
        }
    }
    
    
    
    @IBAction func nextPressed(_ sender: Any)
    {
        DNDataManager.save();
        AppDelegate.go(state: .endingVerification);
    }
    
    
    
    
}
