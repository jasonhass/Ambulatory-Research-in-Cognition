//
// SymbolSelectButton.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class SymbolSelectButton: UIButton {
    
    var touchLocation:CGPoint?;
    var touchTime:Date?
    
    override open func awakeFromNib() {
        //self.layer.borderColor = UIColor(white: 128.0/255.0, alpha: 1.0).cgColor;
        //self.layer.borderWidth = 2;
        //self.layer.cornerRadius = 6;
    }
    
    override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.superview?.layer.borderColor = UIColor(named: "Primary")!.cgColor
        
//        UIView.animate(withDuration: 0.01, animations: {
//            self.superview?.frame = CGRect(x: (self.superview?.frame.origin.x)!, y: (self.superview?.frame.origin.y)! + 2, width: (self.superview?.frame.size.width)!, height: (self.superview?.frame.size.height)!)
//        })
		
        if let t = touches.first
        {
            touchLocation = t.location(in: self.window);
            touchTime = Date();
        }
		super.touchesBegan(touches, with: event)
		
        //self.sendActions(for: UIControl.Event.touchDown);
    }

    override open func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event )
        self.superview?.layer.borderColor = UIColor(white: 128.0/255.0, alpha: 1.0).cgColor
    
//        UIView.animate(withDuration: 0.01, animations: {
//            self.superview?.frame = CGRect(x: (self.superview?.frame.origin.x)!, y: (self.superview?.frame.origin.y)! - 2, width: (self.superview?.frame.size.width)!, height: (self.superview?.frame.size.height)!)
//        })
    }
   
    
}

open class SymbolContainer: UIView {
    
    override open func awakeFromNib() {
        self.layer.borderColor = UIColor(white: 128.0/255.0, alpha: 1.0).cgColor;
        self.layer.borderWidth = 2;
        self.layer.cornerRadius = 6;
    }
    
    override open func layoutSubviews() {
        if shadowOpacity > 0.0 {
            self.addShadow()
        }
    }

    @IBInspectable
    var shadowRadius: CGFloat {
        get {
            return layer.shadowRadius
        }
        set {
            layer.shadowRadius = newValue
        }
    }
    
    @IBInspectable
    var shadowOpacity: Float {
        get {
            return layer.shadowOpacity
        }
        set {
            layer.shadowOpacity = newValue
        }
    }
    
    @IBInspectable
    var shadowOffset: CGSize {
        get {
            return layer.shadowOffset
        }
        set {
            layer.shadowOffset = newValue
        }
    }
    
    @IBInspectable
    var shadowColor: UIColor? {
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
	
	
    // From JAView in joann_ios
    // And https://stackoverflow.com/questions/15704163/draw-shadow-only-from-3-sides-of-uiview
    func addShadow(shadowColor: CGColor = UIColor.black.cgColor,
                   shadowOffset: CGSize = CGSize(width: 0.0, height: 1.0),
                   shadowOpacity: Float = 0.2,
                   shadowRadius: CGFloat = 3.0) {
        //Remove previous shadow views
        //superview?.viewWithTag(119900)?.removeFromSuperview()
        
        //Create new shadow view with frame
        let shadowView = UIView(frame: frame)
        //shadowView.tag = 119900
        shadowView.layer.shadowColor = shadowColor
        shadowView.layer.shadowOffset = shadowOffset
        shadowView.layer.masksToBounds = false
        
        let path = UIBezierPath()
        var x: CGFloat = 0
        var y: CGFloat = 0
        var viewWidth = shadowView.frame.width
        let viewHeight = shadowView.frame.height
        
        // Only add to the bottom
        let top = false
//        let bottom = true
        let left = false
        let right = false
        
        if (!top) {
            y+=(shadowRadius+1)
        }
//        if (!bottom) {
//            viewHeight-=(shadowRadius+1)
//        }
        if (!left) {
            x+=(shadowRadius+1)
        }
        if (!right) {
            viewWidth-=(shadowRadius+1)
        }
        // selecting top most point
        path.move(to: CGPoint(x: x, y: y))
        // Move to the Bottom Left Corner, this will cover left edges
        /*
         |☐
         */
        path.addLine(to: CGPoint(x: x, y: viewHeight))
        // Move to the Bottom Right Corner, this will cover bottom edge
        /*
         ☐
         -
         */
        path.addLine(to: CGPoint(x: viewWidth, y: viewHeight))
        // Move to the Top Right Corner, this will cover right edge
        /*
         ☐|
         */
        path.addLine(to: CGPoint(x: viewWidth, y: y))
        // Move back to the initial point, this will cover the top edge
        /*
         _
         ☐
         */
        path.close()
    
        shadowView.layer.shadowOpacity = shadowOpacity
        shadowView.layer.shadowRadius = shadowRadius
        //shadowView.layer.shadowPath = UIBezierPath(rect: bounds).cgPath
        shadowView.layer.shadowPath = path.cgPath
        shadowView.layer.rasterizationScale = UIScreen.main.scale
        shadowView.layer.shouldRasterize = true
        superview?.insertSubview(shadowView, belowSubview: self)
    }
}





