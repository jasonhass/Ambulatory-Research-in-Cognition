//
//  SignatureView.swift
//  ARC
//
//  Created by Philip Hayes on 5/16/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class SignatureView: UIView {
    
    var path:UIBezierPath = UIBezierPath()
    
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
        UIColor.black.set()
        path.stroke()
    }
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard let location = touches.first?.location(in: self) else {
            return
        }

        path.move(to: location)
    }
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard let location = touches.first?.location(in: self) else {
            return
        }
       
        
        path.addLine(to: location)
        self.setNeedsDisplay()
    
    }
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {

    }
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
    }
    
    func clear(){
        path = UIBezierPath()
        self.setNeedsDisplay()
    }
    func save() -> UIImage?{
        UIGraphicsBeginImageContext(self.frame.size)
        self.layer.render(in: UIGraphicsGetCurrentContext()!)

        guard let img = UIGraphicsGetImageFromCurrentImageContext() else {
            return nil
        }
        UIGraphicsEndImageContext()

        return img
    }

}
