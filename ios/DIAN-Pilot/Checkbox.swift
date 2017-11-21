//
//  Checkbox.swift
//  ARC
//
//  Created by Michael Votaw on 5/15/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class Checkbox: UIButton {

    override func awakeFromNib() {
        super.awakeFromNib();
        
        self.layer.borderColor = DNAppearanceHelper.blueColor().cgColor;
        self.layer.borderWidth = 1;
        self.layer.cornerRadius = 5;
    }
    
    override var isSelected: Bool
    {
        didSet
        {
            if self.isSelected
            {
                self.backgroundColor = DNAppearanceHelper.blueColor();
                self.setImage(#imageLiteral(resourceName: "checkmark"), for: .normal);
            }
            else
            {
                self.backgroundColor = UIColor.white;
                self.setImage(nil, for: .normal);
            }
        }
    }

}
