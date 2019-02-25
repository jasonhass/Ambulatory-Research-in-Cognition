//
// Common.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


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
