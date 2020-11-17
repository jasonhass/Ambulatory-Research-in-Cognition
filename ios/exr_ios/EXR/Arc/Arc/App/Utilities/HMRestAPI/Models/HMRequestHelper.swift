//
// HMRequestHelper.swift
//



import Foundation



public extension Encodable {
    func encode(outputFormatting: JSONEncoder.OutputFormatting? = [.prettyPrinted, .sortedKeys]) -> Data? {
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
    func toString(outputFormatting:JSONEncoder.OutputFormatting? =  [.prettyPrinted, .sortedKeys]) -> String {
		
		guard let data = self.encode(outputFormatting: outputFormatting), let string = String(data: data, encoding: .utf8) else {
            return ""
        }
        return  string
    }
}
public extension Data {
    func decode<T:Decodable>() -> T? {
        do {
            return try JSONDecoder().decode(T.self, from: self)
        } catch {
            print(error)
            return nil
        }
        
    }
    mutating func appendString(value:String?) {
        guard let data = value?.data(using: String.Encoding.utf8) else {
            return
        }
        self.append(contentsOf: data)
    }
}
//T is the request type, S is the type returned from the request
public enum HMAPIRequest<T:Codable,S:Codable> {
    case get(String)
    case post(String)
    case put(String)
    case patch(String)
    case delete(String)
    public func execute(data:T? = nil, completion:((URLResponse?, S?, Error?)->Void)? = nil) {
        self.execute(data: data, params: nil, completion: completion)
    }
    public func execute(data:T? = nil, params:[String:String]? = nil, completion:((URLResponse?, S?, Error?)->Void)? = nil) {
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
	public func executeZip(data:Data, params:[String:String]? = nil, completion:((URLResponse?, S?, Error?)->Void)? = nil) {
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
		request = createZipRequest(method: method, url: requestUrl, data: data, params: params, completion: completion)
		
		request?.execute()
		
	}
    public func executeMultipart(data:Data, params:[String:String]? = nil, completion:((URLResponse?, S?, HMFault?)->Void)? = nil) {
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
        request = createMultiPartRequest(method: method, url: requestUrl, data: data, params: params, completion: completion)
        
        request?.execute()
        
    }
    
    
    public func createRequest(method:BackendRequestMethod, url:String, data:T? = nil, params:[String:String]? = nil, completion:((URLResponse?, S?, Error?)->Void)? = nil) -> HMRequest<S> {
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
        
        request.failure = { err, response in
			if let c = completion {
				c(response, nil, nil)
			}
        }
        
        request.unhandledFailure = {
            err, response in
            //dump(err)
			if let c = completion {
				c(response, nil, nil)
			}
        }
        return request
    }
	 public func createZipRequest(method:BackendRequestMethod, url:String, data:Data, params:[String:String]? = nil, boundary:String = UUID().uuidString, completion:((URLResponse?, S?, Error?)->Void)? = nil) -> HMRequest<S> {
		 var request = HMRequest<S>(method: method, endPoint: url)
		request.headers = HMAPI.authHeaders()
		request.headers?["Content-Type"] = "application/zip"
		
		request.data = data
		
		request.success = {
			response, retval, err in
			
			if let c = completion {
				c(response, retval, err)
			}
		}
		
		request.failure = { err, response in
			if let c = completion {
				c(response, nil, err)
			}
		}
		
		request.unhandledFailure = {
			err, response in
			//dump(err)
			if let c = completion {
				c(response, nil, err)
			}
		}
		return request
		
	}
    public func createMultiPartRequest(method:BackendRequestMethod, url:String, data:Data, params:[String:String]? = nil, boundary:String = UUID().uuidString, completion:((URLResponse?, S?, HMFault?)->Void)? = nil) -> HMRequest<S> {
        var request = HMRequest<S>(method: method, endPoint: url)
        
        request.headers = HMAPI.authHeaders()
        request.headers?["Content-Type"] = "multipart/form-data; boundary=\(boundary)"
        
        request.data = createMultiPartBody(parameters: params,
                                           fileName: "image.png",
                                          data: data,
                                          mimeType: "image/png", boundary: boundary)
        request.success = {
            response, retval, err in
            
            if let c = completion {
                c(response, retval, err)
            }
        }
        
        request.failure = { err, response in
			if let c = completion {
				c(response, nil, nil)
			}
        }
        
        request.unhandledFailure = {
            err, response in
            dump(err)
			if let c = completion {
				c(response, nil, nil)
			}
        }
        return request
        
    }
    public func createMultiPartBody(parameters:[String:String]?, fileName:String, data:Data, mimeType:String, boundary:String) -> Data {
        var body = Data();
        
        if let params = parameters {
            for (key, value) in params {
                body.appendString(value: "--\(boundary)\r\n")
                body.appendString(value:"Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
                body.appendString(value:"\(value)\r\n")
            }
        }
        
        body.appendString(value: "--\(boundary)\r\n")
        body.appendString(value: "Content-Disposition: form-data; name=\"file\"; filename=\"\(fileName)\"\r\n")
        body.appendString(value: "Content-Type: \(mimeType)\r\n\r\n")
        body.append(contentsOf: data)
        body.appendString(value: "\r\n")
        
        body.appendString(value: "--\(boundary)--\r\n")

        return body

    }
    
}
