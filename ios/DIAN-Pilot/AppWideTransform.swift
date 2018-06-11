/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import UIKit

extension String {
    
    func localized(lang:String, key:String) -> String?{
        guard lang != "" else {
            return nil
        }
        let t = DNDataManager.sharedInstance.translation[lang]
        if let v = t?[key], self != v{
            
            return v.cleanedTranslation();
        } else {
            return nil
        }
    }
    
    func localized(key:String) -> String{
        let lang = DNDataManager.sharedInstance.languageKey
        return self.localized(lang:lang, key:key) ?? self;
    }
    
    func localized(lang:String) -> String?{
        guard lang != "" else {
            return nil
        }
        let t = DNDataManager.sharedInstance.keylessTranslation[lang]
        
        if let v = t?[self.lowercased().replacingOccurrences(of: "\'", with: "")], v != "", self != v{
            
            return v.cleanedTranslation();
        } else {
            return nil
        }
        
    }
    
    func localized() -> String {
        let lang = DNDataManager.sharedInstance.languageKey
        
        return self.localized(lang: lang) ?? self;
    }
    
    
    // helper method, where we can place any post-translation cleanup that we need.
    // For instance, some fields may contain "\"\"" (instead of being an empty field ""),

    func cleanedTranslation() -> String
    {
        if self == "\"\""
        {
            return "";
        }
        return self.trimmingCharacters(in: .whitespacesAndNewlines);
    }
}


public class DNLabel : UILabel{
    @IBInspectable var translationKey:String?
    @IBInspectable var colorKey:String?

    
    override open func layoutSubviews(){
        if let colorKey = colorKey {
            self.textColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
        guard let key = translationKey else {
            super.layoutSubviews()

            return
        }
        let lang = DNDataManager.sharedInstance.languageKey

        if let loc = self.text?.localized(lang: lang, key: key) {
            self.text = loc
        }
        super.layoutSubviews()

    }
}
public class DNSlider : UISlider {
    @IBInspectable var colorKey:String?
    override open func layoutSubviews(){
        super.layoutSubviews()
        if let colorKey = colorKey {
            self.tintColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
    }
}
public class DNTextView: UITextView {
    @IBInspectable var translationKey:String?
    @IBInspectable var colorKey:String?

    override open func layoutSubviews(){
        if let colorKey = colorKey {
            self.textColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
        guard let key = translationKey else {
            super.layoutSubviews()

            return
        }

        let lang = DNDataManager.sharedInstance.languageKey

        if let loc = self.text?.localized(lang: lang, key: key) {
            self.text = loc
            self.sizeToFit()
        }
        super.layoutSubviews()

        
    }
}
public class DNTableView : UITableView {
    @IBInspectable var colorKey:String?
    public override func layoutSubviews() {
        super.layoutSubviews()
        if let colorKey = colorKey {
            self.separatorColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
    }
}
public class DNImageView : UIImageView {
    @IBInspectable var colorKey:String?
    public override func layoutSubviews() {
        super.layoutSubviews()
        if let colorKey = colorKey {
            self.backgroundColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
    }
}
public class DNView : UIView {
    @IBInspectable var colorKey:String?
    public override func layoutSubviews() {
        super.layoutSubviews()
        if let colorKey = colorKey {
            self.backgroundColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
        }
    }
}
public class DNTextField : UITextField {
    @IBInspectable var colorKey:String?
    public override func layoutSubviews() {
        super.layoutSubviews()
    }
}
public class DNButton: UIButton {
    @IBInspectable var translationKey:String?
    @IBInspectable var colorKey:String?
    override open func layoutSubviews(){
        if let colorKey = colorKey {
            self.tintColor = DNAppearanceHelper.colorForKey(colorKey: colorKey)
            if backgroundImage(for: .normal) == nil && backgroundColor == nil{
                self.setTitleColor(DNAppearanceHelper.colorForKey(colorKey: colorKey)!, for: .normal)
                self.setTitleColor(DNAppearanceHelper.colorForKey(colorKey: colorKey)!, for: .highlighted)

            }
        }
        let title = self.title(for: .normal)

        guard let key = translationKey else {
            super.layoutSubviews()

            return
        }
        //print(key)
        let lang = DNDataManager.sharedInstance.languageKey

        
        if let loc = title?.localized(lang: lang, key: key) {
            self.setTitle(loc, for: .normal)
            self.layoutSubviews()
            self.titleLabel?.resizeFontForSingleWords()

            
        }
        super.layoutSubviews()

    }
}
