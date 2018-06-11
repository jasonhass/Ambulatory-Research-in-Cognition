/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import UIKit

protocol DNPriceQuestionDelegate: class  {
    func didAnswerWithValue(data:DNPriceInputData)
}
class DNPricesQuestionViewController: DNViewController {
    @IBOutlet var buttons: [UIButton]!
    
    @IBOutlet weak var questionLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    
    weak var delegate:DNPriceQuestionDelegate?
    private var currentTrialData:DNPriceInputData! = nil
    
    
    override func viewDidLoad() {
        super.viewDidLoad();
        
        
        for i in 0..<buttons.count
        {
            let b = buttons[i];
            b.tag = i;
            b.removeTarget(nil, action: nil, for: .touchUpInside);
            b.addTarget(self, action: #selector(didSelect(_:)), for: .touchDown);
        }
    }
    
    
    @IBAction func didSelect(_ sender: UIButton) {
        guard let id = buttons.index(of: sender) else {
            return
        }
        currentTrialData.choice = sender.tag
        currentTrialData.timeTouched = Date()
        delegate?.didAnswerWithValue(data: currentTrialData)
        
        
    }
    
    func presentQuestion(item:DNPriceItem, variation:Int? = 0){
        currentTrialData = item.inputData;
        currentTrialData.referenceDate = Date();
        if let v = variation {
            let formatter = NumberFormatter()
            formatter.numberStyle = .currency
            
            switch v {
            case 0:
                questionLabel.text = "\(item.name)"
                
                buttons.forEach { (b) in
                    b.isHidden = true
                }
                for b in buttons {
                    let priceIndex = buttons.index(of: b)!
                    if priceIndex >= item.prices.count {
                        break
                    }
                    let string = "\(item.prices[priceIndex])"
                    b.setTitle("\(string)", for: .normal)
                    b.isHidden = false
                }
                
            case 1:
                questionLabel.text = "\(item.name)"
                //The price being displayed is at index 1 in this case
                //If item.correct == 1 then the price displayed is the correct value.
                priceLabel.text = "\(item.prices[1])"
                for b in buttons {
                    
                    //                    b.setTitle("\(buttons.index(of: b))", for: .normal)
                    //                    b.isHidden = false
                }
                break
            default:
                break
            }
            
        }
    }
    
}
