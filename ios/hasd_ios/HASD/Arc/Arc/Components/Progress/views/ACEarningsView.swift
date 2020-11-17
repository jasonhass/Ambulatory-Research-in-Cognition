//
// ACEarningsView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import ArcUIKit
public class ACEarningsView : ACTemplateView {
	weak var earningsSection:UIView!

	weak var headerLabel:ACLabel! //.earnings_body0
	weak var separator:ACHorizontalBar!
	weak var newGoalsStack:UIStackView!
	var thisWeeksEarningsLabel:ACLabel!
	var thisStudysEarningsLabel:ACLabel!
	var lastSyncedLabel:ACLabel!
	var viewDetailsButton:ACButton!
	weak var earningsBodyLabel:ACLabel! //.earnings_body0 || .earnings_body1
	
    weak var earningsParentStack:UIStackView!
	
	weak var bonusGoalsSection:UIView!
	weak var bonusGoalsHeader:ACLabel!
	weak var bonusGoalsSeparator:ACHorizontalBar!
	weak var bonusGoalsBodyLabel:ACLabel!
	weak var fourofFourGoal:FourOfFourGoalView!
	weak var twoADayGoal:TwoADayGoalView!
	weak var totalSessionsGoal:TotalSessionGoalView!
	weak var bonusGoalContent:UIView!
	weak var button:ACButton!
	weak var gradientView:UIView!
	weak var bottomGradient:CAGradientLayer?
	weak var errorLabel:ACLabel!
	
	public override func layoutSubviews() {
		super.layoutSubviews()
		bottomGradient?.frame = gradientView.bounds
	}
	public override func content(_ view: UIView) {
		if let v = view as? UIStackView {
			v.layoutMargins = .zero
		}
		self.earningsParentStack = view.stack { [weak self] in
			$0.axis = .vertical
			$0.isLayoutMarginsRelativeArrangement = true
			$0.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
			
			
			$0.stack {
				$0.axis = .vertical
				$0.isLayoutMarginsRelativeArrangement = true
				$0.layoutMargins = UIEdgeInsets(top: 0, left: 24, bottom: 0, right: 24)
			
				self?.errorLabel = $0.acLabel {
					$0.isHidden = true
					$0.textAlignment = .center
					
					Roboto.Style.body($0, color: .white)
					$0.text = "progress_endoftest_nosync"
					
					
				}
			}
			
			//MARK: Earnings Header
			self?.earningsSection = $0.view {
				
				//Earnings
				$0.backgroundColor = ACColor.primaryInfo
				$0.stack {
					
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 20
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 24, left: 24, bottom: 48, right: 24)
					self?.headerLabel = $0.acLabel {
						Roboto.Style.headingMedium($0, color: .white)
						$0.text = "Earnings".localized(ACTranslationKey.faq_earnings_header)
						
					}
					
					self?.separator = $0.acHorizontalBar {
						$0.relativeWidth = 0.15
						$0.color = UIColor(named: "HorizontalSeparator")
						$0.layout {
							$0.height == 2 ~ 999
							
						}
					}
					$0.stack {
						self?.earningsBodyLabel = $0.acLabel {
							Roboto.Style.body($0, color: .white)
						}
					}
					$0.stack {
						$0.distribution = .fillEqually
						$0.stack {
							$0.axis = .vertical
							$0.alignment = .center
							$0.acLabel {
								$0.textAlignment = .center

								Roboto.Style.body($0, color:ACColor.highlight)
								$0.text = "".localized(ACTranslationKey.earnings_weektotal)
							}
							self?.thisWeeksEarningsLabel = $0.acLabel {
								$0.textAlignment = .center

								Roboto.Style.earningsBold($0, color:.white)
								$0.text = "$0.00"
							}
						}
						$0.stack {
							$0.axis = .vertical
							$0.alignment = .center
							$0.acLabel {
								$0.textAlignment = .center
								
								Roboto.Style.body($0, color:ACColor.highlight)
								$0.text = "".localized(ACTranslationKey.earnings_studytotal)
							}
							self?.thisStudysEarningsLabel = $0.acLabel {
								$0.textAlignment = .center
								
								Roboto.Style.earningsBold($0, color:.white)
								$0.text = "$0.00"
							}
						}
					}
					
					self?.newGoalsStack = $0.stack {
						$0.axis = .vertical
						$0.spacing = 8
						$0.distribution = .fillEqually
					}
					
					self?.lastSyncedLabel = $0.acLabel {
						$0.textAlignment = .center

						Roboto.Style.subBody($0, color:UIColor(red:0.71, green:0.73, blue:0.8, alpha:1))
						$0.text = "".localized(ACTranslationKey.earnings_sync)
					}
					
					self?.viewDetailsButton = $0.acButton {
						$0.primaryColor = ACColor.secondary
						$0.secondaryColor = ACColor.secondaryGradient
						$0.setTitleColor(ACColor.badgeText, for: .normal)
						$0.setTitle("".localized(ACTranslationKey.button_viewdetails), for: .normal)
					}
				}
			}
			
			//MARK: Bonus Goal Header
			self?.bonusGoalsSection = $0.view {
				
				//Bonus Goals
				$0.backgroundColor = .white
				$0.stack {
					
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 20
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 24, left: 24, bottom: 48, right: 24)
					self?.bonusGoalsHeader = $0.acLabel {
						Roboto.Style.headingMedium($0, color: ACColor.badgeText)
						$0.text = "Bonus Goals".localized(ACTranslationKey.earnings_bonus_header)
						
					}
					
					self?.bonusGoalsSeparator = $0.acHorizontalBar {
						$0.relativeWidth = 0.15
						$0.color = UIColor(named: "HorizontalSeparator")
						$0.layout {
							$0.height == 2 ~ 999
							
						}
					}
					$0.stack {
						self?.bonusGoalsBodyLabel = $0.acLabel {
							Roboto.Style.body($0, color: ACColor.badgeText)
						}
					}
					
				}
			}

