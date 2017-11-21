//
//  DNAlertViewController.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/18/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

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
