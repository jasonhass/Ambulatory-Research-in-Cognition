//
// ACStudyTotalsViewController.swift
//



import UIKit
import Arc
public class ACStudyTotalsViewController: CustomViewController<ACStudyTotalsView> {
	public override var prefersStatusBarHidden: Bool {return true}

    override public func viewDidLoad() {
        super.viewDidLoad()
		viewRespectsSystemMinimumLayoutMargins = false
       
		update()
		
    }
	
	/** This information is fetched and stored within arc using this key.
		Find other uses of the key to find where it is accessed. Like in the earnnings
		ViewController.
		   
		After a session is submitted this information is updated.
	*/
	func update() {
		let summary:StudySummary? = Arc.read(key: EarningsController.studySummaryKey)
		self.customView.set(studySummary: summary)
	}
	public override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		NotificationCenter.default.addObserver(forName: .ACStudySummaryUpdated, object: self, queue: .main) { (notif) in
			self.update()
		}
	}
	public override func viewDidDisappear(_ animated: Bool) {
		super.viewDidDisappear(animated)
		NotificationCenter.default.removeObserver(self)
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

