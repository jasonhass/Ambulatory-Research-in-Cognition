//
//  DNPricesQuestionViewController.swif.swift
//  ARC
//
//  Created by Michael Votaw on 5/12/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

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
                    let string = item.prices[priceIndex]
                    b.setTitle("\(string)", for: .normal)
                    b.isHidden = false
                }
                
            case 1:
                questionLabel.text = "\(item.name)"
                //The price being displayed is at index 1 in this case
                //If item.correct == 1 then the price displayed is the correct value.
                priceLabel.text = item.prices[1]
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
