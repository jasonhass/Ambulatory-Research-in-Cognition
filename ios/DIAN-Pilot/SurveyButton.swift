/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class SurveyButton: DNButton {

    let blueColor = DNAppearanceHelper.blueColor()

    override func awakeFromNib() {
        super.awakeFromNib();
        self.layer.cornerRadius = 3.0
        self.layer.backgroundColor = blueColor.cgColor
        self.titleLabel?.font = UIFont(name: "Georgia-BoldItalic", size: 20)
        setTitleColor(UIColor.white, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)

        
//        self.addTarget(self, action: #selector(touchUpInside(sender:)), for: .touchUpInside)
    }

//    override var isSelected: Bool {
//        didSet {
//            backgroundColor = blueColor
//            
//        }
//    }
    
    

//    func touchUpInside(sender: UIButton!) {
//        isSelected = !isSelected
//    }

}
class ToggleButton: DNButton {
    
    var blueColor = DNAppearanceHelper.blueColor()
    @IBInspectable var shouldAutoToggle:Bool = true;
    override func awakeFromNib() {
        super.awakeFromNib();
        if let colorKey = self.colorKey, let color = DNAppearanceHelper.colorForKey(colorKey: colorKey) {
            blueColor = color
        }

        self.layer.borderColor = blueColor.cgColor
        if let colorKey = self.colorKey, let color = DNAppearanceHelper.colorForKey(colorKey:colorKey){
            self.layer.borderColor = color.cgColor
        }
        self.layer.borderWidth = 1.0
        self.layer.cornerRadius = 3.0
        self.titleLabel?.font = UIFont(name: "Tahoma", size: 20)

        setTitleColor(UIColor.black, for: .normal)
        setTitleColor(UIColor.white, for: .highlighted)
        setTitleColor(UIColor.white, for: .selected)
        
        
        self.addTarget(self, action: #selector(touchUpInside(sender:)), for: .touchUpInside)
    }
    
    override var isSelected: Bool {
        didSet {
            
            backgroundColor = isSelected ? blueColor : UIColor.clear
            if let colorKey = self.colorKey, let color = DNAppearanceHelper.colorForKey(colorKey:colorKey){
                backgroundColor = isSelected ? color : UIColor.clear
            }
        }
    }
    
    
    
    func touchUpInside(sender: UIButton!) {
        if shouldAutoToggle
        {
            isSelected = !isSelected
        }
    }
    
}
