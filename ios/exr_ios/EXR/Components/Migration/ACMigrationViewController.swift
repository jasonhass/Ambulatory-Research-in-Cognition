//
// ACMigrationViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public extension Notification.Name {
	static let ACMigrationSuccessful = Notification.Name(rawValue: "ACMigrationSuccessful")
	
	static let ACMigrationFailure = Notification.Name(rawValue: "ACMigrationFailure")
	static let ACMigrationElementCompleted = Notification.Name(rawValue: "ACMigrationElementCompleted")
	static let ACMigrationTrigger = Notification.Name("ACMigrationTrigger")

}
public class ACMigrationViewController: CustomViewController<ACMigrationView> {
	var progressBarAnimation:Animate?
    override public func viewDidLoad() {
        super.viewDidLoad()
		NotificationCenter.default.addObserver(forName: .ACMigrationElementCompleted, object: nil, queue: .main) { [weak self](notif) in
			if let value = notif.object as? Double {
				guard let weakSelf = self else {
					return
				}
				weakSelf.progressBarAnimation?.stop()
				let oldValue = Double(weakSelf.customView.installationBar.progress)
				weakSelf.progressBarAnimation = Animate().duration(0.2).curve(.easeOut).run({ (t) -> Bool in
					self?.customView.installationBar.progress = CGFloat(Math.lerp(a: oldValue, b: value, t: t))
					return true

				})
			}
			self?.customView.setNeedsDisplay()
		}
		NotificationCenter.default.addObserver(forName: .ACMigrationTrigger, object: nil, queue: .main) { [weak self] (notif) in
			self?.defaultView()
		}
		NotificationCenter.default.addObserver(forName: .ACMigrationSuccessful, object: nil, queue: .main) { [weak self] (notif) in
			self?.completeView()
		}
		NotificationCenter.default.addObserver(forName: .ACMigrationFailure, object: nil, queue: .main) { [weak self](notif) in
			self?.failureView()
			#if DEBUG
			if let error = (notif.object as? Error)?.localizedDescription {
				Arc.shared.displayAlert(message: "\(error)", options: [.default("Okay", {
					
					
				})])
			
			}
			
			#endif

		}
		
        // Do any additional setup after loading the view.
		
       
		
		if let _ = Arc.shared.appController.flags["migration_in_progress"] {
				defaultView()
			
			if Arc.shared.appController.flags["migrated_legacy"] == false {
				failureView()
			}
			if Arc.shared.appController.flags["migrated_legacy"] == true {
				completeView()
			}
		}
		
    }
	func defaultView() {
		customView.contentStack.alpha = 0
		customView.contentStack.fadeIn()

		set(title: "New in this Update")
		set(separatorWidth: 0.15)
		set(body: """
		We’ve refreshed the look of the app and added some helpful features.
		Now you can:

		+ View tutorials for each test

		+ See your study timeline

		+ See how much you’ve earned
			

		^As before, you’ll receive a notification when it’s time to take
		a test.^
			
		""")
		customView.nextButton.isHidden = true
		customView.installationStack.isHidden = false
		customView.studyButton.isHidden = true
		customView.installUpdateButton.isHidden = true
		customView.bodyLabel.textAlignment = .left

	}
	func completeView() {
		customView.contentStack.alpha = 0
		customView.contentStack.fadeIn()

		defaultView()
		customView.nextButton.isHidden = false
		customView.installationStack.isHidden = true
		customView.nextButton.setNeedsDisplay()

	}
	func failureView() {
		customView.contentStack.alpha = 0
		customView.contentStack.fadeIn()
		set(title: nil)
		set(separatorWidth: nil)
		set(body: """


		There was a problem with the installation. Please check your connection and tap the button below to try again.

		If this problem persists, please contact the DIAN study team for assistance.
			
		""")
		customView.bodyLabel.textAlignment = .center
		customView.nextButton.isHidden = true
		customView.installationStack.isHidden = true
		customView.studyButton.isHidden = false
		customView.installUpdateButton.isHidden = false
		customView.installUpdateButton.setNeedsDisplay()

	}
	func triggerMigration() {
		NotificationCenter.default.post(name: .ACMigrationTrigger, object: nil)
	}
	func set(title:String?) {
		customView.titleLabel.text = title
	}
	func set(body:String?) {
		customView.bodyLabel.text = body
		
		
	}
	func set(separatorWidth:CGFloat?) {
		customView.separator.relativeWidth = separatorWidth ?? 0
		customView.separator.isHidden = separatorWidth == nil
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
