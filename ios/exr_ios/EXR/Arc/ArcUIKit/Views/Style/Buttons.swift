//
// Buttons.swift
//



import Foundation
import UIKit
public extension UIBarButtonItem {
	static func primaryBackButton(title:String, target:Any?, selector:Selector) -> UIBarButtonItem {
		
			
			let backButton = UIButton(type: .custom)
			backButton.frame = CGRect(x: 0, y: 0, width: 60, height: 10)
			backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_blue"), for: .normal)
			backButton.setTitle(title, for: .normal)
			backButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
			backButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: -12)
			backButton.setTitleColor(UIColor(named: "Primary"), for: .normal)
			backButton.addTarget(target, action: selector, for: .touchUpInside)
			
			return UIBarButtonItem(customView: backButton)
				
		
		
	}
}
