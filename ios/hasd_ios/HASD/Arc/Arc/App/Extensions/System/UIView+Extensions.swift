//
// UIView+Extensions.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public extension UIView {
    static func get<T:UIView>(nib:String? = nil) -> T{
		//For multi-module environments get the bundle for the class
		let bundle = Bundle(for: T.self)

        let view = bundle.loadNibNamed((nib ?? String(describing: self)), owner: nil, options: nil)?.first as! T
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }
	
	///Attach a view to the margins of the parent view
    func attachTo(view:UIView?, margins:UIEdgeInsets? = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)) {
		guard let view = view else {return}
		if let margins = margins {
			view.layoutMargins = margins
		}
		layout {
			
			// select an anchor give a priority of 999 (almost Required)
			$0.top == view.layoutMarginsGuide.topAnchor ~ 999
			$0.trailing == view.layoutMarginsGuide.trailingAnchor ~ 999
			$0.bottom == view.layoutMarginsGuide.bottomAnchor ~ 999
			$0.leading == view.layoutMarginsGuide.leadingAnchor ~ 999
			
		}
	}
	///Attach a view to the safe area margins of the parent view
	
    func safeAttachTo(view:UIView?, margins:UIEdgeInsets? = .zero) {
		guard let view = view else {return}
		if let margins = margins {
			view.layoutMargins = margins
		}
		layout {
			
			// select an anchor give a priority of 999 (almost Required)
			$0.top == view.safeAreaLayoutGuide.topAnchor ~ 999
			$0.trailing == view.layoutMarginsGuide.trailingAnchor ~ 999
			$0.bottom == view.safeAreaLayoutGuide.bottomAnchor ~ 999
			$0.leading == view.layoutMarginsGuide.leadingAnchor ~ 999
			
		}
	}
    func anchor(view:UIView) {
        self.addSubview(view)
        let left = view.leftAnchor.constraint(equalTo: self.leftAnchor);
        let right = view.rightAnchor.constraint(equalTo: self.rightAnchor);
        let top = view.topAnchor.constraint(equalTo: self.topAnchor);
        let bottom = view.bottomAnchor.constraint(equalTo: self.bottomAnchor);
        
        left.priority = UILayoutPriority(999);
        right.priority = UILayoutPriority(999);
        top.priority = UILayoutPriority(999);
        bottom.priority = UILayoutPriority(999);
        
        left.isActive = true;
        right.isActive = true;
        top.isActive = true;
        bottom.isActive = true;
        
        self.layoutSubviews()
    }
    func constrain(view:UIView,insets:UIEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)) {
        self.layoutMargins = insets;
        self.addSubview(view)
        let left = view.leftAnchor.constraint(equalTo: self.layoutMarginsGuide.leftAnchor);
        let right = view.rightAnchor.constraint(equalTo: self.layoutMarginsGuide.rightAnchor);
        let top = view.topAnchor.constraint(equalTo: self.layoutMarginsGuide.topAnchor);
        let bottom = view.bottomAnchor.constraint(equalTo: self.layoutMarginsGuide.bottomAnchor);
        
        left.priority = UILayoutPriority(999);
        right.priority = UILayoutPriority(999);
        top.priority = UILayoutPriority(999);
        bottom.priority = UILayoutPriority(999);
        
        left.isActive = true;
        right.isActive = true;
        top.isActive = true;
        bottom.isActive = true;
        
        self.layoutSubviews()
    }
	
	
	@discardableResult func showSpinner(color:UIColor? = nil, backgroundColor:UIColor? = nil, message:String? = nil, messageColor:UIColor? = .white) -> UIActivityIndicatorView {
		
		self.hideSpinner()
		let spinner = UIActivityIndicatorView()
		
		spinner.style = .white
		if let color = color {
			spinner.color = color
		}
		if let backgroundColor = backgroundColor {
			spinner.backgroundColor = backgroundColor
		}
		spinner.frame = self.bounds
		spinner.layer.cornerRadius = self.layer.cornerRadius
		spinner.hidesWhenStopped = true
		//        self.addSubview(spinner)
		self.anchor(view: spinner)
		spinner.alpha = 0
		
		if let message = message {
			spinner.acLabel {
				$0.layout {
					$0.centerX == spinner.centerXAnchor
					$0.centerY == spinner.centerYAnchor + 40
					$0.leading == spinner.leadingAnchor + 16
					$0.trailing == spinner.trailingAnchor - 16
				}
				Roboto.Style.body($0, color: messageColor ?? .black)
				$0.textAlignment = .center
				$0.text = message
			}
		}
		
		UIView.animate(withDuration: 0.6, delay: 0.0, options: .curveEaseInOut, animations: {
			spinner.alpha = 1
		}) { (s) in
			
		}
		spinner.startAnimating()
		
		return spinner
	}
	
    func hideSpinner()
	{
		for s in self.subviews
		{
			if let spinner = s as? UIActivityIndicatorView
			{
				UIView.animate(withDuration: 0.2, delay: 0.0, options: .curveEaseInOut, animations: {
					spinner.alpha = 0
				}) { (s) in
					spinner.stopAnimating()
					spinner.removeFromSuperview();
				}
				
				return;
			}
		}
	}
}

