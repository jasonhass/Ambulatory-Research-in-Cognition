//
// SurveyInputView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

public protocol SurveyInput {
    ///Returns nil if the value returned is invalid
    func getValue() -> QuestionResponse?
    
	
    func setValue(_ value:QuestionResponse?)
	func setError(message:String?)
    var orientation:UIStackView.Alignment {get set}
    var didChangeValue:(()->())? {get set}
    var didFinishSetup:(()->())? {get set}
	var tryNext:(() -> ())? {get set}

}

extension SurveyInput {
	
	public func setError(message: String?) {
		
	}
	
	
    func setValues(_ values:[String]?) {
        
    }
	
	func getInputAccessoryView(selector:Selector) -> UIToolbar{
		let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x:0, y:0, width:320, height:50))
		
		let flexSpace = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
		let done: UIBarButtonItem = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: selector)
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
    
}

