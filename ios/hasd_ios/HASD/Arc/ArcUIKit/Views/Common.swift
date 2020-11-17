//
// Common.swift
//



import Foundation
import UIKit


@objc public enum ACTextStyle : Int{
    /*
     0 - "Roboto-Regular"
     1 - "Roboto-Black"
     2 - "Roboto-Light"
     3 - "Roboto-LightItalic"
     4 - "Roboto-Thin"
     5 - "Roboto-MediumItalic"
     6 - "Roboto-Medium"
     7 - "Roboto-Bold"
     8 - "Roboto-BlackItalic"
     9 - "Roboto-Italic"
     */
    static public let roboto = UIFont.fontNames(forFamilyName: "Roboto")
    
    /*
     0 "Georgia-BoldItalic"
     1 "Georgia-Italic"
     2 "Georgia"
     3 "Georgia-Bold"
     */
    static public let georgia = UIFont.fontNames(forFamilyName: "Georgia")

    case none, body, heading, title, introHeading, selectedBody
    
    var size:CGFloat {
        switch self {
        case .body, .selectedBody:
            return 18.0
        case .heading:
            return 26.0
        case .title:
            return 26.0
        case .introHeading:
            return 22.5
        
        default:
            return 18.0
        }
    }
    var font:UIFont? {
        var f:UIFont?
        switch self {
        
        case .body:
            f = UIFont(name: ACTextStyle.roboto[0], size: self.size)
   
        case .heading:
            f = UIFont(name: ACTextStyle.roboto[0], size: self.size)
       
        case .title:
            f = UIFont(name: ACTextStyle.roboto[7], size: self.size)
            
        case .introHeading:
            f = UIFont(name: ACTextStyle.georgia[1], size: self.size)
            
        case .selectedBody:
            f = UIFont(name: ACTextStyle.roboto[1], size: self.size)
        
        default:
            break
        }
        
        return f
    }
    
}

public enum PhoneClass {
    case iphoneSE, iphone678, iphone678Plus, iphoneX, iphoneXR, iphoneMax
    
    public static func getClass() -> PhoneClass {
        let width:CGFloat = UIScreen.main.bounds.width
        let height:CGFloat = UIScreen.main.bounds.height
        switch (width, height) {
        case (320, 568):
            return .iphoneSE
        case (375, 667):
            return .iphone678
        case (414, 736):
            return .iphone678Plus
        case (375, 812):
            return .iphoneX
        case (414, 896):
            return .iphoneXR
        case (414, 896):
            return .iphoneMax
        default:
            return .iphone678   //should never get here, but this seems like the safest size class to fall back on
        }
    }
}
