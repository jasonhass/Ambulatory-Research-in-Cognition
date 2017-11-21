//
//  SurveyButton.swift
//  DIAN-Pilot
//
//  Created by Geoff Strom on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class SurveyButton: UIButton {

    let blueColor = DNAppearanceHelper.blueColor()

    override func awakeFromNib() {
        super.awakeFromNib();
        self.layer.cornerRadius = 3.0
        self.layer.backgroundColor = blueColor.cgColor
        self.titleLabel?.font = UIFont(name: "Georgia-BoldItalic", size: 20)
        setTitleColor(UIColor.white, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)

        
//        self.addTarget(self, action: #selector(touchUpInside(sender:)), for: .touchUpInside)
    }

//    override var isSelected: Bool {
//        didSet {
//            backgroundColor = blueColor
//            
//        }
//    }
    
    

//    func touchUpInside(sender: UIButton!) {
//        isSelected = !isSelected
//    }

}
class ToggleButton: UIButton {
    
    let blueColor = DNAppearanceHelper.blueColor()
    @IBInspectable var shouldAutoToggle:Bool = true;
    override func awakeFromNib() {
        super.awakeFromNib();
        
        self.layer.borderColor = blueColor.cgColor
        self.layer.borderWidth = 1.0
        self.layer.cornerRadius = 3.0
        self.titleLabel?.font = UIFont(name: "Tahoma", size: 20)

        setTitleColor(UIColor.black, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)
        
        
        self.addTarget(self, action: #selector(touchUpInside(sender:)), for: .touchUpInside)
    }
    
    override var isSelected: Bool {
        didSet {
            backgroundColor = isSelected ? blueColor : UIColor.clear
            
        }
    }
    
    
    
    func touchUpInside(sender: UIButton!) {
        if shouldAutoToggle
        {
            isSelected = !isSelected
        }
    }
    
}
