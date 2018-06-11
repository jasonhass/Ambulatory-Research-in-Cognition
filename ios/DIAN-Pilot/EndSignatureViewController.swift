/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class EndSignatureViewController: DNViewController,SignatureViewDelegate {
    @IBOutlet weak var signatureView:SignatureView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
        self.signatureView.delegate = self

        self.signatureViewContentChanged(state: .empty)

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func undo(sender:UIButton){
        self.signatureView.clear()
    }
    
    
    override func pressedNext(sender: UIButton)
    {
        if signatureView.path.isEmpty {
            return
        }
        if let session = DNDataManager.sharedInstance.currentTestSession, let img = signatureView.save()
        {
            
            session.completeTime = Date() as NSDate;
            session.endSignature = UIImagePNGRepresentation(img) as NSData?;
            session.markFinished();
            DNDataManager.save();
            DNDataManager.sharedInstance.currentTestSession = nil;
            DNDataManager.sharedInstance.isTesting = false;
            
        }
        
        AppDelegate.chooseDisplay(runPeriodicBackgroundTask: true);
    }
    func signatureViewContentChanged(state: SignatureViewContentState) {
        let btns = [self.view.viewWithTag(1) as! UIButton,self.view.viewWithTag(2) as! UIButton]
        
        switch  state {
            
        case .empty:
            
            for b in btns {
                b.alpha = 0.5
                b.isEnabled = false
            }
        case .dirty:
            for b in btns {

                b.alpha = 1
                b.isEnabled = true
            }
            
        }
    }
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}
