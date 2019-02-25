//
// InstructionNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class InstructionNavigationController: UINavigationController {
	var app = Arc.shared
	public var instructions:[Introduction.Instruction]?
	public var nextVc:UIViewController?
	public var nextState:State?
	public var titleOverride:String?
    override open func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
		
    }
	@discardableResult
	open func load(instructions template:String) -> Introduction {
		guard let asset = NSDataAsset(name: template) else {
			fatalError("Missing data asset: \(template)")
		}
		do {
			let intro = try JSONDecoder().decode(Introduction.self, from: asset.data)
			instructions = intro.instructions

			return intro
		} catch {
			fatalError("Invalid asset format: \(template) - Error: \(error.localizedDescription)")
			
		}
	}
	
	override open func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
		_ = displayIntro(index: 0)
	}
	
	private func displayIntro(index:Int) -> Bool {
		
		if let instructions = instructions,
			index < instructions.count
		{
			let instruction = instructions[index]
			let vc:IntroViewController = .get()
			vc.nextButtonTitle = instruction.nextButtonTitle

			self.pushViewController(vc, animated: true)
			
			vc.set(heading:     titleOverride ?? instruction.title,
				   subheading:  instruction.subtitle,
				   content:     instruction.preface)

			vc.nextPressed = {
				[weak self] in
				self?.next(nextQuestion: index + 1)
			}
			return true
		}
		return false
	}
	private func next(nextQuestion: Int) {
		if displayIntro(index: self.viewControllers.count) {
			
		} else {
			
			
			//Subclasses may perform conditional async operations
			//that determine if the app should proceed.
			if let nextState = self.nextState {
				app.appNavigation.navigate(state: nextState, direction: .toRight)
			} else {
				if let vc = nextVc {
					app.appNavigation.navigate(vc: vc, direction: .toRight)

				} else {
					fatalError("Failed to set either state or view controller.")
				}

			}
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
