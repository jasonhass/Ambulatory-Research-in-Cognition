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

@IBDesignable open class HMMarkupLabel: UILabel {
    open var renderer:HMMarkupRenderer!
    @IBInspectable var spacing:CGFloat = 1.33
    
    override open var text: String? {
        didSet {
            markupText()
        }
    }
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        markupText()
    }
    override open func awakeFromNib() {
        super.awakeFromNib()
        markupText()
    }
    
    public func markupText() {
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
@IBDesignable open class HMMarkupTextView: UITextView {
    
    open var renderer:HMMarkupRenderer!
    @IBInspectable var spacing:CGFloat = 1.33
    
    override open var text: String? {
        didSet {
            markupText()
        }
    }
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        markupText()
    }
    override open func awakeFromNib() {
        super.awakeFromNib()
       
        
        markupText()
    }
    
    public func markupText() {
        let text = self.text ?? ""
        renderer = HMMarkupRenderer(baseFont: self.font ?? UIFont.systemFont(ofSize: 18.0))
        let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: text))
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = spacing
        paragraphStyle.alignment = self.textAlignment
        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
        attributedString.addAttributes([.foregroundColor : self.textColor], range: NSMakeRange(0, attributedString.length))
        self.attributedText = attributedString
    }
    
    public func setLink(url:String, range:ClosedRange<Int>) {
        guard let url = URL(string: url) else {
            return
        }
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = spacing
        paragraphStyle.alignment = self.textAlignment
        
        let r = NSMakeRange(range.lowerBound, range.upperBound - range.lowerBound)
        
        if let string = attributedText.mutableCopy() as? NSMutableAttributedString {
            string.setAttributes([.link: url,
                                  .paragraphStyle: paragraphStyle,
                                  .font : self.font?.boldFont() ?? UIFont.systemFont(ofSize: 18.0)
                ], range: r)
            attributedText = string
            self.linkTextAttributes = [
                .foregroundColor: UIColor.white,
                .underlineStyle: NSUnderlineStyle.single.rawValue
                
            ]
        }
        
    }
    
    
}
