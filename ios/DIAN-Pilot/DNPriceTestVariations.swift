//
//  DNPricesATest.swift
//  ARC
//
//  Created by Philip Hayes on 2/23/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import UIKit

//Subclass and modify only relevant portions of the test to comply with the variations requested

public class DNPricesTestVariationA : DNPricesTest {
    override func getTestDescription() -> DNTestDescription {
        return DNTestDescription(title: "Prices Instructions", storyBoardName:"PricesTest", pages: ["Page 1", "Page 2"])
    }

    override func generateTestSet() -> DNPRicesTestData{
        //Override the price set data
        let file = Bundle.main.path(forResource: "VersionA", ofType: ".json")
        let data = NSData(contentsOfFile: file!)
        priceSets = JSON(data: data as! Data)

        //Override the useage of the priceitem
        
        var items:[DNPriceItem] = []
        
        let testSet = priceSets![setId % priceSets!.count];
        itemCount = testSet.count

        for index in 0 ..< testSet.count {
            let testItem = testSet[index].dictionary!
            var prices = [ testItem["price"]!.string!, testItem["item_at_test"]!.string!];
            var correct = (testItem["correct_answer"] == "new") ? 0 : 1
            let item = DNPriceItem(name: testItem["item"]!.string!, prices: prices, correct:correct, goodPrice: 99, inputData: DNPriceInputData());
            items.append(item)
        }
        
            return DNPRicesTestData(items: items.shuffled())
    }
    
}



public class DNPricesTestVariationB : DNPricesTest {
    
    override func getTestDescription() -> DNTestDescription {
        return DNTestDescription(title: "Prices Instructions", storyBoardName:"PricesTest", pages: ["Page 1", "Page 2"])
    }
    
    override func generateTestSet() -> DNPRicesTestData{
        //Override the price set data
        let file = Bundle.main.path(forResource: "VersionB", ofType: ".json")
        let data = NSData(contentsOfFile: file!)
        priceSets = JSON(data: data as! Data)
        let s = super.generateTestSet()
        

       return s
    }
    
}
public class DNPricesTestVariationC : DNPricesTest {
    override func getTestDescription() -> DNTestDescription {
        return DNTestDescription(title: "Prices Instructions", storyBoardName:"PricesTest", pages: ["Page 1", "Page 2"])
    }
    
    override func generateTestSet() -> DNPRicesTestData{
        //Override the price set data
        let file = Bundle.main.path(forResource: "VersionC", ofType: ".json")
        let data = NSData(contentsOfFile: file!)
        priceSets = JSON(data: data as! Data)
        let s = super.generateTestSet()
       
        return s
    }
    
}
