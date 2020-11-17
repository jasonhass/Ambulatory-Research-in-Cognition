//
// Contents.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



  
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
