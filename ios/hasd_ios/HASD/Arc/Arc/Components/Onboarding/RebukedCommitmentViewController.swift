//
// RebukedCommitmentViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

public class RebukedCommitmentViewController: CustomViewController<RebukedCommitmentView>, SurveyInput {

	public weak var surveyInputDelegate: SurveyInputDelegate?
	
	public var orientation: UIStackView.Alignment = .center
	
	open override var preferredStatusBarStyle: UIStatusBarStyle {
		return .lightContent
	}
	
    override public func viewDidLoad() {
        super.viewDidLoad()
		
        // Do any additional setup after loading the view.
		customView.nextButton?.addAction { [weak self] in
			self?.surveyInputDelegate?.tryNextPressed()

		}
    }
	
	public func getValue() -> QuestionResponse? {
		return nil
	}
	
	public func setValue(_ value: QuestionResponse?) {
		
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
