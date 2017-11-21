//
//  BorderTextField.swift
//  DIAN-Pilot
//
//  Created by Geoff Strom on 11/23/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class BorderTextField: UITextField {

    override func awakeFromNib() {
        super.awakeFromNib()
        self.layer.borderColor = DNAppearanceHelper.lightBlueColor().cgColor
        self.layer.borderWidth = 1.0
        self.layer.cornerRadius = 5.0
    }

}
