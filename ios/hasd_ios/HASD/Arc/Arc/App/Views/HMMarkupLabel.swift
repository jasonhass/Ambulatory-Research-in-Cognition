//
// HMMarkupLabel.swift
//



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
