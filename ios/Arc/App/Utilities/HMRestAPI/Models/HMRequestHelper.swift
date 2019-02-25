//
// HMRequestHelper.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation



public extension Encodable {
	public func encode(outputFormatting: JSONEncoder.OutputFormatting? = [.prettyPrinted, .sortedKeys]) -> Data? {
        do {
            let encoder = JSONEncoder()
			if let outputFormatting = outputFormatting {
            	encoder.outputFormatting = outputFormatting
			}
            return try encoder.encode(self)
        } catch {
            print(error)
            return nil
        }
        
    }
	public func toString(outputFormatting:JSONEncoder.OutputFormatting? =  [.prettyPrinted, .sortedKeys]) -> String {
		
		guard let data = self.encode(outputFormatting: outputFormatting), let string = String(data: data, encoding: .utf8) else {
            return ""
        }
        return  string
    }
}
public extension Data {
    public func decode<T:Decodable>() -> T? {
        do {
            return try JSONDecoder().decode(T.self, from: self)
        } catch {
            print(error)
            return nil
        }
        
    }
}
//T is the request type, S is the type returned from the request
public enum HMAPIRequest<T:Codable,S:Codable> {
    case get(String)
    case post(String)
    case put(String)
    case patch(String)
    case delete(String)
    public func execute(data:T? = nil, completion:((URLResponse?, S?, HMFault?)->Void)? = nil) {
        self.execute(data: data, params: nil, completion: completion)
    }
    public func execute(data:T? = nil, params:[String:String]? = nil, completion:((URLResponse?, S?, HMFault?)->Void)? = nil) {
        var request:HMRequest<S>?
        var method:BackendRequestMethod = .get
        var requestUrl:String = ""
        switch self {
        case let .get(url):
            method = .get
            requestUrl = url
            break
        case let .post(url):
            method = .post
            requestUrl = url

            break
        case let .put(url):
            method = .put
            requestUrl = url

            break
        case let .patch(url):
            method = .patch
            requestUrl = url

            break
        case let .delete(url):
            method = .delete
            requestUrl = url

            break
            
        }
        request = createRequest(method: method, url: requestUrl, data: data, params: params, completion: completion)

        request?.execute()

    }
    
    
    
    public func createRequest(method:BackendRequestMethod, url:String, data:T? = nil, params:[String:String]? = nil, completion:((URLResponse?, S?, HMFault?)->Void)? = nil) -> HMRequest<S> {
        var request = HMRequest<S>(method: method, endPoint: url)
        
        request.headers = HMAPI.authHeaders()
        if let params = params {
            request.params = params
        }
        
        if let data = data {
            request.data = data.encode()
        
        }
        request.success = {
            response, retval, err in
            
            if let c = completion {
                c(response, retval, err)
            }
        }
        
        request.failure = { response in
            
        }
        
        request.unhandledFailure = {
            err in
            dump(err)
        }
        return request
    }
    
    
}
