//
// MultilineTextView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
open class MultilineTextView : UIView, SurveyInput, UITextViewDelegate {
	public weak var surveyInputDelegate: SurveyInputDelegate?

    public var orientation: UIStackView.Alignment = .top
    

    @IBOutlet weak var textView: UITextView!
	public var maxCharacters:Int?
	public var minCharacters:Int?
	public var keyboardType:UIKeyboardType = .default {
		didSet {
			textView.keyboardType = keyboardType
		}
	}
    override open func awakeFromNib() {
        super.awakeFromNib()
        textView.delegate = self
        textView.translatesAutoresizingMaskIntoConstraints = false
        textView.isScrollEnabled = false
        textView.layer.borderColor = UIColor(named: "Primary")!.cgColor
        textView.layer.borderWidth = 2.0
        textView.layer.cornerRadius = 8.0
		textView.inputAccessoryView = getInputAccessoryView(selector: #selector(endEditing(_:)))
        textView.becomeFirstResponder()
		surveyInputDelegate?.didFinishSetup()
    }
    
    override open var canBecomeFirstResponder: Bool {
        return true
    }
    
    public func getValue() -> QuestionResponse? {
		guard textView.text.count >= minCharacters ?? 1 else {
			return nil
		}
		if let max = maxCharacters {
			guard textView.text.count <= max else {
				return nil
			}
		}
        return AnyResponse(type: .multilineText,
						   value: textView.text)
    }
	public func setValue(_ value: QuestionResponse?) {
		textView.text = String(describing:  value?.value as? String ?? "")
		if let max = maxCharacters {
			textView.text = String(textView.text.prefix(max))
		}
    }
	
	
	public func setError(message: String?) {
		if message != nil {
			textView.layer.borderColor = UIColor(named: "Error")!.cgColor

		} else {
			textView.layer.borderColor = UIColor(named: "Primary")!.cgColor

		}
	}
	
	func getError() -> String? {
		return ""
	}
	public func textViewDidChange(_ textView: UITextView) {
		if let max = maxCharacters {
			textView.text = String(textView.text.prefix(max))
		}
		surveyInputDelegate?.didChangeValue()
	}
}
