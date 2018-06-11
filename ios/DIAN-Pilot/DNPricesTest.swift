/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
        
        return DNTestDescription(title: "Prices Instructions".localized(), storyBoardName:"PricesTest", pages: ["Page 1"])
    }


    init(setId:Int) {

        super.init()
        let languageKey = "en".localized(key: "language_key")
        let countryKey = "US".localized(key: "country_key")
        // load price sets json
        let file = Bundle.main.path(forResource: "priceSets-\(languageKey)-\(countryKey)", ofType: ".json")
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
        formatter.currencySymbol = ""
        for index in 0 ..< testSet.count {
            let testItem = testSet[index].dictionary!
            
            
            let price =  "\("".localized(key: "money_prefix"))\(testItem["price"]!.string!)\("".localized(key: "money_suffix"))";
            let alternate = "\("".localized(key: "money_prefix"))\(testItem["alt"]!.string!)\("".localized(key: "money_suffix"))";
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
        let arcId = DNDataManager.sharedInstance.arcId!;
        
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

