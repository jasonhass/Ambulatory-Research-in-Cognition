//
// ACChangeAvailabilityViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
import HMMarkup
open class ACChangeAvailabilityViewController: UIViewController {
    public var returnState:State = Arc.shared.appNavigation.previousState() ?? Arc.shared.appNavigation.defaultState()
    public var returnVC:UIViewController?
    
    public var studyChangeView:UIView!
    
    @IBOutlet weak var studyPeriodAdjustView: UIView!
    
    
    @IBOutlet weak var changeTimeButton: ACButton!
    
    @IBOutlet weak var changeDateButton: ACButton!
    
	@IBOutlet weak var deniedLabel:ACLabel!
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if Arc.shared.studyController.getCurrentStudyPeriod() != nil {
			studyPeriodAdjustView.isHidden = true
        }
        if let config = HMMarkupRenderer.config, config.shouldTranslate {
            changeTimeButton.setTitle("CHANGE".localized(ACTranslationKey.button_change), for: .normal)
            changeDateButton.setTitle("CHANGE".localized(ACTranslationKey.button_change), for: .normal)

        }
		
		if Arc.environment?.hidesChangeAvailabilityDuringTest == true {
			changeTimeButton.isEnabled = (Arc.shared.availableTestSession == nil)
			deniedLabel.isHidden = (Arc.shared.availableTestSession == nil)
		}

    }
    @IBAction public func goBackPressed(_ sender: Any) {
        if let vc = returnVC {
            Arc.shared.appNavigation.navigate(vc: vc, direction: .toLeft)
        } else {
            self.navigationController?.popViewController(animated: true)
            //Arc.shared.appNavigation.navigate(state: returnState, direction: .toLeft)
        }
    }
    @IBAction public func changeSchedulePressed(_ sender: UIButton) {
        Arc.shared.appNavigation.navigate(state: ACState.changeSchedule, direction: .toRight)

    }
    @IBAction public func changeStudyDatesPressed(_ sender: UIButton) {
        Arc.shared.appNavigation.navigate(state: ACState.changeStudyStart, direction: .toRight)

    }
    
   
}

open class ACAvailbilityNavigationController:UINavigationController {
    public var prev:UIViewController?
    weak var vc:ACChangeAvailabilityViewController! = nil
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        let v:ACChangeAvailabilityViewController = .get()
        vc = v
        
        pushViewController(vc, animated: true)
        navigationBar.isHidden = true
        // Do any additional setup after loading the view.
    }
    override open func viewDidAppear(_ animated: Bool) {
        vc.returnVC = prev
        super.viewDidAppear(animated)
    }

}
