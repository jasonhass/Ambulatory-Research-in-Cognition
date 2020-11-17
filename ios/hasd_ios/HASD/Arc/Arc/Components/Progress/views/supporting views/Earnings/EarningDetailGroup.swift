//
// EarningDetailGroup.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import Foundation
import ArcUIKit

public class EarningsDetailGroup : UIView {
	weak var headerLabel:ACLabel!
	weak var badge:ACLabel!
	weak var subHeaderLabel:ACLabel!
	weak var cycleTotalLabel:ACLabel!
	weak var cycleTotalView:UIView!
	weak var content:UIStackView!
	init() {
		super.init(frame: .zero)
		build()
	}
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
		build()
	}
	func build() {
		stack {
			$0.attachTo(view: $0.superview, margins: UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0))
			$0.axis = .vertical
			
			//This stack contains the header of the group
			$0.stack {
				$0.axis = .vertical
				$0.alignment = .leading
				$0.spacing = 8
				$0.isLayoutMarginsRelativeArrangement = true
				$0.layoutMargins = UIEdgeInsets(top: 32, left: 24, bottom: 10, right: 24)
				$0.stack {
					
					$0.spacing = 8
					//This week/Completed Testing Cycle
					self.headerLabel = $0.acLabel {
						Roboto.Style.subHeadingBold($0, color:.white)
						$0.textAlignment = .left
					}
					
					//Ongoing
					self.badge =  $0.acLabel {
						Roboto.Style.badge($0)
						$0.textAlignment = .center
						$0.isHidden = true
					}
				}
				
				//Dates
				self.subHeaderLabel = $0.acLabel {
					Roboto.Style.body($0, color:ACColor.highlight)
				}
			}
			
			self.content = $0.stack {
				$0.axis = .vertical
				
			}
			
			self.cycleTotalView = $0.view {
				$0.stack {
					$0.distribution = .fillEqually
					$0.attachTo(view: $0.superview, margins:  UIEdgeInsets(top: 10, left: 24, bottom: 10, right: 24))

					
						
					$0.acLabel {
						Roboto.Style.bodyBold($0, color:.white)
						$0.textAlignment = .left
						$0.text = "".localized(ACTranslationKey.earnings_details_cycletotal)
					}
					self.cycleTotalLabel = $0.acLabel {
						$0.attachTo(view: $0.superview)
						Roboto.Style.headingBold($0, color:.white)
						$0.textAlignment = .right
						
					}
				}
				
			}
		}
		
	}
	
	public func add(section:(body:String, price:String)) {
		var color:UIColor = .clear
		self.cycleTotalView.backgroundColor =  UIColor(white: 1.0, alpha: 0.04)

		if self.content.arrangedSubviews.count % 2 == 0 {
			color = UIColor(white: 1.0, alpha: 0.04)
			self.cycleTotalView.backgroundColor = .clear
		}

		self.content.earningsDetailGoalView {
			$0.set(body: section.body)
			$0.set(value: section.price)
			$0.backgroundColor = color
		}
		
	}
	public func set(header:String) {
		headerLabel.text = header
	}
	public func set(subHeader:String) {
		subHeaderLabel.text = subHeader
	}
	public func set(badge:String?) {
		self.badge.text = badge
		if badge == nil {
			self.badge.isHidden = true
		} else {
			self.badge.isHidden = false
		}
	}
	public func set(cycleTotal:String) {
		cycleTotalLabel.text = cycleTotal
	}
}

extension UIView {
	@discardableResult
	public func earningsDetailGroup(apply closure: (EarningsDetailGroup) -> Void) -> EarningsDetailGroup {
		return custom(EarningsDetailGroup(), apply: closure)
	}
}


