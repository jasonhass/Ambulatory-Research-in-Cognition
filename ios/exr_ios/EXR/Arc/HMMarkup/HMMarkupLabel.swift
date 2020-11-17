//
// HMMarkupLabel.swift
//



import UIKit

@IBDesignable open class HMMarkupLabel: UILabel {
    @IBInspectable public var translationKey:String?
    
    open var renderer:HMMarkupRenderer!
    @IBInspectable public var spacing:CGFloat = 5
    
    open var template:Dictionary<String, String> = [:]
    
    override open var text: String? {
        didSet {
            markupText()
        }
    }
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        //markupText()
    }
    override open func awakeFromNib() {
        super.awakeFromNib()
        
        markupText()
    }
    
    public func markupText() {
        var text = self.text ?? ""
        if let config = HMMarkupRenderer.config, config.shouldTranslate, let key = translationKey {
            text = config.translation?[key] ?? text
        }
        renderer = HMMarkupRenderer(baseFont: self.font)
        //TODO:merge this with the attributes from the new render
        //var prevAttributes = self.attributedText?.attributes(at: 0, effectiveRange: nil)

        let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: text, template: self.template))
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = spacing
        paragraphStyle.alignment = self.textAlignment
        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
        attributedString.addAttributes([.foregroundColor : self.textColor!], range: NSMakeRange(0, attributedString.length))
        self.attributedText = attributedString
    }
    
}
@IBDesignable open class HMMarkupTextView: UITextView {
    
    open var renderer:HMMarkupRenderer!
    @IBInspectable public var spacing:CGFloat = 5.5
    private var _originalText:String?
    override open var text: String? {
        didSet {
            markupText()
        }
    }
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        //markupText()
    }
    override open func awakeFromNib() {
        super.awakeFromNib()
       
        
        //markupText()
    }
    
    public func markupText() {
        let text = self.text ?? ""
        renderer = HMMarkupRenderer(baseFont: self.font ?? UIFont.systemFont(ofSize: 18.0))
        let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: text))
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = spacing
        paragraphStyle.alignment = self.textAlignment
        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
        attributedString.addAttributes([.foregroundColor : self.textColor!], range: NSMakeRange(0, attributedString.length))
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
