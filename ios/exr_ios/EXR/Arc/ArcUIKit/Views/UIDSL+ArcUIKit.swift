//
// UIDSL+ArcUIKit.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

extension UIView {
	@discardableResult
	public func acView(apply closure: (ACView) -> Void) -> ACView {
		return custom(ACView(), apply: closure)
	}
	
	@discardableResult
	public func acLabel(apply closure: (ACLabel) -> Void) -> ACLabel {
		return custom(ACLabel(), apply: closure)
	}
	@discardableResult
	public func acTextView(apply closure: (ACTextView) -> Void) -> ACTextView {
		return custom(ACTextView(), apply: closure)
	}
	@discardableResult
	public func scrollIndicator(apply closure: (IndicatorView) -> Void) -> IndicatorView {
		return custom(IndicatorView(), apply: closure)
	}
	

	
	
}
