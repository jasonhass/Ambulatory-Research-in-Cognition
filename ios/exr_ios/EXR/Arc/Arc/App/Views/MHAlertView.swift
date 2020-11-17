//
// MHAlertView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import HMMarkup
open class MHAlertView: UIView {
	public enum ButtonType {
		case `default`(String, ()->())
		
		case cancel(String, ()->())
		
		//Wait the specified amount of time
		case wait(waitTime:TimeInterval, (MHAlertView)->())
		
		//Delay input for a specified amount of time
		case delayed(name:String, delayTime:TimeInterval, (MHAlertView)->())
	}
	var buttons:[ButtonType]?
	
	@IBOutlet weak var stack:UIStackView!
	@IBOutlet weak var messageLabel:UILabel!
	private var waitTimers:[Timer] = []
	private var delayTimer:Timer?
	private var buttonMap: [UIView:ButtonType] = [:]
    
    private var markupRenderer:HMMarkupRenderer
   
    public required init?(coder aDecoder: NSCoder) {
        //The message label will not exist at this point use a default first
        markupRenderer = HMMarkupRenderer(baseFont: .systemFont(ofSize: 14))
        super.init(coder: aDecoder)

    }
	override open func awakeFromNib() {
		super.awakeFromNib()
        markupRenderer = HMMarkupRenderer(baseFont: messageLabel.font)

	}
	public func set(message:String) {
		messageLabel.attributedText = markupRenderer.render(text: message)

        messageLabel.font = UIFont(name: "Roboto", size: 17)
	}
	public func set(message:String?, buttons:[ButtonType]) {
		//Clear timers in case of rapid reuse
		
		waitTimers.forEach {$0.invalidate()}
		waitTimers = []
		delayTimer?.invalidate()
		delayTimer = nil
		
		
		messageLabel.attributedText = markupRenderer.render(text: message ?? "")
		messageLabel.font = UIFont(name: "Roboto", size: 17)
        self.buttons = buttons
		buttonMap = [:]
		stack.removeSubviews()
		for button in buttons {
			if let b = get(buttonForType: button) {
				buttonMap[b] = button
				stack.addArrangedSubview(b)
			}
		}
		if delayTimer != nil && delayTimer!.isValid {
			self.set(enabled:false)
		}

		
		
	}
	private func get(buttonForType buttonType:ButtonType) -> UIView? {
		switch buttonType {
		case let .`default`(title, _):
			let button:PrimaryButton = .get()
			button.addTarget(self, action: #selector(buttonTapped(_:)), for: .touchUpInside)
			button.setTitle(title, for: .normal)
			return button
		case let .cancel(title, _):
			let button:CancelButton = .get()
			button.addTarget(self, action: #selector(buttonTapped(_:)), for: .touchUpInside)
			button.setTitle(title, for: .normal)
			return button
		
		//We won't return a button here because there is no button to show.
		case let .wait(waitTime, callBack):
			waitTimers.append( Timer.scheduledTimer(withTimeInterval: waitTime, repeats: false) { (timer) in
				callBack(self)
				
			})
			return nil
		
		//Here we return a button but also disable its use for a period of time
		case let .delayed(title, delayTime, _):
			waitTimers.forEach { $0.fireDate += delayTime }
			
			//Create a button
			let button:PrimaryButton = .get()
			button.addTarget(self, action: #selector(buttonTapped(_:)), for: .touchUpInside)
			button.setTitle(title, for: .normal)
			button.isEnabled = false
			
			
			delayTimer = Timer.scheduledTimer(withTimeInterval: delayTime, repeats: false, block: { (timer) in
				self.set(enabled:true)
				
			})
			
			return button

		
		}
		
	}
	public func set(enabled: Bool) {
		//If we set a delay timer
		for view in stack.arrangedSubviews {
			if let button = view as? UIButton {
				button.isEnabled = enabled
			}
		}
	
	}
	@objc func buttonTapped(_ sender:UIButton) {
		if let buttonType = buttonMap[sender] {
		
			switch buttonType {
			case let .`default`(_, callBack):
				callBack()
			case let .cancel(_, callBack):
				callBack()
			case let .delayed(_, _, callBack):
				callBack(self)
				
			default:
				break
			}
			self.remove()
		}
	}
	
	open func remove() {
		if delayTimer?.isValid ?? false {
			delayTimer?.invalidate()
		}
		
		waitTimers.forEach {
			if $0.isValid {
				$0.invalidate()
			}
		}
		
		UIView.animate(withDuration: 0.15, delay: 0.1, options: .curveEaseOut, animations: {
			self.alpha = 0
		}) { [weak self] (_)  in
			self?.removeFromSuperview()

		}
	}
}
