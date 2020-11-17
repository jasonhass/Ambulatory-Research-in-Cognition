//
// TestProgressViewController.swift
//



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
