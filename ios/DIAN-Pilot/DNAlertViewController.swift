/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
func GetAlertController<T:DNAlertViewController>() -> T {
    //Fetch the alerts
    let sb = UIStoryboard.init(name: "AlertViews", bundle: nil)
    
    //Fetch the identifier which is just the classes name in the storyboard.
    let id = T.identifierForClass()
    return sb.instantiateViewController(withIdentifier: id) as! T
    
}

class DNAlertViewController: UIViewController {
    var onConfirm : ((AnyObject?) -> Void)?
    var retValue:AnyObject?
    
    var timeout:TimeInterval?;
    var timeoutTimer:Timer?;


    class func identifierForClass() -> String {
        return "\(self)"
    }
    //Keeping this here until after merge
    class func GetAlertController<T:DNAlertViewController>() -> T {
        //Fetch the alerts
        let sb = UIStoryboard.init(name: "AlertViews", bundle: nil)
        
        //Fetch the identifier which is just the classes name in the storyboard.
        let id = T.identifierForClass()
        return sb.instantiateViewController(withIdentifier: id) as! T
        
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated);
        
        if let t = timeout
        {
            timeoutTimer = Timer.scheduledTimer(timeInterval: t, target: self, selector: #selector(self.confirmationButtonPresssed), userInfo: nil, repeats: false);
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    func confirmationButtonPresssed() {
        self.timeoutTimer?.invalidate();
        // defered to ensure it is performed no matter what code path is taken
        defer {
        }
        let onConfirm = self.onConfirm
        // deliberately set to nil just in case there is a self reference
        self.onConfirm = nil
        guard let block = onConfirm else { return }
        dismiss(animated: false, completion: nil)
        block(self.retValue)
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
