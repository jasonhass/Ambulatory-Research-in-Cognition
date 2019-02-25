//
// TestManagerController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
open class TestController<T:HMTestCodable> : MHController {
    public enum ResponseError : Error {
        case invalidInput
        case invalidQuestionIndex
        case testNotStarted
    }
    
    public func get(response id:String) throws -> T {
        return try get(id: id)
    }
    
    public func get(testStartTime id:String) throws -> Date {
        let test = try get(response: id)
        guard let testDate = test.date else {
            throw TestController.ResponseError.testNotStarted
        }
        
        return Date(timeIntervalSince1970: testDate)
        
    }
    public func start(test id:String) -> T? {
        do {
            var test = try get(response: id)
            test.date = Date().timeIntervalSince1970
            return save(id: id, obj: test)
            
        } catch {
            
            delegate?.didCatch(errors: error)
        }
        
        return nil
        
    }
    public func get(timeSinceStart id:String) throws -> TimeInterval {
        let testDate = try get(testStartTime: id)
        return Date().timeIntervalSince(testDate)
        
    }
}
