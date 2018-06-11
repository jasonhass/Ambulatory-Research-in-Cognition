/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
