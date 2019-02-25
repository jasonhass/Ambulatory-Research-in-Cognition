//
// ACTextView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


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
        setup(isSelected: false)
    }
    
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        setup(isSelected: false)
    }
    
    func setup(isSelected:Bool) {
        self.font = style.font
        textContainerInset = UIEdgeInsets.zero
        textContainer.lineFragmentPadding = 0
        markupText()
        
    }
}
