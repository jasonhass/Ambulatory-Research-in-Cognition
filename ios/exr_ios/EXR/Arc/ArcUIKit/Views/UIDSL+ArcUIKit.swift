//
// UIDSL+ArcUIKit.swift
//



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
