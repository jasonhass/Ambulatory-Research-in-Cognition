//
// PricesTestTutorialViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import UIKit
import ArcUIKit

class PricesTestTutorialViewController: ACTutorialViewController, PricesTestDelegate {
	

	let pricesTest:PricesTestViewController = .get()
	var pricesQuestions:PricesQuestionViewController!
	var selectionMade = false
	
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        if Arc.environment?.priceTestType == .normal {
            self.duration = 33.5
        }
        super.viewDidLoad()
		pricesTest.delegate = self
		pricesTest.autoStart = false
        pricesTest.isPracticeTest = true
		setupScript()
        if self.get(flag: .prices_tutorial_shown) == false {
            self.customView.firstTutorialRun()
			if Arc.get(flag: .tutorial_optional) {
				self.set(flag: .prices_tutorial_shown)
			}
        }
    }
	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		addChild(pricesTest)
		customView.setContent(viewController: pricesTest)
	}
	override func viewWillDisappear(_ animated: Bool) {
		super.viewWillDisappear(animated)
		view.window?.clearOverlay()
		currentHint?.removeFromSuperview()

	}
    override func finishTutorial() {
		removeHint(hint: "Hint")
        self.set(flag: .prices_tutorial_shown)
        super.finishTutorial()
    }
	func didSelectPrice(_ option: Int) {
		view.window?.clearOverlay()
		view.removeHighlight()
		removeHint(hint: "Hint")
		currentHint?.removeFromSuperview()
		tutorialAnimation.resume()
		selectionMade = true
		pricesQuestions.questionDisplay.isUserInteractionEnabled = false
        getNextStep()

	}
	func didSelectGoodPrice(_ option: Int) {
		view.window?.clearOverlay()
		view.removeHighlight()

		currentHint?.removeFromSuperview()
		removeHint(hint: "Hint")

        selectionMade = true
		tutorialAnimation.resume()
        getNextStep()
	}
	func shouldEndTest() -> Bool {
		return false
	}
    
	func getNextStep(forceNext:Bool = false) {
        guard state.conditions.count > 1 else { return }
        let condition = state.conditions[1]
        if state.conditions[0].flag == "prices_middle" || state.conditions[0].flag == "questions3-1" {
            return
        }
        if state.conditions[0].flag == "question3-2" ||
            condition.flag == "end" {
            self.finishTutorial()
            return
        }
        tutorialAnimation.time = condition.time * duration
        resumeTutorialanimation()
    }
	
	func setupScript() {
		state.addCondition(atTime: 0.0, flagName: "hide") { [weak self] in
			
			self?.pricesTest.priceDisplay.isHidden = true
		}
		
		state.addCondition(atTime: 0.02, flagName: "init") { [weak self] in
			self?.pricesTest.priceDisplay.isHidden = false
			
			self?.pricesTest.displayItem()
			self?.pricesTest.buildButtonStackView()
            self?.pricesTest.priceDisplay.isUserInteractionEnabled = false
		}
		
		state.addCondition(atTime: progress(seconds: 1), flagName: "overlay1") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.selectionMade = false

            let shape = OverlayShape.roundedRect(weakSelf.pricesTest.priceDisplay, 8, CGSize(width: -8, height: -8))
            weakSelf.pricesTest.view.overlayView(withShapes: [shape])
			weakSelf.pricesTest.priceDisplay.isUserInteractionEnabled = true
			weakSelf.currentHint = self?.view.window?.hint {
				$0.content = "The Prices test has two parts. *First, evaluate the price.*".localized(ACTranslationKey.popup_tutorial_price_intro)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top == weakSelf.pricesTest.priceDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesTest.priceDisplay.centerXAnchor
					$0.width == weakSelf.pricesTest.priceDisplay.widthAnchor
				}
			}
		}
		
		state.addCondition(atTime: progress(seconds: 11), flagName: "overlay2") { [weak self] in
			guard let weakSelf = self else {
				return
			}
            guard weakSelf.selectionMade == false else {
                weakSelf.selectionMade = false
                return
            }
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.pricesTest.priceDisplay.isUserInteractionEnabled = true
			weakSelf.tutorialAnimation.pause()
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*What do you think?*\n Choose the answer that makes sense to you.".localized(ACTranslationKey.popup_tutorial_choose1)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top == weakSelf.pricesTest.priceDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesTest.priceDisplay.centerXAnchor
					$0.width == weakSelf.pricesTest.priceDisplay.widthAnchor
				}
			}
		}
		
		state.addCondition(atTime: progress(seconds: 11.1), flagName: "overlay3") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.tutorialAnimation.pause()
			weakSelf.progress = 0.25
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*Great choice!*\nLet's try another.".localized(ACTranslationKey.popup_tutorial_greatchoice1)
				$0.buttonTitle = "NEXT".localized(ACTranslationKey.button_next)
				$0.button.addAction {
					weakSelf.tutorialAnimation.resume()
					weakSelf.view.window?.clearOverlay()
					weakSelf.currentHint?.removeFromSuperview()
					weakSelf.selectionMade = false

				}
				$0.layout {
					$0.top == weakSelf.pricesTest.priceDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesTest.priceDisplay.centerXAnchor
                    $0.width == 232
				}
			}
		}
		
		state.addCondition(atTime: progress(seconds: 11.5), flagName: "question2-0") { [weak self] in
			
			self?.pricesTest.nextItem()
			self?.pricesTest.priceDisplay.isUserInteractionEnabled = true
			self?.selectionMade = false

			
		}
		
		state.addCondition(atTime: progress(seconds: 21.5), flagName: "question2-1") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			guard weakSelf.selectionMade == false else {
				weakSelf.selectionMade = false
				return
			}
			weakSelf.pricesTest.priceDisplay.isUserInteractionEnabled = true
            
			weakSelf.tutorialAnimation.pause()
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*What do you think?*\n Choose the answer that makes sense to you.".localized(ACTranslationKey.popup_tutorial_recall)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top == weakSelf.pricesTest.priceDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesTest.priceDisplay.centerXAnchor
					$0.width == weakSelf.pricesTest.priceDisplay.widthAnchor
					$0.bottom <= weakSelf.view.safeAreaLayoutGuide.bottomAnchor - 8
				}
				let offset = (weakSelf.view.frame.width <= 320) ? -16 : -8
				let shape = OverlayShape.roundedRect(weakSelf.pricesTest.priceDisplay, 8, CGSize(width: -8, height: offset))
				weakSelf.pricesTest.view.overlayView(withShapes: [shape])
			}
			
			
		}
		
		state.addCondition(atTime: progress(seconds: 22.5), flagName: "prices_middle") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.pricesTest.priceDisplay.isUserInteractionEnabled = false
            weakSelf.pricesTest.view.overlayView(withShapes: [])
            weakSelf.tutorialAnimation.pause()
            weakSelf.progress = 0.5
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*Another great choice!*\nLet's proceed to part two.".localized(ACTranslationKey.popup_tutorial_greatchoice2)
				$0.buttonTitle = "NEXT".localized(ACTranslationKey.button_next)
				$0.button.addAction {
					weakSelf.tutorialAnimation.resume()
					weakSelf.view.window?.clearOverlay()
					weakSelf.currentHint?.removeFromSuperview()
					weakSelf.pricesQuestions = weakSelf.pricesTest.preparedQuestionController()
					weakSelf.pricesQuestions.shouldAutoProceed = false
					weakSelf.pricesQuestions.delegate = self
					weakSelf.customView.setContent(viewController: weakSelf.pricesQuestions)
					weakSelf.pricesQuestions.buildButtonStackView()
					weakSelf.pricesQuestions.prepareQuestions()
					weakSelf.pricesQuestions.selectQuestion()
					weakSelf.pricesQuestions.questionDisplay.isUserInteractionEnabled = true
                    weakSelf.selectionMade = false
				}
				$0.layout {
					$0.centerX == weakSelf.view.centerXAnchor
					$0.centerY == weakSelf.view.centerYAnchor
					$0.width == 232
				}
			}
		}
		
		state.addCondition(atTime: progress(seconds: 22.6), flagName: "question3-0") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			guard weakSelf.selectionMade == false else {
				weakSelf.selectionMade = false
				return
			}

			weakSelf.currentHint?.removeFromSuperview()
            weakSelf.pricesQuestions.questionDisplay.isUserInteractionEnabled = true
            let shape = OverlayShape.roundedRect(weakSelf.pricesQuestions.questionDisplay, 8, CGSize(width: -8, height: -8))
            weakSelf.pricesQuestions.view.overlayView(withShapes: [shape])
			weakSelf.tutorialAnimation.pause()

			self?.currentHint = self?.view.window?.hint {
				$0.content = "*What do you think?*\nTry your best to recall the price from part one.".localized(ACTranslationKey.popup_tutorial_choose2)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top == weakSelf.pricesQuestions.questionDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesQuestions.questionDisplay.centerXAnchor
					$0.width == weakSelf.pricesQuestions.questionDisplay.widthAnchor
				}
			}
		}
		
		state.addCondition(atTime: progress(seconds: 22.7), flagName: "questions3-1") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.tutorialAnimation.pause()
			weakSelf.progress = 0.75
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*Great choice!*\nLet's try another.".localized(ACTranslationKey.popup_tutorial_greatchoice1)
				$0.buttonTitle = "NEXT".localized(ACTranslationKey.button_next)
				$0.button.addAction {
					weakSelf.tutorialAnimation.resume()
					weakSelf.view.window?.clearOverlay()
					weakSelf.currentHint?.removeFromSuperview()
					weakSelf.pricesQuestions.selectQuestion()
					weakSelf.pricesQuestions.questionDisplay.isUserInteractionEnabled = true
                    weakSelf.selectionMade = false
                    weakSelf.pricesQuestions.topButton.set(selected: false)
                    weakSelf.pricesQuestions.bottomButton.set(selected: false)
				}
				$0.layout {
					$0.top == weakSelf.pricesQuestions.questionDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesQuestions.questionDisplay.centerXAnchor
					$0.width == 232
				}
			}
		}
        
		state.addCondition(atTime: progress(seconds: 32.5), flagName: "question3-2") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			guard weakSelf.selectionMade == false else {
				weakSelf.selectionMade = false
				
				return
				
			}
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.tutorialAnimation.pause()
			weakSelf.pricesQuestions.questionDisplay.isUserInteractionEnabled = true
            let shape = OverlayShape.roundedRect(weakSelf.pricesQuestions.questionDisplay, 8, CGSize(width: -8, height: -8))
            weakSelf.pricesQuestions.view.overlayView(withShapes: [shape])

			
			self?.currentHint = self?.view.window?.hint {
				$0.content = "*What do you think?*\nTry your best to recall the price from part one.".localized(ACTranslationKey.popup_tutorial_choose2)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top == weakSelf.pricesQuestions.questionDisplay.bottomAnchor + 10
					$0.centerX == weakSelf.pricesQuestions.questionDisplay.centerXAnchor
					$0.width == weakSelf.pricesQuestions.questionDisplay.widthAnchor
				}
			}
		}
        
		state.addCondition(atTime: progress(seconds: 33.5), flagName: "end") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			
			weakSelf.finishTutorial()
		}
		
	}
	
	func removeHint(hint:String) {
		_ = state.removeCondition(with: hint)
	}
	func addHint(hint:String, view:UIView? = nil, timeToAdd:Double = 10.0) {
		let time = tutorialAnimation.time + timeToAdd
		print("HINT:", time)
		state.addCondition(atTime: progress(seconds:time), flagName: hint) {
			[weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.pricesQuestions.questionDisplay.isUserInteractionEnabled = true
			weakSelf.pricesQuestions.deselectButtons()
			weakSelf.view.window?.clearOverlay()
			weakSelf.currentHint?.removeFromSuperview()
			weakSelf.tutorialAnimation.pause()
			
			view?.overlay(radius: 24, inset: CGSize(width: 8	, height: 0))
			view?.highlight(radius: 24)
           
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = """
			Tap this matching price.
			""".localized(ACTranslationKey.popup_tutorial_pricetap)
				
				if let view = view {
					
					$0.layout {
						$0.top == view.bottomAnchor + 30
						$0.centerX == view.centerXAnchor
						$0.width == 252
						
					}
				} else {
					$0.layout {
						$0.bottom == weakSelf.view.bottomAnchor - 30
						$0.centerX == weakSelf.view.centerXAnchor
						$0.width == 252
						
					}
				}
			}
           
			
		}
		
		
	}
}
