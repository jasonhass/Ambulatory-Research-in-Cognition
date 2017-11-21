//
//  SymbolTest.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/15/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import CoreData
public class DNSymbolsTest:DNTest {

    private var numSymbols = 8
    
    var selected:[DNSymbolInputData] = []
    var touchLocations:[CGPoint] = []
    var touchTimes:[Date] = []
    
    var currentTestSet:DNSymbolDataSet?
    
    var testSets:[DNSymbolDataSet] = [];
    var testSetAppearanceTime:[NSDate] = []
    private let symbolSetsize:Int = 2
    private let numOptions:Int = 3
    private let numChoices:Int = 2
    let numQuestions:Int = 12;
    var currentQuestion:Int = 1
    
    private var symbols:[Int:UIImage] = [0: #imageLiteral(resourceName: "symbol_0"),
                                         1: #imageLiteral(resourceName: "symbol_1"),
                                         2: #imageLiteral(resourceName: "symbol_2"),
                                         3: #imageLiteral(resourceName: "symbol_3"),
                                         4: #imageLiteral(resourceName: "symbol_4"),
                                         5: #imageLiteral(resourceName: "symbol_5"),
                                         6: #imageLiteral(resourceName: "symbol_6"),
                                         7: #imageLiteral(resourceName: "symbol_7")]

    
    
    override func getTestDescription() -> DNTestDescription {
        return DNTestDescription(title: NSLocalizedString("Symbols Instructions", comment: ""), storyBoardName:"SymbolsTest", pages: ["i1"])
    }
    override init() {
        super.init()
        //create x number of cards on top row

        //create y number of cards on bottom row
        currentTestSet = generateTestSet()
        testSets.append(currentTestSet!);
        
    }
    
    
    func getSymbol(index:Int) -> UIImage {
        let img =  self.symbols[index] ?? #imageLiteral(resourceName: "background_light_16x16")
        return img.copy() as! UIImage;
    }
    
    override public func startTest() {
        super.startTest()
    }

    public func setNumSymbols(count:Int){
        self.numSymbols = count
    }

    public override func selectValue(option: AnyObject?) {

        if let o = option as? Int {
            if o >= 0 {
                setValue(value: o)
            }
        }
        if let o = option as? DNSymbolInputData {
            selected.append(o)
        }
    }

    public override func nextStep() {
        if currentQuestion >= numQuestions {
            return
        }
        currentQuestion += 1
        currentTestSet = generateTestSet()
        testSets.append(currentTestSet!);
    }

    private func setValue(value:Int){
        var s = DNSymbolInputData()
        s.choice = value
        selected.append(s)
    }

    // if two sets have the same values, in any order, the compare function will return true.
    // so [1,2] and [2,1] would be considered the "same" for our purposes.
    private func compareSets(s1:DNSymbolSet, s2:DNSymbolSet) -> Bool
    {
        for i in s1.symbols
        {
            if s2.symbols.contains(i) == false
            {
                return false;
            }
        }
        
        return true;
    }
    
    private func compareSetArray(s1:DNSymbolSet, sets:[DNSymbolSet]) -> Bool
    {
        for s in sets
        {
            if compareSets(s1: s1, s2: s)
            {
                return true;
            }
        }
        return false;
    }
    
    
    private func generateTestSet() -> DNSymbolDataSet{
        
        //Generate options
        var options:[DNSymbolSet] = []

        while options.count < numOptions
        {

            var newSet:DNSymbolSet = DNSymbolSet(symbols: [randomSymbol(), randomSymbol()]);
            
            while(newSet.symbols[0] == newSet.symbols[1])
            {
                newSet = DNSymbolSet(symbols: [randomSymbol(), randomSymbol()]);
            }
            
            if compareSetArray(s1: newSet, sets: options) == false
            {
                options.append(newSet)
            }
        }

        var choices:[DNSymbolSet] = []

        // generate one incorrect choice
        while choices.count == 0
        {

            var newSet:DNSymbolSet = DNSymbolSet(symbols: [randomSymbol(), randomSymbol()]);
            
            while(newSet.symbols[0] == newSet.symbols[1])
            {
                newSet = DNSymbolSet(symbols: [randomSymbol(), randomSymbol()]);
            }

            if compareSetArray(s1: newSet, sets: options) == false
            {
                choices.append(newSet);
            }
        }

        //And now pick a "correct" choice
        let correctChoice = Int(arc4random_uniform(UInt32(options.count)))
        var correctIndex:Int = 1;
        // 50/50 chance of the correct choice being on the left or right.
        if arc4random_uniform(2) > 0
        {
            choices.insert(options[correctChoice], at: 0);
        }
        else
        {
            choices.append(options[correctChoice]);
            correctIndex = 2;
        }
        

        return DNSymbolDataSet(options: options, choices: choices, correct: correctIndex);
    }
    
    
    public override func saveData(testSession: TestSession)
    {
        
        //Get a reference to the application delegate and create a context
        let context = DNDataManager.backgroundContext

        
       
        let sessionNumber = testSession.sessionID;
        let participantId = DNDataManager.sharedInstance.participantId!;
        
        let testObj = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestData", into: context) as! SymbolsTestData

        for i in 0..<testSets.count
        {
            let testSection = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestSectionData", into: context) as! SymbolsTestSectionData 

            if i < selected.count
            {
                let set = testSets[i];
                let selection = selected[i];
                
                let options = NSMutableOrderedSet();
                
                for op in set.options
                {
                    let testItem = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestItem", into: context) as! SymbolsTestItem

                    for i in op.symbols
                    {
                        let glyph = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestGlyph", into: context) as! SymbolsTestGlyph
                        glyph.symbol = "symbol_\(i)"
                        
                        glyph.testItem = testItem;
                        
                    }
                    options.add(testItem);
                }
                
                testSection.options = options.copy() as! NSOrderedSet;
                
                
                let choices = NSMutableOrderedSet();
                for ch in set.choices
                {
                    let testItem = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestItem", into: context) as! SymbolsTestItem

                    for i in ch.symbols
                    {
                        let glyph = NSEntityDescription.insertNewObject(forEntityName: "SymbolsTestGlyph", into: context) as! SymbolsTestGlyph
                        glyph.symbol = "symbol_\(i)"
                        glyph.testItem = testItem;
                    }
                    choices.add(testItem);
                }
                
                testSection.choices = choices.copy() as! NSOrderedSet;
                
                testSection.correct = Int32(set.correct)
                testSection.selected = Int32(selection.choice)
                testSection.selectionTime = selection.timeTouched.timeIntervalSince(self.startTime);
                testSection.appearanceTime = testSetAppearanceTime[i].timeIntervalSince(self.startTime);
                
                testSection.test = testObj;
            }
        }
        
        testObj.date = self.startTime as NSDate
        testObj.testSession = testSession;
        let dict = testObj.dictionaryOfAttributes(excludedKeys: [])
        let data = try! JSONSerialization.data(withJSONObject: dict , options: .prettyPrinted)
        
//        print(String.init(data: data, encoding: .utf8) ?? "")

        DNDataManager.save();
    }
    
    private func randomSymbol() -> Int
    {
        return Int(arc4random_uniform(UInt32(numSymbols)));
    }
}



public struct DNSymbolSet {

    var symbols:[Int]

}

public struct DNSymbolDataSet{

    var options:[DNSymbolSet]
    var choices:[DNSymbolSet]
    var correct:Int
}
public struct DNSymbolInputData {
    var choice:Int
    var timeTouched:NSDate
    var referenceTime:NSDate
    var touchLocation:CGPoint = CGPoint.zero

    init() {
        choice = -1
        timeTouched = NSDate()
        referenceTime = NSDate()
    }
}
