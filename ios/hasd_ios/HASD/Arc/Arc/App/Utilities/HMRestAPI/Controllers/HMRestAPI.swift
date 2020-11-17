//
// HMRestAPI.swift
//



import Foundation
public typealias SuccessHandler = () -> Void
public typealias FailureHandler = (Error, URLResponse?) -> Void

public enum BackendRequestMethod: String {
    case get = "GET", post = "POST", put = "PUT", delete = "DELETE", patch = "PATCH"
}
public enum HMRestAPIError : Error {
	case noResponse
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
struct RequestToken:Hashable {
	let url:URL
	let data:Data?
}
open class HMRestAPI : NSObject, URLSessionDelegate, URLSessionTaskDelegate {
    static public let shared = HMRestAPI()
    public var baseUrl:URL! = nil
    public var session:URLSession?
    public var blackHole:Bool = false
	typealias completionHandler = (Data, URLResponse?) -> Void
	typealias failureHandler = (Error, URLResponse?) -> Void
	var tasks = [RequestToken : [(completionHandler, failureHandler)]]()
	
	override public init() {
        super.init()
        session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
        
    }
    
    
    public func setBaseURL(url:String){
        
        baseUrl = URL(string: url)
    }
    public func execute(backendRequest: BackendRequest) -> URLSessionDataTask? {
        HMLog("Executing------------------------------")
        HMLog(backendRequest.endPoint)

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
        guard let url:URL = (baseUrl == nil || endPoint.contains(baseUrl.path)) ? URL(string: endPoint) : URL(string: endPoint, relativeTo: baseUrl) else {
            print("Error trying to prepare url: \(backendRequest)");
            return nil
        }
        
        var urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: true)
        
        var urlRequest = URLRequest(url: url, cachePolicy: .reloadIgnoringCacheData, timeoutInterval: 10.0)
        
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
		var task : URLSessionDataTask?
		
        DispatchQueue.main.async {
			let token = RequestToken(url: url, data: backendRequest.data)
			if self.tasks.keys.contains(token) {
				self.tasks[token]?.append((backendRequest.didSucceed, backendRequest.didFail))
			} else {
				//0 = to the successHandler for this tuple, 1 = to the failure handler
				//completionHandlers.forEach {
				//		$0.0(data, response)
				//}
				
				//completionHandlers.forEach {$0.1(error, response)}

				self.tasks[token] = [(backendRequest.didSucceed, backendRequest.didFail)]
	
				
				task = self.session!.dataTask(with: urlRequest) { (data, response, error) in
					DispatchQueue.main.async {
						
						
						defer {
							self.tasks.removeValue(forKey: token)
						}
						
					guard let completionHandlers = self.tasks[token] else {return}
						
						if let error = error {
							HMLog("Failing \(completionHandlers.count) responses")
							completionHandlers.forEach {$0.1(error, response)}

							return
						}
                        guard let data = data, let _ = response else {
							completionHandlers.forEach{$0.1(HMRestAPIError.noResponse, nil)}
							
							return
						}
						HMLog("\(url)\n\n")
						HMLog("Decoded Response---------------------------------")
						do {
							let obj = try JSONDecoder().decode(HMResponse.self, from: data).toString()

								HMLog(String(bytes: data, encoding: .utf8) ?? "");
								
						} catch {
                            print(completionHandlers.count)
							//HMLog(error.localizedDescription)
							completionHandlers.forEach {
								$0.1(error, response)
								
							}
							return
						}
						
						
						HMLog("Responding to \(completionHandlers.count) \(token.url) handlers")
						completionHandlers.forEach {
							$0.0(data, response)
						}
						
					}
				}
				task?.resume()
				
			}
		}
		
		
		
		return task
		
		
    }
	public func isWaitingForTask(named:[String]) -> Bool{
		for task in tasks.keys {
			if named.contains( task.url.lastPathComponent) {
				print("is waiting for:\(task)")
				return true
			}
			
		}
		return false
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
