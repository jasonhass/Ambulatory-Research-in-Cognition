//
// Alignment.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



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
