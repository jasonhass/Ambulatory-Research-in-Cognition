//
//  UILabel+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 8/10/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

extension UILabel
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
            var wRect = (w as NSString).boundingRect(with: currentRect.size, options: .usesLineFragmentOrigin, attributes: [NSFontAttributeName: minFont], context: nil);
            
            while wRect.width > currentRect.size.width
            {
                minFont = minFont.withSize(minFont.pointSize - 0.5);
                wRect = (w as NSString).boundingRect(with: currentRect.size, options: .usesLineFragmentOrigin, attributes: [NSFontAttributeName: minFont], context: nil);
            }
        }
        
        self.font = minFont;
    }
}
