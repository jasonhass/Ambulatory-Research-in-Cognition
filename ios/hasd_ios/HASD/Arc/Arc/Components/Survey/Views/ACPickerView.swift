//
// ACPickerView.swift
//



import UIKit

class ACPickerView: UIView, SurveyInput, UIPickerViewDelegate, UIPickerViewDataSource {
   
	public weak var surveyInputDelegate: SurveyInputDelegate?


    public var orientation: UIStackView.Alignment = .top
   
    
    @IBOutlet weak var picker: UIPickerView!
    
    var _question:Survey.Question?
    var _items:[String]?
    let calendar = Calendar.current
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        picker.delegate = self
        picker.dataSource = self
        surveyInputDelegate?.didFinishSetup()

        
    }
    public func set(_ items:[String]) {
        _items = items
        picker.reloadAllComponents()

    }

    
    public func set(question: Survey.Question) {
        _question = question
        picker.reloadAllComponents()
    }
    public func getValue() -> QuestionResponse? {
        
        let selectedIndex = picker.selectedRow(inComponent: 0)
        let selectedTextValue = self.pickerView(picker,
                                                titleForRow: selectedIndex,
                                                forComponent: 0)
        
        return AnyResponse(type: .choice, value: selectedIndex, textValue: selectedTextValue)
    }
    
    public func setValue(_ value: QuestionResponse?) {
        guard let index = value?.value as? Int else {
            return
        }
        picker.selectRow(index, inComponent: 0, animated: true)
    }
    
    
    @IBAction func valueChanged(_ sender: Any) {
        self.surveyInputDelegate?.didChangeValue();
        
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return _question?.answers?.count ?? _items?.count ?? 0
    }

    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return _question?.answers?[row].value as? String ?? _items?[row] 
    }
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        surveyInputDelegate?.didChangeValue()
    }
}
