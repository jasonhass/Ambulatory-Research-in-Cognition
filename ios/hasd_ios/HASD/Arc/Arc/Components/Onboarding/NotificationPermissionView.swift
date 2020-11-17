//
// NotificationPermissionView.swift
//



import UIKit
import ArcUIKit
public class NotificationPermissionView: ACTemplateView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
	public override init() {
		super.init()
		backgroundColor = UIColor(red:0.02, green:0.06, blue:0.18, alpha:1)
	}
	
	public required init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	override public func footer(_ view: UIView) {
		view.stack {
			$0.axis = .horizontal
			$0.alignment = .bottom
			$0.distribution = .fillEqually
			$0.view {
				$0.backgroundColor = .clear
			}
			$0.image {
				$0.image = UIImage(named: "allow-notifications-arrow", in: Bundle(for: self.classForCoder), compatibleWith: nil)
				$0.contentMode = .scaleAspectFit
				
			}
			
		}
		
		view.acLabel {
			Roboto.Style.body($0, color: .white)
			$0.textAlignment = .center
			$0.text = "*Please tap \"allow\"* so we can tell you when tests are available.".localized(ACTranslationKey.onboarding_notifications_popup)
			
		}
		
		//Push up the content to be slightly below the notification
		view.view {
			$0.layout {
				$0.height == 40 ~ 999
			}
		}
	}
}
