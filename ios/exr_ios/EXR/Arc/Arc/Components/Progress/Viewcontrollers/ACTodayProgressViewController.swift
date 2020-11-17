//
// ACTodayProgressViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

public class ACTodayProgressViewController: CustomViewController<ACTodayProgressView>, ACTodayProgressViewDelegate {
	var thisStudy:ThisStudyExpressible = Arc.shared.studyController
	var thisWeek:ThisWeekExpressible = Arc.shared.studyController
	
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
	
	public override init() {
		super.init()
        
        // If we've finished the baseline then this must be a paid test
        if get(flag: .baseline_completed) {
            set(flag: .paid_test_completed)
			remove(flag: .tutorial_optional)
        }
        
		set(flag: .baseline_completed)
        
		//Todo: Have this injected instead this behavior is needed elsewhere in the app.
		customView.delegate = self
		guard let config = Arc.shared.studyController.todaysProgress() else {
			return
		}
		
		let isComplete = config.sessionsStarted == config.totalSessions
		if isComplete {
			customView.set(completed: true)
			if thisStudy.studyState == .baseline {
				customView.set(sessionsCompleted: config.sessionsCompleted, isPlural: config.sessionsCompleted != 1, isPractice: true)

			} else {
				customView.set(sessionsCompleted: config.sessionsCompleted, isPlural: config.sessionsCompleted != 1)
			}
			customView.set(sessionsRemaining: nil)
		} else {
			customView.set(completed: false)
			customView.set(sessionsCompleted: config.sessionsCompleted, isPlural: config.sessionsCompleted != 1)
			customView.set(sessionsRemaining: config.totalSessions - config.sessionsStarted)
		}
		
		for index in 0 ..< config.sessionData.count {
			let session = config.sessionData[index]
			
			customView.set(progress: Double(session.progress)/Double(session.total),
						   for: index)
		}
		//We've completed the test and we need to let the app know
		
		Arc.shared.currentTestSession = nil
		
	}
	
	required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	override public func viewDidLoad() {
        super.viewDidLoad()

		if app.participantId != nil {
			Arc.shared.uploadTestData()
		}
		
    }
	
	public func nextPressed() {
		
		//TODO: put an earnings available check for studies that do not
		//present earnings to the user.
		if thisStudy.studyState == .baseline {
			Arc.shared.nextAvailableState()
		} else {
			Arc.shared.appNavigation.navigate(vc: EarningsViewController(isPostTest: true), direction: .toRight)
		}
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

