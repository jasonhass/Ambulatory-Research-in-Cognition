//
//  PickerAlertViewController.swift
//  ARC
//
//  Created by Philip Hayes on 5/15/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit
class PickerAlertViewController: DNAlertViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    
    
    @IBOutlet weak var picker: UIPickerView!
    
    
    var columns:Array<Array<String>> = Array();
    var defaultRows:Array<Int> = Array();
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        
        for i in 0..<defaultRows.count
        {
            let d = defaultRows[i];
            picker.selectRow(d, inComponent: i, animated: false);
        }
        
        super.viewWillAppear(true)
        self.view.backgroundColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 0.7043309564)
        self.setRetValue();
        picker.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
        
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func confirm(_ sender: AnyObject) {
//        self.retValue = picker.date as AnyObject?
        
        self.confirmationButtonPresssed()
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return columns[component].count;
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return columns.count;
    }
    
    func pickerView(_ pickerView: UIPickerView, rowHeightForComponent component: Int) -> CGFloat {
        return 30;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return columns[component][row];
    }
    
    
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        
        setRetValue();
    }
    
    func setRetValue()
    {
        var selectedComponents: Array<String> = Array();
        for i in 0..<columns.count
        {
            selectedComponents.append(columns[i][self.picker.selectedRow(inComponent: i)]);
        }
        self.retValue = selectedComponents as AnyObject;
    }
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}
