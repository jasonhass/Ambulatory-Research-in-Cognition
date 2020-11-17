//
// RebukedCommitmentView.swift
//



import Foundation
import ArcUIKit
import HMMarkup
public class RebukedCommitmentView : ACTemplateView {
	weak var contactStudyCoordinatorButton:ACButton!
	public override func content(_ view: UIView) {
		backgroundColor = ACColor.primary
		backgroundView.image = UIImage(named: "availability_bg", in: Bundle(for: self.classForCoder), compatibleWith: nil)
		view.view {
			$0.backgroundColor = .clear
		}

			view.acLabel {
				
				Roboto.Style.headingMedium($0, color: .white)
				$0.text = "".localized(ACTranslationKey.onboarding_nocommit_landing_header)
				$0.textAlignment = .center
				$0.layout {
					$0.height == 100
				}
				
			}
			
			view.acLabel {
				Roboto.Style.body($0, color: .white)
				$0.text = "".localized(ACTranslationKey.onboarding_nocommit_landing_body)
				$0.textAlignment = .center
				$0.layout {
					$0.centerY == view.centerYAnchor ~ 500
	
				}
			}
			
		view.view {
			$0.backgroundColor = .clear
		}
		
	}
	public override func footer(_ view: UIView) {
		contactStudyCoordinatorButton = view.acButton {
			$0.primaryColor = .clear
			$0.secondaryColor = .clear
			$0.topColor = .clear
			$0.bottomColor = .clear
			$0.setTitleColor(.white, for: .normal)
			$0.titleLabel?.textAlignment = .center

			$0.tintColor = .black
			$0.titleLabel?.textColor = .black
			$0.setTitle("".localized(ACTranslationKey.resources_contact), for: .normal)
			$0.addAction {
				Arc.shared.appNavigation.navigate(vc:Arc.shared.appNavigation.defaultHelpState() , direction: .toRight)
			}
		
			Roboto.PostProcess.link($0.titleLabel!)
			
		}
		
		nextButton = view.acButton {
			$0.primaryColor = ACColor.secondary
			$0.secondaryColor = ACColor.secondaryGradient
			$0.setTitleColor(ACColor.badgeText, for: .normal)
			$0.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
			
		}
	}
	
}



