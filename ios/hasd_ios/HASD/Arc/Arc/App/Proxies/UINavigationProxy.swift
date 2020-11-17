//
// UINavigationProxy.swift
//



import UIKit
open class Proxy {
	open func apply() {
		
	}
}

open class UINavigationBarProxy : Proxy {
	override open func apply() {
		super.apply()
		
		UINavigationBar.appearance().setBackgroundImage(UIImage(), for: UIBarMetrics.default)
		UINavigationBar.appearance().shadowImage = UIImage()
		UINavigationBar.appearance().isTranslucent = true
		UINavigationBar.appearance().tintColor = UIColor(named: "Primary")
	}
}
