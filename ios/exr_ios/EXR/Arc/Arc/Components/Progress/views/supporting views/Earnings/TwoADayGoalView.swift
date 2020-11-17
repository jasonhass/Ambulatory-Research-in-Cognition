//
// TwoADayGoalView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import ArcUIKit
public class TwoADayGoalView : GoalView {
	
	weak public var progressGroup:GoalDayTileGroup!
	
	override func buildContent(view: UIView) {
		
		self.goalBodyLabel = view.acLabel {
			Roboto.Style.body($0)
			$0.text = "".localized(ACTranslationKey.earnings_2aday_body)
			.replacingOccurrences(of: "{AMOUNT}", with: "$6.00")
		}
		
		//If you don't have anything to set use _ in
		self.progressGroup = view.goalDayTileGroup { _ in}
		set(goalRewardText: "$0.00 Bonus".localized(ACTranslationKey.earnings_bonus_incomplete)
		.replacingOccurrences(of: "{AMOUNT}", with: "$6.00"))
	}
	public override func clear() {
		progressGroup.clear()
	}
	public func add(tiles:[String]) {
		clear()
		for i in tiles {
			add(tile: i, progress: 0.0)
		}

	}
	public func add(tiles:[(String, Double)]) {
		for i in tiles {
			add(tile: i.0, progress: i.1)
		}
		
	}
	
	public func add(tile:String, progress:Double) {
		progressGroup.add(tile: tile, progress: progress)
	}
	public func set(progress:Double, forIndex index:Int) {
		progressGroup.set(progress: progress, forIndex: index)
	}
}

extension UIView {
	@discardableResult
	public func twoADayGoalView(apply closure: (TwoADayGoalView) -> Void) -> TwoADayGoalView {
		return custom(TwoADayGoalView(), apply: closure)
	}
}
