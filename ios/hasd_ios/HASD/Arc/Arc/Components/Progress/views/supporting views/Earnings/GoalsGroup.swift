//
// GoalsGroup.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import Foundation
import ArcUIKit
public class GoalsGroup : UIStackView {
	weak var fourofFourGoal:FourOfFourGoalView!
	weak var twoADayGoal:TwoADayGoalView!
	weak var totalSessionsGoal:TotalSessionGoalView!
	init() {
		super.init(frame:.zero)
	}
	
	required init(coder: NSCoder) {
		super.init(coder:coder)
	}
	func build() {
		
		backgroundColor = ACColor.primaryInfo
		stack {
			
			$0.attachTo(view: $0.superview)
			$0.axis = .vertical
			$0.alignment = .fill
			$0.spacing = 16
			$0.isLayoutMarginsRelativeArrangement = true
			$0.layoutMargins = UIEdgeInsets(top: 12, left: 8, bottom: 48, right: 8)
			
			
			self.fourofFourGoal = $0.fourOfFourGoalView {
				$0.set(titleText: "4 Out of 4".localized(ACTranslationKey.earnings_4of4_header))
				$0.set(isUnlocked: false)
				
			}
			
			//2 a day goal
			self.twoADayGoal = $0.twoADayGoalView {
				$0.set(titleText: "2-A-Day".localized(ACTranslationKey.earnings_2aday_header))
				$0.set(isUnlocked: false)
				
				
			}
			
			
			self.totalSessionsGoal = $0.totalSessionGoalView {
				$0.set(titleText: "21 Sessions".localized(ACTranslationKey.earnings_21tests_header))
				$0.set(isUnlocked: false)
				
			}
			
		}
		
	}
	
}
