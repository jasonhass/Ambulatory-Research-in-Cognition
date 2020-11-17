//
// ACMigrationView.swift
//




import UIKit
import ArcUIKit
public class ACMigrationView: UIView {
	weak var titleLabel:ACLabel!
	weak var bodyLabel:ACLabel!
	weak var separator:ACHorizontalBar!
	weak var installationLabel:ACLabel!
	weak var installationBar:StepperProgressView!
	weak var installationStack:UIStackView!
	weak var nextButton:ACButton!
	weak var studyButton:ACButton!
	weak var installUpdateButton:ACButton!
	weak var contentStack:UIStackView!
	init() {
		super.init(frame: .zero)
		build()
	}
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
		build()
		
	}
	func build() {
		backgroundColor = .white
		contentStack = stack { [unowned self] in
			$0.axis = .vertical
			$0.distribution = .fill
			$0.alignment = .fill
			$0.spacing = 16
			$0.attachTo(view: self, margins: UIEdgeInsets(top: 42, left: 24, bottom: 24, right: 24))
			
			self.titleLabel = $0.acLabel {
				Roboto.Style.heading($0)
				
			}
			
			self.separator = $0.acHorizontalBar {
				$0.relativeWidth = 0.15
				$0.layout {
					$0.height == 2 ~ 999
				}
				
			}
			self.bodyLabel = $0.acLabel {
				Roboto.Style.body($0)
				
			}
			$0.view {
				$0.backgroundColor = .clear
				$0.setContentHuggingPriority(.defaultLow, for: .vertical)
			}
			self.studyButton = $0.acButton {
				$0.isHidden = true
				$0.primaryColor = .clear
				$0.secondaryColor = .clear
				$0.setTitle("Contact Study Team", for: .normal)

				Roboto.Style.bodyBold($0.titleLabel!, color:ACColor.primary)
				$0.setTitleColor(ACColor.primary, for: .normal)
				
				Roboto.PostProcess.link($0)
				
				$0.addAction {
					Arc.shared.appNavigation.navigate(state: Arc.shared.appNavigation.defaultContact(), direction: .toRight)
				}
			}
			
			self.nextButton = $0.acButton {
				$0.isHidden = true
				$0.primaryColor = ACColor.primary
				$0.secondaryColor = ACColor.primaryGradient
				$0.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
				
				$0.addAction {
					Arc.shared.nextAvailableState()
				}
			}
			self.installUpdateButton = $0.acButton {
				$0.isHidden = true
				$0.primaryColor = ACColor.primary
				$0.secondaryColor = ACColor.primaryGradient
				$0.setTitle("INSTALL UPDATE", for: .normal)
				
				$0.addAction {
					NotificationCenter.default.post(name: .ACMigrationTrigger, object: nil)
				}
			}
			self.installationStack = $0.stack {
				$0.axis = .vertical
				$0.spacing = 15
				self.installationLabel = $0.acLabel {
					Georgia.Style.subtitle($0, color: ACColor.primaryText)
					$0.text = "Installing update..."
					$0.textAlignment = .center
				}
				self.installationBar = $0.stepperProgress {
					$0.layout {
						$0.height == 9
					}
					
					$0.config.barWidth = 9
					$0.config.barInset = 0
					$0.config.outlineColor = ACColor.primary
					$0.config.backgroundColor = .clear
					$0.config.foregroundColor = ACColor.primary
					$0.progress = 0.0
				}
				
			}
			
		}
	}

}



