//
// ACStudyTotalsView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import ArcUIKit

public class ACStudyTotalsView : UIView {
	private weak var contentStack:UIStackView!
	
	private weak var titleLabel:ACLabel!
	private weak var separator:ACHorizontalBar!
	private weak var bodyLabel:ACLabel!
	
	private var animations:[String:Animate] = [:]
	private weak var button:ACButton!
	
	private weak var totalsEarned:EarningsDetailGoalView!
	private weak var testsTaken:EarningsDetailGoalView!
	private weak var daysTested:EarningsDetailGoalView!
	private weak var goalsMet:EarningsDetailGoalView!
	
	public func set(studySummary:StudySummary?) {
		guard let summary = studySummary?.response.summary else {
			return
		}
		var animationParams:Animate.Config = Animate.Config()
		animationParams.duration = 1.0
		animationParams.delay = 0.5
		self.totalsEarned.set(value: summary.total_earnings)
		fade(self.totalsEarned, animationParams: animationParams, delay: 1.4)
		
		self.testsTaken.set(value: "\(summary.tests_taken)")
		fade(self.testsTaken, animationParams: animationParams, delay: 1.6)

		self.daysTested.set(value: "\(summary.days_tested)")
		fade(self.daysTested, animationParams: animationParams, delay: 1.8)

		self.goalsMet.set(value: "\(summary.goals_met)")
		fade(self.goalsMet, animationParams: animationParams, delay: 2.0)

		

	}
	fileprivate func fade(_ view:UIView?, animationParams:Animate.Config, delay:Double) {
		var params = animationParams
		params.delay = delay
		view?.fadeIn(params)
			.translate(params)
	}
	public override init(frame: CGRect) {
		super.init(frame: .zero)
		translatesAutoresizingMaskIntoConstraints = false
		layoutMargins = .zero
		backgroundColor = ACColor.primaryInfo
		var animationParams:Animate.Config = Animate.Config()
		animationParams.duration = 1.0
		animationParams.delay = 0.5
		

		contentStack = stack {[unowned self] in
			let _ = $0
			
			$0.axis = .vertical
			$0.alignment = .fill
			$0.spacing = 20
			$0.isLayoutMarginsRelativeArrangement = true
			$0.attachTo(view: self)

			$0.layoutMargins.top = 24
			$0.layoutMargins.bottom = 24
//			$0.layoutMargins.left = 0
//			$0.layoutMargins.right = 0
			$0.stack {
				$0.axis = .vertical
				$0.alignment = .fill
				$0.spacing = 20
				$0.isLayoutMarginsRelativeArrangement = true
				$0.layoutMargins.top = 24
				$0.layoutMargins.bottom = 24
				$0.layoutMargins.left = 24
				$0.layoutMargins.right = 24
				self.titleLabel = $0.acLabel {
					Roboto.Style.headingBold($0, color: ACColor.secondaryText)
					$0.text = "".localized(ACTranslationKey.progress_studytotals_header)
					
					
				}
				
				self.separator = $0.acHorizontalBar {
					$0.layout {
						$0.height == 2 ~ 999
					}
					$0.relativeWidth = 0.15
					animationParams.delay = 1.0
					
					$0.fadeIn(animationParams)
						.translate(animationParams)
				}
				
				self.bodyLabel = $0.acLabel {
					
					Roboto.Style.body($0, color: ACColor.secondaryText)
					$0.text = "".localized(ACTranslationKey.progress_studytotals_body)
					animationParams.delay = 1.2
					
					$0.fadeIn(animationParams)
						.translate(animationParams)
				}
			}
			
			
			
			self.totalsEarned = $0.earningsDetailGoalView {
				$0.set(body: "".localized(ACTranslationKey.progress_studytotals_earned))
				$0.set(value: "0.00")
				$0.backgroundColor = UIColor(white: 1.0, alpha: 0.04)
				animationParams.delay = 1.4
				
				$0.fadeIn(animationParams)
					.translate(animationParams)

			}
			self.testsTaken = $0.earningsDetailGoalView {
				$0.set(body: "".localized(ACTranslationKey.progress_studytotals_tests))
				$0.set(value: "0")
				$0.backgroundColor = .clear
				animationParams.delay = 1.6
				
				$0.fadeIn(animationParams)
					.translate(animationParams)

			}
			self.daysTested = $0.earningsDetailGoalView {
				$0.set(body: "".localized(ACTranslationKey.progress_studytotals_days))
				$0.set(value: "0")
				$0.backgroundColor = UIColor(white: 1.0, alpha: 0.04)
				animationParams.delay = 1.8
				
				$0.fadeIn(animationParams)
					.translate(animationParams)

			}
			self.goalsMet = $0.earningsDetailGoalView {
				$0.set(body: "".localized(ACTranslationKey.progress_studytotals_goals))
				$0.set(value: "0")
				$0.backgroundColor = .clear
				animationParams.delay = 2.0
				
				$0.fadeIn(animationParams)
					.translate(animationParams)

			}
			$0.view {
				$0.backgroundColor = .clear
			}
			$0.stack {
				$0.axis = .vertical
				$0.alignment = .center
				let sup = $0
				self.button = $0.acButton {
					$0.layout {
						$0.leading == sup.leadingAnchor + 24
						$0.trailing == sup.trailingAnchor - 24
						
					}
					
					$0.primaryColor = ACColor.secondary
					$0.secondaryColor = ACColor.secondaryText
					$0.setTitle("".localized(ACTranslationKey.button_returntohome), for: .normal)
					$0.setTitleColor(ACColor.badgeText, for: .normal)
					
					$0.addAction {
						Arc.shared.nextAvailableState()
						NotificationCenter.default.post(name: .ACDateChangeNotification, object: nil)
					}
					
					animationParams.delay = 2.2
					
					$0.fadeIn(animationParams)
						.translate(animationParams)
				}
			}
			
			
		}
	}
	
	required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
