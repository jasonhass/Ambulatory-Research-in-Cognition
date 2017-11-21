//
//  DNAppearanceHelper.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class DNAppearanceHelper: NSObject {

    static func blueColor() -> UIColor
    {
        return UIColor(red: 13.0 / 255.0, green: 143.0 / 255.0, blue: 192.0 / 255.0, alpha: 1.0);
    }
    
    static func lightBlueColor() -> UIColor
    {
        return UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);
    }
    
    static func UIColorErrorRed() -> UIColor
    {
        return UIColor(red: 214.0/255.0, green: 91.0/255.0, blue: 91.0/255.0, alpha: 1.0);
    }
    
    static func darkGrey() -> UIColor
    {
        return UIColor(white: 77.0 / 255.0, alpha: 1.0);
    }
    
    
}

// Add border to a particlar side of a layer
extension CALayer {
    
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

// Add error state border to views
extension UIView {
    func setErrorBorder()
    {
        self.layer.borderWidth = 2;
        self.layer.borderColor = DNAppearanceHelper.UIColorErrorRed().cgColor;
    }
    
    func clearErrorBorder()
    {
        self.layer.borderWidth = 0;
        self.layer.borderColor = UIColor.clear.cgColor;
    }
}
