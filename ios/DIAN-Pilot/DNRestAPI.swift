/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import MobileCoreServices
import SystemConfiguration
import CoreData

var ARC_VERSION_INFO_KEY = "ARC_VERSION"

typealias ServiceResponse = (_ data:Data?, _ statusCode:Int, _ response: URLResponse?, _ error: Error?) -> Void


class DNRestAPI: NSObject {


    
    #if QA
        let baseURL = "[URL GOES HERE]";   // QA link
    #elseif TU
        let baseURL = "[URL GOES HERE]"; // Prod link
    #elseif EXR_QA
        let baseURL = "[URL GOES HERE]";
    #elseif EXR
        let baseURL = "[URL GOES HERE]";
    #elseif CS
        let baseURL = "[URL GOES HERE]"; // local simple server
        let AuthToken = "[AUTH TOKEN GOES HERE]"; // simple server token
    #else //DBG
        let baseURL:String = "";
        let AuthToken:String = "";
    #endif
    
    var PRINT_REQUESTS:Bool = false;
    var PRINT_A_LOT_OF_STUFF:Bool = false;
    
    var lastRequest: URLRequest?;
    var lastResponse: HTTPURLResponse?;
    var lastError: Error?;
    lazy var sharedDelegate:DNRestSessionDelegate = DNRestSessionDelegate();
    static let shared = DNRestAPI();
    lazy var sharedSession:URLSession = URLSession(configuration: URLSessionConfiguration.default, delegate: self.sharedDelegate, delegateQueue: nil);
    var imageDataTasks: Dictionary<NSValue, URLSessionDataTask> = Dictionary();
    
    var minExpirationTime:TimeInterval = 3600; // minimum caching of 1 hour

    //Set this to true if you want the app to save all zip files into the Documents directory, instead of the tmp dir.
    var storeZips:Bool = false;
    var sendData:Bool = true;
    var requireRegistration:Bool = true;
    var sendPing:Bool = true;
    var dontRandomize:Bool = false;
    var skipBaseline:Bool = false;
    //MARK: - URLSession methods
    
