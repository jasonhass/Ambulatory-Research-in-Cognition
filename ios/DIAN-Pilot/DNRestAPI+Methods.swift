/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import Zip


extension DNRestAPI
{
    
    //MARK: - General Data Reporting
    
    func registerParticipant(arcId:String, raterId:String, onCompletion:@escaping(_ error:Error?)->Void)
    {
        
        DNLog("\(#function)");
        let parameters:Dictionary<String,String> = ["subject_id":arcId, "device_id":UIDevice.current.identifierForVendor!.uuidString, "registrar_code": raterId];
        
        if sendData && requireRegistration
        {
            self.makeRequest(action: "deviceRegistration", method: "POST", parameters: parameters, files: nil) { (data, statusCode, response, error) in
                if statusCode == 200
                {
                    
                    DNDataManager.sharedInstance.arcId = arcId;
                    
                    let jsonString = String(data: data ?? Data(), encoding: String.Encoding.utf8)?.replacingOccurrences(of: "text=", with: "")
                    
                    if let obj = self.parseJSON(jsonData:jsonString?.data(using: String.Encoding.utf8)) as? Dictionary<String,Int> {
                    
                        DNDataManager.sharedInstance.visitCount = (obj["visitId"] ?? -1) + 1;
                    }
                }
                onCompletion(error);
            }
        }
        else
        {
            onCompletion(nil);
        }
        
    }
    func registerParticipant(arcId:String, verificationCode:String, onCompletion:@escaping(_ error:Error?)->Void)
    {
        
        DNLog("\(#function)");
        let parameters:Dictionary<String,String> = ["verification_code": verificationCode,"subject_id":arcId, "device_id":UIDevice.current.identifierForVendor!.uuidString];
        
        if sendData
        {
            self.makeRequest(action: "deviceRegistration", method: "POST", parameters: parameters, files: nil) { (data, statusCode, response, error) in
                if statusCode == 200
                {
                    
                    DNDataManager.sharedInstance.arcId = arcId;
                    
                    let jsonString = String(data: data ?? Data(), encoding: String.Encoding.utf8)?.replacingOccurrences(of: "text=", with: "")
                    
                    if let obj = self.parseJSON(jsonData:jsonString?.data(using: String.Encoding.utf8)) as? Dictionary<String,Int> {
                        
                        DNDataManager.sharedInstance.visitCount = (obj["visitId"] ?? -1) + 1;
                        if(self.skipBaseline && DNDataManager.sharedInstance.visitCount == 0) {
                            DNDataManager.sharedInstance.visitCount = 1
                        }
                    }
                }
                
                onCompletion(error);
            }
        }
        else
        {
            DNDataManager.sharedInstance.visitCount = 0;
            if(self.skipBaseline && DNDataManager.sharedInstance.visitCount == 0) {
                DNDataManager.sharedInstance.visitCount = 1
            }
            onCompletion(nil);
        }
        
    }
    
