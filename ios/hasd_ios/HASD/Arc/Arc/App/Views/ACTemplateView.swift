//
// ACTemplateView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
import HMMarkup


/** ACTemplate view is a view that is automatically nested in a scrollview exposed as
 	"root"

	There is an ImageView called backgroundView. It can be used to layer things behind all
	content on the screen.

	The when subclassing override the header, content, and footer functions accordingly to organize your views.

	If a view becomes scrollable then a scroll indicator will appear on the screen.
*/
open class ACTemplateView: UIView, UIScrollViewDelegate {
	public var root:UIScrollView!
	var backgroundView:UIImageView!
	public var nextButton:ACButton?
	var renderer:HMMarkupRenderer!
	var shouldShowScrollIndicator: Bool = true
	var spacerView:UIView!
	var bottomScrollIndicatorView: UIView!
	
	var topScrollIndicatorView : UIView!
	
	var scrollIndicatorLabel:UILabel!
	private var contentView:UIView!
	public init() {
		super.init(frame: .zero)
		
		self.backgroundColor = .white
		build()
		
		
		NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
		NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
		
	}
	open override func didMoveToWindow() {
		super.didMoveToWindow()
		
	}
	open override func layoutSubviews() {
		super.layoutSubviews()
		scrollIndicatorState(root)
	}
	/// creates the views contents
	/// Creates an imageview and scrollview in front of it. 
	func build() {
		if root != nil {
			root.removeFromSuperview()
		}
		let v = self
		backgroundView = image {
			$0.backgroundColor = .clear
			$0.contentMode = .scaleAspectFill
			$0.layout {
				
				// select an anchor give a priority of 999 (almost Required)
				$0.top == v.topAnchor ~ 999
				$0.trailing == v.trailingAnchor ~ 999
				$0.bottom <= v.bottomAnchor ~ 999
				$0.leading == v.leadingAnchor ~ 999
				
			}
		}
		root = scroll {[unowned self] in
			
			
			contentView = $0.stack {
				$0.spacing = 8
				$0.axis = .vertical
				$0.alignment = .fill
				$0.isLayoutMarginsRelativeArrangement = true
				$0.layoutMargins = UIEdgeInsets(top: 24,
												left: 24,
												bottom: 24,
												right: 24)
				//An internal override point for handling content that will appear above all other content.
				header($0)
				
				//This will be where the main content of a view should go.
				content($0)
				
				//This vew allows the footer to stack from the bottom of the screen up.
				//Once space runs out the view will expand downward and become scrollable.
				spacerView = $0.view {
					$0.accessibilityLabel = "spacer"
					$0.translatesAutoresizingMaskIntoConstraints = false
					$0.isHidden = true
					$0.setContentHuggingPriority(UILayoutPriority(rawValue: 200), for: .vertical)
				}
				
				//This is for items that you want to always be below all other content.
				footer($0)
			}
			contentView.layout {
				
				// select an anchor give a priority of 999 (almost Required)
				$0.top == contentView.superview!.topAnchor ~ 999
				$0.trailing == contentView.superview!.trailingAnchor ~ 999
				$0.bottom == contentView.superview!.bottomAnchor ~ 999
				$0.leading == contentView.superview!.leadingAnchor ~ 999
				$0.width == self.widthAnchor ~ 999
				$0.height >= self.safeAreaLayoutGuide.heightAnchor ~ 500
			}
			
		}
		root.layout { [unowned self] in
			$0.top == safeAreaLayoutGuide.topAnchor ~ 999
			$0.trailing == safeAreaLayoutGuide.trailingAnchor ~ 999
			$0.bottom == safeAreaLayoutGuide.bottomAnchor ~ 999
			$0.leading == safeAreaLayoutGuide.leadingAnchor ~ 999
			$0.width == self.widthAnchor ~ 999
			$0.height == self.heightAnchor ~ 999
		}

		self.topScrollIndicatorView = scrollIndicator {
			$0.alpha = 0
			$0.configure(with: IndicatorView.Config(primaryColor: ACColor.primaryText,
													secondaryColor: ACColor.primaryText,
													textColor: .white,
													cornerRadius: 24.0,
													arrowEnabled: false,
													arrowAbove: false))
			
			$0.container!.isLayoutMarginsRelativeArrangement = true
			$0.container!.layoutMargins = UIEdgeInsets(top: 15, left: 15, bottom: 15, right: 15)
			
			
			$0.container!.axis = .horizontal
			$0.button {
				$0.titleLabel!.textAlignment = .center
				$0.setTitle("SHOW MORE".localized(ACTranslationKey.popup_showmore), for: .normal)
				$0.tintColor = ACColor.secondary
				Roboto.Style.bodyBold($0.titleLabel!, color: ACColor.secondary)
				$0.titleLabel!.numberOfLines = 1
				$0.addAction {
					[weak self] in
					guard let weakSelf = self else {
						return
					}
					weakSelf.scrollToTop()
				}
			}
			$0.image {
				$0.image = UIImage(named: "arrow_up_white", in: Bundle(for: self.classForCoder), compatibleWith: nil)
				$0.contentMode = .scaleAspectFit
				$0.layout {
					$0.width == 20 ~ 999
					$0.height == 20 ~ 999
				}
				
			}
			
			
			$0.layout {
				
				$0.top == v.safeAreaLayoutGuide.topAnchor + 20 ~ 999
				$0.centerX == v.centerXAnchor
				$0.height >= 40
			}
		}
		
		self.bottomScrollIndicatorView = scrollIndicator {
			$0.configure(with: IndicatorView.Config(primaryColor: ACColor.primaryText,
												 secondaryColor: ACColor.primaryText,
												 textColor: .white,
												 cornerRadius: 24.0,
												 arrowEnabled: false,
                                                 arrowAbove: false))
			
				$0.container!.isLayoutMarginsRelativeArrangement = true
				$0.container!.layoutMargins = UIEdgeInsets(top: 15, left: 15, bottom: 15, right: 15)

			
			$0.container!.axis = .horizontal
				$0.button {
					$0.titleLabel!.textAlignment = .center
					$0.setTitle("SHOW MORE".localized(ACTranslationKey.popup_showmore), for: .normal)
					$0.tintColor = ACColor.secondary
					Roboto.Style.bodyBold($0.titleLabel!, color: ACColor.secondary)
					$0.titleLabel!.numberOfLines = 1
					$0.addAction {
						[weak self] in
						guard let weakSelf = self else {
							return
						}
						weakSelf.scrollToBottom()
					}
				}
				$0.image {
					$0.image = UIImage(named: "cut-ups/icons/arrow_down_white")
					$0.contentMode = .scaleAspectFit
					$0.layout {
						$0.width == 20 ~ 999
						$0.height == 20 ~ 999
					}

				}
				
				
			$0.layout {
				
				$0.bottom == v.safeAreaLayoutGuide.bottomAnchor - 20 ~ 999
				$0.centerX == v.centerXAnchor
				$0.height >= 40
			}
		}
		root.delegate = self
		
		scrollIndicatorState(root)

	}
	open func header(_ view:UIView) {
		
	}
	/// Layout content for the view override this method to add content to a
	/// pre-constrained scrollview, this will also  automatically add a scroll
	/// indicator to the view. Keyboard insets will be handled when they appear.
	///
	/// - Parameter view: the root view to add content to. You will need to constrain the view to the edges and ensure that the height and width can be determined by the inner content.
	open func content(_ view:UIView) {
	
	}
	
