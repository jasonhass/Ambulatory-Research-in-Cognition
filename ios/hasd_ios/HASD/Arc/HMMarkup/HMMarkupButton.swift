//
// HMMarkupButton.swift
//



import UIKit
@IBDesignable open class HMMarkupButton: UIButton {
    @IBInspectable var translationKey:String?
	@IBInspectable var numberOfLines:Int = 0
    open var renderer:HMMarkupRenderer!
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        setup(isSelected: false)
    }
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        setup(isSelected: false)
    }
    override open func layoutSubviews() {
        super.layoutSubviews()
        //setup(isSelected: isHighlighted)
    }
	
	/// Applies standard formatting to strings before converting any markup text to the proper font face in the form of a attributed string.
	/// - Parameter isSelected: signals if it should use different styles if needed in sub-classes
    open func setup(isSelected:Bool){

        self.titleLabel?.lineBreakMode = .byWordWrapping
        self.titleLabel?.textAlignment = .center
        self.titleLabel?.numberOfLines = numberOfLines
        if let key = translationKey, let config = HMMarkupRenderer.config, config.shouldTranslate {
            let text = config.translation?[key] ?? title(for: .normal)
            setTitle(text, for: .normal)
        }
    }
    override open func setTitle(_ title: String?, for state: UIControl.State) {
        if let title = title, let config = HMMarkupRenderer.config, config.shouldTranslate {
            let text = config.translation?[title] ?? title
            super.setTitle(text, for: state)
            if let attr = attributedTitle(for: state) {
                let t = NSMutableAttributedString(attributedString: attr)
                t.mutableString.setString(text)
                setAttributedTitle(t, for: state)
            }
        } else {
            super.setTitle(title, for: .normal)
        }
//        markupText(for: state)
    }
//    public func markupText(for state:UIControl.State) {
//        guard let titleLabel = titleLabel else {
//            return
//        }
//        var text = self.title(for: state) ?? ""
//        if let config = HMMarkupRenderer.config, config.shouldTranslate, let key = translationKey {
//            text = config.translation?[key] ?? text
//        }
//        renderer = HMMarkupRenderer(baseFont: titleLabel.font)
//        let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: text))
//        let paragraphStyle = NSMutableParagraphStyle()
//        paragraphStyle.alignment = titleLabel.textAlignment
//        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
//        attributedString.addAttributes([.foregroundColor : titleLabel.textColor], range: NSMakeRange(0, attributedString.length))
//        var title = attributedTitle(for: state).
//        setAttributedTitle(attributedString, for: state)
//    }
    open override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        setup(isSelected: true)
        
    }
    open override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        setup(isSelected: false)
        
    }
}
