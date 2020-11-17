//
// EXGridTestViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




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
