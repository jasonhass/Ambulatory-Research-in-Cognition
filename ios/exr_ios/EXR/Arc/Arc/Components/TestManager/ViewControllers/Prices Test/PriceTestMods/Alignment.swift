//
// Alignment.swift
//


import Foundation
public extension PriceTestMod {
	
	//This is a single mod you can put multiple in a file, if related.
	static func align(to value:NSTextAlignment) -> Self {
		return PriceTestMod { viewController in

			viewController.itemNameLabel.textAlignment = value
			viewController.itemPriceLabel.textAlignment = value
			viewController.goodPriceLabel.textAlignment = value
			viewController.questionAlignment = value
			
			return viewController
		}
	}
}
