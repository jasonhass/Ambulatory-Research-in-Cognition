//
// FinishedNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

open class FinishedNavigationController: BasicSurveyViewController {
    override open func viewDidLoad() {
        super.viewDidLoad()
		//self.isNavigationBarHidden = true
        shouldShowHelpButton = false
        displayHelpButton(shouldShowHelpButton)
        shouldShowBackButton = true
        displayBackButton(shouldShowBackButton)
        // Do any additional setup after loading the view.
		guard let session = Arc.shared.currentTestSession else {return}
		guard let study = Arc.shared.currentStudy else {return}
		Arc.shared.studyController.mark(finished: session, studyId: study)

    }
	
	
	//Override this to write to other controllers
	override open func valueSelected(value:QuestionResponse, index:String) {
        
		guard let session = Arc.shared.currentTestSession else {return}
		guard let study = Arc.shared.currentStudy else {return}
        guard let v = value.value as? Int else {
            return
        }
		
		if v == 0 {
			Arc.shared.studyController.mark(interrupted:true, sessionId: session, studyId: study)
		} else if v == 1 {
			Arc.shared.studyController.mark(interrupted:false, sessionId: session, studyId: study)

		}
		//Arc.shared.currentTestSession = nil
		
	}

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