    func requestVerificationCode(arcId:String, onCompletion:@escaping(_ error:Error?)->Void)
    {
        
        DNLog("\(#function)");
        let parameters:Dictionary<String,String> = ["subject_id":arcId];
        
        if sendData
        {
            self.makeRequest(action: "verificationCode", method: "POST", parameters: parameters, files: nil) { (data, statusCode, response, error) in
                if statusCode == 200
                {
      
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
        DNLog("\(#function)");
        if DNDataManager.sharedInstance.arcId != nil
        {
            let deviceId = UIDevice.current.identifierForVendor!.uuidString;
            
            if self.sendData && self.sendPing
            {
                let actionString = "deviceheartbeat/\(deviceId)";
                self.makeBackgroundRequest(action: actionString, method: "PUT", parameters: nil, files: nil, onCompletion: { (data, statusCode, response, error) in
                    
                })
            }
        }
    }
    
    
    func sendWakeSleepSchedule(wakeSleepTimes:Array<dayTime>)
    {
        DNLog("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.arcId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Wake Sleep Schedule");
                let dateData = NSMutableDictionary();
                let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                dateData.setObject(p, forKey: "arcId" as NSString);
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
                let versionString = DNDataManager.sharedInstance.info?[ARC_VERSION_INFO_KEY] as? Int ?? 0
                dateData.setObject(versionString, forKey: "version" as NSString);

                dateData.setObject(wakeSleepData, forKey: "wakeSleepData" as NSString);
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename, forceStore: true);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    
                    #if CS
                    let actionString = "?access_token=" + self.AuthToken;
                    #else
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    #endif
                    
                    self.enqueueFile(action: actionString, file: p);
                    self.processEnqueuedFiles();
                }
            }
        }
        catch
        {
            DNLog("error creating json file for sendTestSchedule: \(error)")
        }
    }
    
        
    func sendTestSchedule(forVisit  visit:TestVisit, delay:TimeInterval = 0)
    {
        DNLog("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.arcId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Test Session Schedule");
                let dateData = NSMutableDictionary();
                let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                
                dateData.setObject(deviceId, forKey: "deviceId" as NSString);
                dateData.setObject(visit.visitID, forKey:"visitId" as NSCopying);
                dateData.setObject(p, forKey: "arcId" as NSString);
                if let sessions = visit.testSessions
                {
                    let sessionData = NSMutableArray();
                    for i in 0..<sessions.count
                    {
                        let session = sessions[i] as! TestSession;
                        let d = NSMutableDictionary();
                        d.setObject(session.sessionID, forKey: "sessionId" as NSString);
                        d.setObject(session.sessionDate!.timeIntervalSince1970, forKey: "sessionDate" as NSString);
                        
                        sessionData.add(d);
                    }
                    let versionString = DNDataManager.sharedInstance.info?[ARC_VERSION_INFO_KEY] as? Int ?? 0
                    dateData.setObject(versionString, forKey: "version" as NSString);
                    
                    dateData.setObject(sessionData, forKey: "sessions" as NSString);
                }
                
                
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename, forceStore: true);
                
                if let p = path, sendData == true
                {
                    #if CS
                        let actionString = "?access_token=" + self.AuthToken;
                    #else
                        let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    #endif
                    
                    self.enqueueFile(action: actionString, file: p);
                    self.processEnqueuedFiles();
                }
            }
        }
        catch
        {
            DNLog("error creating json file for sendTestSchedule: \(error)")
        }
    }
    
    func sendArcDateUpdate(forVisit  visit:TestVisit)
    {
        DNLog("\(#function)");
        do
        {
            if let p = DNDataManager.sharedInstance.arcId
            {
                let filename =  self.filename(forParticpantId: p, andMethodName: "Visit Date Update");
                let dateData = NSMutableDictionary();
                let versionString = DNDataManager.sharedInstance.info?[ARC_VERSION_INFO_KEY] as? Int ?? 0
                dateData.setObject(versionString, forKey: "version" as NSString);
                
                dateData.setObject(visit.visitID, forKey:"visitId" as NSCopying);
                dateData.setObject(visit.visitStartDate!.timeIntervalSince1970, forKey: "visitStartDate" as NSString);
                dateData.setObject(visit.visitEndDate!.timeIntervalSince1970, forKey: "visitEndDate" as NSString);
                dateData.setObject(visit.userStartDate!.timeIntervalSince1970, forKey: "userStartDate" as NSString);
                dateData.setObject(visit.userEndDate!.timeIntervalSince1970, forKey: "userEndDate" as NSString);
                dateData.setObject(p, forKey: "arcId" as NSString);
                let data = try JSONSerialization.data(withJSONObject: dateData, options: .prettyPrinted);
                
                let path = self.createZipFile(files: ["\(filename).json": data], filename: filename, forceStore: true);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    #if CS
                        let actionString = "?access_token=" + self.AuthToken;
                    #else
                        let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    #endif
                    
                    self.enqueueFile(action: actionString, file: p);
                    self.processEnqueuedFiles();
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
        //TODO actually call Radiologics
        onCompletion([]);
    }
    
    
    //MARK: - Session-specific Reporting
    
    
    func sendFinishedTestSession(session: TestSession, delay:TimeInterval = 0)
    {
        DNLog("\(#function)");
        var data:Data?;
        
        do
        {
            if let p = DNDataManager.sharedInstance.arcId
            {
                var s = self.prepSessionData(session: session, p: p);
                let versionString = DNDataManager.sharedInstance.info?[ARC_VERSION_INFO_KEY] as? Int ?? 0
                s.setObject(versionString, forKey: "version");
                data = try JSONSerialization.data(withJSONObject: s, options: .prettyPrinted);
            }
        }
        catch
        {
            DNLog("error creating json file for sendFinishedTestSession: \(error)")
        }
        
        if let p = DNDataManager.sharedInstance.arcId, let d = data
        {
            let filename =  self.filename(forParticpantId: p, andMethodName: "Visit-\(session.testVisit!.visitID) Session-\(session.sessionID)");
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
                #if CS
                    let actionString = "?access_token=" + self.AuthToken;
                #else
                    let actionString = "devicesubmit/\(deviceId)?inbody=true";
                #endif
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
        
        DNLog("\(#function) \(DateFormatter.localizedString(from: session.sessionDate! as Date, dateStyle: .short, timeStyle: .none))");
        do
        {
            if let p = DNDataManager.sharedInstance.arcId
            {
                let filename = "\(p) Visit-\(session.testVisit!.visitID) Session-\(session.sessionID) \(Date().filenameSafeString())";
                let s = self.prepSessionData(session: session, p: p);
                let versionString = DNDataManager.sharedInstance.info?[ARC_VERSION_INFO_KEY] as? Int ?? 0
                s.setObject(versionString, forKey: "version");
                let data = try JSONSerialization.data(withJSONObject: s, options: .prettyPrinted);
                let path = self.createZipFile(files: ["\(filename).json":data], filename: filename);
                
                if let p = path, sendData == true
                {
                    let deviceId = UIDevice.current.identifierForVendor!.uuidString;
                    #if CS
                        let actionString = "?access_token=" + self.AuthToken;
                    #else
                        let actionString = "devicesubmit/\(deviceId)?inbody=true";
                    #endif
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
        //TODO actually call Radiologics
        
        onCompletion(1, []);
    }
    
    
    //MARK: - helpers
    
    
    func prepSessionData(session: TestSession, p:String) -> MutableOrderedDictionary
    {
        let deviceString = deviceInfo();
        let deviceId = UIDevice.current.identifierForVendor!.uuidString;
        let s = session.dictionaryOfAttributes(excludedKeys: NSSet()) as! MutableOrderedDictionary;
        
        s.setObject(session.testVisit!.visitID, forKey: "visitId");
        s.setObject(deviceString, forKey: "deviceInfo");
        s.setObject(deviceId, forKey: "deviceId");
        s.setObject(p, forKey: "arcId");
        s.setObject(session.sessionID, forKey: "sessionId");
        s.removeObject(forKey: "sessionID");
        s.alphabetized();
        
        return s;
    }
    
    func deviceInfo() -> String
    {
        let deviceString = " \(UIDevice.current.systemName)|\(deviceIdentifier())|\(UIDevice.current.systemVersion)";
        return deviceString;
    }
    
    func filename(forParticpantId arcId:String, andMethodName methodName:String) -> String
    {
        return "\(arcId) \(methodName) \(Date().filenameSafeString())";
    }
    
    @discardableResult func createZipFile(files:Dictionary<String,Data>, filename:String, forceStore:Bool = false) -> URL?
    {
        var tempPath:URL!;
        
        if forceStore || self.storeZips
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
            
            
            if !forceStore && self.storeZips == false
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
