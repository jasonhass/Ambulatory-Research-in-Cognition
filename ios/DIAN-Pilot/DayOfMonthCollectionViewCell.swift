//
//  DayOfMonthCollectionViewCell.swift
//  ARC
//
//  Created by Michael Votaw on 5/15/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class DayOfMonthCollectionViewCell: UICollectionViewCell {

    @IBOutlet weak var dateLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        
        self.updateSelectable();
    }
    
    override func draw(_ rect: CGRect) {
        super.draw(rect);
        
        let path = UIBezierPath(rect: rect);
        UIColor(white: 230/255, alpha: 1.0).setStroke();
        path.lineWidth = 1;
        path.stroke();
    }
   
    
    var isSelectable:Bool = true
    {
        didSet
        {
            updateSelectable();
        }
    }

    
    func updateSelected()
    {
        if self.isSelected
        {
            self.backgroundColor = DNAppearanceHelper.blueColor();
            dateLabel.textColor = UIColor.white;
        }
        else
        {
            self.backgroundColor = UIColor.clear;
            dateLabel.textColor = DNAppearanceHelper.darkGrey();
        }
    }
    
    func updateSelectable()
    {
        if !isSelectable
        {
            dateLabel.textColor = DNAppearanceHelper.darkGrey();
            dateLabel.alpha = 0.5
        }
        else
        {
            dateLabel.alpha = 1.0;
            if isSelected
            {
                dateLabel.textColor = UIColor.white;
            }
            else
            {
                dateLabel.textColor = DNAppearanceHelper.darkGrey();
            }
        }
    }
}
