//
// TimeView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit

open class TimeView: UIView, SurveyInput {
	public weak var surveyInputDelegate: SurveyInputDelegate?

    public var orientation: UIStackView.Alignment = .top
    

    @IBOutlet weak var parentStack: UIStackView!
    @IBOutlet weak var picker:UIDatePicker!
    
    let calendar = Calendar.current
    
    private let dateFormatter:DateFormatter = DateFormatter()
    
    private var _value:String?
	
	public weak var hint:HintView?
    
    override open func awakeFromNib() {
        super.awakeFromNib()
		
        self.dateFormatter.dateFormat = "h:mm a"

        if let date = self.dateFormatter.date(from: "12:00 PM") {
            picker.setDate(date, animated: false)
        }
		surveyInputDelegate?.didFinishSetup()
        
        if Arc.get(flag: .time_picker_hint_shown) == false {
            let stackTop = parentStack.topAnchor.constraint(equalTo: self.topAnchor, constant: 62)
            stackTop.isActive = true
            
            self.hint = hint {
                $0.content = "".localized(ACTranslationKey.popup_scroll)
                $0.configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
                                                        secondaryColor: UIColor(named:"HintFill")!,
                                                        textColor: .black,
                                                        cornerRadius: 8.0,
                                                        arrowEnabled: true,
                                                        arrowAbove: false))
                $0.updateHintContainerMargins()
                $0.updateTitleStackMargins()
                $0.layout {
                    $0.bottom == picker.topAnchor
                    $0.centerX == picker.centerXAnchor
                    $0.width >= 232
                    $0.height >= 68
                    $0.width <= self.widthAnchor
                    
                }
            }
        }
    }
 
    public func getValue() -> QuestionResponse? {
		
        let value = self.dateFormatter.string(from: picker.date)

        return AnyResponse(type: .time, value: value)
    }
    
    public func setValue(_ value: QuestionResponse?) {
		
		guard let value = value?.value as? String else {
			return
		}
		guard let date = dateFormatter.date(from: value) else {
			return
		}
		picker.date = date

    }
    
    @IBAction func valueChanged(_ sender: Any) {
        // Hide the hint after the first TimeView
        // The value must change to advance past the first TimeView, so this is safe
        Arc.set(flag: .time_picker_hint_shown)
        
        self.surveyInputDelegate?.didChangeValue();
    }
    
    //MARK: TextFields
	
	
	public func setError(message: String?) {
		if message != nil {
			
			
		} else {
			
			
		}
	}
}
