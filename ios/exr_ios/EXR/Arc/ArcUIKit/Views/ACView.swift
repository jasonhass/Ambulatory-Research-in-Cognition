//
// ACView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

@IBDesignable open class ACView: UIView  {
	
    @IBInspectable public var borderColor:UIColor = UIColor.black {
        didSet {
            layer.borderColor = borderColor.cgColor
        }
    }
    
    @IBInspectable public var borderThickness:CGFloat = 0.0 {
        didSet {
            print(borderThickness)
            layer.borderWidth = borderThickness
        }
    }
	@IBInspectable public var cornerRadius:CGFloat = 0.0 {
		didSet {
			layer.cornerRadius = cornerRadius
		}
	}
	
	open override func layoutSubviews() {
		super.layoutSubviews()
		layer.cornerRadius = cornerRadius
		layer.borderColor = borderColor.cgColor
		layer.borderWidth = borderThickness
	}
	override open func awakeFromNib() {
		super.awakeFromNib()
		layer.cornerRadius = cornerRadius
		layer.borderColor = borderColor.cgColor
		layer.borderWidth = borderThickness
	}
	
	override open func prepareForInterfaceBuilder() {
		super.prepareForInterfaceBuilder()
		layer.cornerRadius = cornerRadius
		layer.borderColor = borderColor.cgColor
		layer.borderWidth = borderThickness

	}
	
}

