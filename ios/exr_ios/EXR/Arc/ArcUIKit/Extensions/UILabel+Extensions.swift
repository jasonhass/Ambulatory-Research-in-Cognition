//
// UILabel+Extensions.swift
//



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
