//
// UIView + Pulse.swift
//


import Foundation
import QuartzCore

func createAnimatedLayer(from view: UIView, strokeColor: UIColor, fillColor: UIColor, overlayShape:OverlayShape) -> CAShapeLayer {
	let layer = CAShapeLayer()
    let path = overlayShape.path(forView: view)
	layer.frame = view.bounds
	layer.path = path.cgPath
	layer.strokeColor = strokeColor.cgColor
	layer.lineWidth = 2
	layer.fillColor = fillColor.cgColor
	layer.lineCap = CAShapeLayerLineCap.round
	
	
	
	layer.zPosition = 100
	return layer
}

extension CALayer {
	func animatePulsingBorder(to scale:Double = 1.3, for duration: Double = 1.0, looping:Bool = true) {
		//scale animation
		let scaleAnimation = CABasicAnimation(keyPath: "transform.scale")
		scaleAnimation.toValue = scale
		scaleAnimation.duration = duration
		scaleAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeOut)
		
		//opacity animation
		let opacityAnimation = CABasicAnimation(keyPath: "opacity")
		opacityAnimation.fromValue = 1.0
		opacityAnimation.toValue = 0.0
		opacityAnimation.duration = duration
		opacityAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeOut)
		
		if looping {
			scaleAnimation.repeatCount = Float.infinity
			opacityAnimation.repeatCount = Float.infinity
		}
		
		self.add(scaleAnimation, forKey: "pulsing")
		self.add(opacityAnimation, forKey: "opacity")
		
	}
}

extension UIView {
	
	static var highlightId:Int {
		get {
			return 666420
		}
	}
	
	public func highlight(radius:CGFloat = 0.0) {
		let color = UIColor.tutorialHighLight
		let newView = OverlayView()
		newView.tag = UIView.highlightId
		newView.backgroundColor = .clear
		
		self.window?.addSubview(newView)
		newView.frame = self.convert(self.bounds, to: nil)

        let animatedLayer = createAnimatedLayer(from: newView, strokeColor: color, fillColor: .clear, overlayShape:  OverlayShape.roundedRect(self, radius, CGSize(width: -8, height: -8)))
		
		animatedLayer.animatePulsingBorder(to: 1.3, for: 1.0, looping: true)
		newView.layer.addSublayer(animatedLayer)
	}
	public func removeHighlight() {
		
		while let view = window?.viewWithTag(UIView.highlightId) {
			view.removeFromSuperview()
		}
		
	}
	public func hasHighlight() -> Bool {
		guard let window = window else { return false }
		return window.subviews.contains {
			$0.tag == UIView.highlightId && $0.frame == self.frame
		}
	}
	public func getTopLevelView() -> UIView {
		var view = self
		while let parent = view.superview {
			view = parent
		}
		
		return view
	}
}
