//
// ProxyManager.swift
//



import Foundation
open class ProxyManager {
	static public let shared = ProxyManager()
	
	
	
	public func apply() {
		UINavigationBarProxy().apply()
	}
}
