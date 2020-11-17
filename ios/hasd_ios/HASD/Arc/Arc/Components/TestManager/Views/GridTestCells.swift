//
// GridTestCells.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
open class GridImageCell:UICollectionViewCell
{
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var dotView: BorderedView!
    var isPracticeCell:Bool = false
    var touchLocation:CGPoint?
    var touchTime:Date?
    func setImage(image:UIImage?){
        
        self.image.image = image
        self.image.backgroundColor = .clear
    }
    
    override open var isSelected: Bool {
        willSet {
            if newValue == true
            {
                self.backgroundColor = UIColor(red:0, green:0.37, blue:0.52, alpha:1) //UIColor(red: 13.0 / 255.0, green: 143.0 / 255.0, blue: 192.0 / 255.0, alpha: 1.0);
                self.image.isHidden = true
                if isPracticeCell {
                    self.dotView.isHidden = false
                }
            }
            else
            {
                self.backgroundColor = UIColor(red: 191.0/255.0, green: 215.0/255.0, blue: 224.0/255.0, alpha: 1.0) //UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);
                self.dotView.isHidden = true
            }
        }
    }
    
    
    override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let t = touches.first
        {
            touchLocation = t.location(in: self.window);
            touchTime = Date();
        }
        
        if self.isPracticeCell && self.isSelected {
            return
        }
        
        super.touchesBegan(touches, with: event)
        super.touchesEnded(touches, with: event);
    }
    
    override open func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    }
    
    override open func prepareForReuse() {
        self.isSelected = false;
        super.prepareForReuse();
    }
    func clear() {
        touchTime = nil;
        touchLocation = nil;
        setImage(image: nil)
        image.isHidden = true;
    }
    
}
open class GridFCell:UICollectionViewCell {
    @IBOutlet weak var label: UILabel!
    func setCharacter(character:String){
        label.text = character
    }
    
    override open func prepareForReuse() {
        self.isSelected = false;
        super.prepareForReuse();
    }
    
    override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
		super.touchesBegan(touches, with: event)
		self.contentView.layer.backgroundColor = UIColor(named:"Primary")!.cgColor
        self.label.textColor = UIColor.white
    }
	open override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
		self.contentView.layer.backgroundColor = UIColor(named:"Primary")!.cgColor
		self.label.textColor = UIColor.white
		super.touchesBegan(touches, with: event)
		

	}
    override open func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
		
		self.contentView.layer.backgroundColor = UIColor.clear.cgColor
		self.label.textColor = UIColor(named: "Primary")
		super.touchesEnded(touches, with: event)
		
		
//        self.contentView.layer.backgroundColor = UIColor(red: 191.0/255.0, green: 215.0/255.0, blue: 224.0/255.0, alpha: 1.0).cgColor
//        self.label.textColor = UIColor(red:38.0/255.0, green:94.0/255.0, blue:130.0/255.0, alpha:1)
    }
    
}
