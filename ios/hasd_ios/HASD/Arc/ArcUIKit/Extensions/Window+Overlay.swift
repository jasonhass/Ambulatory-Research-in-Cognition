//
// Window+Overlay.swift
//



import UIKit
public enum OverlayShape {
	case rect(UIView), roundedRect(UIView, CGFloat, CGSize), circle(UIView)
	
	public func path() -> UIBezierPath {
		
		switch self {
		case .rect(let view):
			var rect = view.superview?.convert(view.frame, to: nil) ?? view.bounds
			rect = rect.insetBy(dx: -8, dy: -8)
			return UIBezierPath.init(rect: rect)
		case .circle(let view):
			var rect = view.superview?.convert(view.frame, to: nil) ?? view.bounds
			rect = rect.insetBy(dx: -8, dy: -8)

			return UIBezierPath(arcCenter: CGPoint(x: rect.midX, y: rect.midY), radius: max(rect.width/2, rect.height/2) , startAngle: CGFloat.pi , endAngle: CGFloat.pi + CGFloat.pi * 2, clockwise: true)
        case .roundedRect(let view, let cornerRadius, let inset):
			var rect = view.superview?.convert(view.frame, to: nil) ?? view.bounds
			
            rect = rect.insetBy(dx: inset.width, dy: inset.height)

			return UIBezierPath(roundedRect: rect, cornerRadius: cornerRadius)
		}
	}
    
    public func path(forView parent: UIView) -> UIBezierPath {

        switch self {
        case .rect(let view):
            var rect = parent.convert(view.frame, from: view.superview)
            rect = rect.insetBy(dx: -8, dy: -8)
            return UIBezierPath.init(rect: rect)
        case .circle(let view):
            var rect = parent.convert(view.frame, from: view.superview)
            rect = rect.insetBy(dx: -8, dy: -8)
            return UIBezierPath(arcCenter: CGPoint(x: rect.midX, y: rect.midY), radius: max(rect.width/2, rect.height/2) , startAngle: CGFloat.pi , endAngle: CGFloat.pi + CGFloat.pi * 2, clockwise: true)
        case .roundedRect(let view, let cornerRadius, let inset):
            var rect = parent.convert(view.frame, from: view.superview)
            rect = rect.insetBy(dx: inset.width, dy: inset.height)
            return UIBezierPath(roundedRect: rect, cornerRadius: cornerRadius)
        }
    }

}
public class OverlayView: UIView {
	override public func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
		let maskPath = (layer.mask as? CAShapeLayer)?.path
		if maskPath?.contains(point, using: .evenOdd, transform: .identity) == true {
			print("contains: \(point)")
			return true
		} else {
			print("does not contain: \(point)")

			return false
		}
		
	}
}
extension UIView {
    public func overlay(radius:CGFloat = 8.0, inset:CGSize = CGSize(width: -8, height: -8)) {
		if let window = window {
			window.overlayView(withShapes: [.roundedRect(self, radius, inset)])
		}
	}
}
extension UIView {
	static var overlayId:Int {
		get {
			return 95384754
		}
	}
	
	public func clearOverlay() {
		if let oldView = self.viewWithTag(UIWindow.overlayId) as? OverlayView {
            oldView.tag = 0
			UIView.animate(withDuration: 0.5, delay: 0, options: .curveLinear, animations: {
				oldView.alpha = 0
				
			}, completion: { (stop) in
				oldView.removeFromSuperview()
			})
		}
	}
	public func overlayView(withShapes shapes:[OverlayShape]) {
		clearOverlay()
		
		
		let overlay = OverlayView(frame: self.bounds)
		overlay.tag = UIWindow.overlayId
		overlay.backgroundColor = UIColor.init(white: 0, alpha: 0.8)
		overlay.alpha = 0
		self.addSubview(overlay)
		
		let path = UIBezierPath()
		path.append(UIBezierPath.init(rect: self.bounds))
		
		for v in shapes {
			path.append(v.path(forView: self))
		}
		
		
		let maskLayer = CAShapeLayer()
		maskLayer.path = path.cgPath
		maskLayer.frame = self.bounds
		maskLayer.fillRule = .evenOdd
		overlay.layer.mask = maskLayer
		
		UIView.animate(withDuration: 0.35, delay: 0.0, options: .curveLinear, animations: {
			overlay.alpha = 1
			
		}, completion: { (stop) in
			
		})
		
		for v in self.subviews {
			if v.tag == UIView.highlightId {
				self.bringSubviewToFront(v)
			}
		}
	}
	
}


public class JABorderedView: UIView {
	
	@IBInspectable public var borderWidth:CGFloat = 2;
	@IBInspectable public var cornerRadius:CGFloat = 0;
	@IBInspectable public var borderColor:UIColor = .black;
	
	public override func awakeFromNib() {
		super.awakeFromNib();
		setup();
	}
	
	public func setup() {
		self.layer.borderWidth = borderWidth;
		self.layer.cornerRadius = cornerRadius;
		self.layer.borderColor = borderColor.cgColor;
	}
	
}
