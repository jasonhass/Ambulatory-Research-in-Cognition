//
// HMMarkupLabel.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class HMMarkupLabel: UILabel {
    
    open var renderer:HMMarkupRenderer!
    @IBInspectable var spacing:CGFloat = 0
    
    override open var text: String? {
        didSet {
            markupText()
        }
    }

    override open func awakeFromNib() {
        super.awakeFromNib()
        markupText()
    }
    
    private func markupText() {
        let text = self.text ?? ""
        renderer = HMMarkupRenderer(baseFont: self.font)
        let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: text))
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = spacing
        paragraphStyle.alignment = self.textAlignment
        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
        attributedString.addAttributes([.foregroundColor : self.textColor], range: NSMakeRange(0, attributedString.length))
        self.attributedText = attributedString
    }
    
}
