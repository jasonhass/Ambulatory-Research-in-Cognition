//
// TestCountDownView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public class TestCountDownView: UIView {
	weak var countLabel:ACLabel!
	public override init(frame: CGRect) {
		super.init(frame: .zero)
		build()
		
	}
	
	required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
		build()
	}
	
	private func build(){
		backgroundColor = .white
		stack { [weak self] in
			let stack = $0
			
			
			$0.layout {
				$0.centerX == self!.centerXAnchor
				$0.centerY == self!.centerYAnchor - 40
				
			}
			$0.axis = .vertical
			$0.alignment = .center
			$0.acLabel {
				stack.setCustomSpacing(32, after: $0)
				$0.text = "".localized(ACTranslationKey.testing_begin)
				Roboto.Style.subHeading($0, color: ACColor.badgeText)
			}

			self?.countLabel = $0.acLabel {
				
				stack.setCustomSpacing(12, after: $0)
				$0.text = " 3 "
				Georgia.Style.veryLargeTitle($0)
			}
			
			$0.acHorizontalBar {
				$0.layout {
					$0.width == 70
					$0.height == 2
				}
				$0.relativeWidth = 1.0
				
				
			}
		}
	}
}
