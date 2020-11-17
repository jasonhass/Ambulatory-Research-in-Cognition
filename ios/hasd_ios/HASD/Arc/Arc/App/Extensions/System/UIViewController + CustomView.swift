//
// UIViewController + CustomView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



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
