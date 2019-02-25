//
// SymbolsTestController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
open class SymbolsTestController : TestController<SymbolsTestResponse> {
    public var symbolDefinitions:[Int:String]?
    
    public func setSymbolDefinitions(definitions:[Int:String]){
        symbolDefinitions = definitions
        
    }
    public func generateTest(numSections:Int, numSymbols:Int) -> SymbolsTest {
        var test = SymbolsTest()
        if numSymbols <= 2 || numSections <= 0 {
            fatalError("Invalid Test Input.")
        }
        for _ in 0..<numSections {
            test.sections.append(generateTestSet(numOptions: 3, numSymbols: numSymbols))
        }
        return test
    }
    // if two sets have the same values, in any order, the compare function will return true.
    // so [1,2] and [2,1] would be considered the "same" for our purposes.
    private func compareSets(s1:SymbolsTest.SymbolSet, s2:SymbolsTest.SymbolSet) -> Bool
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
    
    private func compareSetArray(s1:SymbolsTest.SymbolSet, sets:[SymbolsTest.SymbolSet]) -> Bool
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
    private func randomSymbol(_ numberOfSymbols:Int) -> Int
    {
        return Int.random(in: 0..<numberOfSymbols);
    }
    private func generateTestSet(numOptions:Int, numSymbols:Int) -> SymbolsTest.Section{
        
        //Generate options
        var options:[SymbolsTest.SymbolSet] = []
        
        while options.count < numOptions
        {
            
            var newSet:SymbolsTest.SymbolSet = SymbolsTest.SymbolSet(symbols: [randomSymbol(numSymbols), randomSymbol(numSymbols)]);
            
            while(newSet.symbols[0] == newSet.symbols[1])
            {
                newSet = SymbolsTest.SymbolSet(symbols: [randomSymbol(numSymbols), randomSymbol(numSymbols)]);
            }
            
            if compareSetArray(s1: newSet, sets: options) == false
            {
                options.append(newSet)
            }
        }
        
        var choices:[SymbolsTest.SymbolSet] = []
        
        // generate one incorrect choice
        while choices.count == 0
        {
            
            var newSet:SymbolsTest.SymbolSet = SymbolsTest.SymbolSet(symbols: [randomSymbol(numSymbols), randomSymbol(numSymbols)]);
            
            while(newSet.symbols[0] == newSet.symbols[1])
            {
                newSet = SymbolsTest.SymbolSet(symbols: [randomSymbol(numSymbols), randomSymbol(numSymbols)]);
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
        
        
        return SymbolsTest.Section(options: options, choices: choices, correct: correctIndex);
    }
    private func convertSymbols(symbolSet:Array<SymbolsTest.SymbolSet>) -> Array<Array<String>>{
        return symbolSet.compactMap({ (set) -> Array<String> in
            var values:Array<String> = []
            for symbol in set.symbols {
                if let newSymbols = symbolDefinitions,
                    let newSymbol = newSymbols[symbol] {
                    values.append("\(newSymbol)")
                    
                } else {
                    values.append("\(symbol)")
                    
                }
            }
            return values
        })
    }
    public func createResponse(withTest symbolsTest:SymbolsTest, id:String = UUID().uuidString) -> String {
        var response = SymbolsTestResponse(id:id)
        
        for item in symbolsTest.sections {
            var section = SymbolsTestResponse.Section()
            section.correct = item.correct
            section.options = convertSymbols(symbolSet: item.options)
            section.choices = convertSymbols(symbolSet: item.choices)
            
            
            response.sections.append(section)
        }
        return save(id:id, obj:response).id!
    }
	
	public func get(questionCount id:String) -> Int {
		do {
			let response = try get(response: id)
			return response.sections.count
			
			
		} catch {
			delegate?.didCatch(errors: error)
		}
		return 0
	}
    public func get(question:Int, id:String) -> SymbolsTestResponse.Section? {
        do {
            let response = try get(response: id)
            if (0..<response.sections.count).contains(question) {
                return response.sections[question]
            } else {
                fatalError("Invalid index suplied")
            }
            
        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil
    }
    public func mark(timeTouched index:Int, date:Date, id:String) -> SymbolsTestResponse? {
        do {
            let startDate = try get(testStartTime:id)
            let timeSince = date.timeIntervalSince(startDate)
            var response = try get(response: id)
            if (0..<response.sections.count).contains(index) {
                response.sections[index].selection_time = timeSince
            } else {
                fatalError("Invalid index suplied")
            }
            return save(id: id, obj: response)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        return nil
    }
    public func mark(appearanceTimeForQuestion index:Int, id:String) -> SymbolsTestResponse? {
        do {
            let timeSince = try get(timeSinceStart: id)

            var response = try get(response: id)
            if (0..<response.sections.count).contains(index) {
                response.sections[index].appearance_time = timeSince
            } else {
                fatalError("Invalid index suplied")
            }
            return save(id: id, obj: response)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        return nil
    }
    public func set(choice:Int, forQueston index: Int, id:String) -> SymbolsTestResponse? {
        do {
            

            var response = try get(response: id)
            if  (0..<response.sections.count).contains(index) {
                response.sections[index].selected = choice
                return save(id: id, obj: response)

            } else {
                fatalError("Invalid index suplied")
            }
            
        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil
    }
    
}
