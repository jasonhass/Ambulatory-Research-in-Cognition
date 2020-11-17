//
// StepperProgressview.swift
//


import UIKit

public class StepperProgressView : UIView {
	public struct Config {
		public var foregroundColor:UIColor = .blue
		public var backgroundColor:UIColor = .lightGray
		
		public var barInset:CGFloat = 10
		public var barWidth:CGFloat = 32
		public var outlineColor:UIColor = .black
		
	}
	public var progress:CGFloat = 0.5 { didSet {setNeedsDisplay()}}
	public var pos:CGFloat?
	public var config:Config = Config()
	public let endRectView:UIView = UIView()
	var outlinePath:UIBezierPath = UIBezierPath()
	var fillPath:UIBezierPath = UIBezierPath()
	
	
	public override init(frame: CGRect) {
		super.init(frame: frame)
		backgroundColor = .clear
		build()
		
	}
	
	
	public required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)

	}
	func build() {
		endRectView.backgroundColor = .clear
		self.addSubview(endRectView)

	}
	public override func draw(_ rect: CGRect) {
		super.draw(rect)
		
		let context = UIGraphicsGetCurrentContext()
		context?.setStrokeColor(config.outlineColor.cgColor)
		outlinePath = UIBezierPath(roundedRect: bounds.insetBy(dx:1.0, dy: config.barInset), cornerRadius: bounds.height/2.0)

		outlinePath.lineWidth = 1.0
		outlinePath.stroke()
		var endRect = CGRect(x: (frame.width -  frame.height) * progress, y: rect.midY - frame.height/2.0, width: frame.height, height: frame.height)
		if let pos = pos {
			endRect = CGRect(x: pos - frame.height/2, y: rect.midY - frame.height/2.0, width: frame.height, height: frame.height)
		}
		endRectView.frame = endRect

		fillPath.move(to: CGPoint(x: rect.minX + config.barWidth / 2.0, y: rect.midY))
		fillPath.addLine(to: CGPoint(x: endRect.midX , y: endRect.midY))
		context?.setStrokeColor(config.foregroundColor.cgColor)
		fillPath.lineCapStyle = .round
		fillPath.lineWidth = config.barWidth
		fillPath.stroke()
		
		context?.addEllipse(in: endRect)
		context?.setFillColor(config.foregroundColor.cgColor)
		context?.fillPath()
		
	}
}

extension UIView {
	
	@discardableResult
	public func stepperProgress(apply closure: (StepperProgressView) -> Void) -> StepperProgressView {
		return custom(StepperProgressView(), apply: closure)
	}
	
}
