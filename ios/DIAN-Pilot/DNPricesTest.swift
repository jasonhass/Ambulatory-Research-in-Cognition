//
//  DNPricesTest.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/21/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import CoreData
public class DNPricesTest : DNTest {
    private var numSymbols = 4
    var itemCount = 10
    var testData:DNPRicesTestData!
    var priceSets:JSON?;
    var setId = 0
    override func getTestDescription() -> DNTestDescription {
        
        return DNTestDescription(title: NSLocalizedString("Prices Instructions", comment: ""), storyBoardName:"PricesTest", pages: ["Page 1"])
    }


    init(setId:Int) {

        super.init()
        
        // load price sets json
        let file = Bundle.main.path(forResource: "priceSets", ofType: ".json")
        let data = NSData(contentsOfFile: file!)
        priceSets = JSON(data: data! as Data)
        self.setId = setId;

        testData = generateTestSet()
    }

    override public func startTest() {
        super.startTest()

    }

    func generateTestSet() -> DNPRicesTestData{
        var items:[DNPriceItem] = []
        let testSet = priceSets![setId % priceSets!.count];

        itemCount = testSet.count
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        
        for index in 0 ..< testSet.count {
            let testItem = testSet[index].dictionary!
            
            
            let price =  formatter.string(from: NSNumber(value: Float(testItem["price"]!.string!)!))!;
            let alternate = formatter.string(from: NSNumber(value: Float(testItem["alt"]!.string!)!))!;
            let itemName = testItem["item"]!.string!
            var prices = [price , alternate];
            prices.shuffle();
            let correct = prices.index(where: { (d) -> Bool in
                d == price
            });
            let item = DNPriceItem(name: itemName, prices: prices, correct:correct!, goodPrice: 99, inputData:DNPriceInputData());
            items.append(item)
        }

        return DNPRicesTestData(items: items)
    }

    
    
    public override func saveData(testSession: TestSession)
    {
        
        
        //Get a reference to the application delegate and create a context
        let context = DNDataManager.backgroundContext

        
       
        let sessionNumber = testSession.sessionID;
        let participantId = DNDataManager.sharedInstance.participantId!;
        
        let testObject:PriceTestData = NSManagedObject.createIn(context: context)
        testObject.date = self.startTime as NSDate
        for i in 0..<testData.items.count
        {
            let testItem:PriceTestItemData = NSManagedObject.createIn(context: context)
            
            let set = testData.items[i];

            testItem.item = set.name
            testItem.goodPrice = Int32(set.goodPrice);
            testItem.price = set.prices[set.correct]
            testItem.altPrice = set.prices[set.correct == 0 ? 1 : 0];
            testItem.correctIndex = Int32(set.correct)
            testItem.selectedIndex = Int32(set.inputData.choice)
            testItem.selectionTime = set.inputData.timeTouched.timeIntervalSince(self.startTime)
            testItem.questionDisplayTime = set.inputData.referenceDate.timeIntervalSince(self.startTime)
            testItem.stimulusDisplayTime = set.inputData.stimulusDisplayTime.timeIntervalSince(self.startTime);
            
            testItem.testData = testObject;
        }
        
        testSession.pricesTest = testObject;
        
        DNDataManager.save();
    }
    
}
struct DNPriceItem{
    var name:String
    var prices:[String]
    var correct:Int = 99;
    var goodPrice:Int = 99;
    var inputData:DNPriceInputData = DNPriceInputData();
}
struct DNPRicesTestData {
    var items:[DNPriceItem]

}

public struct DNPriceInputData {
    var choice:Int = -1
    var timeTouched:Date = Date()
    var referenceDate:Date = Date()
    var stimulusDisplayTime:Date = Date();
    
}

