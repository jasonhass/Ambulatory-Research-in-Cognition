//
// TestProgressViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
public protocol TestProgressViewControllerDelegate : class {
	func testProgressDidComplete()
}
public class TestProgressViewController: CustomViewController<TestProgressView> {
	public weak var delegate:TestProgressViewControllerDelegate?
	private var timer:Timer?
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
	init(title:String, subTitle:String, count:Int, maxCount:Int = 3) {
		
		super.init()
		
		customView.title = title
		customView.subTitle = subTitle
		customView.count = count
		customView.maxCount = maxCount
		customView.dividerWidth = 1.0
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	override public func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
	
    }
    
	public override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		
		customView.dividerWidth = 1.0
		
		
	}
	public func set(count:Int) {
		customView.count = count
	}
	public func set(maxCount:Int) {
		customView.maxCount = maxCount
	}
	public func waitAndExit(time:TimeInterval) {
		timer = Timer.scheduledTimer(withTimeInterval: time, repeats: false, block: {[weak self] (timer) in
			self?.delegate?.testProgressDidComplete()
			
		})
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
