//
// ACPostCycleFinishView.swift
//



import Foundation
import ArcUIKit
public protocol ACPostCycleFinishViewDelegate : class {
	func nextPressed()
}
public class ACPostCycleFinishView : UIView {
	private weak var backgroundImage:UIImageView!
	private weak var contentStack:UIStackView!
	public weak var headerImageView:UIImageView!
	
	private weak var titleLabel:ACLabel!
	private weak var separator:ACHorizontalBar!
	private weak var bodyLabel:ACLabel!
	
	private var animations:[String:Animate] = [:]
	private weak var button:ACButton!
	public weak var delegate:ACPostCycleFinishViewDelegate?
	
	public override init(frame: CGRect) {
		super.init(frame: .zero)
		translatesAutoresizingMaskIntoConstraints = false
		backgroundColor = ACColor.primaryInfo
		var animationParams:Animate.Config = Animate.Config()
		animationParams.duration = 0.3
		animationParams.delay = 0.5
		backgroundImage = image {
			$0.image = UIImage(named: "finished_bg", in: Bundle(for: self.classForCoder), compatibleWith: nil)
			$0.contentMode = .scaleAspectFill
			
			$0.alpha = 0
			$0.layout {
				$0.top == topAnchor
				$0.trailing == trailingAnchor
				$0.bottom == bottomAnchor
				$0.leading == leadingAnchor
			}
			$0.fadeIn(animationParams)
		}
		
		animationParams.duration = 1.0
		
		contentStack = stack {[unowned self] in
			let _ = $0
			$0.attachTo(view: self)
			
			$0.axis = .vertical
			$0.alignment = .fill
			$0.spacing = 20
			$0.isLayoutMarginsRelativeArrangement = true
			$0.layoutMargins.bottom = 24
			$0.layoutMargins.left = 24
			$0.layoutMargins.right = 24

			self.headerImageView = $0.image {
				$0.image = UIImage(named: "finished-medal", in: Bundle(for: self.classForCoder), compatibleWith: nil)
				$0.contentMode = .top
				$0.setContentHuggingPriority(.fittingSizeLevel, for: .vertical)
				animationParams.delay = 0.6
				$0.fadeIn(animationParams)
					.translate(animationParams)
			}
			
			self.titleLabel = $0.acLabel {
				Roboto.Style.headingBold($0, color: ACColor.secondaryText)
				$0.text = "".localized(ACTranslationKey.progress_cyclecomplete_header)
				animationParams.delay = 0.8
				$0.fadeIn(animationParams)
					.translate(animationParams)
				
			}
			
			self.separator = $0.acHorizontalBar {
				$0.layout {
					$0.height == 2 ~ 999
				}
				$0.relativeWidth = 0.15
				animationParams.delay = 1.0
				
				$0.fadeIn(animationParams)
					.translate(animationParams)
			}
			self.bodyLabel = $0.acLabel {
				
				Roboto.Style.body($0, color: ACColor.secondaryText)
				$0.text = "".localized(ACTranslationKey.progress_cycleorstudycomplete_body)
				animationParams.delay = 1.2
				
				$0.fadeIn(animationParams)
					.translate(animationParams)
			}
			$0.stack {
				$0.axis = .vertical
				$0.alignment = .center
				let sup = $0
				self.button = $0.acButton {
					$0.layout {
						$0.leading == sup.leadingAnchor + 8
						$0.trailing == sup.trailingAnchor - 8
						
					}
					
					$0.primaryColor = ACColor.secondary
					$0.secondaryColor = ACColor.secondaryText
					$0.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
					$0.setTitleColor(ACColor.badgeText, for: .normal)
					
                    $0.addAction {
                        if Arc.shared.getSurveyStatus() == SurveyAvailabilityStatus.finished {
                            Arc.shared.appNavigation.navigate(vc: ACStudyTotalsViewController(), direction:.toRight)
                        } else {
                            Arc.shared.appNavigation.navigate(state: Arc.shared.appNavigation.defaultState(), direction: .toRight)
                        }

                    }
					
					animationParams.delay = 1.4
					
					$0.fadeIn(animationParams)
						.translate(animationParams)
				}
			}
			
		
			
		}
		
		

	}
	
	required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

}

