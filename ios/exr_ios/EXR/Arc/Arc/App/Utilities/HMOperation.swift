//
// HMOperation.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import Foundation

public class HMOperation {
    public enum ConcurrencyMode {
        case serial
        case multi(Int)
        case unlimited
    }
    public enum OperationStatus {
        case complete
        case failed
        case cancelled
    }
    var complete:Bool = false
    let dispatchGroup:DispatchGroup = DispatchGroup()
    let queue:OperationQueue = OperationQueue()
    public init(_ concurrencyMode:ConcurrencyMode = .unlimited) {
        switch concurrencyMode {
        case .serial:
            queue.maxConcurrentOperationCount = 1
        case .multi(let opCount):
            queue.maxConcurrentOperationCount = opCount
        case .unlimited:
            queue.maxConcurrentOperationCount = -1
            
        }
    }
    public func sync(operations:[()->OperationStatus]) {
        
        
        print("performing tasks")
        var status:OperationStatus = .complete
        for op in operations {
            guard status == .complete else {
                print("failed")
                break
            }
            dispatchGroup.enter()
            queue.addOperation {
                status = op()
                print(status)
                self.dispatchGroup.leave()
                
            }
            dispatchGroup.wait()
        }
        dispatchGroup.wait()
        complete = true
        print("completed")
        
    }
    public func async(operations:[()->Void]) {
        
        
        print("performing tasks")
        for op in operations {
            
            dispatchGroup.enter()
            queue.addOperation {
                op()
                self.dispatchGroup.leave()
            }
        }
        dispatchGroup.wait()
        complete = true
        
    }
}
