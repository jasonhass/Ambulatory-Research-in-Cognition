//
//  SymbolSelectButton.swift
//  DIAN-Pilot
//
//  Created by Michael Votaw on 11/22/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import UIKit

class SymbolSelectButton: UIButton {

    var touchLocation:CGPoint?;
    var touchTime:Date?
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let t = touches.first
        {
            touchLocation = t.location(in: self.window);
            touchTime = Date();
        }
        
        self.sendActions(for: UIControlEvents.touchDown);
    }

}
