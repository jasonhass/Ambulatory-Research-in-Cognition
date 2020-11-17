//
// StepperProgressview.swift
//


import UIKit

public class StepperProgressView : UIView {
	public struct Config {
		public var foregroundColor:UIColor = .blue
		public var backgroundColor:UIColor = .lightGray
		
		public var barInset:CGFloat = 8
		public var barWidth:CGFloat = 32
		public var endRadius:CGFloat = 25
		public var outlineColor:UIColor = .black
		
	}
	public var progress:CGFloat = 0.5
	public var config:Config = Config() {
		didSet {
			print("updating")
			outlinePath = UIBezierPath(roundedRect: bounds.insetBy(dx:0.0, dy: config.barInset), cornerRadius: bounds.height/2.0)
			setNeedsDisplay()
		}
	}
	
	var outlinePath:UIBezierPath = UIBezierPath()
	var fillPath:UIBezierPath = UIBezierPath()
	public override init(frame: CGRect) {
		super.init(frame: frame)
		backgroundColor = .clear
		outlinePath = UIBezierPath(roundedRect: bounds.insetBy(dx:0.0, dy: config.barInset), cornerRadius: bounds.height/2.0)
		config.endRadius = frame.height/2.0
		
	}
	
	
	public required init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
	}
	
	public override func draw(_ rect: CGRect) {
		super.draw(rect)
		
		let context = UIGraphicsGetCurrentContext()
		context?.setStrokeColor(config.outlineColor.cgColor)
		outlinePath.stroke()
		let endRect = CGRect(x: (frame.width -  config.endRadius * 2) * progress, y: 0, width: config.endRadius * 2, height: config.endRadius * 2)
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
