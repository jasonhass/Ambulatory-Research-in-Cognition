//
// SliderView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

open class SliderView: UIView, SurveyInput {
    public var didFinishSetup: (() -> ())?
    
    public var orientation: UIStackView.Alignment = .bottom
    
    
    @IBOutlet weak var selectedContainer: UIStackView!
    
    @IBOutlet weak var valueLabel: UILabel!
	@IBOutlet weak var detailLabel: UILabel!

    @IBOutlet weak var sliderContainer: BorderedUIView!
    @IBOutlet weak var valueSlider: UISlider!
    @IBOutlet weak var minLabel: UILabel!
    @IBOutlet weak var maxLabel: UILabel!
    
    public var didChangeValue: (() -> ())?
	public var tryNext:(() -> ())?

    var first = true
	var hideSelectedAfterFirst = false
    private var _value:Float? = nil
    
    
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
	public func setHidesSelectedAfterFirst(value:Bool) {
		hideSelectedAfterFirst = value
	}
    public func hideSelectedContainer() {
		//Prevents layout change when toggled off
        selectedContainer.alpha = 0
    }
    
    public func getValue() -> QuestionResponse? {
		guard let value = _value else {
			return AnyResponse(type: .slider, value: nil)
		}
		let res = AnyResponse(type: .slider, value: ((value)/valueSlider.maximumValue))
        return res
    }
    public func setValue(_ value: QuestionResponse?) {
		
        defer {
            updateText(_value)            
        }
		
		if let value = value?.value as? Float {
            first = false
			_value = value * valueSlider.maximumValue
			valueSlider.value = value * valueSlider.maximumValue
            if hideSelectedAfterFirst {
                hideSelectedContainer()
            }
		} else {
			_value = nil
			valueSlider.value = valueSlider.maximumValue / 2
            
		}
		

    }
    private func updateText(_ value:Float?) {
        
        guard let value = value else {
            valueLabel.text = ""
            detailLabel.text = "Drag to select"
            return
        }
        detailLabel.text = "You've selected:"
        valueLabel.text = "\(Int(value))"
    }
    func set(min:Float, max:Float, minMessage:String?, maxMessage:String?) {
        valueSlider.minimumValue = min
        valueSlider.maximumValue = max
        valueSlider.value = max / 2
        valueSlider.setThumbImage(UIImage(named: "cut-ups/slider/static"), for: .normal)
        valueSlider.setThumbImage(UIImage(named: "cut-ups/slider/pressed"), for: .highlighted)
        //sliderContainer.clipsToBounds = false
        //self.bringSubviewToFront(valueSlider)
        valueSlider.layer.zPosition = 10000
        valueLabel.text = "\(Int(valueSlider.value))"

        minLabel.text = minMessage
        maxLabel.text = maxMessage
        
    }
    
    @IBAction func valueChanged(_ sender: UISlider) {
		
        if !first {
            _value = sender.value
			
			print("printvalue:\(valueLabel.text)")
			if hideSelectedAfterFirst {
				hideSelectedContainer()
			}
            didChangeValue?()
		} else {
			
		}
		
        first = false
        updateText(_value)

    }
    
    @IBAction func touched(_ sender: Any) {
        
    }
    @IBAction func released(_ sender: Any) {
        
    }
}
