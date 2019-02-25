//
// PasswordView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit
open class PasswordView : UIView, SurveyInput, UITextFieldDelegate {
    public var orientation: UIStackView.Alignment = .top
    public var didChangeValue: (() -> ())?
    public var tryNext:(() -> ())?
    public var didFinishSetup: (() -> ())?

	@IBOutlet weak var textField:UITextField!
	@IBOutlet weak var secureButton:UIButton!
	@IBOutlet weak var borderView:BorderedUIView!
	override open func awakeFromNib() {
		super.awakeFromNib()
        set(secure: false)
		textField.inputAccessoryView = getInputAccessoryView(selector: #selector(PasswordView.doneButtonAction))
	}
	
	@IBAction func toggleSecure(_ sender: Any) {
		set(secure: !textField.isSecureTextEntry)
	}
    
    func openKeyboard() {
        textField.becomeFirstResponder()
    }
    
	func set(secure: Bool) {
		textField.isSecureTextEntry = secure
		secureButton.isSelected = !secure
	}
	public func getValue() -> QuestionResponse? {
		return AnyResponse(type: .password, value: textField.text)
	}
	public func setValue(_ value: QuestionResponse?) {
		textField.text = String(describing: value?.value as? String ?? "")
	}
	@objc func doneButtonAction() {
        tryNext?()
		textField.resignFirstResponder()
	}
	public func setError(message: String?) {
		if message != nil {
			borderView.borderColor = UIColor(named: "Error")!
			borderView.layoutSubviews()
		} else {
			borderView.borderColor = UIColor(named: "Primary")!
            borderView.layoutSubviews()
		}
	}
	
	
}
