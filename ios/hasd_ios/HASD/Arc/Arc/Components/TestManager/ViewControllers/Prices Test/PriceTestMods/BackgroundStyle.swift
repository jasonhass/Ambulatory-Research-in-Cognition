//
// BackgroundStyle.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



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
