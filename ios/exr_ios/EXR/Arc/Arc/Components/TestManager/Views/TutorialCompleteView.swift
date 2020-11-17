//
// TutorialCompleteView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public protocol TutorialCompleteViewDelegate : class {
	func closePressed()
}
public class TutorialCompleteView: ACTemplateView {
	var animation:Animate = Animate().duration(0.3).curve(.easeIn)
	var originPoint:CGPoint = .zero
	var keyFrames:[CGPoint] = []
	var path:UIBezierPath
	var title:ACLabel!
	
	public weak var tutorialDelegate:TutorialCompleteViewDelegate?
	
	var progress:CGFloat = 0 {
		didSet {
			title.alpha = Math.lerp(a: 0, b: 1.0, t: progress)
			let scale = Math.lerp(a: 0.7, b: 1.0, t: progress)
			title.transform = CGAffineTransform.identity.scaledBy(x: scale, y: scale)
			setNeedsDisplay()
		}
	}
	public override init() {
		path = UIBezierPath()

		super.init()
		path.lineWidth = 14.0
		
		keyFrames = [CGPoint(x: -20, y: -20),
						CGPoint(x: 0, y: 0),
						CGPoint(x: 40, y: -40)]
		
	}
	
	public required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	
	public func updateProgress(_ value:CGFloat) {
		let oldValue = progress
		animation.run { [weak self]
			t in
			self?.progress = CGFloat(Math.lerp(a: oldValue, b: value, t: CGFloat(t)))
			return true
		}
	}
	
	
	
	
	
	public override func header(_ view: UIView) {
		view.view {
			$0.backgroundColor = .clear
		}
	}
	public override func content(_ view: UIView) {
		title = view.acLabel {
			$0.alpha = 0
			$0.text = "Tutorial Complete".localized(ACTranslationKey.testing_tutorial_complete)
			Roboto.Style.headingBold($0, color: UIColor(named:"Primary Info"))
			$0.textAlignment = .center
			$0.layout { [weak self] in
				
				$0.centerY == self!.centerYAnchor ~ 999

			}
		}
	}
	public override func footer(_ view: UIView) {
		view.view {
			$0.backgroundColor = .clear
		}
		nextButton = view.acButton {
			$0.translatesAutoresizingMaskIntoConstraints = false
			$0.setTitle("Close".localized(ACTranslationKey.button_close), for: .normal)
			$0.addAction { [weak self] in
				self?.tutorialDelegate?.closePressed()
			}
			
		}
	}
	
	override public func draw(_ rect: CGRect) {
		super.draw(rect)
		originPoint = center.applying(CGAffineTransform(translationX: -10, y: -120))
		let context = UIGraphicsGetCurrentContext()
		path.removeAllPoints()
		var start = CGPoint(x: originPoint.x + keyFrames[0].x,
							y: originPoint.y + keyFrames[0].y)
		var end = CGPoint(x: originPoint.x + keyFrames[1].x,
						  y: originPoint.y + keyFrames[1].y)
		path.move(to: start)
		
		if progress < 0.5 {
			
			let keyProgress = CGFloat(Math.clamp(Double(progress * 2.0)))
			path.addLine(to: Math.lerp(a: start, b: end, t: keyProgress))
		} else {
			path.addLine(to:end)
			start = end
			end = CGPoint(x: originPoint.x + keyFrames[2].x,
						  y: originPoint.y + keyFrames[2].y)
			
			let keyProgress = CGFloat(Math.clamp(Double((progress - 0.5)  * 2.0)))
			path.addLine(to: Math.lerp(a: start, b: end, t: keyProgress))
			
		}
		context?.setStrokeColor(UIColor(named:"Primary Info")!.cgColor)
		path.stroke()
	}
}
