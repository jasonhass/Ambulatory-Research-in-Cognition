/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation


class DNRestSessionDelegate:NSObject, URLSessionDelegate, URLSessionDataDelegate, URLSessionTaskDelegate, URLSessionDownloadDelegate
{
    
    var taskCompletion:NSMutableDictionary = NSMutableDictionary();
    var taskData:NSMutableDictionary = NSMutableDictionary();
    var taskErrors:NSMutableDictionary = NSMutableDictionary();
    var taskResponses:NSMutableDictionary = NSMutableDictionary();
    var invalidateSession:Bool = false;
    
    //MARK: - Session delegate
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?)
    {
        if let e = error
        {
            taskErrors[task.taskIdentifier] = e;
        }
        
        let error = taskErrors[task.taskIdentifier] as? Error;
        let response = taskResponses[task.taskIdentifier] as? URLResponse ?? task.response;
        let data = taskData[task.taskIdentifier] as? Data;
        let completion = taskCompletion[task.taskIdentifier] as? ServiceResponse;
        
        if let c = completion
        {
            self.finishTask(task:task, onCompletion: c, data: data, response: response, error: error);
        }
        
        
        taskErrors.removeObject(forKey: task.taskIdentifier);
        taskResponses.removeObject(forKey: task.taskIdentifier);
        taskData.removeObject(forKey: task.taskIdentifier);
        taskCompletion.removeObject(forKey: task.taskIdentifier);
        
        if invalidateSession
        {
            session.finishTasksAndInvalidate();
        }
        
    }
    
    //MARK: - Data task delegate
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive response: URLResponse, completionHandler: @escaping (URLSession.ResponseDisposition) -> Void) {
        
        taskResponses[dataTask.taskIdentifier] = response;
        
        completionHandler(.allow);
    }
    
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data)
    {
        var currentData:Data = taskData[dataTask.taskIdentifier] as? Data ?? Data();
        currentData.append(data);
        taskData[dataTask.taskIdentifier] = currentData;
    }
    
    //MARK: - Download task delegate
    
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL)
    {
        do
        {
            let d = try Data(contentsOf: location);
            self.taskData[downloadTask.taskIdentifier] = d;
        }
        catch
        {
            DNLog("task #\(downloadTask.taskIdentifier) error loading downloaded data: \(error)");
        }
    }
    
    //MARK: - helper methods
    
    func finishTask(task:URLSessionTask, onCompletion:ServiceResponse?, data:Data?, response:URLResponse?, error:Error?)
    {
        if let d = data
        {
            DNLog("task #\(task.taskIdentifier) response data: \(String(describing: String(data:d, encoding:.utf8)))");
        }
        
        if let e = error
        {
            DNLog("task #\(task.taskIdentifier) error: \(e)");
        }
        
        if DNRestAPI.shared.PRINT_REQUESTS
        {
            if let r = response
            {
                DNLog("task #\(task.taskIdentifier) response response: \(r)");
            }
            else
            {
                DNLog("task #\(task.taskIdentifier) no response for a task!");
            }
            DNLog("task #\(task.taskIdentifier)response errors: \(String(describing: error))");
            DNLog("+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+");
        }
        
        DNRestAPI.shared.lastError = error;
        
        
        // if error is a cancellation error (if the application has manually cancelled the task)
        // just quit. We don't need to do anything with the onCompletion handler
        
        if let e = error as NSError?, e.code == -999
        {
            return;
        }
        
        
        // first, check status codes. If we get a 404, don't even bother trying to parse anything.
        // Just call onCompletion with response and errors, and bail
        
        if let httpResponse = response as? HTTPURLResponse
        {
            DNRestAPI.shared.lastResponse = httpResponse;
            let errorCodes:Array<Int> = [400,401,404,500];
            if errorCodes.contains(httpResponse.statusCode)
            {
                var e = error;
                if error == nil
                {
                    e = NSError(domain: "com.dian.arc", code: httpResponse.statusCode, userInfo: nil);
                    DNRestAPI.shared.lastError = e;
                }
                onCompletion?(data, httpResponse.statusCode, response, e);
                return;
            }
            
            onCompletion?(data, httpResponse.statusCode, response, error);
        }
        else
        {
            onCompletion?(data, 404,response, error);
        }
        
        
    }
    
}