    override init() {
        super.init();
        
        self.sharedSession.configuration.httpMaximumConnectionsPerHost = 1;
    }
    
    
    func makeBackgroundRequest(action:String, method:String, parameters:Dictionary<String, Any>?, files:Dictionary<String, String>?, timeout:TimeInterval = 60, onCompletion: @escaping ServiceResponse)
    {
        self.lastRequest = nil;
        self.lastResponse = nil;
        self.lastError = nil;
        
        let bgId = NSUUID().uuidString;
        let sessionConfigutation = URLSessionConfiguration.background(withIdentifier: bgId);
        let sessionDelegate = DNRestSessionDelegate();
        sessionDelegate.invalidateSession = true;
        let session = URLSession(configuration: sessionConfigutation, delegate: sessionDelegate, delegateQueue: nil);
        
        
        let url = self.buildUrl(action: action, method: method, parameters: parameters);
        let request = self.buildRequest(url: url!, method: method, parameters: parameters, files: nil, timeout: timeout);
        self.lastRequest = request as URLRequest;
        
        if PRINT_REQUESTS
        {
            DNLog("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            DNLog("reqeust url: \(String(describing: url))");
            DNLog("method: \(method)");
            DNLog("parameters: \(String(describing: parameters))");
            DNLog("files: \(String(describing: files))");
        }
        
        var task:URLSessionTask?;
        
        
        
        if method == "GET"
        {
            task = session.downloadTask(with: request as URLRequest);
        }
        else
        {
            if files == nil && parameters != nil
            {
                self.addJsonFormBody(request: request, parameters: parameters!);
                task = session.downloadTask(with: request as URLRequest);
            }
            else
            {
                let boundary = self.generateBoundaryString();
                let formBodyPath = self.createFormBody(boundary: boundary, formData: parameters, files: files);
                let fileURL = URL(fileURLWithPath: formBodyPath!);
                request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
                task = session.uploadTask(with: request as URLRequest, fromFile: fileURL);
            }
        }
        
        if let t = task
        {
            DNLog("starting background task #\(t.taskIdentifier) for action \(action)");
            sessionDelegate.taskCompletion[t.taskIdentifier] = onCompletion;
            t.resume();
        }
    }
    
    
    @discardableResult func makeRequest(action:String, method:String, parameters:Dictionary<String, Any>?, files: Dictionary<String, String>?, timeout:TimeInterval = 60, onCompletion: @escaping ServiceResponse) -> URLSessionDataTask
    {
        self.lastRequest = nil;
        self.lastResponse = nil;
        self.lastError = nil;
        
        let session = self.sharedSession;
        
        let url = self.buildUrl(action: action, method: method, parameters: parameters);
        
        let request = self.buildRequest(url: url!, method: method, parameters: parameters, files: files, timeout:timeout);
        
        if method != "GET"
        {
            if files == nil && parameters != nil
            {
                self.addJsonFormBody(request: request, parameters: parameters!);
            }
            else
            {
                self.addFormBodyStream(request: request, parameters: parameters, files: files);
            }
        }
        
        self.lastRequest = request as URLRequest;
        
        if PRINT_REQUESTS
        {
            DNLog("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            DNLog("reqeust url: \(String(describing: url))");
            DNLog("method: \(method)");
            DNLog("parameters: \(String(describing: parameters))");
            DNLog("files: \(String(describing: files))");
            if PRINT_A_LOT_OF_STUFF
            {
                if files == nil && parameters != nil && request.httpBody != nil
                {
                    let paramJSON = String(data: request.httpBody!, encoding: .utf8);
                    DNLog("json parameters: \(String(describing: paramJSON))");
                }
            }
            
        }
        
        let task = session.dataTask(with: request as URLRequest);
        DNLog("starting task #\(task.taskIdentifier) for action \(action)");
        self.sharedDelegate.taskCompletion[task.taskIdentifier] = onCompletion;
        
        
        task.resume();
        
        return task;
    }
    
    
    func sendFile(action:String, background:Bool = true, file:URL, timeout:TimeInterval = 60, delay:TimeInterval = 0, onCompletion: @escaping ServiceResponse)
    {
        if FileManager.default.fileExists(atPath: file.path) == false
        {
            DNLog("Error sending file, file \(file.lastPathComponent) does not exist");
            onCompletion(nil, 500, nil, NSError(domain: "com.happyMedium.Arc", code: 500, userInfo: ["errorString":"file \(file.lastPathComponent) does not exist"]));
            return;
        }
        
        
        self.lastRequest = nil;
        self.lastResponse = nil;
        self.lastError = nil;
        
        let method:String = "POST";
        
        var session:URLSession?;
        var sessionDelegate:DNRestSessionDelegate?;
        if background
        {
            let bgId = NSUUID().uuidString;
            let sessionConfigutation = URLSessionConfiguration.background(withIdentifier: bgId);
            sessionDelegate = DNRestSessionDelegate();
            sessionDelegate?.invalidateSession = true;
            session = URLSession(configuration: sessionConfigutation, delegate: sessionDelegate!, delegateQueue: nil);
        }
        else
        {
            sessionDelegate = self.sharedDelegate;
            session = self.sharedSession;
        }
        
        
        let url = self.buildUrl(action: action, method: method, parameters: nil);
        let request = self.buildRequest(url: url!, method: method, parameters: nil, files: nil, timeout: timeout);
        self.lastRequest = request as URLRequest;
        
        if PRINT_REQUESTS
        {
            DNLog("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            DNLog("reqeust url: \(String(describing: url))");
            DNLog("method: \(method)");
            DNLog("file: \(file)");
        }
        
        var task:URLSessionTask?;
        
        request.setValue("application/octet-stream", forHTTPHeaderField: "Content-Type")
        task = session!.uploadTask(with: request as URLRequest, fromFile: file);
        
        
        let fileOnCompletion:ServiceResponse = {
            data, statusCode, response, error in
            
            //if the MD5 sums don't match, then we should throw some sort of error.
            if let d = data, let serverMD5 = String(data:d, encoding:.utf8)
            {
                let fileData = NSData(contentsOf: file);
                if let fileMD5 = fileData?.MD5(), (fileMD5 as String) == serverMD5
                {
                    //file uploaded successfully
                    onCompletion(data, statusCode, response, error);
                    
                    if self.storeZips == false
                    {
                        do
                        {
                            try FileManager.default.removeItem(at: file);
                        }
                        catch
                        {
                            print("error deleting file: \(error)");
                        }
                    }
                    
                    return;
                }
            }

            //if we get here, the file didn't upload successfully.
            
            if self.storeZips == false
            {
                do
                {
                    try FileManager.default.removeItem(at: file);
                }
                catch
                {
                    print("error deleting file: \(error)");
                }
            }
            onCompletion(data, statusCode, response, NSError(domain: "com.dian.arc", code: statusCode, userInfo: nil));
        }
        
        
        
        if let t = task
        {
            DNLog("starting task #\(t.taskIdentifier) for filename \(file.lastPathComponent)");
            sessionDelegate?.taskCompletion[t.taskIdentifier] = fileOnCompletion;
            t.perform(#selector(t.resume), with: nil, afterDelay: delay);
        }
    }
    
    // MARK: - file upload enqueuing

    // Add a file to the UploadQueue.
    
    func enqueueFile(action:String, background:Bool = true, file:URL)
    {
        DNLog("Enqueuing file \(file.lastPathComponent)");
        let queueItem:UploadQueue = NSManagedObject.createIn(context: DNDataManager.backgroundContext);
        queueItem.action = action;
        queueItem.filepath = file.path;
        queueItem.background = background;
        queueItem.added = NSDate();
        DNDataManager.save();
    }
    
    // Process any existing UploadQueue items who haven't been successfully uploaded yet.
    // On failure, the queueItem.attempts will be incremented.
    // On success, queueItem.success will be marked true. If we're not storing data, it and the file will be deleted from the device.
    
    func processEnqueuedFiles()
    {
        DNLog("Processing enqueued files...");
        let request:NSFetchRequest<UploadQueue> = NSFetchRequest<UploadQueue>(entityName: "UploadQueue");
        request.predicate = NSPredicate(format: "success = %@", NSNumber(booleanLiteral: false));
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            
            for queueItem in results
            {
                if let action = queueItem.action, let filepath = queueItem.filepath
                {
                    let fileUrl = URL(fileURLWithPath: filepath);
                    
                    // If we're not actually sending data, then let's at least do some testing
                    if self.sendData == false
                    {
                        DNLog("Pretending to upload \(fileUrl.lastPathComponent).");
                        
                        if FileManager.default.fileExists(atPath: filepath)
                        {
                            DNLog("file exists!");
                        }
                        else
                        {
                            DNLog("file does not exist!");
                        }
                        
                        if arc4random() % 2 == 0
                        {
                            queueItem.success = true;
                            DNLog("pretend upload successful!");
                        }
                        else
                        {
                            queueItem.success = false;
                            queueItem.attempts = queueItem.attempts + 1;
                            DNLog("pretend upload failed!");
                        }
                        
                        DNDataManager.save();
                        continue;
                    }
                    
                    // otherwise, sendData = true, and let's actually send the data.
                    
                    DNLog("Attempting to send enqueued file \(fileUrl.lastPathComponent)");
                    
                    // If for some reason the file no longer exists, then let's just skip it.
                    if FileManager.default.fileExists(atPath: filepath) == false
                    {
                        DNLog("file \(fileUrl.lastPathComponent) does not exist! Deleting this QueueItem");
                        DNDataManager.backgroundContext.delete(queueItem);
                        DNDataManager.save();
                        continue;
                    }
                    
                    self.sendFile(action: action, background: queueItem.background, file: fileUrl, timeout: 60, delay: 0, onCompletion: { (data, statusCode, response, error) in
                       
                        if let e = error
                        {
                            DNLog("Error uploading file: \(fileUrl.lastPathComponent): \(e)");
                            queueItem.attempts =  queueItem.attempts + 1;
                            DNDataManager.save();
                        }
                        else
                        {
                            queueItem.success = true;
                            if self.storeZips == false
                            {
                                do{
                                    if FileManager.default.fileExists(atPath: filepath)
                                    {
                                        try FileManager.default.removeItem(at: fileUrl);
                                    }
                                    DNDataManager.backgroundContext.delete(queueItem);
                                }
                                catch
                                {
                                    DNLog("error deleting file: \(error)");
                                }
                            }
                            
                            DNDataManager.save();
                        }
                        
                    });
                }
            }
            DNDataManager.save();
        }
        catch
        {
            DNLog("error retrieving upload queue: \(error)");
        }
    }
    
    //MARK: - helpers
    
    
    func buildUrl(action:String, method:String, parameters:Dictionary<String, Any>?) -> URL?
    {
        let urlComponents = NSURLComponents(string: baseURL.appending(action));
        if method == "GET" && parameters != nil
        {
            urlComponents?.queryItems = self.buildQueryItems(parameters: parameters!);
        }
        
        return urlComponents?.url;
    }
    
    func buildRequest(url:URL, method:String, parameters:Dictionary<String, Any>?, files: Dictionary<String, String>?, timeout:TimeInterval = 60) -> NSMutableURLRequest
    {
        let request = NSMutableURLRequest(url: url);
        request.httpMethod = method;
        request.timeoutInterval = timeout;
        request.setValue("application/json", forHTTPHeaderField: "Accept");
        return request;
    }
    
    func addJsonFormBody(request:NSMutableURLRequest, parameters:Dictionary<String, Any>)
    {
        request.httpBody = self.JSONify(data: parameters)!
        request.setValue("application/json", forHTTPHeaderField: "Content-Type");
    }
    
    func addFormBodyStream(request:NSMutableURLRequest, parameters:Dictionary<String, Any>?, files:Dictionary<String, String>?)
    {
        let boundary = self.generateBoundaryString();
        
        let tempPath = self.createFormBody(boundary: boundary, formData: parameters, files: files);
        if let t = tempPath
        {
            let resultingInputStream = InputStream(fileAtPath: t);
            request.httpBodyStream = resultingInputStream;
            
            do
            {
                let attr = try FileManager.default.attributesOfItem(atPath: t);
                let fileSize:UInt64 = attr[FileAttributeKey.size] as! UInt64;
                request.setValue("\(fileSize)", forHTTPHeaderField: "Content-Length");
            }
            catch
            {
                DNLog("error opening InputStream: \(error)");
            }
        }
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
    }
    
    
    func buildQueryItems(parameters:Dictionary<String, Any>) -> Array<URLQueryItem>
    {
        var queries = Array<URLQueryItem>();
        
        for (name, val) in parameters
        {
            if val is Array<Any>
            {
                let arrayName = "\(name)[]";
                for v in val as! Array<Any>
                {
                    if let newQuery = self.getQueryItem(name:arrayName, val:v)
                    {
                        queries.append(newQuery);
                    }
                }
            }
            else
            {
                if let newQuery = self.getQueryItem(name:name, val:val)
                {
                    queries.append(newQuery);
                }
            }
        }
        return queries;
    }
    
    func getQueryItem(name:String, val:Any) -> URLQueryItem?
    {
        if val is String
        {
            let newQuery = URLQueryItem(name: name, value: val as? String);
            return newQuery;
        }
        else if val is NSNumber
        {
            let newQuery = URLQueryItem(name: name, value: (val as! NSNumber).stringValue);
            return newQuery;
        }
        return nil;
    }
    
    
    // creating a temporary InputStream to store large form bodies (like with big videos).
    // Much of this is based on the methods used by AlamoFire's networking library
    
    func createFormBody(boundary:String, formData:Dictionary<String, Any>?, files:Dictionary<String,String>?) -> String?
    {
        _ = NSMutableData();
        
        var formSections = Array<DNFormBodySection>();
        
        if let d = formData
        {
            for (name, value) in d
            {
                let headerData = "Content-Disposition: form-data; name=\"\(name)\"\r\n\r\n".data(using: String.Encoding.utf8)!;
                let bodyData = "\(value)".data(using: String.Encoding.utf8)!;
                let body = InputStream(data:bodyData);
                
                let section = DNFormBodySection(headerData: headerData, body: body, contentLength: UInt64(bodyData.count), endData:nil);
                formSections.append(section);
            }
        }
        
        if let f = files
        {
            for (name, filepath) in f
            {
                if FileManager.default.fileExists(atPath: filepath)
                {
                    do
                    {
                        var headerData = "Content-Disposition: form-data; name=\"file[]\"; filename=\"\(name)\"\r\n".data(using: String.Encoding.utf8)!;
                        headerData.append("Content-Type: \(self.getMimeType(filepath: filepath))\r\n\r\n".data(using: String.Encoding.utf8)!);
                        
                        guard let body = InputStream(fileAtPath: filepath) else
                        {
                            continue;
                        }
                        
                        guard let fileLength = try FileManager.default.attributesOfItem(atPath: filepath)[.size] as? NSNumber else
                        {
                            continue;
                        }
                        
                        let section = DNFormBodySection(headerData: headerData, body: body, contentLength: fileLength.uint64Value, endData: "\r\n".data(using: String.Encoding.utf8));
                        formSections.append(section);
                    }
                    catch
                    {
                        DNLog("error creating file body section: \(error)");
                    }
                }
            }
        }
        
        let tempName = "\(UUID().uuidString).dat"
        let tempPath = NSTemporaryDirectory().appendingFormat(tempName)
        
        let contentBoundary = "--\(boundary)\r\n".data(using: String.Encoding.utf8)!;
        let lastBoundary = "--\(boundary)--\r\n".data(using: String.Encoding.utf8)!;
        if PRINT_REQUESTS
        {
            DNLog("writing stream to: \(tempPath)");
        }
        guard let output = OutputStream(toFileAtPath: tempPath, append: false) else
        {
            DNLog("error creating output stream");
            return nil;
        }
        
        
        output.open();
        
        let streamBufferSize = 1024;
        
        for section in formSections
        {
            
            writeData(data: contentBoundary, to: output);
            // write header info
            writeData(data: section.headerData, to: output);
            
            
            // write input stream
            let input = section.body;
            input.open();
            
            while input.hasBytesAvailable {
                var buffer = [UInt8](repeating: 0, count: streamBufferSize)
                let bytesRead = input.read(&buffer, maxLength: streamBufferSize)
                
                if let streamError = input.streamError {
                    DNLog("error reading from stream: \(streamError)");
                    input.close();
                    output.close();
                    return nil;
                }
                
                if bytesRead > 0 {
                    if buffer.count != bytesRead {
                        buffer = Array(buffer[0..<bytesRead])
                    }
                    
                    writeBuffer(buffer:&buffer, to: output);
                } else {
                    break
                }
            }
            input.close();
            
            // write end info, if available
            if let e = section.endData
            {
                writeData(data: e, to: output);
            }
            
            
        }
        
        writeData(data: lastBoundary, to: output);
        
        output.close();
        
        return tempPath;
    }
    
    
    private func writeData(data:Data, to outputStream:OutputStream)
    {
        var buffer = [UInt8](repeating: 0, count: data.count)
        data.copyBytes(to: &buffer, count: data.count)
        writeBuffer(buffer: &buffer, to: outputStream);
    }
    
    private func writeBuffer(buffer: inout [UInt8], to outputStream:OutputStream)
    {
        var bytesToWrite = buffer.count
        
        while bytesToWrite > 0, outputStream.hasSpaceAvailable
        {
            let bytesWritten = outputStream.write(buffer, maxLength: bytesToWrite)
            
            if let error = outputStream.streamError
            {
                DNLog("error with outputStream: \(error)");
                return;
            }
            
            bytesToWrite -= bytesWritten
            
            if bytesToWrite > 0 {
                buffer = Array(buffer[bytesWritten..<buffer.count])
            }
        }
    }
    
    //mimetypes for most any video/images. Anything else will just be treated as an octet-stream.
    internal let mimeTypes = [
        "gif": "image/gif",
        "jpeg": "image/jpeg",
        "jpg": "image/jpeg",
        "png": "image/png",
        "tif": "image/tiff",
        "tiff": "image/tiff",
        "wbmp": "image/vnd.wap.wbmp",
        "ico": "image/x-icon",
        "jng": "image/x-jng",
        "bmp": "image/x-ms-bmp",
        "svg": "image/svg+xml",
        "svgz": "image/svg+xml",
        "webp": "image/webp",
        "3gpp": "video/3gpp",
        "3gp": "video/3gpp",
        "ts": "video/mp2t",
        "mp4": "video/mp4",
        "mpeg": "video/mpeg",
        "mpg": "video/mpeg",
        "mov": "video/quicktime",
        "webm": "video/webm",
        "flv": "video/x-flv",
        "m4v": "video/x-m4v",
        "mng": "video/x-mng",
        "asx": "video/x-ms-asf",
        "asf": "video/x-ms-asf",
        "wmv": "video/x-ms-wmv",
        "avi": "video/x-msvideo"
    ];
    
    func getMimeType(filepath:String) -> String
    {
        let url = URL(fileURLWithPath: filepath);
        
        
        if let id = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, url.pathExtension as CFString, nil)?.takeRetainedValue(),
            let contentType = UTTypeCopyPreferredTagWithClass(id, kUTTagClassMIMEType)?.takeRetainedValue()
        {
            return contentType as String
        }
        
        
        if let mimetype = self.mimeTypes[url.pathExtension.lowercased()]
        {
            return mimetype;
        }
        
        return "application/octect-stream";
    }
    
    func generateBoundaryString() -> String
    {
        return "Boundary-\(NSUUID().uuidString)"
    }
    
    
    // Parses the response header, looking for a Cache-Control header.
    // Grabs the "max-age" parameter and creates an NSDate object pointing to a time in the future.
    // ie if the header contains "Cache-Control: public, max-age=3600", this function will return
    // an NSDate object with a value of 1 hour from the current date.
    func getExpirationDate(response: HTTPURLResponse) -> NSDate?
    {
        if let cc = response.allHeaderFields["Cache-Control"] as? String
        {
            do
            {
                let regex = try NSRegularExpression(pattern: "max-age=(\\d+)", options: []);
                let matches = regex.matches(in: cc, options: [], range: NSMakeRange(0, (cc as NSString).length ));
                if matches.count > 0
                {
                    let sub = (cc as NSString).substring(with: matches[0].rangeAt(1));
                    if let subVal = Int(sub)
                    {
                        let expirationDate = NSDate(timeIntervalSinceNow: TimeInterval(subVal));
                        return expirationDate;
                    }
                }
            }
            catch
            {
                
            }
        }
        
        return nil;
    }
    
    //MARK: - Reachability
    
    // borrowed from http://stackoverflow.com/questions/25623272/how-to-use-scnetworkreachability-in-swift
    
    func isConnectedToNetwork() -> Bool
    {
        
        var zeroAddress = sockaddr_in()
        zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
        zeroAddress.sin_family = sa_family_t(AF_INET)
        
        guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                SCNetworkReachabilityCreateWithAddress(nil, $0)
            }
        }) else {
            return false
        }
        