// Add border to a particlar side of a layer
public extension CALayer {
    
    func addBorder(edge: UIRectEdge, color: UIColor, thickness: CGFloat) -> CALayer{
        
        let border = CALayer()
        
        switch edge {
        case UIRectEdge.top:
            border.frame = CGRect(x: 0, y: 0, width: self.frame.width, height: thickness)
            break
        case UIRectEdge.bottom:
            border.frame = CGRect(x: 0, y: self.frame.height - thickness, width: self.frame.width, height: thickness)
            break
        case UIRectEdge.left:
            border.frame = CGRect(x: 0, y: 0, width: thickness, height: self.frame.height)
            break
        case UIRectEdge.right:
            border.frame = CGRect(x: self.frame.width - thickness, y:0, width: thickness, height: self.frame.height)
            break
        default:
            break
        }
        
        border.backgroundColor = color.cgColor;
        
        self.addSublayer(border)
        return border
    }
}


@IBDesignable
open class BorderedView : UIView {
    
    @IBInspectable public var borderTop:Bool = false
    @IBInspectable public var borderRight:Bool = false
    @IBInspectable public var borderBottom:Bool = false
    @IBInspectable public var borderLeft:Bool = false
    @IBInspectable public var cornerTopLeft:Bool = false
    @IBInspectable public var cornerTopRight:Bool = false
    @IBInspectable public var cornerBottomLeft:Bool = false
    @IBInspectable public var cornerBottomRight:Bool = false
    @IBInspectable public var borderColor:UIColor = UIColor.black
    @IBInspectable public var borderThickness:CGFloat = 1.0
    @IBInspectable public var cornerRadius:CGFloat = 5.0
    
    //    @IBInspectable var shadowColor:UIColor = UIColor.clear
    //    @IBInspectable var shadowRadius:CGFloat = 0
    //    @IBInspectable var shadowOpacity:Float = 0
    var borders:[CALayer]? = []
    
