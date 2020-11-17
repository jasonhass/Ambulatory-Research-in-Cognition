//
// ACContactNavigationController.swift
//



import UIKit

class ACContactNavigationController: UINavigationController {

	override func viewDidLoad() {
		super.viewDidLoad()
		let contact:ACContactViewController = .get()
		pushViewController(contact, animated: true)
		navigationBar.isHidden = true
		// Do any additional setup after loading the view.
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
