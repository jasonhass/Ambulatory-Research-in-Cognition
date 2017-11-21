//
//  DNRestSessionDelegate.swift
//  ARC
//
//  Created by Michael Votaw on 5/22/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

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
        let completion = taskCompletion[task.taskIdentifier] as! ServiceResponse;
                
        self.finishTask(onCompletion: completion, data: data, response: response, error: error);
        
        
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
            DNLog("error loading downloaded data: \(error)");
        }
    }
    
    //MARK: - helper methods
    
    func finishTask(onCompletion:ServiceResponse?, data:Data?, response:URLResponse?, error:Error?)
    {
        if let d = data
        {
            DNLog("response data: \(String(data:d, encoding:.utf8))");
        }
        
        if PRINT_REQUESTS
        {
            if let r = response
            {
                DNLog("response response: \(r)");
            }
            else
            {
                DNLog("no response for a task!");
            }
            DNLog("response errors: \(error)");
            DNLog("+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+");
        }
        
        DNRestAPI.shared.lastError = error;
        
        
        // if error is a cancellation error (if the application has manually cancelled the task)
        // just quit. We don't need to do anything with the onCompletion handler
        
        if let e = error as? NSError, e.code == -999
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
