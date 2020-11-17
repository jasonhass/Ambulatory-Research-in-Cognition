//
// ACButton.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit
import HMMarkup
@IBDesignable open class ACButton : HMMarkupButton {

    @IBInspectable public var cornerRadius:CGFloat = 24.0 {
        didSet {
            layer.cornerRadius = cornerRadius
        }
    }
    open override var isSelected: Bool{
        didSet {
            self.setNeedsDisplay()

        }
    }
	
    @IBInspectable public var primaryColor:UIColor = UIColor(named: "Primary") ?? UIColor.white
    @IBInspectable public var secondaryColor:UIColor = UIColor(named: "Primary Gradient") ?? UIColor.gray
	@IBInspectable public var topColor:UIColor = UIColor(white: 1.0, alpha: 0.25)
	@IBInspectable public var bottomColor:UIColor = UIColor(white: 0.0, alpha: 0.25)
	
	public init() {
		super.init(frame:.zero)
		Roboto.Style.bodyBold(titleLabel!)
		layout {
			$0.width >= 216 ~ 950
			$0.height >= 48 ~ 950
			$0.height == 48 ~ 250
			//$0.bottom >= self.bottomAnchor - 40 ~ 250
		}
	}
	
	required public init?(coder: NSCoder) {
		super.init(coder: coder)
	}
	
    open override func draw(_ rect: CGRect) {
		let config = Drawing.GradientButton(cornerRadius: cornerRadius,
											  primaryColor: primaryColor,
											  secondaryColor: secondaryColor,
											  topShadowColor: topColor,
											  bottomShadowColor: bottomColor,
											  isSelected: isSelected,
											  isEnabled: isEnabled)
        config.draw(rect)
    }
    override open func setup(isSelected:Bool){
        super.setup(isSelected:isSelected)
//        tintColor = .clear
        imageView?.layer.zPosition = 1
        
        if isEnabled {
            self.alpha = 1.0
        } else {
            self.alpha = 0.5
        }
        layer.cornerRadius = cornerRadius
        self.setNeedsDisplay()

      
        
    }
    
    open override func setTitle(_ title: String?, for state: UIControl.State) {
        super.setTitle(title, for: state)
        

    }
    open override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        isSelected = true

    }
    open override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        isSelected = false


    }
    open override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesCancelled(touches, with: event)
        isSelected = false


    }
}

extension UIView {
	
	@discardableResult
	public func acButton(apply closure: (ACButton) -> Void) -> ACButton {
		return custom(ACButton(), apply: closure)
	}
	
}
