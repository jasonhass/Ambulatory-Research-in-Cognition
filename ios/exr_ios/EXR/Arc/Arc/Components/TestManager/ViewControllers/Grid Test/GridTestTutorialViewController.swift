//
// GridTestTutorialViewController.swift
//


import UIKit
import ArcUIKit
class GridTestTutorialViewController: ACTutorialViewController, GridTestViewControllerDelegate {
	
	enum TestPhase {
		case start, fs, fsTimed, recallFirstStep, recallFirstChoiceMade, recallSecondChoiceMade, showingReminder, recall, end
	}
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
	
	let test:GridTestViewController = .get()
	
	var selectionMade = false
	var isMakingSelections = false
    var showingSelectNextTwo = false
	var lockIncorrect = false
	var maxGridSelected = 3
	var gridSelected = 0
	var phase:TestPhase = .start
    override func viewDidLoad() {
		duration = 25
        super.viewDidLoad()
        test.isPracticeTest = true
		test.shouldAutoProceed = false
		test.delegate = self
		setupScript()
		addChild(test)
		customView.setContent(viewController: test)
		test.tapOnTheFsLabel.isHidden = true
        // Do any additional setup after loading the view.
		//If these flags are set then we don't have to hide the xbutton
		
        if self.get(flag: .grids_tutorial_shown) == false  {
			if Arc.get(flag: .tutorial_optional) {
				self.set(flag: .grids_tutorial_shown)
			}

            self.customView.firstTutorialRun()
        }
    }
	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
	}
	override func viewWillDisappear(_ animated: Bool) {
		super.viewWillDisappear(animated)
		test.view.clearOverlay()
		view.removeHighlight()
		currentHint?.removeFromSuperview()
		
	}
    override func finishTutorial() {
        self.set(flag: .grids_tutorial_shown)
        super.finishTutorial()
    }
	func didSelect(){
		
		
        if showingSelectNextTwo == false {
            currentHint?.removeFromSuperview()
        }
		switch phase {
		
		
		case .start:
			test.view.clearOverlay()
			view.removeHighlight()
			tutorialAnimation.resume()

			break
		case .recallFirstStep:
			test.view.clearOverlay()
			view.removeHighlight()
			removeHint(hint: "hint")
			tutorialAnimation.time = 11.5
			addFirstHint(hint: "hint")
			tutorialAnimation.resume()

		case .recallFirstChoiceMade, .recallSecondChoiceMade:
            if showingSelectNextTwo == false {
                test.view.clearOverlay()
                removeHint(hint: "hint")
            }
            showingSelectNextTwo = true
            view.removeHighlight()
			tutorialAnimation.time = 10
			needHelp()
			tutorialAnimation.resume()


		case .recall:
			test.view.clearOverlay()
			view.removeHighlight()
			removeHint(hint: "hint")

			needHelp()

			tutorialAnimation.resume()
			
		case .fs:
			test.view.clearOverlay()
			view.removeHighlight()
			tutorialAnimation.resume()

			test.collectionView.isUserInteractionEnabled = false
		
		case .fsTimed:
			test.view.clearOverlay()
			view.removeHighlight()
			test.collectionView.isUserInteractionEnabled = true

		
		
		case .end:
			test.view.clearOverlay()
			view.removeHighlight()
			removeHint(hint: "hint")

			tutorialAnimation.time = 24.5
			tutorialAnimation.resume()
			break
		case .showingReminder:
			removeHint(hint: "hint")

		}
		
		selectionMade = true
	}
	
	func didSelectGrid(indexPath: IndexPath) {
		gridSelected += 1
		showDot(on: indexPath)
        hideImages()
		switch gridSelected  {
		case 1:
            maybeShowSelectNextTwoHint()
			phase = .recallFirstChoiceMade
		case 2:
			phase = .recallSecondChoiceMade
		case 3:
            removeFinalHint()
			test.collectionView.isUserInteractionEnabled = false
			phase = .end
		default:
			phase = .recallFirstStep
		}
		
		didSelect()
	}
	
	func didSelectLetter(indexPath: IndexPath) {
			didSelect()
	}
	
	func didDeselectLeter(indexPath: IndexPath) {
		didSelect()
	}
    
    func showDot(on indexPath: IndexPath) {
        guard let cell = self.test.collectionView.cellForItem(at: indexPath) as? GridImageCell else { return }
        cell.dotView.isHidden = false
    }
    
    func hideImages() {
        for indexPath in self.test.symbolIndexPaths {
            guard let cell = self.test.collectionView.cellForItem(at: indexPath) as? GridImageCell else { return }
            cell.image.isHidden = true
        }
    }

	func setupScript() {
		state.addCondition(atTime: progress(seconds: 0), flagName: "start-0") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.buildStartScreen(message: "In this three part test, you’ll be asked to *recall the location* of these items.".localized(ACTranslationKey.popup_tutorial_grid_recall),
									  buttonTitle: "Got it".localized(ACTranslationKey.popup_gotit))
			
		}
		state.addCondition(atTime: progress(seconds: 0), flagName: "start-1") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
			weakSelf.test.displaySymbols()
			weakSelf.test.view.overlayView(withShapes: [])
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = "The items will be placed in a grid of boxes. *Remember which box each item is in.* You will have 3 seconds.".localized(ACTranslationKey.popup_tutorial_rememberbox)
				$0.buttonTitle = "I'm Ready".localized(ACTranslationKey.popup_tutorial_ready)
				$0.onTap = { [weak self] in
					
					weakSelf.phase = .start
					self?.didSelect()
				}
				
				$0.layout {
					$0.centerY == weakSelf.view.centerYAnchor
					$0.centerX == weakSelf.view.centerXAnchor

					$0.width == weakSelf.test.collectionView.widthAnchor - 75
					
				}
			}
			
		}
		state.addCondition(atTime: progress(seconds: 3), flagName: "start-2") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.progress = 0.25
			weakSelf.tutorialAnimation.pause()
			weakSelf.test.displaySymbols()
			weakSelf.test.view.overlayView(withShapes: [])
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = "*Great!*\nLet's proceed to part two.".localized(ACTranslationKey.popup_tutorial_part2)
				$0.buttonTitle = "NEXT".localized(ACTranslationKey.button_next)
				$0.onTap = { [weak self] in
					self?.didSelect()
				}
				
				$0.layout {
					$0.centerY == weakSelf.view.centerYAnchor
					$0.centerX == weakSelf.view.centerXAnchor
					
					$0.width == weakSelf.test.collectionView.widthAnchor - 20
					
				}
			}
			
		}
		
		state.addCondition(atTime: progress(seconds: 3), flagName: "fs-0") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.test.displayFs()
			weakSelf.test.collectionView.isUserInteractionEnabled = true

			
			
		}
		state.addCondition(atTime: progress(seconds: 3.5), flagName: "fs-2") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
			weakSelf.phase = .fs
			let index = weakSelf.test.fIndexPaths[3]
			guard let cell = weakSelf.test.overlayCell(at: index) else {
				return
			}

			weakSelf.test.collectionView.isUserInteractionEnabled = true

			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = "Tap this letter F.".localized(ACTranslationKey.popup_tutorial_tapf1)
				$0.targetView = cell
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
				$0.layout {
					$0.top >= weakSelf.view.safeAreaLayoutGuide.topAnchor + 24
					$0.leading >= weakSelf.view.safeAreaLayoutGuide.leadingAnchor + 24
					$0.trailing <= weakSelf.view.safeAreaLayoutGuide.trailingAnchor - 24
					$0.bottom <= weakSelf.view.safeAreaLayoutGuide.bottomAnchor - 24
					
					$0.centerY == cell.centerYAnchor + 80 ~ 500
					$0.centerX == cell.centerXAnchor ~ 500
					
					
					
				}
			}
			
		}
		state.addCondition(atTime: progress(seconds: 3.5), flagName: "fs-3") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.progress = 0.5
			weakSelf.tutorialAnimation.pause()
			weakSelf.test.view.overlayView(withShapes: [])
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = "*Perfect!*\nNow: *Tap all the F’s* you see as quickly as you can.\nYou will have 8 seconds.".localized(ACTranslationKey.popup_tutorial_tapf2)
				$0.buttonTitle = "I'm Ready".localized(ACTranslationKey.popup_tutorial_ready)
				$0.onTap = { [weak self] in
					self?.didSelect()
					weakSelf.phase = .fsTimed
					weakSelf.isMakingSelections = false
					weakSelf.test.collectionView.isUserInteractionEnabled = true
				}
				
				$0.layout {
					$0.centerY == weakSelf.view.centerYAnchor
					$0.centerX == weakSelf.view.centerXAnchor
					
					$0.width == weakSelf.test.collectionView.widthAnchor
					
				}
			}
			
		}
		
		state.addCondition(atTime: progress(seconds: 11.5), flagName: "fs-4") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.progress = 0.75
			weakSelf.tutorialAnimation.pause()
			weakSelf.test.view.overlayView(withShapes: [])
			weakSelf.test.collectionView.isUserInteractionEnabled = false
			weakSelf.phase = .fs
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = "*Nice work!*\nDon't worry if you didn't find them all.".localized(ACTranslationKey.popup_tutorial_tapf3)
				$0.buttonTitle = "NEXT".localized(ACTranslationKey.button_next)
				$0.onTap = {
					weakSelf.phase = .recallFirstStep
					weakSelf.didSelect()
					weakSelf.test.clearGrids()
					weakSelf.buildStartScreen(message: "In the final part of the test, you will select the three boxes where these items were located in part one.".localized(ACTranslationKey.popup_tutorial_selectbox),
											  buttonTitle: "I'm Ready".localized(ACTranslationKey.popup_tutorial_ready))
				}
				
				$0.layout {
					$0.centerY == weakSelf.view.centerYAnchor
					$0.centerX == weakSelf.view.centerXAnchor
					
					$0.width == weakSelf.test.collectionView.widthAnchor
					
				}
			}
			
		}
		
		state.addCondition(atTime: progress(seconds: 11.5), flagName: "symbols-0") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.test.displayGrid()
			weakSelf.test.collectionView.isUserInteractionEnabled = true
			weakSelf.needHelp()
			weakSelf.isMakingSelections = true

			
		}
		state.addCondition(atTime: progress(seconds: 25), flagName: "end") { [weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.finishTutorial()
		}
	}
	func buildStartScreen(message:String, buttonTitle:String) {
		tutorialAnimation.pause()
		let view = customView.containerView.view {
			$0.backgroundColor = .white
			$0.layout {
				$0.top == customView.containerView.topAnchor ~ 998
				$0.leading == customView.containerView.leadingAnchor ~ 998
				$0.trailing == customView.containerView.trailingAnchor ~ 998
				$0.bottom == customView.containerView.bottomAnchor ~ 998
			}
		}
		var image:UIImageView!
		let _ = view.stack {
			$0.translatesAutoresizingMaskIntoConstraints = false
			$0.distribution = .fillEqually
			$0.axis = .horizontal
			$0.spacing = 20
			
			
			$0.image {
				$0.image = #imageLiteral(resourceName: "phone")
			}
			image = $0.image {
				$0.image = #imageLiteral(resourceName: "key")
			}
			$0.image {
				$0.image = #imageLiteral(resourceName: "pen")
			}
			$0.layout {
				
				$0.centerX == view.centerXAnchor
				
				$0.height == 100
				$0.centerY == view.centerYAnchor - 100
				
			}
		}
		currentHint = view.window?.hint {
			$0.content = message
			$0.buttonTitle = buttonTitle
			$0.onTap = { [weak self] in
				view.removeFromSuperview()
				self?.didSelect()
			}
            $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                    secondaryColor: UIColor(named:"HintFill")!,
                                                    textColor: .black,
                                                    cornerRadius: 8.0,
                                                    arrowEnabled: true,
                                                    arrowAbove: true))
            $0.updateHintContainerMargins()
            $0.updateTitleStackMargins()
			$0.layout {
				$0.centerY == image.bottomAnchor + 100
				$0.centerX == image.centerXAnchor
				$0.width == view.widthAnchor - 48
			
			}
		}
	}
	func removeHint(hint:String) {
		_ = state.removeCondition(with: hint)
	}
	func needHelp() {
		
		let time = tutorialAnimation.time + 3
		print("HINT:", time, ":",  progress(seconds:time))
		state.addCondition(atTime: progress(seconds:time), flagName: "hint") {
			[weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
			
            weakSelf.maybeRemoveSelectNextTwoHint()
			
			//Otherwise let's give them a choice.
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content  = "".localized(ACTranslationKey.popup_tutorial_needhelp)
				$0.buttonTitle = "".localized(ACTranslationKey.popup_tutorial_remindme)
				$0.onTap = {[weak self] in
					weakSelf.currentHint?.removeFromSuperview()
					//Remove the hint so that it will fire again
					self?.removeHint(hint: "hint")
					//If they selected one we'll show the double hint.
					if self?.gridSelected == 1 {
						self?.addDoubleHint(hint: "hint", seconds: 0.0)
						self?.tutorialAnimation.resume()
						self?.phase = .showingReminder
					//If they selected two, then we show the single hint
					} else if self?.gridSelected == 2 {
						self?.addFinalHint(hint: "hint", seconds: 0.0)
						self?.tutorialAnimation.resume()
						self?.phase = .showingReminder

					} else {
						//Otherwise show the first,
						//the user has removed a selection before this hint appeared
						self?.addFirstHint(hint: "hint", seconds: 0.0)
						self?.tutorialAnimation.resume()
					}
					
				}
				
				$0.layout {
					$0.centerX == weakSelf.view.centerXAnchor
					$0.centerY == weakSelf.view.centerYAnchor
					
				}
			}
			
		}
	}
	func addDoubleHint(hint:String, seconds:TimeInterval = 3.0) {
		let time = tutorialAnimation.time + seconds
		print("HINT:", time, ":",  progress(seconds:time))
		state.addCondition(atTime: progress(seconds:time), flagName: hint) {
			[weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
			let selected = weakSelf.test.collectionView.indexPathsForSelectedItems ?? []
			var index = weakSelf.test.symbolIndexPaths.filter {
				return !selected.contains($0)
			}
            if index.count > 2 {
                index.removeLast()
            }
			weakSelf.test.overlayCells(at: index)
			
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content  = "".localized(ACTranslationKey.popup_tutorial_tapbox2)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: false))
                $0.updateHintContainerMargins()
                $0.titleStack.layoutMargins = UIEdgeInsets(top: 12, left: 8, bottom: 26, right: 8)
				$0.layout {
					
					$0.centerX == weakSelf.view.centerXAnchor
					$0.top >= weakSelf.view.safeAreaLayoutGuide.topAnchor
					$0.leading >= weakSelf.view.safeAreaLayoutGuide.leadingAnchor + 20
					$0.trailing <= weakSelf.view.safeAreaLayoutGuide.trailingAnchor - 20

					$0.bottom == weakSelf.test.collectionView.topAnchor - 8
				
					
				}
			}
			
		}
		
		
	}
	func addFinalHint(hint:String, seconds:TimeInterval = 3.0) {
		let time = tutorialAnimation.time + seconds
		print("HINT:", time, ":",  progress(seconds:time))
		state.addCondition(atTime: progress(seconds:time), flagName: hint) {
			[weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
            guard let index = weakSelf.test.symbolIndexPaths.first(where: {weakSelf.test.collectionView.cellForItem(at: $0)?.isSelected == false}) else { return }
            guard let _ = weakSelf.test.overlayCell(at: index) else {
				return
			}
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = """
				*Hint:* One item was located
				in this box. Tap here.
				""".localized(ACTranslationKey.popup_tutorial_tapbox3)
				
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: false))
                $0.updateHintContainerMargins()
                $0.titleStack.layoutMargins = UIEdgeInsets(top: 12, left: 8, bottom: 26, right: 8)
                
				$0.layout {
					$0.centerX == weakSelf.view.centerXAnchor
					$0.top >= weakSelf.view.safeAreaLayoutGuide.topAnchor
					$0.leading >= weakSelf.view.safeAreaLayoutGuide.leadingAnchor + 20
					$0.trailing <= weakSelf.view.safeAreaLayoutGuide.trailingAnchor - 20

					$0.bottom == weakSelf.test.collectionView.topAnchor - 8
				}
			}
			
		}
		
		
	}
	func addFirstHint(hint:String, seconds:TimeInterval = 3.0) {
		let time = tutorialAnimation.time + seconds
		print("HINT:", time, ":",  progress(seconds:time))
		state.addCondition(atTime: progress(seconds:time), flagName: hint) {
			[weak self] in
			guard let weakSelf = self else {
				return
			}
			weakSelf.tutorialAnimation.pause()
			let index = weakSelf.test.symbolIndexPaths[min(2, weakSelf.gridSelected)]
			guard let cell = weakSelf.test.overlayCell(at: index) else {
				return
			}
			weakSelf.currentHint = weakSelf.view.window?.hint {
				$0.content = """
				*Hint:* One item was located
				in this box. Tap here.
				""".localized(ACTranslationKey.popup_tutorial_boxhint)
                $0.targetView = cell
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: true))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
                
				$0.layout {
					$0.centerX == weakSelf.view.centerXAnchor
					$0.width == 252
					
					if index.row/5 > 2 {
						//If above
						$0.bottom == cell.topAnchor + 40
						
					} else {
						
						$0.top == cell.bottomAnchor + 30
						
					}
					
				}
			}
			
		}
		
		
	}
    func maybeShowSelectNextTwoHint() {
        showingSelectNextTwo = true
        self.test.view.overlayView(withShapes: [.roundedRect(test.collectionView, 8, CGSize(width: 0, height: 0))])
        currentHint?.removeFromSuperview()
        self.removeHint(hint: "hint")
        self.currentHint = self.view.window?.hint {
            $0.content = "".localized(ACTranslationKey.popup_tutorial_tapbox)
            $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                    secondaryColor: UIColor(named:"HintFill")!,
                                                    textColor: .black,
                                                    cornerRadius: 8.0,
                                                    arrowEnabled: true,
                                                    arrowAbove: false))
            $0.updateHintContainerMargins()
            $0.titleStack.layoutMargins = UIEdgeInsets(top: 12, left: 8, bottom: 26, right: 8)
            $0.layout {
                $0.centerX == self.view.centerXAnchor
				$0.top >= self.view.safeAreaLayoutGuide.topAnchor
				$0.leading >= self.view.safeAreaLayoutGuide.leadingAnchor + 20
				$0.trailing <= self.view.safeAreaLayoutGuide.trailingAnchor - 20

				$0.bottom == self.test.collectionView.topAnchor - 8
            }
        }
    }
    
    func maybeRemoveSelectNextTwoHint() {
        guard let hint = self.currentHint else { return }
        if hint.content == "".localized(ACTranslationKey.popup_tutorial_tapbox) {
            self.currentHint?.removeFromSuperview()
            self.currentHint = nil
            showingSelectNextTwo = false
            self.removeHint(hint: "hint")
            
        }
        
    }
    
    func removeFinalHint() {
        self.currentHint?.removeFromSuperview()
        self.currentHint = nil
        self.removeHint(hint: "hint")
    }
    

}
