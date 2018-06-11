/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class TextAlertViewController: DNAlertViewController {

    @IBOutlet weak var textLabel: UILabel!
    private var message:String?
    @IBOutlet weak var okayButton: DNButton!
    private var confirmString:String?
    var timein:TimeInterval?;
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        if (message != nil) {
            setText(string: message!)
        }

        if confirmString != nil
        {
            okayButton.translationKey = nil;
            okayButton.setTitle(confirmString, for: .normal);
        }
        
        if let t = timein
        {
            okayButton.isEnabled = false;
            
            Timer.scheduledTimer(timeInterval: t, target: self, selector: #selector(self.enableOkayButton), userInfo: nil, repeats: false);
        }
        textLabel.superview?.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))

    }
    public func setText(string:String){
       
        if textLabel == nil {
            message = string
            
        } else {
            textLabel.text = string
            
            
        }
        
    }
    
    
    
    public func setConfirmText(string:String)
    {
        if okayButton != nil {
            okayButton.translationKey = nil
            
        }
        confirmString = string;
        if okayButton != nil
        {
            okayButton.setTitle(confirmString, for: .normal);
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirm(_ sender: AnyObject) {
        self.confirmationButtonPresssed()
    }
    
    func enableOkayButton()
    {
        self.okayButton.isEnabled = true;
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
