//
// HMRequest.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

//This is to enforce a statically typed return from the api
public enum HMRequestResource:String
{
    case dashboard = ""
    
}
public struct HMRequest<S:Codable> : BackendRequest {
    
	public enum StatusCode {
		
	}

    
    
    
    public typealias SuccessHandler = (URLResponse?,S?, HMFault?) -> ()
    public typealias RequestFailureHandler = (Error, URLResponse?) -> ()
    
    public var task:URLSessionDataTask?
    
    public var endPoint:String
    public var method: BackendRequestMethod
    public var headers: [String : String]?
    fileprivate var _params: [String: String] = [:]
    public var response: HTTPURLResponse?
    public var data: Data?
    
    public var success:SuccessHandler?
    public var failure:RequestFailureHandler?
    public var unhandledFailure:FailureHandler?
    
    public init(method:BackendRequestMethod ,endPoint:String, resource:HMRequestResource = .dashboard){
        self.method = method
        if !endPoint.contains("https")
        {
            self.endPoint = resource.rawValue + endPoint
        }
        else
        {
            self.endPoint = endPoint;
        }
        
        // if the endpoint contains query parameters, we need to parse those and put them
        // into the params variable.
        if endPoint.contains("?")
        {
            let endPoints = endPoint.components(separatedBy: "?");
            self.endPoint = endPoints[0];
            
            if endPoint.count > 1
            {
                let queries = endPoints[1].components(separatedBy: "&");
                for query in queries
                {
                    let p = query.components(separatedBy: "=");
                    if p.count == 2
                    {
                        self.params[p[0]] = p[1].removingPercentEncoding;
                    }
                }
            }
            
        }
        
        //client_id is in every request
    }
    
    @discardableResult public static func performRequest(method:BackendRequestMethod, endPoint:String, completion: SuccessHandler?) -> HMRequest<S>
    {
        var request = HMRequest<S>(method: method, endPoint: endPoint);
        
        request.headers = HMAPI.defaultHeaders()
        request.success = {
            response, retval, err in
            
            if let c = completion {
                c(response, retval, err)
            }
        }
        
        
        
        request.unhandledFailure = {
            err, response in
            dump(err)
			if let c = completion {
				c(response, nil, nil)
			}
        }
        request.execute()
        return request;
    }
}
public extension HMRequest {
    var params: [String : String] {
        get {
            var p = _params
			
			//If one is set don't overwrite it. Migration uses this.
			if p["device_id"] == nil {
            	p["device_id"] = "\(HMAPI.shared.clientId ?? "")"
			}
            return p
            
        }
        set {
            _params = newValue
        }
    }
    func didSucceed(with data: Data, response:URLResponse?) {
        //        print(String(data: data, encoding: String.Encoding.utf8))
        let decoder = JSONDecoder()
        var responseBody:S?
        var err:HMFault?
		
		
        guard data.count > 0 else {
            success?(response,responseBody,err)
            return
        }
		
        if let body = data as? S
        {
            responseBody = body
            success?(response, responseBody, err);
            return;
        }
        
        do {
            err = try decoder.decode(HMFault.self, from:data)
            
        } catch {
            do {
                responseBody = try decoder.decode(S.self, from:data)
                
            } catch {
                print("error decoding data for \(String(describing:response?.url))");
                unhandledFailure?(error, response)
                return
            }
        }
        success?(response,responseBody,err)
        
    }
    func didFail(with error: Error, response:URLResponse?) {
        if let unhandledFailure = unhandledFailure {
            unhandledFailure(error, response)
        }
    }
}
