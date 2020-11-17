//
// CancelButton.swift
//



import UIKit


open class CancelButton : UIButton {
	override open func setTitle(_ title: String?, for state: UIControl.State) {
		let attributedString = NSAttributedString(string: title ?? "", attributes: [NSAttributedString.Key.underlineStyle : NSUnderlineStyle.single.rawValue,
																					NSAttributedString.Key.foregroundColor : UIColor(named:"Primary")!])
		
		super.setAttributedTitle(attributedString, for: .normal)
	}
	
}
