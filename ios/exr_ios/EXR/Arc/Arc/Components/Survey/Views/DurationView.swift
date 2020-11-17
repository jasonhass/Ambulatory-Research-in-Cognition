//
// DurationView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
open class DurationView: UIView, SurveyInput{
    public var orientation: UIStackView.Alignment = .top
    
	@IBOutlet weak var picker: UIDatePicker!
	public weak var surveyInputDelegate: SurveyInputDelegate?

	
	let calendar = Calendar.current

	override open func awakeFromNib() {
		super.awakeFromNib()
		
	}

	public func getValue() -> QuestionResponse? {
		let interval = picker.countDownDuration
		
		return AnyResponse(type: .duration, value: interval.localizedInterval())
	}

	public func setValue(_ value: QuestionResponse?) {
		
		
		var interval:TimeInterval = 0.0
		defer {
            
			picker.countDownDuration = interval
           
		}
		
		guard let v = value?.value as? String else {
			return
		}
		
		
		let components = v.components(separatedBy: CharacterSet(charactersIn: ","))
			
		for component in components {
			if component.contains("hr") {
				let hours = component.replacingOccurrences(of: " hr", with: "").trimmingCharacters(in: .whitespacesAndNewlines)
				interval += Double(Int(hours) ?? 0) * TimeInterval.Unit.hour.rawValue
			}
			
			if component.contains("min") {
				let minutes = component.replacingOccurrences(of: " min", with: "").trimmingCharacters(in: .whitespacesAndNewlines)
				interval += Double(Int(minutes) ?? 0) * TimeInterval.Unit.minute.rawValue
			}
		}
		
		

		
	}
    @IBAction func valueChanged(_ sender: Any) {
        self.surveyInputDelegate?.didChangeValue();
        
    }
    
}
