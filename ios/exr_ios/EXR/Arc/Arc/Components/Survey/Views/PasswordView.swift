//
// PasswordView.swift
//



import UIKit
open class PasswordView : UIView, SurveyInput, UITextFieldDelegate {
    public var orientation: UIStackView.Alignment = .top
    

	@IBOutlet weak var textField:UITextField!
	@IBOutlet weak var secureButton:UIButton!
	@IBOutlet weak var borderView:BorderedUIView!
	public weak var surveyInputDelegate: SurveyInputDelegate?
	override open func awakeFromNib() {
		super.awakeFromNib()
        set(secure: false)
		textField.inputAccessoryView = getInputAccessoryView(selector: #selector(PasswordView.doneButtonAction))
		surveyInputDelegate?.didFinishSetup()
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
        surveyInputDelegate?.tryNextPressed()
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
