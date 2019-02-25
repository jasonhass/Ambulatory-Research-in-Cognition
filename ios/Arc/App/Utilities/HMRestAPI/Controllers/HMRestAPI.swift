//
// HMRestAPI.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
public typealias SuccessHandler = () -> Void
public typealias FailureHandler = (Error) -> Void

public enum BackendRequestMethod: String {
    case get = "GET", post = "POST", put = "PUT", delete = "DELETE", patch = "PATCH"
}

public protocol BackendRequest {
    var endPoint: String { get }
    var method: BackendRequestMethod { get }
    var headers: [String: String]? { get set}
    var data: Data? { get set }
    var params: [String: String] { get set}
    var task:URLSessionDataTask? {get set}
    func didSucceed(with data: Data, response:URLResponse?)
    func didFail(with error: Error, response:URLResponse?)
    mutating func execute()
    func cancel()
}

public struct BackendRequestHeader {
    static public let contentTypeOctetStream = ["Content-Type": "application/octet-stream"]
    static public let acceptOctetStream = ["Accept": "application/octet-stream"]
    
    static public let contentTypeJson = ["Content-Type": "application/json"]
    static public let acceptJson = ["Accept": "application/json"]
}
public extension BackendRequest {
    func cancel() {
        task?.cancel()
    }
    mutating func execute() {
        
        task = HMRestAPI.shared.execute(backendRequest: self)
        
    }
    mutating func execute(_ baseUrl:String) {
        HMRestAPI.shared.setBaseURL(url: baseUrl)
        task = HMRestAPI.shared.execute(backendRequest: self)
    }
}
open class HMRestAPI : NSObject, URLSessionDelegate, URLSessionTaskDelegate {
    static public let shared = HMRestAPI()
    public var baseUrl:URL! = nil
    public var session:URLSession?
    public var blackHole:Bool = false
	override public init() {
        super.init()
        session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
        
    }
    
    
    public func setBaseURL(url:String){
        
        baseUrl = URL(string: url)
    }
    public func execute(backendRequest: BackendRequest) -> URLSessionDataTask? {
//        HMLog("Executing------------------------------")
//        HMLog(backendRequest.endPoint)
//
//        HMLog(String(data: backendRequest.data ?? Data(), encoding: .utf8) ?? "")
        if blackHole {
            backendRequest.didSucceed(with: Data(), response: HTTPURLResponse(url: URL(string: backendRequest.endPoint)!, statusCode: 200, httpVersion: nil, headerFields: nil))
            return nil
        }
        var charset = CharacterSet.urlPathAllowed;
        charset.remove(charactersIn: "+@");
        guard let endPoint = backendRequest.endPoint.addingPercentEncoding(withAllowedCharacters: charset)
            else {
                print("Error trying to url escape endpoint: \(backendRequest)");
                return nil;
        }
        let url:URL = endPoint.contains(baseUrl.path) ? URL(string: endPoint)! : URL(string: endPoint, relativeTo: baseUrl)!
        
        var urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: true)
        
        var urlRequest = URLRequest(url: url, cachePolicy: .reloadIgnoringCacheData, timeoutInterval: 60.0)
        
        urlRequest.httpMethod = backendRequest.method.rawValue
        
        if let data = backendRequest.data {
            urlRequest.httpBody = data
        }
        
        if let headers = backendRequest.headers {
            for (key, value) in headers {
                urlRequest.addValue(value, forHTTPHeaderField: key)
            }
        }
        

        var queries:Array<URLQueryItem> = []
        
        for (key,value) in backendRequest.params {
            guard let _ = urlRequest.url else {
                break
            }
            queries.append(URLQueryItem(name: key, value: value))
        }
        urlComponents?.queryItems = queries
        
        //Update request with query
        urlRequest.url = urlComponents?.url
        
       
        
        let task = session!.dataTask(with: urlRequest) { (data, response, error) in
            
            guard let data = data, let r = response, error == nil else {
                if let error = error {
                    backendRequest.didFail(with: error, response: response)
                }
                return
            }
//            HMLog("Reponse---------------------------------")
//            
//            HMLog(response?.url?.absoluteString ?? "")
//            HMLog(String(data: data, encoding: .utf8) ?? "")
//            HMLog("Decoded Response---------------------------------")
//
//            do {
//                HMLog(try JSONDecoder().decode(HMResponse.self, from: data).toString());
//            } catch {
//                HMLog(error.localizedDescription)
//            }
			
			backendRequest.didSucceed(with: data, response: r)
            
        }
        
        task.resume()
        return task
    }
//	public func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
//		let credential = URLCredential(trust: challenge.protectionSpace.serverTrust!)
//		completionHandler(URLSession.AuthChallengeDisposition.useCredential, credential)
//		
//		//        if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust {
//		//            if challenge.protectionSpace.host == baseUrl.absoluteString {
//		//                let credential = URLCredential(trust: challenge.protectionSpace.serverTrust!)
//		//                completionHandler(URLSession.AuthChallengeDisposition.useCredential, credential)
//		//            }
//		//        }
//	}
    
    
}
