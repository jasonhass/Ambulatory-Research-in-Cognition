//
//  DNRestAPI+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 5/23/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import Zip

extension DNRestAPI
{
    
    //MARK: - General Data Reporting
    
    func registerParticipant(participantId:String, raterId:String, onCompletion:@escaping(_ error:Error?)->Void)
    {
        print("\(#function)");
        let parameters:Dictionary<String,String> = ["subject_id":participantId, "device_id":UIDevice.current.identifierForVendor!.uuidString, "registrar_code": raterId];
        
        if sendData
        {
            self.makeRequest(action: "deviceRegistration", method: "POST", parameters: parameters, files: nil) { (data, statusCode, response, error) in
                if statusCode == 200
                {
                    DNDataManager.sharedInstance.participantId = participantId;
                }
                
                onCompletion(error);
            }
        }
        else
        {
            onCompletion(nil);
        }
        
    }
    
    func sendDailyPing()
    {
        print("\(#function)");
        if let p = DNDataManager.sharedInstance.participantId
        {
            let deviceId = UIDevice.current.identifierForVendor!.uuidString;
            
            if self.sendData
            {
                let actionString = "deviceheartbeat/\(deviceId)";
                self.makeBackgroundRequest(action: actionString, method: "PUT", parameters: nil, files: nil, onCompletion: { (data, statusCode, response, error) in
                    
                })
            }
        }
    }
    
    
    func sendWakeSleepSchedule(wakeSleepTimes:Array<dayTime>)
    {
        print("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.participantId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Wake Sleep Schedule");
                let dateData = NSMutableDictionary();
                let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                dateData.setObject(p, forKey: "participantId" as NSString);
                dateData.setObject(deviceId, forKey: "deviceId" as NSString);
                dateData.setObject(Date().timeIntervalSince1970, forKey: "createdOn" as NSString);
                let wakeSleepData = NSMutableArray();
                
                let dayOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
                for i in 0..<wakeSleepTimes.count
                {
                    let wake = wakeSleepTimes[i].wake!.localizedString(dateStyle: .none, timeStyle: .short);
                    let sleep = wakeSleepTimes[i].bed!.localizedString(dateStyle: .none, timeStyle: .short);
                    let weekday = dayOfWeek[i];
                    
                    let d = NSMutableDictionary();
                    d.setObject(wake, forKey: "wake" as NSString);
                    d.setObject(sleep, forKey: "bed" as NSString);
                    d.setObject(weekday, forKey: "weekday" as NSString);
                    wakeSleepData.add(d);
                }
                
                dateData.setObject(wakeSleepData, forKey: "wakeSleepData" as NSString);
                
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    self.sendFile(action: actionString, background: true, file: p, timeout: 60, delay: 3, onCompletion: { (data, statusCode, response, error) in
                      
                        if let e = error
                        {
                            DNLog("error sending sleep/wake schedule: \(e)");
                        }
                    })
                }
            }
        }
        catch
        {
            DNLog("error creating json file for sendTestSchedule: \(error)")
        }
    }
    
        
    func sendTestSchedule(forArc arc:TestArc, delay:TimeInterval = 0)
    {
        print("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.participantId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Test Session Schedule");
                let dateData = NSMutableDictionary();
                let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                
                dateData.setObject(deviceId, forKey: "deviceId" as NSString);
                dateData.setObject(arc.arcID, forKey:"arcID" as NSCopying);
                dateData.setObject(p, forKey: "participantId" as NSString);
                if let sessions = arc.testSessions
                {
                    let sessionData = NSMutableArray();
                    for i in 0..<sessions.count
                    {
                        let session = sessions[i] as! TestSession;
                        let d = NSMutableDictionary();
                        d.setObject(session.sessionID, forKey: "sessionID" as NSString);
                        d.setObject(session.sessionDate!.timeIntervalSince1970, forKey: "sessionDate" as NSString);
                        
                        sessionData.add(d);
                    }
                    
                    dateData.setObject(sessionData, forKey: "sessions" as NSString);
                }
                
                
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename);
                
                if let p = path, sendData == true
                {
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    self.sendFile(action: actionString, background: true, file: p, timeout: 60, delay: delay, onCompletion: { (data, statusCode, response, error) in
                        
                        if let e = error
                        {
                            DNLog("error sending test schedule: \(e)");
                        }
                    })
                }
            }
        }
        catch
        {
            DNLog("error creating json file for sendTestSchedule: \(error)")
        }
    }
    
    func sendArcDateUpdate(forArc arc:TestArc)
    {
        print("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.participantId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Arc Date Update");
                let dateData = NSMutableDictionary();
                
                dateData.setObject(arc.arcID, forKey:"arcID" as NSCopying);
                dateData.setObject(arc.arcStartDate!.timeIntervalSince1970, forKey: "arcStartDate" as NSString);
                dateData.setObject(arc.arcEndDate!.timeIntervalSince1970, forKey: "arcEndDate" as NSString);
                dateData.setObject(arc.userStartDate!.timeIntervalSince1970, forKey: "userStartDate" as NSString);
                dateData.setObject(arc.userEndDate!.timeIntervalSince1970, forKey: "userEndDate" as NSString);
                dateData.setObject(p, forKey: "participantId" as NSString);
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    self.sendFile(action: actionString, background: true, file: p, timeout: 60, onCompletion: { (data, statusCode, response, error) in
                        
                        if let e = error
                        {
                            DNLog("error sending Arc update: \(e)");
                        }
                    })
                }
            }
        }
        catch
        {
            DNLog("error creating json file for sendArcDateUpdate: \(error)")
        }
        
    }
    
    func sendNotificationData(forEntry entry:NotificationEntry, onCompletion: @escaping(_ errors:Array<Error>)->Void)
    {
        //TODO format data to json format
        onCompletion([]);
    }
    
    
    //MARK: - Session-specific Reporting
    
    
    func sendFinishedTestSession(session: TestSession, delay:TimeInterval = 0)
    {
        print("\(#function)");
        var data:Data?;
        
        do
        {
            if let p = DNDataManager.sharedInstance.participantId
            {
                let s = self.prepSessionData(session: session, p: p);
                
                data = try JSONSerialization.data(withJSONObject: s, options: .prettyPrinted);
            }
        }
        catch
        {
            DNLog("error creating json file for sendFinishedTestSession: \(error)")
        }
        
        if let p = DNDataManager.sharedInstance.participantId, let d = data
        {
            let filename =  self.filename(forParticpantId: p, andMethodName: "Arc-\(session.testArc!.arcID) Session-\(session.sessionID)");
            let jsonFilename = "\(filename).json";
            var filesToSend: Dictionary<String, Data> = [jsonFilename: d];
            
            // get the start and end signatures from the session
            
            if let startImg = session.startSignature
            {
                let startName = "\(filename) start.png";
                filesToSend[startName] = startImg as Data;
            }
            
            if let endImg = session.endSignature
            {
                let endName = "\(filename) end.png";
                filesToSend[endName] = endImg as Data;
            }
            
            let path = self.createZipFile(files: filesToSend, filename: filename);
            
            if let p = path, sendData == true
            {
                let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                let actionString = "devicesubmit/\(deviceId)?inbody=true";
                self.sendFile(action: actionString, background: true, file: p, timeout: 60, delay: delay, onCompletion: { (data, statusCode, response, error) in
                    
                    if let e = error
                    {
                        DNLog("error sending test session: \(e)");
                    }
                    else
                    {
                        session.uploaded = true;
                        if self.storeZips == false
                        {
                            session.clearData();
                        }
                        DNDataManager.save();
                    }
                })
            }

        }
    }
    
    func sendMissedTestSession(session: TestSession, delay:TimeInterval = 0)
    {
        
        print("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.participantId
            {
                let filename = "\(p) Arc-\(session.testArc!.arcID) Session-\(session.sessionID) \(Date().filenameSafeString())";
                let s = self.prepSessionData(session: session, p: p);
                
                let data = try JSONSerialization.data(withJSONObject: s, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json":data], filename: filename);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    self.sendFile(action: actionString, background: true, file: p, timeout: 60, delay: delay, onCompletion: { (data, statusCode, response, error) in
                        
                        if let e = error
                        {
                            DNLog("error sending missed test session: \(e)");
                        }
                        else
                        {
                            session.uploaded = true;
                            if self.storeZips == false
                            {
                                session.clearData();
                            }
                            DNDataManager.save();
                        }
                    })
                }

            }
        }
        catch
        {
            DNLog("error creating json file for sendMissedTestSession: \(error)")
        }
    }
    
    //MARK: - Data Requests
    
    func getCompletedArcCount(onCompletion:@escaping(_ count:Int, _ errors:Array<Error>)->Void)
    {        
        onCompletion(1, []);
    }
    
    
    //MARK: - helpers
    
    
    func prepSessionData(session: TestSession, p:String) -> MutableOrderedDictionary
    {
        let deviceString = deviceInfo();
        let deviceId = UIDevice.current.identifierForVendor!.uuidString;
        let s = session.dictionaryOfAttributes(excludedKeys: NSSet()) as! MutableOrderedDictionary;
        
        s.setObject(session.testArc!.arcID, forKey: "arcID");
        s.setObject(deviceString, forKey: "deviceInfo");
        s.setObject(deviceId, forKey: "deviceID");
        s.setObject(p, forKey: "participantID");
        s.alphabetized();
        
        return s;
    }
    
    func deviceInfo() -> String
    {
        let deviceString = " \(UIDevice.current.systemName)|\(deviceIdentifier())|\(UIDevice.current.systemVersion)";
        return deviceString;
    }
    
    func filename(forParticpantId participantId:String, andMethodName methodName:String) -> String
    {
        return "\(participantId) \(methodName) \(Date().filenameSafeString())";
    }
    
    @discardableResult func createZipFile(files:Dictionary<String,Data>, filename:String) -> URL?
    {
        var tempPath:URL!;
        
        if self.storeZips
        {
            tempPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0];
        }
        else
        {
            tempPath = URL(fileURLWithPath: NSTemporaryDirectory());
        }
        let tempZip = tempPath.appendingPathComponent("\(filename).zip");
        
        
        var tempFiles:Array<URL> = Array();
        
        do
        {
            for (filename, data) in files
            {
                let tempFile = tempPath.appendingPathComponent(filename);
                try data.write(to: tempFile);
                tempFiles.append(tempFile);
            }
            
            if FileManager.default.fileExists(atPath: tempZip.path)
            {
                try FileManager.default.removeItem(at: tempZip);
            }
            
            
            try Zip.zipFiles(paths: tempFiles, zipFilePath: tempZip, password: nil, progress: nil);
            
            
            if self.storeZips == false
            {
                for (filename, _) in files
                {
                    let tempFile = tempPath.appendingPathComponent(filename);
                    if FileManager.default.fileExists(atPath: tempFile.path)
                    {
                        try FileManager.default.removeItem(at: tempFile);
                    }
                }
            }
            
            return tempZip;
        }
        catch
        {
            DNLog("error writing file: \(error)");
            return nil;
        }
    }
    
}