    override public init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setBorders()
        setCorners()
        //        setShadow()
    }
    
    override open func prepareForInterfaceBuilder() {
        if(self.layer.frame.width > 0 && borderThickness > 0){
            setBorders()
        }
        setCorners()
        //        setShadow()
    }
    
    override open func layoutSubviews() {
        //        if(self.layer.frame.width > 0 && borderThickness > 0){
        setBorders()
        //        }
        setCorners()
        //        setShadow()
        if shadowOpacity > 0.0 {
            self.addShadow()
        }
        
        
    }
    
    @IBInspectable
    public var shadowRadius: CGFloat {
        get {
            return layer.shadowRadius
        }
        set {
            layer.shadowRadius = newValue
        }
    }
    
    @IBInspectable
     public var shadowOpacity: Float {
        get {
            return layer.shadowOpacity
        }
        set {
            layer.shadowOpacity = newValue
        }
    }
    
    @IBInspectable
     public var shadowOffset: CGSize {
        get {
            return layer.shadowOffset
        }
        set {
            layer.shadowOffset = newValue
        }
    }
    
    @IBInspectable
     public var shadowColor: UIColor? {
        get {
            if let color = layer.shadowColor {
                return UIColor(cgColor: color)
            }
            return nil
        }
        set {
            if let color = newValue {
                layer.shadowColor = color.cgColor
            } else {
                layer.shadowColor = nil
            }
        }
    }

    func addShadow(shadowColor: CGColor = UIColor.black.cgColor,
                   shadowOffset: CGSize = CGSize(width: 1.0, height: 2.0),
                   shadowOpacity: Float = 0.4,
                   shadowRadius: CGFloat = 3.0) {
        //Remove previous shadow views
        superview?.viewWithTag(119900)?.removeFromSuperview()
        
        //Create new shadow view with frame
        let shadowView = UIView(frame: frame)
        shadowView.tag = 119900
        shadowView.layer.shadowColor = shadowColor
        shadowView.layer.shadowOffset = shadowOffset
        shadowView.layer.masksToBounds = false
        
        shadowView.layer.shadowOpacity = shadowOpacity
        shadowView.layer.shadowRadius = shadowRadius
        shadowView.layer.shadowPath = UIBezierPath(rect: bounds).cgPath
        shadowView.layer.rasterizationScale = UIScreen.main.scale
        shadowView.layer.shouldRasterize = true
        superview?.insertSubview(shadowView, belowSubview: self)
    }
    
    
    func setCorners(){
        var corners:UIRectCorner = .topLeft
        
        if cornerTopLeft{
            corners.insert(.topLeft)
        } else {
            corners.remove(.topLeft)
        }
        if cornerTopRight{
            corners.insert(.topRight)
        }
        if cornerBottomLeft{
            corners.insert( .bottomLeft)
        }
        if cornerBottomRight{
            corners.insert( .bottomRight)
        }
        if(!cornerTopLeft && !cornerTopRight && !cornerBottomRight && !cornerBottomLeft){
            
            self.layer.mask = nil
            
        } else {
            
            let path = UIBezierPath(roundedRect: self.bounds, byRoundingCorners: corners, cornerRadii: CGSize(width: cornerRadius, height: cornerRadius))
            let mask = CAShapeLayer()
            mask.path = path.cgPath
            self.layer.mask = mask
            
        }
    }
    func setBorders(){
        
        clearBorders()
        var layer:CALayer
        if borderTop && borderRight && borderLeft && borderBottom {
            self.layer.borderColor = borderColor.cgColor
            self.layer.borderWidth = borderThickness
            self.layer.cornerRadius = cornerRadius
            
        } else {
            self.layer.borderColor = nil
            self.layer.cornerRadius = 0

            if borderTop {
                layer = self.layer.addBorder(edge: .top, color: borderColor, thickness: borderThickness)
                borders?.append(layer)
            }
            if borderRight {
                layer = self.layer.addBorder(edge: .right, color: borderColor, thickness: borderThickness)
                borders?.append(layer)
            }
            if borderBottom {
                layer = self.layer.addBorder(edge: .bottom, color: borderColor, thickness: borderThickness)
                borders?.append(layer)
            }
            if borderLeft {
                layer = self.layer.addBorder(edge: .left, color: borderColor, thickness: borderThickness)
                borders?.append(layer)
            }
        }
        
        
    }
    
    func clearBorders(){
        if borders?.isEmpty == false {
            borders!.forEach{ $0.removeFromSuperlayer()}
            borders = [CALayer]()
        }
    }
}
