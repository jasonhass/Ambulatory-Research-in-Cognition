//
// SegmentedTextView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit
public class SegmentedTextView : UIView, SurveyInput, UIKeyInput, UITextInputTraits{
    public var orientation: UIStackView.Alignment = .top
    public var didChangeValue: (() -> ())?
    public var tryNext:(() -> ())?
    public var didFinishSetup: (() -> ())?

	@IBOutlet weak var inputStack: UIStackView!

	private var _value:[String] = [] {
		didSet {
			updateValue(newValue: self._value)
		}
	}
	
	
	//MARK: Text Input: Either override or assign default values to get
	//desired behavior
	public var keyboardType: UIKeyboardType = .numberPad
	override open var inputAccessoryView: UIView? {
		let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x:0, y:0, width:320, height:50))
		
		let flexSpace = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
		let done: UIBarButtonItem = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(SegmentedTextView.doneButtonAction))
        done.setTitleTextAttributes([NSAttributedString.Key.font : UIFont(name: "Roboto-Bold", size: 18)!,
                                     NSAttributedString.Key.underlineStyle : NSUnderlineStyle.single.rawValue,
                                     NSAttributedString.Key.foregroundColor : UIColor(named:"Primary")!], for: .normal)
		
		var items:[UIBarButtonItem] = []
		items.append(flexSpace)
		items.append(done)
		
		doneToolbar.items = items
		doneToolbar.sizeToFit()
		return doneToolbar
	}
	
	
	override open var canBecomeFirstResponder: Bool {
		return true
	}
	
	@objc func doneButtonAction() {
        tryNext?()
		resignFirstResponder()
	}
	public var hasText: Bool {
		return _value.count > 0
	}
	public func set(length:UInt) {
		
		self.inputStack.removeSubviews()
		for _ in 0 ..< length {
			let segment:TextSegment = .get()
			self.inputStack.addArrangedSubview(segment)
		}
		self.updateValue(newValue: _value)
	}
	public func insert(spaceAtIndex index:UInt, ofSize size:CGFloat) {
		guard index < self.inputStack.arrangedSubviews.count else {
			return
		}
		let view = self.inputStack.arrangedSubviews[Int(index)]
		self.inputStack.setCustomSpacing(size, after:view)
	}
	public func insertText(_ text: String) {
		if _value.count < inputStack.arrangedSubviews.count {
			_value.append(text)
		}
	}
	
	public func deleteBackward() {
		if hasText {
			_value.removeLast()
		}
	}
    
    func openKeyboard() {
        becomeFirstResponder()
    }
	
	
	
	
	public func setValue(_ value: QuestionResponse?) {
		_value = []
		if var buffer = value?.value as? String {
			while buffer.count > 0 && _value.count < inputStack.arrangedSubviews.count{
				_value.insert("\(buffer.removeLast())", at: 0)
			}
		}
	}
	
	private func updateValue(newValue:[String]) {
		let higlightedIndex = newValue.count
		let filledIndex:Int = (higlightedIndex > -1) ? higlightedIndex : -1
		var index = 0
		for view in self.inputStack.arrangedSubviews {
			
			let view = view as! BorderedView
			
			let child = view.subviews.first as! UILabel
			UIView.animate(withDuration: 0.35, animations: {
				
				if index == higlightedIndex {
					view.borderThickness = 3
				} else {
					view.borderThickness = 1
				}
				
				if index <= filledIndex {
					view.borderColor = UIColor(named: "Primary")!
				} else {
					view.borderColor = UIColor.lightGray
					
				}
			})
			if index >= self._value.count {
				child.text = " "
			} else {
				child.text = newValue[index]
			}
			index += 1
			view.layoutSubviews()
		}
		didChangeValue?()
	}
	
	public func getValue() -> QuestionResponse? {
        if _value.count < inputStack.arrangedSubviews.count {
            return AnyResponse(type: .segmentedText, value: nil)
        }
		return AnyResponse(type: .segmentedText, value: _value.joined())
	}
	override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
		becomeFirstResponder()
	}
	
	public func setError(message: String?) {
        var borderColor = UIColor(named: "Primary")!
        if message != nil {
            borderColor = UIColor(named: "Error")!
        }
        for view in inputStack.arrangedSubviews {
            if let v = view as? BorderedView {
                v.borderColor = borderColor
                v.layoutSubviews()
            }
        }
    }
}
