//
// AsyncAwait.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



import Foundation

public typealias AsyncAwait<Request, Response> = (Request, @escaping (Response)->()) -> ()


public struct Async<Request, Response> {
    
    public let action:AsyncAwait<Request, Response>
    
   
    public init(_ action: @escaping AsyncAwait<Request, Response>){
    
        self.action = action
    }
    
}


public struct Await<Request, Response> {
    
    public let dispatch = DispatchGroup()
    public let action:Async<Request, Response>
    
    
    public init(_ action:@escaping AsyncAwait<Request, Response>) {
        
        self.action = Async(action)
    }
    
   
    public func execute(_ input:Request) -> Response {
    
        var response:Response!
        
        dispatch.enter()
        
        action.action(input) {res  in
        
            response = res
            
            self.dispatch.leave()
        }
       
        dispatch.wait()
        
        return response
    }
    
}
