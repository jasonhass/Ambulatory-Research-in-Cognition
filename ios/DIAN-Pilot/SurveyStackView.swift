/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
fileprivate let cornerRadius:CGFloat = 3.0
protocol SurveyStackViewDelegate:class {
    func didOutputValue<T>(view:SurveyStackView, value:T)
    
}

class SurveyStackView:UIStackView {
    weak var delegate:SurveyStackViewDelegate?
    @IBInspectable var questionKey:Int = 0
    required init(coder: NSCoder) {
        super.init(coder: coder)
    }
    @IBOutlet weak var displayLabel:UILabel!
    
    func setDisplayLabel(title:String){
        if displayLabel != nil {
            displayLabel.text = title
        }
    }
    
    @IBAction func editValuePressed(sender:AnyObject){
        print("Button")
    }
    
    func showValue(){
        displayLabel.superview?.isHidden = false
    }
    func hideValue(){
        displayLabel.superview?.isHidden = true
        
    }
    func hide(index:Int){
        self.arrangedSubviews[index].isHidden = true
    }
    func show(index:Int){
        self.arrangedSubviews[index].isHidden = false
    }

}

class SurveyStackSingleSelectView: SurveyStackView {
    @IBOutlet var buttons:[UIButton]!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        for button in buttons {
            button.layer.cornerRadius = cornerRadius

        }
    }
    override func editValuePressed(sender:AnyObject){

        if let s = sender as? UIButton {
            var selectedIndex:Int = -1;
            for i in 0..<buttons.count
            {
                let button = buttons[i];
                if button == s
                {
                    button.isSelected = true
                    selectedIndex = i;
                }
                else
                {
                    button.isSelected = false;
                }
            }
            
            let selection = selectedIndex == -1 ? nil : selectedIndex == 0;
            delegate?.didOutputValue(view: self, value: selection)
        }
        
    }
    
   
}
class SurveyStackButtonView: SurveyStackView {
    @IBOutlet weak var button:SurveyButton?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        hideValue()
        button?.layer.cornerRadius = cornerRadius
    }
    
    func showButton(){
        button?.isHidden = false
    }
    func hideButton(){
        button?.isHidden = true
    }
    override func editValuePressed(sender:AnyObject){
        delegate?.didOutputValue(view: self, value:"")

       
        
    }
}

class SurveyStackRangeView: SurveyStackView {
    @IBOutlet weak var slider:UISlider!
    override func awakeFromNib() {
        super.awakeFromNib()
        slider.setMinimumTrackImage(#imageLiteral(resourceName: "range_track"), for: .normal)
        slider.setMaximumTrackImage(#imageLiteral(resourceName: "range_track"), for: .normal)
        slider.setThumbImage(#imageLiteral(resourceName: "range_thumb"), for: .normal)
    }
    override func editValuePressed(sender: AnyObject) {

        delegate?.didOutputValue(view: self, value:slider.value)

    }
}



class SurveyStackInputView: SurveyStackView, UITextFieldDelegate {
    override func editValuePressed(sender: AnyObject) {

        if let s = sender as? UITextField {
            delegate?.didOutputValue(view: self, value:s.text ?? "")
            
        }

    }
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder();
        return false;
    }
    
}


class SurveyStackPickerView: SurveyStackView {
    @IBOutlet weak var button:SurveyButton?

    
    override func awakeFromNib() {
        super.awakeFromNib()
        hideValue()
        button?.layer.cornerRadius = cornerRadius
    }
    
    override func editValuePressed(sender: AnyObject) {
    
        delegate?.didOutputValue(view: self, value:"")

    }
    
    func showButton(){
        button?.isHidden = false
    }
    func hideButton(){
        button?.isHidden = true
    }
}
