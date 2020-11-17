//
// ACTextView.swift
//



import UIKit
import HMMarkup

@IBDesignable open class ACTextView : HMMarkupTextView {
    
    
    
    public var style:ACTextStyle = .none {
        didSet {
            setup(isSelected: false)
        }
    }
    
    @IBInspectable var styleId:Int = 0 {
        didSet {
            style = ACTextStyle(rawValue: styleId) ?? .none
            
        }
    }
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        //setup(isSelected: false)
    }
    
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        //setup(isSelected: false)
    }
    
    func setup(isSelected:Bool) {
        self.font = style.font
        textContainerInset = UIEdgeInsets.zero
        textContainer.lineFragmentPadding = 0
        markupText()
        
    }
}
