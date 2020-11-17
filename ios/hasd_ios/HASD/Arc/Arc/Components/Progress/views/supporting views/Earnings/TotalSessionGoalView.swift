//
// TotalSessionGoalView.swift
//


import ArcUIKit
public class TotalSessionGoalView : GoalView {
	var current:Double = 0
	var total:Double = 21

	var progress:CGFloat {
		let value = max(min(total, current), 0.0)

		return CGFloat(value/total)
	}

	weak public var countLabel:ACLabel!
	weak var stepperProgressBar:StepperProgressView!
	
	override func buildContent(view: UIView) {
		self.goalBodyLabel = view.acLabel {
			Roboto.Style.body($0)
			$0.text = "".localized(ACTranslationKey.earnings_21tests_body)
			.replacingOccurrences(of: "{AMOUNT}", with: "$5.00")
		}
		view.stepperProgress { [unowned self] in
			$0.layout {
				$0.height == 36 ~ 999
			}
			$0.config.outlineColor = .clear
			$0.config.barWidth = 12
			$0.config.foregroundColor = ACColor.highlight
			$0.progress = 1.0
			self.stepperProgressBar = $0.stepperProgress {
				$0.attachTo(view: $0.superview)
				$0.config.outlineColor = .clear
				
				$0.config.barWidth = 12
				$0.progress = 0
				$0.config.foregroundColor = ACColor.primaryInfo
				self.countLabel = $0.endRectView.acLabel {
					$0.attachTo(view: $0.superview)
					
					$0.text = "0"
					$0.textAlignment = .center
					Roboto.Style.body($0, color:.white)
				}
			}
			
		}
		set(goalRewardText: "$0.00 Bonus".localized(ACTranslationKey.earnings_bonus_incomplete)
		.replacingOccurrences(of: "{AMOUNT}", with: "$5.00"))
	}
	
	public func set(current:Int) {
        let value = max(min(total, Double(current)), 0.0)
		self.current = value
		self.countLabel.text = "*\(Int(value))*"
		self.stepperProgressBar.progress = self.progress
	
	}
	public func set(total:Double) {
		self.total = total
		self.stepperProgressBar.progress = self.progress

	}
}

extension UIView {
	@discardableResult
	public func totalSessionGoalView(apply closure: (TotalSessionGoalView) -> Void) -> TotalSessionGoalView {
		return custom(TotalSessionGoalView(), apply: closure)
	}
}
