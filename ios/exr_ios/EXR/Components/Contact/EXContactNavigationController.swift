//
// EXContactNavigationController.swift
//



import UIKit

public class EXContactNavigationController: UINavigationController {
	var prev:UIViewController?
	weak var vc:EXContactViewController! = nil
	
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
	override public func viewDidLoad() {
		super.viewDidLoad()
		let contact:EXContactViewController = .get()
		vc = contact

		pushViewController(contact, animated: true)
		navigationBar.isHidden = true
		// Do any additional setup after loading the view.
	}
	override public func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		vc.returnVC = prev
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
