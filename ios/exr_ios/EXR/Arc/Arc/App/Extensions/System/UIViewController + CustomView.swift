//
// UIViewController + CustomView.swift
//


import Foundation
/// The HasCustomView protocol defines a customView property for UIViewControllers to be used in exchange of the regular view property.
/// In order for this to work, you have to provide a custom view to your UIViewController at the loadView() method.
public protocol HasCustomView {
	associatedtype CustomView: UIView
}

public extension HasCustomView where Self: UIViewController {
	/// The UIViewController's custom view.
	var customView: CustomView {
		guard let customView = view as? CustomView else {
			fatalError("Expected view to be of type \(CustomView.self) but got \(type(of: view)) instead")
		}
		return customView
	}
}
open class CustomViewController<CustomView: UIView>: ArcViewController {
	public var customView: CustomView {
		return view as! CustomView //Will never fail as we're overriding 'view'
	}
	
	override open func loadView() {
		view = CustomView()
	}
	
}
