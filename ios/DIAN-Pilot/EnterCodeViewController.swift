/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class EnterCodeViewController: DNViewController, UITextFieldDelegate {

    @IBOutlet weak var codeTextField: BorderTextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        let toolbar = UIToolbar();
        let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(self.closeTextField));
        let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil);
        toolbar.items = [space, doneButton];
        toolbar.sizeToFit();
        
        codeTextField.inputAccessoryView = toolbar;

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func exit(segue:UIStoryboardSegue){
        
    }
    
    @IBAction func verifyPressed(_ sender: Any) {
        
        guard let t = codeTextField.text else
        {
            return;
        }
        
        guard let arcId = DNDataManager.sharedInstance.arcId else
        {
            return;
        }
        
        if t.count < 6
        {
            return;
        }
        
        
        DNRestAPI.shared.registerParticipant(arcId: arcId, verificationCode: t, onCompletion: { (error) in
            if error != nil
            {
                
                
                DispatchQueue.main.async {
                    dump(error)
                    self.view.isUserInteractionEnabled = true;
                    self.codeTextField.setErrorBorder();
                }
            }
            else
            {
                
                DispatchQueue.main.async {
                    DNDataManager.sharedInstance.hasAuthenticated = true;
                    if DNDataManager.sharedInstance.visitCount > 1
                    {
                        AppDelegate.go(state: .setupArcDate);
                    }
                    else
                    {
                        TestVisit.createVisit(forDate: Date());
                        AppDelegate.go(state: .setupTime);
                    }
                    
                }
                
            }
        })
        
    }

    func closeTextField()
    {
        codeTextField.resignFirstResponder();
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder();
        return false;
    }
    
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        codeTextField.clearErrorBorder();
    }

}
