//
// CircularProgressView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import UIKit

public class CircularProgressView : UIView {
	
	public struct Config {
		var strokeWidth:CGFloat = 10
		var trackColor:UIColor = .black
		var barColor:UIColor = .blue
		var progress:Double = 0.1
	}
	
	let trackPath = UIBezierPath()
	let barPath = UIBezierPath()
	let barEdgePath = UIBezierPath()
	
	var startAngle = (3.0 * CGFloat.pi)/2.0
	var endAngle = 2.0 * CGFloat.pi
	var config:Config = Config()
	var progress:Double = 0.1
	
	
	public func configure(_ config:Config) {
		self.config = config
	}
	override public func draw(_ rect: CGRect) {
		super.draw(rect)
		let context = UIGraphicsGetCurrentContext()
		trackPath.removeAllPoints()
		trackPath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
						 radius: rect.width/2.0 - config.strokeWidth,
						 startAngle: startAngle,
						 endAngle: startAngle + endAngle,
						 clockwise: true)
		trackPath.lineWidth = config.strokeWidth
		context?.setStrokeColor(config.trackColor.cgColor)
		trackPath.stroke()
		barEdgePath.removeAllPoints()
		
		if progress > 0 {
			barEdgePath.lineCapStyle = .round
			barEdgePath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
							   radius: rect.width/2.0 - config.strokeWidth,
							   startAngle: startAngle - 0.03 ,
							   endAngle: startAngle + endAngle * CGFloat(progress + 0.005),
							   clockwise: true)
			barEdgePath.lineWidth = config.strokeWidth + 4
			//		context?.setStrokeColor(barEdgeColor.cgColor)
			barEdgePath.stroke(with: .clear, alpha: 1.0)
		}
		barPath.removeAllPoints()
		barPath.lineCapStyle = .round
		barPath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
					   radius: rect.width/2.0 - config.strokeWidth,
					   startAngle: startAngle,
					   endAngle: startAngle + endAngle * CGFloat(progress),
					   clockwise: true)
		barPath.lineWidth = config.strokeWidth
		context?.setStrokeColor(config.barColor.cgColor)
		barPath.stroke()
		
		
		
		
	}
}
