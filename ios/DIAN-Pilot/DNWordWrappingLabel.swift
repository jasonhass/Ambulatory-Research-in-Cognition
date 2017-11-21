//
//  DNWordWrappingLabel.swift
//  ARC
//
//  Created by Michael Votaw on 7/5/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class DNWordWrappingLabel: UILabel {

    @IBInspectable var baseFontSize:CGFloat = 50;
    override var text: String?
    {
        didSet {
            
            // Break the text string into individual words,
            // for each word in the text string, find how small we need to make the text
            // before the word fits on one line.
            // We need to do this to prevent words from character-wrapping in awkward ways
            // like
            // "cheeseburge
            //  r"
            
            if let t = text
            {
                let words = t.components(separatedBy: .whitespacesAndNewlines);
                
                var maxFontSize = baseFontSize;
                
                let testLabel = UILabel();
                testLabel.frame = self.frame;
                testLabel.font = self.font;
                testLabel.lineBreakMode = .byWordWrapping;
                testLabel.numberOfLines = 1;
                
                for w in words
                {
                    repeat
                    {
                        testLabel.text = w;
                        testLabel.font = self.font.withSize(maxFontSize);
                        testLabel.sizeToFit();
                        if testLabel.frame.size.width > self.frame.size.width
                        {
                            maxFontSize -= 1;
                        }
                    } while testLabel.frame.size.width > self.frame.size.width
                }
                
                self.font = self.font.withSize(maxFontSize);
                
            }
        }
    }

}
