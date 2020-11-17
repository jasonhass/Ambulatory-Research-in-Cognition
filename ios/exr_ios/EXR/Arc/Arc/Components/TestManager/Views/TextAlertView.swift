//
// TextAlertView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

open class TextAlertView: UIView {
    
    var timeout:TimeInterval?;
    var timeoutTimer:Timer?;
    
    var onConfirm : (() -> Void)?
    
    @IBOutlet weak var textLabel: UILabel!
    private var message:String?
    @IBOutlet weak var okayButton: UIButton!
    private var confirmString:String?
    var timein:TimeInterval?;
    
    
    
    override open func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override open func didMoveToSuperview() {
        super.didMoveToSuperview()
        
        if (message != nil) {
            setText(string: message!)
        }
        
        if confirmString != nil
        {
            okayButton.setTitle(confirmString, for: .normal);
        }
        
        if let t = timein
        {
            okayButton.isEnabled = false;
            
            Timer.scheduledTimer(timeInterval: t, target: self, selector: #selector(self.enableOkayButton), userInfo: nil, repeats: false);
        }
        if let t = timeout
        {
            timeoutTimer = Timer.scheduledTimer(timeInterval: t, target: self, selector: #selector(self.confirmationButtonPresssed), userInfo: nil, repeats: false);
        }
//        textLabel.superview?.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
    }
    
    public func setText(string:String){
        
        if textLabel == nil {
            message = string
            
        } else {
            textLabel.text = string
            
            
        }
        
    }
    @objc func confirmationButtonPresssed() {
        self.timeoutTimer?.invalidate();
        onConfirm?()
        self.removeFromSuperview()
    }
    
    
    public func setConfirmText(string:String)
    {
        if okayButton != nil {
//            okayButton.translationKey = nil
            
        }
        confirmString = string;
        if okayButton != nil
        {
            okayButton.setTitle(confirmString, for: .normal);
        }
    }
    @IBAction func confirm(_ sender: AnyObject) {
        confirmationButtonPresssed()
    }
    
    @objc func enableOkayButton()
    {
        self.okayButton.isEnabled = true;
    }
}
