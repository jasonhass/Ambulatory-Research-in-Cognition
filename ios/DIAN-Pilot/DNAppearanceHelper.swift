/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class DNAppearanceHelper: NSObject {

    static func blueColor() -> UIColor
    {
        
        
        var color:UIColor =  UIColor(red: 13.0 / 255.0, green: 143.0 / 255.0, blue: 192.0 / 255.0, alpha: 1.0);
        
        #if DBG
        #elseif EXR
            color =  UIColor(red: 42.0 / 255.0, green: 112.0 / 255.0, blue: 70.0 / 255.0, alpha: 1.0);
        #endif

        return color
    }
    
    static func lightBlueColor() -> UIColor
    {
        var color:UIColor = UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);

        #if DBG
        #elseif EXR
            color =  UIColor(red: 117.0 / 255.0, green: 216.0 / 255.0, blue: 180.0 / 255.0, alpha: 1.0);
        #endif

        return color
    }
    
    static func UIColorErrorRed() -> UIColor
    {
        return UIColor(red: 214.0/255.0, green: 91.0/255.0, blue: 91.0/255.0, alpha: 1.0);
    }
    
    static func darkGrey() -> UIColor
    {
        return UIColor(white: 77.0 / 255.0, alpha: 1.0);
    }
    
    static func colorForKey(colorKey:String) -> UIColor? {
        if colorKey == "Primary" {
            return blueColor()
        }
        if colorKey == "PrimaryLight" {
            return lightBlueColor()
        }

        return nil
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
