//
// PrimaryButton.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class PrimaryButton : UIButton {
	@IBInspectable var primaryColor:UIColor = UIColor(named: "Primary")!
	@IBInspectable var secondaryColor:UIColor = UIColor(named: "Primary Gradient")!
	var gradient:CAGradientLayer?
    override open func awakeFromNib() {
		super.awakeFromNib()
		setup(isSelected: false)
	}
	
	override open func prepareForInterfaceBuilder() {
		super.prepareForInterfaceBuilder()
		setup(isSelected: false)
	}
	
	
	override open var isHighlighted: Bool {
		didSet {
			setup(isSelected: isHighlighted)
		}
	}
	override open var isEnabled: Bool {
		didSet {
			if !isEnabled {
				setup(isSelected: false)

			} else {
				setup(isSelected: isHighlighted)

			}
		}
	}
	override open var intrinsicContentSize: CGSize {
		return CGSize(width: 216, height: 48)
	}
	override open func layoutIfNeeded() {
		super.layoutIfNeeded()
		
	}
	override open func layoutSubviews() {
		super.layoutSubviews()
		setup(isSelected: isHighlighted)
	}
	func setup(isSelected:Bool){
		CATransaction.begin()
		CATransaction.setAnimationDuration(0.03)
		layer.cornerRadius = 24
		layer.shadowOffset = CGSize(width: 0, height: 1)
		layer.shadowColor = UIColor(red:0, green:0, blue:0, alpha:0.5).cgColor
		layer.shadowOpacity =  1
		layer.shadowRadius = (!isSelected) ? 2 : 0
		let gradient = self.gradient ?? CAGradientLayer()
		gradient.frame = CGRect(x: 0, y: 0, width: 216, height: 48)
		gradient.colors = (!isSelected && isEnabled) ? [secondaryColor.cgColor,
										 primaryColor.cgColor] : [primaryColor.cgColor,
																				 primaryColor.cgColor]
		
		if isEnabled {
			self.alpha = 1.0
		} else {
			self.alpha = 0.5
		}
		
		gradient.locations = [0, 1]
		gradient.startPoint = CGPoint(x: 0.5, y: 0)
		gradient.endPoint = CGPoint(x: 0.5, y: 1)
		gradient.cornerRadius = 24
		if gradient.superlayer == nil {
			self.gradient = gradient
			layer.addSublayer(gradient)
		}
		CATransaction.commit()
	}
}
