//
// EXGridTestViewController.swift
//



import Arc
import UIKit

open class CRGridTestViewController : GridTestViewController {
    override open func displayReady()
    {
        
        guard isVisible else {
            return
        }
        
        Arc.shared.displayAlert(message: "".localized(ACTranslationKey.grids_overlay3), options: [
			.wait(waitTime: 2.0, {
			
                $0.set(message:"".localized(ACTranslationKey.grids_overlay3_pt2))
		}),
		.wait(waitTime: 4.0, {
			[weak self] in
			self?.displayGrid()
			if let s = self {
				s.tapOnTheFsLabel.isHidden = true
			}
			$0.removeFromSuperview()
		})])
		
		
       
        
        
    }
}
