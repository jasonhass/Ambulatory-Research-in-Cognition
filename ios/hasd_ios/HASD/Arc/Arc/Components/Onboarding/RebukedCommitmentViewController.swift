//
// RebukedCommitmentViewController.swift
//



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
