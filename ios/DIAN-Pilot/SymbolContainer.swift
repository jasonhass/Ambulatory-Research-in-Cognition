//
//  SymbolContainer.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class SymbolContainer: UIView {

    override func awakeFromNib() {
        self.layer.borderColor = UIColor(white: 128.0/255.0, alpha: 1.0).cgColor;
        self.layer.borderWidth = 2;
    }
}
