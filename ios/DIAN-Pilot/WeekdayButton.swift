/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class WeekdayButton: DNButton {

    var isTimeSet:Bool = false {
        didSet {
                self.setNeedsDisplay();
        }
    }
    
    override var isSelected: Bool {
        willSet {
            if newValue == true
            {
                setSelectedAppearance();
            }
            else
            {
                setUnselectedAppearance();
            }
        }
    }
    
    override func awakeFromNib() {
        self.setTitleColor(DNAppearanceHelper.blueColor(), for: UIControlState.selected);
        self.setTitleColor(UIColor.white, for: UIControlState.normal);
        self.titleEdgeInsets = UIEdgeInsets(top: -8, left: 0, bottom: 0, right: 0);
        self.backgroundColor = DNAppearanceHelper.blueColor();
    }
    
    func setSelectedAppearance()
    {
        self.backgroundColor = UIColor.white;
        self.setTitleColor(DNAppearanceHelper.blueColor(), for: UIControlState.normal);
    }
    
    func setUnselectedAppearance()
    {
        self.backgroundColor = DNAppearanceHelper.blueColor();
        self.setTitleColor(UIColor.white, for: UIControlState.normal);
    }
    
    override func draw(_ rect: CGRect) {
        super.draw(rect);
        
        if isTimeSet
        {
            let circlePath = UIBezierPath(arcCenter: CGPoint(x: rect.width * 0.5,y: rect.height * 0.8), radius: CGFloat(3), startAngle: CGFloat(0), endAngle:CGFloat(M_PI * 2), clockwise: true)
            
            if isSelected
            {
                DNAppearanceHelper.blueColor().set();
            }
            else
            {
                UIColor.white.set();
            }
            
            circlePath.fill();
        }

    }
}
