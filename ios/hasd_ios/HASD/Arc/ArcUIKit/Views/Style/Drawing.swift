//
// Drawing.swift
//



import Foundation

public protocol ACDrawable{
	func draw(_ rect:CGRect)
}
public struct Drawing {
	
	public static func drawInnerShadow(context:CGContext,
									   path:CGPath,
									   shadowColor:CGColor,
									   offSet:CGSize,
									   blurRadius:CGFloat) {
		
		guard let opaqueShadowColor = shadowColor.copy(alpha: 1.0) else {
			assertionFailure("Color could not be copied.")
			return
		}
		context.saveGState()
		
			context.addPath(path)
		
			context.clip()
		
		
			context.setAlpha(shadowColor.alpha)
		
			context.beginTransparencyLayer(auxiliaryInfo: nil)
				context.setShadow(offset: offSet, blur: blurRadius, color: opaqueShadowColor)
				context.setBlendMode(.sourceOut)
				context.setFillColor(opaqueShadowColor)
				context.addPath(path)
				context.fillPath()
			context.endTransparencyLayer()
		context.restoreGState()
		
		
	}
	
	public struct CircularBar : ACDrawable {
		private var startAngle = (3.0 * CGFloat.pi)/2.0
		private var endAngle = 2.0 * CGFloat.pi
		
		
		private var trackPath:UIBezierPath = UIBezierPath()
		private var barEdgePath:UIBezierPath = UIBezierPath()
		private var barPath:UIBezierPath = UIBezierPath()
		
		
		public var strokeWidth:CGFloat = 10
		public var trackColor:UIColor = .black
		public var barColor:UIColor = .blue
		public var progress:Double = 0
		public var size:CGFloat =  24
		
		public init() {
			
		}
		public func draw(_ rect:CGRect) {
			let context = UIGraphicsGetCurrentContext()
			trackPath.removeAllPoints()
			trackPath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
							 radius: size/2.0 - strokeWidth,
							 startAngle: startAngle,
							 endAngle: startAngle + endAngle,
							 clockwise: true)
			trackPath.lineWidth = strokeWidth
			context?.setStrokeColor(trackColor.cgColor)
			trackPath.stroke()
			barEdgePath.removeAllPoints()
			
			if progress > 0 {
				barEdgePath.lineCapStyle = .round
				barEdgePath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
								   radius: size/2.0 - strokeWidth,
								   startAngle: startAngle - 0.01 ,
								   endAngle: startAngle + endAngle * CGFloat(progress) + 0.01,
								   clockwise: true)
				barEdgePath.lineWidth = strokeWidth + 4
				//		context?.setStrokeColor(barEdgeColor.cgColor)
				barEdgePath.stroke(with: .clear, alpha: 1.0)
			}
			barPath.removeAllPoints()
			barPath.lineCapStyle = .round
			barPath.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
						   radius: size/2.0 - strokeWidth,
						   startAngle: startAngle,
						   endAngle: startAngle + endAngle * CGFloat(progress),
						   clockwise: true)
			barPath.lineWidth = strokeWidth
			context?.setStrokeColor(barColor.cgColor)
			barPath.stroke()
			
		}
		
		
	}
	public struct Shadow {
		var color:UIColor = .black
		var offset:CGSize = .zero
		var blur:CGFloat = 5.0
	}
