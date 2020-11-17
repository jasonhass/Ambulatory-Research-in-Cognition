//
// UIControl+Closure.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit
class ClosureSleeve {
	let closure: () -> ()
	
	init(attachTo: AnyObject, closure: @escaping () -> ()) {
		self.closure = closure
		objc_setAssociatedObject(attachTo, "[\(UUID().uuidString)]", self, .OBJC_ASSOCIATION_RETAIN)
	}
	
	@objc func invoke() {
		closure()
	}
}

extension UIControl {
	public func addAction(for controlEvents: UIControl.Event = .primaryActionTriggered, action: @escaping () -> ()) {
		let sleeve = ClosureSleeve(attachTo: self, closure: action)
		addTarget(sleeve, action: #selector(ClosureSleeve.invoke), for: controlEvents)
	}
	
}