	open func footer(_ view:UIView) {
		
		
	}
	
	@objc func scrollToTop() {
		self.root.delegate = nil
		
		var config = Animate.Config()
		config.curve = .easeIn
		
		self.topScrollIndicatorView.fadeOut(config)
		
		let offset = self.root.contentOffset.y
		Animate().curve(.easeInOut).duration(0.9).delay(0.3).run { [weak self] (progress) -> Bool in
			guard let weakSelf = self, weakSelf.superview != nil else {
				return false
			}
			
			let newValue = Math.lerp(a: Double(offset), b: 0.0, t: progress)
			self?.root.contentOffset.y = CGFloat(newValue)
			return true
		}
		
		//Run a second delayed automation to reassign the delegate so that normal functionality will persist
		Animate().delay(1.3).curve(.none).run { [weak self] (progress) -> Bool in
			guard let weakSelf = self, weakSelf.superview != nil else {
				return false
			}
			weakSelf.root.delegate = weakSelf
			return true
		}
		//Show the top bar
		config.delay = 1.2
		self.bottomScrollIndicatorView.fadeIn(config)
	}
	
	@objc func scrollToBottom() {
		//Disable the scroll delegate to prevent interactive
		//scrolling effects
		
		var config = Animate.Config()
		config.curve = .easeIn
		
		//Fade the scroll indicator out
		self.bottomScrollIndicatorView.fadeOut(config)
		
		//Capture the current state of the view that we need for animation
		let offset = self.root.contentOffset.y
		let bottom = self.root.contentSize.height - self.root.bounds.height
		
		self.root.delegate = nil

		
		//Run the animation
		Animate().curve(.easeInOut).duration(0.9).delay(0.3).run { [weak self] (progress) -> Bool in
			
			//Check for existence and display status
			guard let weakSelf = self, weakSelf.superview != nil else {
				return false
			}

			//Interpolate using progress which is a double from 0.0 to 1.0
			let newValue = Math.lerp(a: Double(offset), b: Double(bottom), t: progress)
			weakSelf.root.contentOffset.y = CGFloat(newValue)
			return true
		}
		
		//Run a second delayed automation to reassign the delegate so that normal functionality will persist
		Animate().delay(1.3).curve(.none).duration(0).run { [weak self] (progress) -> Bool in
			guard let weakSelf = self, weakSelf.superview != nil else {
				return false
			}
			weakSelf.root.delegate = weakSelf
			return true
		}
		
		//Show the top bar
		config.delay = 1.2
		self.topScrollIndicatorView.fadeIn(config)
	}
	
