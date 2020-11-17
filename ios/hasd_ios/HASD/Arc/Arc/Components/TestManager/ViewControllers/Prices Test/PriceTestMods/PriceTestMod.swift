//
// PriceTestMod.swift
//


import Foundation
import Arc
public struct PriceTestMod {
	
	
	public let closure: (PricesTestViewController) -> PricesTestViewController
	
	public func apply(to priceTest:PricesTestViewController) -> PricesTestViewController {
		closure(priceTest)
	}
}

