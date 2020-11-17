//
// HideGoodPrice.swift
//


import Foundation
public extension PriceTestMod {
	static func hideGoodPrice(_ value:Bool) -> Self {
		return PriceTestMod { viewController in

			
			viewController.goodPriceLabel.isHidden = value
			viewController.buttonStack.isHidden = true
			
			return viewController
		}
	}
}
