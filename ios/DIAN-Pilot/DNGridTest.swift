//
//  DNGridTest.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/21/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import CoreData
public class DNGridTest : DNTest {
    private var numSymbols = 3
    private var numQuestions = 2
    private var numFs = 8
    var testData:[DNGridTestData]!
    var selected:[DNGridInputSet] = [] //Can be replaced with a touch information object
    var gridTestMetrics:[DNGridTestMetrics] = [];
    
    private var symbols:[UIImage] = [#imageLiteral(resourceName: "key"),
                                     #imageLiteral(resourceName: "phone"),
                                     #imageLiteral(resourceName: "pen")]
    private var usedSymbols:[Int] = []

    
    
    override func getTestDescription() -> DNTestDescription {
        return DNTestDescription(title: NSLocalizedString("Grids Instructions", comment: ""), storyBoardName:"GridTest", pages: ["Page 1", "Page 2"])
    }


    override init() {
        super.init()
        testData = generateTestSets()
    }

    override public func startTest() {
        super.startTest()
    }


    public func getNumSymbols() -> Int
    {
        return self.numSymbols;
    }
    
    public func getNumQuestions() -> Int
    {
        return self.numQuestions;
    }

    public override func selectValue(option: AnyObject?) {

        if let v = option as? DNGridInputSet {
            setValue(value: v)
        }
        
    }

    private func setValue(value:DNGridInputSet){
        selected.append(value);
    }
    
    private func generateTestSets() -> Array<DNGridTestData>
    {
        var sets:Array<DNGridTestData> = Array();
        
        for i in 0..<numQuestions
        {
            usedSymbols = [];
            sets.append(self.generateTestSet());
        }
        return sets;
    }

    private func generateTestSet() -> DNGridTestData{
        //Generate image set
        let imageSet = generateImageGridSet(setWidth: 5, setHeight: 5)
        //generate F set
        let fSet = generateFGridSet(setWidth: 6, setHeight: 10)
        return DNGridTestData(imageSet: imageSet, fSet: fSet)
    }

    
    public override func saveData(testSession:TestSession)
    {
        //Get a reference to the application delegate and create a context
        let context = DNDataManager.backgroundContext
        
        
        let sessionNumber = testSession.sessionID;
        let participantId = DNDataManager.sharedInstance.participantId!;
        
        var imageCount:Int = 0;
        var testObj:GridTestData = NSManagedObject.createIn(context: context)
        
        for n in 0..<numQuestions
        {
            let testItem:GridTestSectionData = NSManagedObject.createIn(context: context)
            
            
            for y in 0..<testData[n].imageSet.grids.count
            {
                for x in 0..<testData[n].imageSet.grids[y].count
                {
                    let imgIndex = testData[n].imageSet.grids[y][x];
                    if imgIndex > -1
                    {
                        
                        imageCount += 1;
                        let displayedOption:GridTestImage = NSManagedObject.createIn(context: context)
                        displayedOption.x = Int64(x)
                        displayedOption.y = Int64(y)
                        displayedOption.image = "\(self.imageNameAt(index: imgIndex))"
                        displayedOption.section = testItem;
                    }
                }
            }
          
            testItem.fCount = Int64(selected[n].selectedFs)
            testItem.eCount = Int64(selected[n].selectedEs)
            testItem.displaySymbols = gridTestMetrics[n].displaySymbols;
            testItem.displayDistraction = gridTestMetrics[n].displayDistraction;
            testItem.displayTestGrid = gridTestMetrics[n].displayTestGrid;
            
            for i in 0..<numSymbols
            {
                
                if i < selected[n].selectedGridItems.count
                {
                    let s = selected[n].selectedGridItems[i];
                    
                    let time = s.timeTouched.timeIntervalSince(self.startTime);
                    
                    let tap:GridTestTap = NSManagedObject.createIn(context: context)
                    tap.selectionTime = time
                    tap.x = Int64(s.gridLocationX)
                    tap.y = Int64(s.gridLocationY)
                    
                    tap.section = testItem;
                }
            }
            
            testItem.test = testObj;
        }
        
        testObj.date = self.startTime as NSDate
        
        testObj.testSession = testSession;

        let dict = testObj.dictionaryOfAttributes(excludedKeys: [])
        let data = try! JSONSerialization.data(withJSONObject: dict , options: .prettyPrinted)
        
//        print(String.init(data: data, encoding: .utf8) ?? "")
        DNDataManager.save();
    }
 
    
    public func imageAt(index:Int) ->UIImage
    {
        let img = self.symbols[index];
        
        return img.copy() as! UIImage;
    }
    
    public func imageNameAt(index:Int) -> String
    {
        let names = ["key", "phone", "pen"];
        
        return names[index];
        
    }
 
    /**>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     Generate test data to be referenced by the view controller

     */
    private func generateImageGridSet(setWidth:Int, setHeight:Int) -> DNGridSet {
        var array = Array<Array<Int>>(repeating: Array<Int>(repeating: -1, count: setWidth), count: setHeight)
        var set:Set<Int> = []
        while set.count < numSymbols {
            set.insert(Int(arc4random_uniform(5)))
        }
        for row in 0 ..< array.count {
            if set.contains(row) {
                let index = Int(arc4random_uniform(5))
                
                var imgIndex = Int(arc4random_uniform(UInt32(symbols.count)))
                while usedSymbols.contains(imgIndex) {
                    imgIndex = Int(arc4random_uniform(UInt32(symbols.count)))
                }
                usedSymbols.append(imgIndex)
                
                array[row][index] = imgIndex;
            }
//            print(array[row])

        }

        return DNGridSet(grids: array)
    }
    
    private func generateFGridSet(setWidth:Int, setHeight:Int) -> DNGridSet {
        var array = Array<Array<Int>>(repeating: Array<Int>(repeating: 0, count: setWidth), count: setHeight)

        var items:Array<Int> = Array();
        
        items.append(-1);
        items.append(setWidth * setHeight);
        
        var count = 0;
        while count < numFs
        {
            items.sort();
            let minMax = findGap(items: items);
            
            items.append( (minMax.min + 1) + Int( arc4random_uniform(UInt32( minMax.max - minMax.min - 1))));
            count += 1;
        }
        
        items.sort();
        
        items.removeFirst();
        items.removeLast();
        
        for i in items
        {
            let row = i / setWidth;
            let col = i % setWidth;
            array[row][col] = 1;
        }
 
        return DNGridSet(grids: array)
    }
    
    
    func findGap(items:Array<Int>) -> (min:Int, max:Int)
    {
        var gap = 0;
        var min = 0;
        var max = 0;
        for i in 0..<items.count - 1
        {
            if items[i + 1] - items[i] > gap
            {
                gap = items[i + 1] - items[i];
                min = items[i];
                max = items[i + 1];
            }
        }
        
        return (min:min, max:max);
    }
    
}
//Model components
public struct DNGridSet {

    var grids:Array<Array<Int>>

}
public struct DNGridTestData {
    var imageSet:DNGridSet
    var fSet:DNGridSet
}

public struct DNGridInputSet
{
    var selectedFs:Int
    var selectedEs:Int
    var selectedGridItems:[DNGridInputData]
}

public struct DNGridInputData {
    var gridLocationX:Int
    var gridLocationY:Int
    var timeTouched:NSDate
}

public struct DNGridTestMetrics
{
    var displaySymbols:TimeInterval = -1;
    var displayDistraction:TimeInterval = -1;
    var displayTestGrid:TimeInterval = -1;
}



