/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class DatePickerAlertViewController: DNAlertViewController {
    
    
    
    @IBOutlet weak var datePicker: UIDatePicker!
    var datePickerMode:UIDatePickerMode = .time;
    var defaultDate:Date?;
    var defaultDuration:TimeInterval?;
    override func viewDidLoad() {
        super.viewDidLoad()
        datePicker.datePickerMode = self.datePickerMode;
        datePicker.minuteInterval = 5;
        if datePickerMode == .countDownTimer
        {
            datePicker.countDownDuration = defaultDuration ?? 5 * 60;
        }
        else
        {
            datePicker.date = defaultDate ?? datePicker.date.roundedTo(minutes: datePicker.minuteInterval);
        }
        
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        self.view.backgroundColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 0.7043309564)

        datePicker.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
        
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func datePicked(_ sender: UIDatePicker) {
        
        switch sender.datePickerMode
        {
            case .time:
            self.retValue = sender.date as AnyObject?
            case .date:
            self.retValue = sender.date as AnyObject?
            case .dateAndTime:
            self.retValue = sender.date as AnyObject?
            case .countDownTimer:
            self.retValue = sender.countDownDuration as AnyObject?
        }
    }
    
    @IBAction func confirm(_ sender: AnyObject) {
        
        
        switch datePicker.datePickerMode
        {
        case .time:
            self.retValue = datePicker.date as AnyObject?
        case .date:
            self.retValue = datePicker.date as AnyObject?
        case .dateAndTime:
            self.retValue = datePicker.date as AnyObject?
        case .countDownTimer:
            self.retValue = datePicker.countDownDuration as AnyObject?
        }
        
        self.confirmationButtonPresssed()
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


