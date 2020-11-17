//
// ToggleButton.swift
//



import UIKit

open class SurveyButton: UIButton {
    public var color = UIColor(named: "Primary")!

    
    override open func awakeFromNib() {
        super.awakeFromNib();
        self.layer.cornerRadius = 3.0
        self.layer.backgroundColor = color.cgColor
        self.titleLabel?.font = UIFont(name: "Georgia-BoldItalic", size: 20)
        setTitleColor(UIColor.white, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)

    }
    

    
}
open class ToggleButton: UIButton {
    
    public var color = UIColor(named: "Primary")!
    @IBInspectable public var shouldAutoToggle:Bool = true;
    override open func awakeFromNib() {
        super.awakeFromNib();
        
        
        self.layer.borderColor = color.cgColor
        
        self.layer.borderWidth = 1.0
        self.layer.cornerRadius = 3.0
        self.titleLabel?.font = UIFont(name: "Tahoma", size: 20)
        
        setTitleColor(UIColor.black, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)
        
        
        self.addTarget(self, action: #selector(touchUpInside(sender:)), for: .touchUpInside)
    }
    
    override open var isSelected: Bool {
        didSet {
            
            backgroundColor = isSelected ? color : UIColor.clear
            
        }
    }
    
    
    
    @objc public func touchUpInside(sender: UIButton!) {
        if shouldAutoToggle
        {
            isSelected = !isSelected
        }
    }
    
}