			//MARK: Bonus Goals Content
			self?.bonusGoalContent = $0.view { [weak self] in
				
				//Bonus Goals
				$0.backgroundColor = ACColor.primaryInfo
				$0.stack {
					
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 16
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 12, left: 8, bottom: 48, right: 8)
					
					
					self?.fourofFourGoal = $0.fourOfFourGoalView {
						$0.set(titleText: "4 Out of 4".localized(ACTranslationKey.earnings_4of4_header))
						$0.set(isUnlocked: false)
						
					}
					
					//2 a day goal
					self?.twoADayGoal = $0.twoADayGoalView {
						$0.set(titleText: "2-A-Day".localized(ACTranslationKey.earnings_2aday_header))
						$0.set(isUnlocked: false)
						
						
					}
					
					
					self?.totalSessionsGoal = $0.totalSessionGoalView {
						$0.set(titleText: "21 Sessions".localized(ACTranslationKey.earnings_21tests_header))
						$0.set(isUnlocked: false)
						
					}
					self?.button = $0.acButton {
						$0.primaryColor = ACColor.secondary
						$0.secondaryColor = ACColor.secondaryGradient
						$0.setTitleColor(ACColor.badgeText, for: .normal)
						$0.setTitle("".localized(ACTranslationKey.button_viewfaq), for: .normal)
					}
					
					
				}
			}
			
			
		}
		
		self.gradientView = self.view { [weak self] in
			guard let weakSelf = self else {
				return
			}
			$0.isHidden = true

			$0.layout {
				$0.bottom == weakSelf.bottomAnchor ~ 999
				$0.trailing == weakSelf.trailingAnchor ~ 999
				$0.leading == weakSelf.leadingAnchor ~ 999
				$0.top == weakSelf.safeAreaLayoutGuide.bottomAnchor - 96 ~ 999
			}
			bottomGradient(view: $0)
			weakSelf.nextButton = $0.acButton {
				$0.layout {
					$0.bottom == safeAreaLayoutGuide.bottomAnchor - 24
					$0.leading == safeAreaLayoutGuide.leadingAnchor + 32
					$0.trailing == safeAreaLayoutGuide.trailingAnchor - 32
				}
				
				InfoView.ButtonStyle.secondary.configure(button: $0)
				$0.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
			}
		}
	}
	func clearRewards() {
		newGoalsStack.removeSubviews()
	}
	func add(reward:(name:String, value:String)) {
		newGoalsStack.goalBackgroundView {
            $0.config.isUnlocked = true
			$0.layout {
				$0.height == 40 ~ 999
			}
			$0.stack {
				$0.attachTo(view: $0.superview, margins: UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10))
				$0.alignment = .center
				$0.acLabel {
					Roboto.Style.goalReward($0, color: ACColor.badgeText)
				
					$0.text = reward.name
					$0.adjustsFontSizeToFitWidth = true
					$0.minimumScaleFactor = 0.5
					
				}
				$0.view {
					$0.backgroundColor = .clear
					
				}
				$0.acLabel {
					$0.textAlignment = .right
					$0.setContentCompressionResistancePriority(.required, for: .horizontal)
					Roboto.Style.goalRewardBold($0, color: ACColor.badgeText)
					$0.text = reward.value
					
				}
			}
		}
	}
	func bottomGradient(view:UIView){
		let gradient = CAGradientLayer()
		gradient.frame = view.bounds
		gradient.colors = [	UIColor(red:0.04, green:0.12, blue:0.33, alpha:0).cgColor,	UIColor(red:0.04, green:0.12, blue:0.33, alpha:1).cgColor]
		gradient.locations = [0, 1]
		gradient.startPoint = CGPoint(x: 0.5, y: 0)
		gradient.endPoint = CGPoint(x: 0.5, y: 0.51)
		view.layer.addSublayer(gradient)
		bottomGradient = gradient
	}
}
