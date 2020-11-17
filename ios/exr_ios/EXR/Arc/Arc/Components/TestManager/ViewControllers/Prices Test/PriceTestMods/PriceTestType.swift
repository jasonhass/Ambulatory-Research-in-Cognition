//
// PriceTestType.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



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
