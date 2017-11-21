//
//  wkCopyButton.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class wkCopyButton: UIButton {

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
        self.layer.borderColor = DNAppearanceHelper.blueColor().cgColor;
        self.layer.borderWidth = 1.0;
    }
    
    func setSelectedAppearance()
    {
        self.backgroundColor = DNAppearanceHelper.blueColor();
        self.setTitleColor(UIColor.white, for: .normal);
    }
    
    func setUnselectedAppearance()
    {
        self.backgroundColor = UIColor.white;
        self.setTitleColor(DNAppearanceHelper.darkGrey(), for: .normal);
    }

}
