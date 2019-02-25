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

open class IntroViewController: UIViewController {
    
    @IBOutlet weak var headingLabel: UILabel!
    @IBOutlet weak var subheadingLabel: UILabel!
    @IBOutlet weak var contentTextview: UITextView!
	@IBOutlet weak var nextButton:UIButton!

    var heading:String?
    var subheading:String?
    var content:String?
	var nextButtonTitle:String?
    var nextPressed:(()->Void)?
    var templateHandler:((Int)->Dictionary<String,String>)?
    var instructionIndex:Int = 0
	var shouldHideBackButton = false
    var isIntersitial = false

    open var renderer:HMMarkupRenderer!
    
    override open func viewDidLoad() {
        super.viewDidLoad()
		renderer = HMMarkupRenderer(baseFont: contentTextview.font!)

        // Do any additional setup after loading the view.
		if let nav = self.navigationController, nav.viewControllers.count > 1 {
			let backButton = UIButton(type: .custom)
			backButton.frame = CGRect(x: 0, y: 0, width: 80, height: 32)
			backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_white"), for: .normal)
			backButton.setTitle("BACK", for: .normal)
			backButton.titleLabel?.font = UIFont(name: "Roboto-Medium", size: 14)
			backButton.imageEdgeInsets = UIEdgeInsets(top: 0, left: -8, bottom: 0, right: 0)
			//backButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: -8, bottom: 0, right: 0)
			backButton.setTitleColor(UIColor(named: "Secondary"), for: .normal)
			backButton.backgroundColor = UIColor(named:"Secondary Back Button Background")
			backButton.layer.cornerRadius = 16.0
			backButton.addTarget(self, action: #selector(self.backPressed), for: .touchUpInside)
			//NSLayoutConstraint(item: backButton, attribute: NSLayoutConstraint.Attribute.left, relatedBy: NSLayoutConstraint.Relation.equal, toItem: super.view, attribute: NSLayoutConstraint.Attribute.left, multiplier: 1, constant: -75).isActive = true
			let leftButton = UIBarButtonItem(customView: backButton)
			
			//self.navigationItem.setLeftBarButton(leftButton, animated: true)
			self.navigationItem.leftBarButtonItem = leftButton
		}

    }
	@objc func backPressed() {
		self.navigationController?.popViewController(animated: true)
	}
    @IBAction func nextButtonPressed(_ sender: Any) {
        nextPressed?()
    }
    func set(heading:String?, subheading:String?, content:String?) {
        self.heading = heading
        self.subheading = subheading
        self.content = content
        
        if isViewLoaded {
            headingLabel.text = heading
            subheadingLabel.text = subheading
            contentTextview.text = content
			if let nextButtonTitle = nextButtonTitle {
				nextButton.setTitle(nextButtonTitle, for: .normal)
			}
            parseText(content: content)
        }
    }
    
    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if shouldHideBackButton {
            self.navigationItem.leftBarButtonItem?.isEnabled = false
            self.navigationItem.leftBarButtonItem?.customView?.isHidden = true
        }

		self.navigationItem.rightBarButtonItem = nil
        headingLabel.text = heading
        subheadingLabel.text = subheading
        contentTextview.text = content
		if let nextButtonTitle = nextButtonTitle {
			nextButton.setTitle(nextButtonTitle, for: .normal)
		}
        parseText(content: content)
        self.navigationController?.navigationBar.backgroundColor = .clear
    }
	open override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)

	}
    func parseText(content: String?) {
        guard let content = content else { return }
        let template = templateHandler?(instructionIndex) ?? [:]

        let markedUpString = renderer.render(text: content, template:template)
        let attributedString = NSMutableAttributedString(attributedString: markedUpString)
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 7
		let foregroundColor:UIColor = .white
//        attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
		attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: foregroundColor, range: NSMakeRange(0, attributedString.length))
        contentTextview.attributedText = attributedString
    }
	override open func viewDidLayoutSubviews() {
		super.viewDidLayoutSubviews()
		contentTextview.setContentOffset(CGPoint.zero, animated: false)

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
