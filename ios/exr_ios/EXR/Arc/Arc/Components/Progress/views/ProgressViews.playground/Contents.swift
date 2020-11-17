//
// Contents.swift
//


  
import UIKit
import PlaygroundSupport

class MyViewController : UIViewController {
    override func loadView() {
        let view = UIView()
        view.backgroundColor = .white

		let item = CircularProgressView(frame:CGRect(x: 100, y: 200, width: 200, height: 200))
		item.backgroundColor = .clear
        view.addSubview(item)
		
		
		let stepper = StepperProgressView(frame: CGRect(x: 24, y: 24, width: 300, height: 50))
		stepper.backgroundColor = .lightGray
		stepper.config.barInset = 10
		stepper.config.barWidth = 32
		view.addSubview(stepper)
		
        self.view = view
		
		
    }
}
// Present the view controller in the Live View window
PlaygroundPage.current.liveView = MyViewController()
