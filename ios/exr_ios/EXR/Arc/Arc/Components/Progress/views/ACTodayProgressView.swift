//
// ACTodayProgressView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import ArcUIKit
public protocol ACTodayProgressViewDelegate : class {
	func nextPressed()
}
public class ACTodayProgressView : UIView {
	private weak var backgroundImage:UIImageView!
	private var progressViews:[CircularProgressView] = []
	private weak var contentStack:UIStackView!
	private weak var titleLabel:ACLabel!
	private weak var sessionCompletionLabel:ACLabel!
	private weak var sessionRemainingLabel:ACLabel!
	private weak var badgeLabel:ACLabel!
	private weak var badgeView:UIView!
    private weak var bottomLabel:ACLabel!
	private var animations:[String:Animate] = [:]
	private weak var button:ACButton!
	public weak var delegate:ACTodayProgressViewDelegate?
    var thisStudy:ThisStudyExpressible = Arc.shared.studyController
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
			let stack = $0
			$0.layout {
				$0.centerX == centerXAnchor
				$0.centerY == centerYAnchor - 40
				$0.leading == safeAreaLayoutGuide.leadingAnchor + 16
				$0.trailing == safeAreaLayoutGuide.trailingAnchor - 16
			}
			
			$0.axis = .vertical
			$0.alignment = .center
			self.titleLabel = $0.acLabel {
				stack.setCustomSpacing(34, after: $0)
				Roboto.Style.headingBold($0, color: ACColor.secondaryText)
				$0.text = "progress_schedule_header"
				animationParams.delay = 0.8
				$0.fadeIn(animationParams)
					.translate(animationParams)
				
			}
			//This is the group of four progress views in the
			//middle of the page.
			$0.stack {
				stack.setCustomSpacing(32, after: $0)

                for _ in 1 ... 4 {
					
					let v = $0.circularProgress {
						$0.layout {
							$0.width == 64
							$0.height == 64
						}
						
						$0.config.strokeWidth = 6
						$0.config.barColor = ACColor.highlight
						$0.config.trackColor = ACColor.primary
						$0.config.size = 64
						$0.ellipseConfig.size = 64
						$0.checkConfig.scale = 0.5
						$0.progress = 0
						$0.isHidden = true
					}
					progressViews.append(v)
					animationParams.delay = 1.0

					$0.fadeIn(animationParams)
						.translate(animationParams)
				}
			}
			self.sessionCompletionLabel = $0.acLabel {
				stack.setCustomSpacing(22, after: $0)
				$0.textAlignment = .center
				Roboto.Style.subHeading($0, color: ACColor.secondaryText)
				$0.text = ""
				animationParams.delay = 1.2

				$0.fadeIn(animationParams)
					.translate(animationParams)
				
			}
			self.sessionRemainingLabel = $0.acLabel {
				stack.setCustomSpacing(22, after: $0)
                $0.textAlignment = .center

				Roboto.Style.subHeading($0, color: ACColor.secondaryText)
				$0.text = ""
				animationParams.delay = 1.4

				$0.fadeIn(animationParams)
					.translate(animationParams)
			}
			self.badgeView = $0.view{
                $0.backgroundColor = ACColor.badgeBackground
                $0.layer.cornerRadius = 4
                $0.clipsToBounds = true
                
                self.badgeLabel = $0.acLabel {

                    
                    Roboto.Style.goalReward($0, color:ACColor.badgeText)
                    $0.text = "progress_schedule_status2"
                }
                self.badgeLabel.attachTo(view: $0, margins: UIEdgeInsets(top: 3,
                                                                         left: 10,
                                                                         bottom: 3,
                                                                         right: 10))
                animationParams.delay = 1.4
                $0.fadeIn(animationParams)
                .translate(animationParams)
            }
        }
        if thisStudy.studyState == .baseline {
            bottomLabel = acLabel {
                
                Roboto.Style.body($0, color: ACColor.secondaryText)
                $0.text = "progress_practice_body2"
                //$0.font = UIFont(name: "Roboto", size: 17)
                $0.textAlignment = .center
                animationParams.delay = 1.4
                
                $0.layout {
                    $0.leading == safeAreaLayoutGuide.leadingAnchor + 17
                    $0.trailing == safeAreaLayoutGuide.trailingAnchor - 17
                    $0.bottom == safeAreaLayoutGuide.bottomAnchor - 100
                }
                
                $0.fadeIn(animationParams)
                    .translate(animationParams)
            }
        }
		
		button = acButton {
			$0.layout {
				$0.bottom == safeAreaLayoutGuide.bottomAnchor - 24
				$0.leading == safeAreaLayoutGuide.leadingAnchor + 32
				$0.trailing == safeAreaLayoutGuide.trailingAnchor - 32

			}
			InfoView.ButtonStyle.secondary.configure(button: $0)
			
			$0.setTitle("".localized(ACTranslationKey.button_next), for: .normal)
			$0.setTitleColor(ACColor.badgeText, for: .normal)
			
			$0.addAction { [weak self] in
				self?.delegate?.nextPressed()
			}
			
			animationParams.delay = 1.6
			
			$0.fadeIn(animationParams)
				.translate(animationParams)
		}
		
		
		
		
		
	}
	
	required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	public func set(sessionsCompleted:Int?, isPlural:Bool = false, isPractice:Bool = false) {
		guard let sessionsCompleted = sessionsCompleted else {
			sessionCompletionLabel.isHidden = true
			return
		}
		sessionCompletionLabel.isHidden = false
		var key = (isPlural) ? ACTranslationKey.progress_schedule_body2 : ACTranslationKey.progress_schedule_body1
		
		if isPractice {
			key = ACTranslationKey.progress_practice_body1
		}
		sessionCompletionLabel.text = "".localized(key)
			.replacingOccurrences(of: "{#}", with: "\(sessionsCompleted)")
	}
	
	public func set(sessionsRemaining:Int?) {
		guard let sessionsRemaining = sessionsRemaining else {
			sessionRemainingLabel.isHidden = true
			return
		}
		sessionRemainingLabel.isHidden = false
		sessionRemainingLabel.text = "".localized(ACTranslationKey.progress_schedule_status1)
			.replacingOccurrences(of: "{#}", with: "\(sessionsRemaining)")
        sessionRemainingLabel.textColor = UIColor(named: "TodaysProgressMoreToGo")
	}
	public func set(completed:Bool) {
		if completed {
			badgeView.isHidden = false
		} else {
			badgeView.isHidden = true
		}
	}
	
	public func set(progress:Double, for session:Int)  {
        assert(session < progressViews.count)
		let view = progressViews[session]

		if progress < 0 {
			UIView.animate(withDuration: 0.25) {
				//If we're hidden don't perform any animations
				view.isHidden = true
				self.layoutSubviews()
			}
			
			return
		} else {
			UIView.animate(withDuration: 0.25) {
				view.isHidden = false
				self.layoutSubviews()
			}
		}
		
		
		let oldVal = view.progress
		
		animations["\(session)"]?.stop()
		animations["\(session)"] = Animate()
			.delay(0.8 + 0.25 * Double(session + 1))
			.duration(0.25 * Double(session + 1))
			.curve(.easeOut)
			.run({ (time) -> Bool in
				
			view.progress = Math.lerp(a: oldVal, b: progress, t: time)
			
			return true
		})
	}
	
	

	
}