        var flags: SCNetworkReachabilityFlags = []
        if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
            return false
        }
        
        let isReachable = flags.contains(.reachable)
        let needsConnection = flags.contains(.connectionRequired)
        
        return (isReachable && !needsConnection)
    }
    
    func isConnectedToWifi() -> Bool
    {
        var zeroAddress = sockaddr_in()
        zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
        zeroAddress.sin_family = sa_family_t(AF_INET)
        
        guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                SCNetworkReachabilityCreateWithAddress(nil, $0)
            }
        }) else {
            return false
        }
        
        var flags: SCNetworkReachabilityFlags = []
        if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
            return false
        }
        
        let isReachable = flags.contains(.reachable)
        let needsConnection = flags.contains(.connectionRequired)
        let isWWAN = flags.contains(.isWWAN);
        return (isReachable && !isWWAN && !needsConnection);
    }
    
    func JSONify(data:Any) -> Data?
    {
        do
        {
            let jsonData = try JSONSerialization.data(withJSONObject: data, options: []);
            return jsonData;
        }
        catch
        {
            DNLog("error serializing object: \(error)");
        }
        return nil;
        
    }
    
    func parseJSON(jsonData:Data?) -> Any?
    {
        if let j = jsonData
        {
            do
            {
                let json = try JSONSerialization.jsonObject(with: j, options: .allowFragments);
                return json;
                
            }
            catch
            {
                DNLog("error serializing JSON: \(error)")
            }
        }
        return nil;
    }
    
}
