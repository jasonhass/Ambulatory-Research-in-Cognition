//
// TestProgressView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public class TestProgressView:UIView {
	weak private var progressBar:CircularProgressView!
	weak private var divider:ACHorizontalBar!
	weak private var titleLabel:ACLabel!
	weak private var subtitleLabel:ACLabel!
	weak private var countLabel:ACLabel!
	weak private var maxLabel:ACLabel!
	
	private var defaultAnimation = Animate().duration(0.8).curve(.easeOut)
	var title:String? {
		get {
			return titleLabel.text
		}
		set  {
			titleLabel.text = newValue
		}
	}
	var subTitle:String? {
		get {
			return subtitleLabel.text
		}
		set {
			subtitleLabel.text = newValue
		}
	}
	var dividerWidth:Double  = 1.0{
		didSet {
			divider.relativeWidth = CGFloat(min(dividerWidth, 1.0))
		}
	}
	var count:Int = 0 {
		didSet {
			let old = Double(oldValue)/Double(maxCount)
			let new = Double(count)/Double(maxCount)
			
			defaultAnimation.stop()
			defaultAnimation = defaultAnimation.run {  [weak self] (t) -> Bool in
				self?.progressBar.progress = Math.lerp(a: old, b: new, t: t)
				
				return true
			}
			countLabel.text = "\(count)"

			
		}
	}
	var maxCount:Int = 3
	init() {
		super.init(frame: .zero)
		translatesAutoresizingMaskIntoConstraints = false
		backgroundColor = ACColor.primaryInfo
		progressBar = circularProgress { [unowned self] in
			$0.config.trackColor = ACColor.primary
			$0.config.barColor = ACColor.highlight
			$0.config.strokeWidth = 20
			$0.config.size = 216
			
			$0.ellipseConfig.color = ACColor.highlight
			$0.ellipseConfig.alpha = 0
			$0.ellipseConfig.size = 216
			$0.checkConfig.strokeColor = ACColor.primaryInfo
			$0.checkConfig.size = 100
			$0.layout {
				$0.centerX == self.centerXAnchor
				$0.centerY == self.centerYAnchor + 40
				$0.width == 216
				$0.height == 216
			}
			
			
			
			
			
		}
		let bar = progressBar!

		self.countLabel = acLabel {
			Georgia.Style.largeTitle($0, color: .white)
			$0.layer.zPosition = -1
			$0.text = "\(count)"
			$0.layout {
				$0.centerX == bar.centerXAnchor - 25
				$0.centerY == bar.centerYAnchor - 15
			}
		}
		
		
		
		self.maxLabel = acLabel {
			$0.layer.zPosition = -1
			
			Georgia.Style.largeTitle($0, color: .white)
			$0.text = "\(maxCount)"
			$0.layout {
				$0.centerX == bar.centerXAnchor + 20
				$0.centerY == bar.centerYAnchor + 10
			}
		}
		
		
		self.divider = acHorizontalBar {
			$0.color = ACColor.highlight
			$0.layer.zPosition = -1
			
			$0.transform = CGAffineTransform.identity.rotated(by: CGFloat(Math.toRadians(-60.0)))
			$0.relativeWidth = 1.0
			$0.layout {
				$0.centerX == bar.centerXAnchor
				$0.centerY == bar.centerYAnchor
				$0.width == 40
				$0.height == 2
				
			}
		}
		
		sendSubviewToBack(countLabel)
		sendSubviewToBack(maxLabel)
		sendSubviewToBack(divider)

		stack { [unowned self] in
			$0.axis = .vertical
			$0.alignment = .center
			$0.spacing = 8
			self.titleLabel = $0.acLabel {
				Roboto.Style.headingBold($0, color: .white)
                $0.numberOfLines = 0
                $0.textAlignment = .center
			}
			self.subtitleLabel = $0.acLabel {
				Georgia.Style.subtitle($0, color: .white)
			}
			$0.layout {
				$0.centerX == self.centerXAnchor ~ 1000
				$0.bottom == self.progressBar.topAnchor - 40
                $0.leading == self.layoutMarginsGuide.leadingAnchor + 16 ~ 999
                $0.trailing == self.layoutMarginsGuide.trailingAnchor + 16 ~ 999
			}
			
		}
		
		
		
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
