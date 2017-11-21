//
//  SurveyStackView.swift
//  ARC
//
//  Created by Philip Hayes on 5/9/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

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