	public required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	@objc func keyboardWillShow(notification: NSNotification) {
		setBottomScrollInset(value: 40)
	}
	
	@objc func keyboardWillHide(notification: NSNotification){
		setBottomScrollInset(value: 0)
	}
	public func setBottomScrollInset(value:CGFloat) {
		var inset = root.contentInset
		
		inset.bottom = value
		
		root.contentInset = inset
	}

	// MARK: ScrollView
	public func scrollViewDidScroll(_ scrollView: UIScrollView) {
		self.scrollIndicatorState(scrollView)
	}
	public func scrollViewWillBeginDecelerating(_ scrollView: UIScrollView) {
		self.scrollIndicatorState(scrollView)
		
	}
	public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
		self.scrollIndicatorState(scrollView)
		
	}
	public func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
		bottomScrollIndicatorView.isHidden = true
		topScrollIndicatorView.isHidden = true
	}
	
	private func scrollIndicatorState(_ scrollView: UIScrollView) {
		guard bottomScrollIndicatorView != nil else {
			return 
		}
		guard shouldShowScrollIndicator else {
			bottomScrollIndicatorView.alpha = 0
			return
		}
		guard let nextButton = nextButton else {
			bottomScrollIndicatorView.alpha = 0

			return
		}
		let contentHeight = scrollView.contentSize.height
		
		let viewHeight = scrollView.bounds.height
		let offset = scrollView.contentOffset.y
		
		let effectiveHeight = contentHeight - viewHeight - 20
		let maxProgress = contentHeight - viewHeight - effectiveHeight
		
		let progress = min(maxProgress, max(offset - effectiveHeight, 0))
		let convertedRect = nextButton.superview!.convert(nextButton.frame, to: scrollView)
		
		guard !scrollView.bounds.contains(convertedRect) && !scrollView.bounds.intersects(convertedRect) else {
			bottomScrollIndicatorView.alpha = 0
			return
		}
		let alpha:CGFloat = 1.0 - (progress/maxProgress)
		bottomScrollIndicatorView.alpha = alpha
		
	}
}
