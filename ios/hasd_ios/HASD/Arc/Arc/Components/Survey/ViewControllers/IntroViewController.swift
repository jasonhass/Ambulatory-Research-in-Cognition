//
// IntroViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import HMMarkup
import ArcUIKit
public enum IntroViewControllerStyle : String {
	case standard, dark, test, grids, symbols, prices
	
	public func set(view:InfoView, heading:String?, subheading:String?, content:String?, template:[String:String] = [:]) {
		switch self {
		case .standard:
			view.setHeading(heading)
			view.setSubHeading(subheading)
			view.setContentText(content, template: template)
		case .test:
			view.addSpacer()
			view.setHeading(subheading)
			view.addSpacer()
			view.setContentLabel(content, template: template)
			
			Roboto.Style.headingMedium(			view.infoContent.headingLabel!
				, color: ACColor.secondary)
			
			view.infoContent.headingLabel!.layout {
				$0.centerY == view.centerYAnchor - 40
			}
            
            view.infoContent.contentLabel?.textColor = UIColor(named: "Badge Gray")
            
			view.nextButton!.layout {
				$0.bottom == view.safeAreaLayoutGuide.bottomAnchor - 24
			}
			view.infoContent.headingLabel?.textAlignment = .center
			view.infoContent.contentLabel?.textAlignment = .center
			view.backgroundColor = UIColor(named:"Primary Info")
			view.infoContent.alignment = .center
            view.setButtonColor(style:.secondary)
			
		case .grids:
			view.setSubHeading(heading)
			view.setIntroHeading(subheading)
			view.setSeparatorWidth(0.0)
			view.setIntroContentText(content, template: template)
			view.backgroundColor = UIColor(named:"Primary Info")
			view.infoContent.alignment = .leading
			view.backgroundView.image = UIImage(named: "grids_bg", in: Bundle(for: view.classForCoder), compatibleWith: nil)
            view.setButtonColor(style:.secondary)

		case .symbols:
			view.setSubHeading(heading)
			view.setIntroHeading(subheading)
			view.setSeparatorWidth(0.0)
			view.setIntroContentText(content, template: template)
			view.backgroundColor = UIColor(named:"Primary Info")
			view.infoContent.alignment = .leading
			view.backgroundView.image = UIImage(named: "symbols_bg", in: Bundle(for: view.classForCoder), compatibleWith: nil)
           	view.setButtonColor(style:.secondary)

		case .prices:
			view.setSubHeading(heading)
			view.setIntroHeading(subheading)
			view.setSeparatorWidth(0.0)
			view.setIntroContentText(content, template: template)
			view.backgroundColor = UIColor(named:"Primary Info")
			view.infoContent.alignment = .leading
			view.backgroundView.image = UIImage(named: "prices_bg", in: Bundle(for: view.classForCoder), compatibleWith: nil)
            view.setButtonColor(style:.secondary)
		
		case .dark:
			view.setSubHeading(heading)
			view.setHeading(subheading)
			view.setSeparatorWidth(0.0)
			view.setContentText(content, template: template)
			view.backgroundColor = UIColor(named:"Primary Info")
			view.infoContent.alignment = .leading
		}
	}
}
open class IntroViewController: CustomViewController<InfoView> {
    
