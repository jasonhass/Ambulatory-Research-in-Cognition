//
// EarningDetailGoalView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import Foundation
import ArcUIKit
public class EarningsDetailGoalView : UIView {
	weak var bodyLabel:ACLabel!
	weak var valueLabel:ACLabel!
	init() {
		super.init(frame: .zero)
		build()
	}
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
		build()
	}
	func build() {
		backgroundColor = .clear
		isOpaque = false
		stack { [unowned self] in
			$0.attachTo(view: $0.superview, margins: UIEdgeInsets(top: 10, left: 24, bottom: 10, right: 24))
			$0.distribution = .fill
			self.bodyLabel = $0.acLabel {
				Roboto.Style.body($0, color:.white)
				$0.textAlignment = .left
			}
			self.valueLabel =  $0.acLabel {
				Roboto.Style.body($0, color:.white)
				$0.textAlignment = .right
			}
		}
	}
	public func set(body:String) {
		bodyLabel.text = body
	}
	public func set(value:String) {
		valueLabel.text = value
	}
}

extension UIView {
	@discardableResult
	public func earningsDetailGoalView(apply closure: (EarningsDetailGoalView) -> Void) -> EarningsDetailGoalView {
		return custom(EarningsDetailGoalView(), apply: closure)
	}
}


