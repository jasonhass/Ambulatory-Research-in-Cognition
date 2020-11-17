//
// PrivacyStack.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit



public class PrivacyStack: UIView {
	weak var button:UIButton!
	public init() {
		super.init(frame: .zero)
		
		stack { [weak self] in
			$0.axis = .vertical
			$0.alignment = .center
			$0.acLabel {
				$0.textAlignment = .center
				$0.text = "By signing in you agree to our"
					.localized(ACTranslationKey.bysigning_key)
				$0.numberOfLines = 0
				
			}
			
            let attributes:[NSAttributedString.Key:Any] = [
                .foregroundColor : UIColor(named: "Primary") as Any,
                .font : UIFont(name: "Roboto-Bold", size: 16.0) as Any,
                .underlineStyle: NSUnderlineStyle.single.rawValue
            ]
            let privacyTitle = NSAttributedString(string: "Privacy Policy".localized(ACTranslationKey.privacy_linked), attributes: attributes)
            
			self?.button = $0.button {
                $0.setAttributedTitle(privacyTitle, for: .normal)
                $0.setTitle("Privacy Policy".localized(ACTranslationKey.privacy_linked),
                            for: .normal)
                $0.setTitleColor(UIColor(named: "Primary"), for: .normal)
                Roboto.PostProcess.link($0)
			}
		}
		.layout { [weak self] in
			guard let weakSelf = self else {
				return
			}
			$0.top == weakSelf.topAnchor ~ 999
			$0.trailing == weakSelf.trailingAnchor ~ 999
			$0.bottom == weakSelf.bottomAnchor ~ 999
			$0.leading == weakSelf.leadingAnchor ~ 999
			$0.width == weakSelf.widthAnchor ~ 999
			$0.height == weakSelf.heightAnchor ~ 999
		}
	}
	
	required public init?(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	/*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}


extension UIView {
	
	@discardableResult
	public func privacyStack(apply closure: (PrivacyStack) -> Void) -> PrivacyStack {
		return custom(PrivacyStack(), apply: closure)
	}
	
}
