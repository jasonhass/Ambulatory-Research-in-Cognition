//
// FourOfFourGoalView.swift
//


import ArcUIKit
public class FourOfFourGoalView : GoalView {
	weak var progressGroup:ACCircularProgressGroupStackView!
	
	override func buildContent(view: UIView) {
		self.goalBodyLabel = view.acLabel {
			Roboto.Style.body($0)
			$0.text = "".localized(ACTranslationKey.earnings_4of4_body)
			.replacingOccurrences(of: "{AMOUNT}", with: "$1.00")
		}
		
		self.progressGroup = view.circularProgressGroup {
			$0.layout {
				$0.height == 56 ~ 999
			}
			$0.alignment = .center
			$0.config.strokeWidth = 4
			$0.config.size = 56
			$0.ellipseConfig.size = 56
			$0.checkConfig.size = 25
			$0.addProgressViews(count: 4)
			
		}
		set(goalRewardText: "$0.00 Bonus".localized(ACTranslationKey.earnings_bonus_incomplete)
		.replacingOccurrences(of: "{AMOUNT}", with: "$1.00"))
	}
	public func set(progress:Double, for index:Int) {
		progressGroup.set(progress: progress, for: index)
	}
}

extension UIView {
	@discardableResult
	public func fourOfFourGoalView(apply closure: (FourOfFourGoalView) -> Void) -> FourOfFourGoalView {
		return custom(FourOfFourGoalView(), apply: closure)
	}
}
