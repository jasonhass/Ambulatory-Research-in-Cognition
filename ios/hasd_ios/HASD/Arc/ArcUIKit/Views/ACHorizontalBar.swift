//
// ACHorizontalBar.swift
//



import UIKit

public class ACHorizontalBar: UIView {
	public var config : Drawing.HorizontalBar
	public var animation:Animate = Animate()
		.duration(0.4)
		.delay(0)
		.curve(.none)
	
	public var relativeWidth : CGFloat {
		
		
		get {
			return config.progress
		}
		set {
			if window != nil {
				animation.stop()
				let old = config.progress
				animation.run {[weak self] t in
					guard let weakSelf = self else {
					
						return false
					}
					guard weakSelf.window != nil else {
						return false
					}
					weakSelf.config.progress = Math.lerp(
						a: old,
						b: newValue,
						t: CGFloat(t))
				
					weakSelf.setNeedsDisplay()
					return true
				}
			
			} else {
				config.progress = newValue
				setNeedsDisplay()
			}
		}
	}
	public var color : UIColor? {
		get {
			return config.primaryColor
		}
		set {
			config.primaryColor = newValue
			setNeedsDisplay()
		}
	}
	
	public var cornerRadius:CGFloat {
		set {
			config.cornerRadius = newValue
			setNeedsDisplay()
		}
		get {
			return config.cornerRadius
		}
	}
	init() {
		config = Drawing.HorizontalBar(
			cornerRadius: 0,
			primaryColor: UIColor(red:0.97,
								  green:0.75,
								  blue:0.08,
								  alpha:1),
			progress: 1.0)
		super.init(frame: .zero)
		backgroundColor = .clear
		
	}
	
	required init?(coder: NSCoder) {
		config = Drawing.HorizontalBar(
			
			cornerRadius: 0,
			primaryColor: UIColor(red:0.97,
								  green:0.75,
								  blue:0.08,
								  alpha:1),
			progress: 1.0)
		super.init(coder: coder)
		backgroundColor = .clear

	}
	/*
     Only override draw() if you perform custom drawing.
     An empty implementation adversely affects performance during animation.*/
    override public func draw(_ rect: CGRect) {
		config.draw(rect)
		
    }
	

}
extension UIView {
	@discardableResult
	public func acHorizontalBar(apply closure: (ACHorizontalBar) -> Void) -> ACHorizontalBar {
		return custom(ACHorizontalBar(), apply: closure)
	}
}