//	public struct ShadowBorder : ACDrawable {
//		var topShadow:Shadow = .init(color: ., offset: <#T##CGSize#>, blur: <#T##CGFloat#>)
//		
//		public func draw(_ rect: CGRect) {
//			<#code#>
//		}
//		
//		
//	}
	
	public struct Ellipse : ACDrawable {

		public var color:UIColor = .highlight
		public var radius:CGFloat = 100.0
		public var alpha:CGFloat = 1.0
		public var size:CGFloat = 200.0 {
			didSet {
				radius = size/2.0
			}
		}
		private var path:UIBezierPath = UIBezierPath()
		
		public init() {
			
		}
		public func draw(_ rect: CGRect) {
			let context = UIGraphicsGetCurrentContext()
			path.removeAllPoints()
			path.addArc(withCenter: CGPoint(x: rect.midX, y: rect.midY),
						radius: radius,
						startAngle: 0,
						endAngle: CGFloat.pi * 2,
						clockwise: true)
			context?.setFillColor(color.cgColor)
			path.fill(with: .normal, alpha: alpha)
			
		}
		
		
	}
	public struct CheckMark : ACDrawable {
		public var keyFrames:[CGPoint] = []
		public var path:UIBezierPath = UIBezierPath()
		public var offset:CGPoint = .zero
		public var strokeColor:UIColor = .primaryInfo
		public var progress:Double = 0
		public var scale:CGFloat = 1.0
		
		public var defaultSize:CGFloat = 60
		public var size:CGFloat = 60 {
			didSet {
				scale = size / defaultSize

			}
		}
		public init() {
			path.lineWidth = 14.0
			
			keyFrames = [CGPoint(x: -30, y: 0),
						 CGPoint(x: -10, y:20),
						 CGPoint(x: 30, y: -20)]
		}
		public func draw(_ rect:CGRect) {
			let context = UIGraphicsGetCurrentContext()
			path.removeAllPoints()
			path.lineWidth = 14.0 * scale
			var start = CGPoint(x: rect.midX + keyFrames[0].x * scale,
								y: rect.midY + keyFrames[0].y * scale)
			var end = CGPoint(x: rect.midX + keyFrames[1].x * scale,
							  y: rect.midY + keyFrames[1].y * scale)
			path.move(to: start)
			
			if progress < 0.5 {
				
				let keyProgress = CGFloat(Math.clamp(Double(progress * 2.0)))
				path.addLine(to: Math.lerp(a: start, b: end, t: keyProgress))
			} else {
				path.addLine(to:end)
				start = end
				end = CGPoint(x: rect.midX + keyFrames[2].x * scale,
							  y: rect.midY + keyFrames[2].y * scale)
				
				let keyProgress = CGFloat(Math.clamp(Double((progress - 0.5)  * 2.0)))
				path.addLine(to: Math.lerp(a: start, b: end, t: keyProgress))
				
			}
			context?.setStrokeColor(strokeColor.cgColor)
			
			path.stroke()
		}
	}
	
	public struct HorizontalBar : ACDrawable {
		public var cornerRadius:CGFloat
		public var primaryColor:UIColor?
		public var progress:CGFloat
		
		
		public func draw(_ rect:CGRect) {
			
			let visible = CGRect(x: rect.origin.x, y: rect.origin.y, width: rect.width * progress, height: rect.height)
			let path = UIBezierPath(roundedRect: visible,
									byRoundingCorners: .allCorners,
									cornerRadii: CGSize(width: cornerRadius, height: cornerRadius))
			path.addClip()
			let context = UIGraphicsGetCurrentContext()
			context?.setFillColor((primaryColor ?? .black).cgColor)
			path.fill()
		}
	}
	public struct GradientButton : ACDrawable {
		var cornerRadius:CGFloat
		var primaryColor:UIColor
		var secondaryColor:UIColor
		public var topShadowColor:UIColor
		public var bottomShadowColor:UIColor
		var isSelected:Bool
		var isEnabled:Bool
		
		
		
		public func draw(_ rect:CGRect) {
			let path = UIBezierPath(roundedRect: rect,
									byRoundingCorners: .allCorners,
									cornerRadii: CGSize(width: cornerRadius, height: cornerRadius))
			path.addClip()
			let context = UIGraphicsGetCurrentContext()
			
			let colors = (!isSelected && isEnabled) ? [secondaryColor.cgColor,
													   primaryColor.cgColor] : [primaryColor.cgColor,
																				primaryColor.cgColor]
			
			let colorSpace = CGColorSpaceCreateDeviceRGB()
			
			let colorLocations:[CGFloat] = [0.0, 1.0]
			
			let gradient = CGGradient(colorsSpace: colorSpace,
									  colors: colors as CFArray,
									  locations: colorLocations)!
			
			let startPoint = CGPoint.zero
			let endPoint = CGPoint(x:0, y:rect.height)
			context?.setAlpha(isEnabled ? 1.0 : 0.5)
			context?.drawLinearGradient(gradient, start: startPoint, end: endPoint, options:[])
			
			guard let c = context, isEnabled, !isSelected else {
				return
			}
			Drawing.drawInnerShadow(context: c, path: path.cgPath, shadowColor: topShadowColor.cgColor, offSet: CGSize(width: 0.0, height: 2.0), blurRadius: 3.0)
			Drawing.drawInnerShadow(context: c, path: path.cgPath, shadowColor: bottomShadowColor.cgColor, offSet: CGSize(width: 0.0, height: -4.0), blurRadius: 3.0)
		}
	}

	
	public struct BadgeGradient : ACDrawable {
		
		public var cornerRadius:CGFloat = 6.0
		public var primaryColor:UIColor = .badgeGradientStart
		public var secondaryColor:UIColor = .badgeGradientEnd
		
		public var borderColor:UIColor = .badgeBackground
		public var isUnlocked:Bool = false
		public var startPoint:CGPoint? = nil
		public var endPoint:CGPoint? = nil
		
		public init() {
			
		}
		public func draw(_ rect:CGRect) {
			var path = UIBezierPath(roundedRect: rect,
									byRoundingCorners: .allCorners,
									cornerRadii: CGSize(width: cornerRadius, height: cornerRadius))
			path.addClip()
			let context = UIGraphicsGetCurrentContext()
			
			let colors = (isUnlocked) ? [secondaryColor.cgColor,
													   primaryColor.cgColor] : [UIColor.clear.cgColor,
																				UIColor.clear.cgColor]
			
			let colorSpace = CGColorSpaceCreateDeviceRGB()
			
			let colorLocations:[CGFloat] = [0.0, 1.0]
			
			let gradient = CGGradient(colorsSpace: colorSpace,
									  colors: colors as CFArray,
									  locations: colorLocations)!
			let startPoint = self.startPoint ?? CGPoint.zero
			let endPoint = self.endPoint ?? CGPoint(x:rect.width, y:0)
			
			context?.drawLinearGradient(gradient, start: startPoint, end: endPoint, options:[])
			path = UIBezierPath(roundedRect: rect.insetBy(dx: 1, dy: 1),
									byRoundingCorners: .allCorners,
									cornerRadii: CGSize(width: cornerRadius, height: cornerRadius))
			path.lineWidth = 2
			if isUnlocked {
				context?.setStrokeColor(borderColor.cgColor)
				path.setLineDash(nil, count:0, phase: 0)
				path.stroke()
			} else {
				let dashPattern: [CGFloat] = [4.0, 4.0]
				path.setLineDash(dashPattern, count: dashPattern.count, phase: 0)
				context?.setStrokeColor(UIColor.badgeGray.cgColor)
				path.stroke()

			}
		}
	}
}
