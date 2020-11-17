//
// PriceTestType.swift
//


import Foundation
public enum PriceTestType {
    case normal, simplified, simplifiedCentered
	
	//Since we're applying the changes to a class no return value is necessary.
	public func applyMods(viewController:PricesTestViewController) {
		var mods:[PriceTestMod] = []
		switch self {
		case .normal:
			break
		case .simplified:
			//To use a mod simply add them to an array of changes/transformations
			mods = [.hideGoodPrice(true)]
		case .simplifiedCentered:
			
			mods = [.align(to: .center),
					.hideGoodPrice(true),
					.backgroundColor(.pricesTestBackground),
					.questionBorder()]
		}
		
		//Apply each transformation. If you're transforming a struct
		//The return value is required. Store it in a variable. Return it.
		mods.forEach {
			_ = $0.apply(to: viewController)
		}
	}
}
