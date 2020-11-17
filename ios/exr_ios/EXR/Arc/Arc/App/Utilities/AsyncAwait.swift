//
// AsyncAwait.swift
//


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
