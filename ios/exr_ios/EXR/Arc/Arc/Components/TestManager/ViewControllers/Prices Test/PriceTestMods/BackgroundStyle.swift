//
// BackgroundStyle.swift
//


import Foundation
public extension PriceTestMod {
	static func backgroundColor(_ value:UIColor) -> Self {
		return PriceTestMod { viewController in

			viewController.view.backgroundColor = value
			
			return viewController
		}
	}
	static func questionBorder() -> Self {
		return PriceTestMod { viewController in
			let borderColor =  UIColor(red:0.42, green:0.45, blue:0.45, alpha:1)
			viewController.priceContainer.borderColor = borderColor
			viewController.priceContainer.borderThickness = 1.0
			
			return viewController
		}
	}
}
