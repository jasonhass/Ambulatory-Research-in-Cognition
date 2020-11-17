//
// PricesTestController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

public enum ResponseError : Error {
    case invalidInput
    case invalidQuestionIndex
    case testNotStarted
}
open class PricesTestController : TestController<PriceTestResponse> {
    
    private var loadedTestFile:Array<Array<PriceTest.Item>>?
    private var loadedTestFileName:String?;
    public func loadTest(index:Int, file:String) -> PriceTest {
        
        do {
            if self.loadedTestFile == nil || self.loadedTestFileName != file
            {
				guard let asset = NSDataAsset(name: file) else {
					fatalError("Missing asset named \(file)")
				}
                let tests = try JSONDecoder().decode([[PriceTest.Item]].self, from: asset.data)
                self.loadedTestFile = tests;
                self.loadedTestFileName = file;
            }
            
            guard let tests = self.loadedTestFile else {
                fatalError("Failed to load tests from file \(file)");
            }
            
			let modIndex = index % tests.count
            let items = tests[modIndex]
            return PriceTest(items: items)
        } catch {
            fatalError("Invalid asset format: \(file) - Error: \(error.localizedDescription)")
            
        }
    }
    
    
    
    
    
    
    
    //create a response
    public func createResponse(id:String = UUID().uuidString, withTest priceTest:PriceTest) -> String {
        let response = self.createPriceTestResponse(id: id, withTest: priceTest);
        return save(id: id, obj: response).id!
    }
    
    public func createPriceTestResponse(id:String = UUID().uuidString, withTest priceTest:PriceTest) -> PriceTestResponse
    {
        var response = PriceTestResponse(id: id)
        
        for item in priceTest.items {
            var choice = PriceTestResponse.Choice(item: item)
            choice.correct_index = Int.random(in: 0...1)
            response.sections.append(choice)
        }
        
        return response;
    }
    
    //read in a response
	public func get(testCount id:String) -> Int {
		do {
			let response = try get(response: id)
			return response.sections.count
			
		} catch {
			delegate?.didCatch(errors: error)
		}
		return 0
	}
   
 
    public func get(question:Int, id:String) -> PriceTestResponse.Choice? {
        do {
        let response = try get(response: id)
        if question < response.sections.count {
            return response.sections[question]
        }
        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil
    }
    
	public func get(correctOptionforQuestion index:Int, id:String) -> Int? {
        if let question = get(question: index, id: id) {
            
			return question.correct_index
        }
        return nil
    }
	
    public func get(option:Int, forQuestion index:Int, id:String) -> String? {
        if let question = get(question: index, id: id) {
            
            switch question.correct_index {
            case 0:
                if option == 0 {
                    return question.price
                } else {
                    return question.alt_price
                }
            case 1:
                if option == 0 {
                    return question.alt_price
                } else {
                    return question.price
                }
            default:
                fatalError("There are only two options.")
                
            }
        }
        return nil
    }
	
	/// Marks the display time for a question, used to track the users
	/// response time in seconds
	/// - Parameter id: The id of the survey being taken
	/// - Parameter index: the index of the question in the survey being taken.
    public func mark(questionDisplayTime id:String, index:Int) -> PriceTestResponse? {
        do {
            
            var response = try get(response: id)
            let startDate = try get(testStartTime: id)
            response.sections[index].question_display_time = Date().timeIntervalSince(startDate)
            
            return save(id: id, obj: response)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        return nil

	}
	/// Tracks the time that the user commited to touching a value, when they release
	/// their touch on the button their confirmation time will be when they first touched
	/// the item.
	/// - Parameter id: The id of the survey being taken
	/// - Parameter index: the index of the question in the survey being taken.
    public func mark(timeTouched id:String, index:Int) -> PriceTestResponse? {
        do {
            
            var response = try get(response: id)
            let timeSince = try get(timeSinceStart: id)
            response.sections[index].selection_time = timeSince
            return save(id: id, obj: response)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        return nil
        
    }
	
	/// The time that the question was displayed to the user
	/// - Parameter id: The id of the survey being taken
	/// - Parameter index: the index of the question in the survey being taken.
    public func mark(stimulusDisplayTime id:String, index:Int) -> PriceTestResponse? {
        do {
            
            var response = try get(response: id)
            let timeSince = try get(timeSinceStart: id)
            response.sections[index].stimulus_display_time = timeSince
            return save(id: id, obj: response)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        return nil
        
    }
    public func set(goodPrice:Int, id:String, index:Int) -> PriceTestResponse? {
        do {
            var response = try get(response: id)
            response.sections[index].good_price = goodPrice
            return save(id: id, obj: response)
            
        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil
        
    }
    
    public func set(choice:Int, id:String, index:Int) -> PriceTestResponse? {
        do {
            var response = try get(response: id)
            let timeSince = try get(timeSinceStart: id)
            //Choice of 0 == price, 1 == alt
            response.sections[index].selected_index = choice
            response.sections[index].selection_time = timeSince
            return save(id: id, obj: response)

        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil

    }
    
    
    
    
}