    @IBOutlet weak var headingLabel: UILabel!
    @IBOutlet weak var subheadingLabel: UILabel!
    @IBOutlet weak var contentTextview: UITextView!
	@IBOutlet weak var nextButton:UIButton!
    var nextButtonImage:String?
	var style:IntroViewControllerStyle = .standard
    var heading:String?
    var subheading:String?
    var content:String?
	var nextButtonTitle:String?
	var tutorialButton:HMMarkupButton?
	weak var inputDelegate:SurveyInputDelegate? {
		get {
			return customView.inputDelegate
		}
		set {
			customView.inputDelegate = newValue
		}
	}
    var templateHandler:((Int)->Dictionary<String,String>)?
    var instructionIndex:Int = 0
	var shouldHideBackButton = false
    var isIntersitial = false

    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override open func viewDidLoad() {
        super.viewDidLoad()
		
		customView.backgroundColor = UIColor(named: "Primary")
        // Do any additional setup after loading the view.
		if let nav = self.navigationController, nav.viewControllers.count > 1 {
			let backButton = UIButton(type: .custom)
			backButton.frame = CGRect(x: 0, y: 0, width: 80, height: 32)
			backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_white"), for: .normal)
			backButton.setTitle("BACK".localized(ACTranslationKey.button_back), for: .normal)
			backButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
			backButton.imageEdgeInsets = UIEdgeInsets(top: 0, left: -8, bottom: 0, right: 0)
			//backButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: -8, bottom: 0, right: 0)
			backButton.setTitleColor(UIColor(named: "Secondary"), for: .normal)
			backButton.backgroundColor = UIColor(named:"Secondary Back Button Background")
			backButton.layer.cornerRadius = 20.0
			backButton.addTarget(self, action: #selector(self.backPressed), for: .touchUpInside)
			//NSLayoutConstraint(item: backButton, attribute: NSLayoutConstraint.Attribute.left, relatedBy: NSLayoutConstraint.Relation.equal, toItem: super.view, attribute: NSLayoutConstraint.Attribute.left, multiplier: 1, constant: -75).isActive = true
			let leftButton = UIBarButtonItem(customView: backButton)
			
			//self.navigationItem.setLeftBarButton(leftButton, animated: true)
			self.navigationItem.leftBarButtonItem = leftButton
		}
		customView.nextButton?.addTarget(self, action: #selector(nextButtonPressed(_:)), for: .primaryActionTriggered)
    }
	
	@objc func backPressed() {
		self.navigationController?.popViewController(animated: true)
	}
	
  	@objc func nextButtonPressed(_ sender: Any) {
		inputDelegate?.nextPressed(input: customView.inputItem, value: customView.inputItem?.getValue())
    }
	public func set(heading:String?, subheading:String?, content:String?, template:[String:String] = [:]) {
		
        
		style.set(view: customView, heading: heading, subheading: subheading, content: content, template: template)
		if style == .grids || style == .prices || style == .symbols {
			let button = HMMarkupButton()
			tutorialButton = button
			button.setTitle("View a Tutorial".localized(ACTranslationKey.testing_tutorial_link), for: .normal)
			Roboto.Style.bodyBold(button.titleLabel!, color:.white)
			Roboto.PostProcess.link(button)
			
			button.addAction {[weak self] in
				self?.currentHint?.removeFromSuperview()
				self?.view.window?.clearOverlay()
				self?.view.window?.removeHighlight()
				self?.set(flag: .tutorial_complete)
				//TODO: This will soon be depricated
				if self?.style == .grids {
					self?.present(GridTestTutorialViewController(), animated: true) {
						
					}
				}
				if self?.style == .prices {
                    if Arc.environment?.priceTestType == .simplified || Arc.environment?.priceTestType == .simplifiedCentered {
                        self?.present(SimplifiedPricesTestTutorialViewController(), animated: true) {}
                    } else {
                        self?.present(PricesTestTutorialViewController(), animated: true) {}
                    }

				}
				if self?.style == .symbols {
					self?.present(SymbolsTutorialViewController(), animated: true) {
						
					}
				}
				
			}
			customView.setAdditionalFooterContent(button)
            
            var showTutorialPrompt = false
            switch self.style {
            case .prices:
                showTutorialPrompt = !get(flag: .prices_tutorial_shown)
            case .symbols:
                showTutorialPrompt = !get(flag: .symbols_tutorial_shown)
            case .grids:
                showTutorialPrompt = !get(flag: .grids_tutorial_shown)
            default:
                break
            }
			if showTutorialPrompt {
				currentHint = customView.nextButton!.hint {
					$0.content = "".localized(ACTranslationKey.popup_tutorial_view)
                    $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                            secondaryColor: UIColor(named:"HintFill")!,
                                                            textColor: .black,
                                                            cornerRadius: 8.0,
                                                            arrowEnabled: true,
                                                            arrowAbove: true))
                    $0.updateHintContainerMargins()
                    $0.updateTitleStackMargins()
					$0.layout {
						$0.width == customView.nextButton!.widthAnchor
						$0.height == customView.nextButton!.heightAnchor + 20
						$0.centerX == customView.nextButton!.centerXAnchor
						$0.centerY == customView.nextButton!.centerYAnchor
					}
				}
			}
		}
    }
	public func updateNextbutton() {
		if let nextButtonTitle = nextButtonTitle, !nextButtonTitle.isEmpty {
			customView.nextButton?.setTitle(nextButtonTitle.localized(nextButtonTitle), for: .normal)
		} else {
			if nextButtonImage == nil {
				customView.nextButton?.setTitle("NEXT".localized(ACTranslationKey.button_next), for: .normal)
			} else {
				customView.nextButton?.setTitle(nil, for: .normal)
			}
		}
		
		if let nextButtonTitle = nextButtonImage {
			customView.nextButton?.setImage(UIImage(named: nextButtonTitle), for: .normal)
		} else {
			customView.nextButton?.setImage(nil, for: .normal)
			
		}
	}
    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if shouldHideBackButton {
            self.navigationItem.leftBarButtonItem?.isEnabled = false
            self.navigationItem.leftBarButtonItem?.customView?.isHidden = true
        }

		self.navigationItem.rightBarButtonItem = nil
        
		
		
        self.navigationController?.navigationBar.backgroundColor = .clear
		updateNextbutton()
    }
	open override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		if style != .standard && style != .test {
			customView.setSeparatorWidth(0.15)
		}
		guard let tutorialButton = tutorialButton else {
			return
		}
		
		if get(flag: .tutorial_complete)
			&& !get(flag: .tutorial_grats) {
			
			set(flag: .tutorial_grats)
			view.overlayView(withShapes: [.roundedRect(tutorialButton, 8.0, CGSize(width: -8, height: -8))])
			currentHint = view.window?.hint {
				$0.content = "".localized(ACTranslationKey.popup_tutorial_complete)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: false))
                $0.updateTitleStackMargins()
				$0.layout {
					$0.bottom == tutorialButton.topAnchor - 20
					$0.centerX == tutorialButton.centerXAnchor
					$0.width == tutorialButton.widthAnchor
				}
				$0.buttonTitle = "".localized(ACTranslationKey.popup_gotit)
                $0.updateHintContainerMargins()
				$0.onTap = { [weak self] in
					self?.currentHint?.removeFromSuperview()
					self?.view.window?.clearOverlay()
					self?.view.window?.removeHighlight()
				}
			}
			
		}

	}
    
    open override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.currentHint?.removeFromSuperview()
    }
	
	override open func viewDidLayoutSubviews() {
		super.viewDidLayoutSubviews()
		//contentTextview.setContentOffset(CGPoint.zero, animated: false)

	}


	
}
