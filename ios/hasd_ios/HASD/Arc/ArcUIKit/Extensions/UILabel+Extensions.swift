//
// UILabel+Extensions.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit
public extension UIFont {
	func family(_ family:String) -> UIFont {
		var attributes = fontDescriptor.fontAttributes
		attributes[.family] = family
		
		return UIFont(descriptor: UIFontDescriptor(fontAttributes: attributes), size:0)
	}
	func face(_ face:String) -> UIFont {
		var attributes = fontDescriptor.fontAttributes
		
		attributes[.face] = face
		
		return UIFont(descriptor: UIFontDescriptor(fontAttributes: attributes), size:0)
	}
    func boldFont() -> UIFont {
        return addingSymbolicTraits(.traitBold)
    }
	
    func italicFont() -> UIFont {
        return addingSymbolicTraits(.traitItalic)
    }
	
	func size(_ value:CGFloat) -> UIFont {
		var attributes = fontDescriptor.fontAttributes

		attributes[.size] = value
		
		return UIFont(descriptor: UIFontDescriptor(fontAttributes: attributes), size:value)
	}
	func size(_ value:Double) -> UIFont {
		return size(CGFloat(value))
	}
	func size(_ value:Int) -> UIFont {
		return size(CGFloat(value))
	}
	func addingSymbolicTraits(_ traits: UIFontDescriptor.SymbolicTraits) -> UIFont {
        let newTraits = fontDescriptor.symbolicTraits.union(traits)
        guard let descriptor = fontDescriptor.withSymbolicTraits(newTraits) else {
			assertionFailure("Failed to add attribute to font!")
            return self
        }
        
        return UIFont(descriptor: descriptor, size: 0)
    }
}

public extension UILabel
{
	
    // Resizes font size so that single words won't wrap characters if they're too long to fit
    
    func resizeFontForSingleWords()
    {
        guard let currentFont = self.font else { return; }
        guard let currentText = self.text else { return; }
        
        var minFont = currentFont;
        let words = currentText.components(separatedBy: " ");
        let currentRect = self.frame;
        
        for w in words
        {
            var wRect = (w as NSString).boundingRect(with: currentRect.size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: minFont], context: nil);
            
            while wRect.width > currentRect.size.width
            {
                minFont = minFont.withSize(minFont.pointSize - 0.5);
                wRect = (w as NSString).boundingRect(with: currentRect.size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: minFont], context: nil);
            }
        }
        
        self.font = minFont;
    }
}
