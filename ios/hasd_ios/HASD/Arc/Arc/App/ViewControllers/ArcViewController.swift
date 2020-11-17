//
// ArcViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit


open class ArcViewController: UIViewController {
	public var app:Arc {
		get {
			return Arc.shared
		}
		set {
			
		}
	}
	public var currentHint:HintView?
	public override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
		super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
	}
	public init() {
		super.init(nibName: nil, bundle: nil)
		modalPresentationStyle = .fullScreen

	}
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
		modalPresentationStyle = .fullScreen

	}
    override open func viewDidLoad() {
        super.viewDidLoad()
		

        // Do any additional setup after loading the view.
    }
	open override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
	}
	open override func viewDidDisappear(_ animated: Bool) {
		super.viewDidDisappear(animated)
		NotificationCenter.default.removeObserver(self)
	}
	open func apply(forVersion version:String) {
		let major:Int = Int(version.components(separatedBy: ".")[0]) ?? 0
		let minor:Int = Int(version.components(separatedBy: ".")[1]) ?? 0
		let patch:Int = Int(version.components(separatedBy: ".")[2]) ?? 0
		for flag in ProgressFlag.prefilledFlagsFor(major: major, minor: minor, patch: patch) {
			set(flag: flag)
		}
	}
	open func get(flag:ProgressFlag) -> Bool {
		return app.appController.flags[flag.rawValue] ?? false
	}
	open func set(flag:ProgressFlag) {
		app.appController.flags[flag.rawValue] = true
	}
	open func remove(flag:ProgressFlag) {
		app.appController.flags[flag.rawValue] = false
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
