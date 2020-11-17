//
// EarningsDetailViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import ArcUIKit

open class EarningsDetailViewController : CustomViewController<ACEarningsDetailView> {
	
	
	open override func viewDidLoad() {
		super.viewDidLoad()
		
			let backButton = UIButton(type: .custom)
			backButton.frame = CGRect(x: 0, y: 0, width: 100, height: 32)
			backButton.setImage(UIImage(named: "cut-ups/icons/arrow_left_white"), for: .normal)
			backButton.setTitle("BACK".localized(ACTranslationKey.button_back), for: .normal)
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
		
		
//		customView.root.refreshControl = UIRefreshControl()
//		customView.root.addSubview(customView.root.refreshControl!)
		customView.root.refreshControl?.addTarget(self, action: #selector(refresh(sender:)), for: UIControl.Event.valueChanged)

		customView.root.alwaysBounceVertical = true
		
		NotificationCenter.default.addObserver(self, selector: #selector(didSync(notification:)), name: .ACEarningDetailsUpdated, object: nil)
		didSync(notification:  nil)
		
	}
	open override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
        self.navigationController?.navigationBar.barStyle = .black
		self.navigationController?.isNavigationBarHidden = false
	}
	open override var preferredStatusBarStyle: UIStatusBarStyle {
		return .lightContent
	}
	
	@objc func refresh(sender:AnyObject)
	{
		
		//my refresh code here..
		print("refreshing")
		NotificationCenter.default.post(name: .ACSessionUploadComplete, object: Arc.shared.sessionController.sessionUploads)
		
	}
	@objc func backPressed()
	{
		
		self.navigationController?.popViewController(animated: true)
		
	}
	@objc func didSync(notification:Notification?) {
		OperationQueue.main.addOperation { [weak self] in
			guard let weakSelf = self else{
				return
			}
			weakSelf.customView.root.refreshControl?.endRefreshing()

			if let fetchDate = weakSelf.app.appController.lastFetched[EarningsController.detailKey] {
				weakSelf.customView.set(synched: fetchDate)
			}
			if let details:EarningDetail = weakSelf.app.appController.read(key: EarningsController.detailKey),
				let earnings = details.response?.earnings{
				weakSelf.customView.clear()
				weakSelf.update(earnings)
			}
		}
		
		
	}
	
	func update(_ earnings:EarningDetail.Response.Earnings) {
		let fetchDate = app.appController.lastFetched[EarningsController.detailKey]
		
		customView.set(studyTotal: earnings.total_earnings)
		if let date = fetchDate {
			customView.set(synched: date)
		}
		guard let cycles = earnings.cycles else {
			return
		}
		for cycle in cycles {
			
			customView.add(cycle: cycle)
		}
	}
	
}
