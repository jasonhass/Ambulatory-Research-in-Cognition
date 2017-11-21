//
//  WeekdayButton.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class WeekdayButton: UIButton {

    var isTimeSet:Bool = false {
        didSet {
                self.setNeedsDisplay();
        }
    }
    
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
        self.setTitleColor(DNAppearanceHelper.blueColor(), for: UIControlState.selected);
        self.setTitleColor(UIColor.white, for: UIControlState.normal);
        self.titleEdgeInsets = UIEdgeInsets(top: -8, left: 0, bottom: 0, right: 0);
        self.backgroundColor = DNAppearanceHelper.blueColor();
    }
    
    func setSelectedAppearance()
    {
        self.backgroundColor = UIColor.white;
        self.setTitleColor(DNAppearanceHelper.blueColor(), for: UIControlState.normal);
    }
    
    func setUnselectedAppearance()
    {
        self.backgroundColor = DNAppearanceHelper.blueColor();
        self.setTitleColor(UIColor.white, for: UIControlState.normal);
    }
    
    override func draw(_ rect: CGRect) {
        super.draw(rect);
        
        if isTimeSet
        {
            let circlePath = UIBezierPath(arcCenter: CGPoint(x: rect.width * 0.5,y: rect.height * 0.8), radius: CGFloat(3), startAngle: CGFloat(0), endAngle:CGFloat(M_PI * 2), clockwise: true)
            
            if isSelected
            {
                DNAppearanceHelper.blueColor().set();
            }
            else
            {
                UIColor.white.set();
            }
            
            circlePath.fill();
        }

    }
}
