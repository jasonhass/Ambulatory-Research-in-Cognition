/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import CoreData

extension TestSession
{
    static func getFinishedSessionsForUploading() -> Array<TestSession>
    {
        let request:NSFetchRequest<TestSession> = NSFetchRequest<TestSession>(entityName: "TestSession");
        request.predicate = NSPredicate(format: "uploaded == FALSE AND finishedSession == TRUE");
        request.sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            return results;
        }
        catch
        {
            DNLog("error retrieving sessions data: \(error)");
        }
        
        return [];
    }
    
    static func getMissedSessionsForUploading() -> Array<TestSession>
    {
        let request:NSFetchRequest<TestSession> = NSFetchRequest<TestSession>(entityName: "TestSession");
        request.predicate = NSPredicate(format: "uploaded == FALSE AND missedSession == TRUE");
        request.sortDescriptors = [NSSortDescriptor(key:"sessionDate", ascending:true)];
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            return results;
        }
        catch
        {
            DNLog("error retrieving sessions data: \(error)");
        }
        
        return [];
    }
    
    
    func isLastSession() -> Bool
    {
        if self.testVisit == nil || self.testVisit!.testSessions == nil
        {
            return false;
        }
        
        return self.testVisit!.testSessions!.index(of: self) == self.testVisit!.testSessions!.count - 1;
    }
    
    func isFirstSession() -> Bool
    {
        if self.testVisit == nil || self.testVisit!.testSessions == nil
        {
            return false;
        }
        
        return self.testVisit!.testSessions!.index(of: self) == 0;
    }
    
    func hasTakenWakeSurvey() -> Bool
    {
        return self.wakeSurvey != nil;
    }
    
    func markMissed()
    {
        self.missedSession = true;
        DNDataManager.save();
    }
    
    func markFinished()
    {
        self.missedSession = false;
        self.finishedSession = true;
        DNDataManager.save();
    }
    
    
    // clears all useful data from the Session. It only keeps data related to start date, which Arc it's part of,
    // and whether or not it was finished or missed.
    
    func clearData()
    {
        let relationships = self.entity.relationshipsByName;
        
        // delete all of the relationships
        for (name, _) in relationships
        {
            if name == "testVisit"
            {
                continue;
            }
            
            if let v = self.value(forKey: name) as? NSManagedObject
            {
                DNDataManager.backgroundContext.delete(v);
            }
        }
        
        // and now clear out any data we don't absolutely need to keep the app running
        self.completeTime = nil;
        self.endSignature = nil;
        self.startSignature = nil;
        self.startTime = nil;
        self.willUpgradePhone = false;
        self.interrupted = false;
        
        DNDataManager.save();
        
        // and now, delete any notifications 
        
        NotificationEntry.clearPastNotifications();
        
    }
    
    func clearNotifications()
    {
        let request:NSFetchRequest<NotificationEntry> = NSFetchRequest<NotificationEntry>(entityName: "NotificationEntry");
        
        
        request.predicate = NSPredicate(format: "notificationIdentifier beginswith %@ AND visitID = %d AND sessionID = %d", "TestSession", self.testVisit!.visitID, self.sessionID);
        request.sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
        let notifications = NotificationEntry.getNotifications(withFetchRequest: request);
        NotificationEntry.manageDeleteNotifications(notifications: notifications);
    }
    
    override func dictionaryOfAttributes(excludedKeys: NSSet) -> AnyObject {
        let ex = excludedKeys.addingObjects(from: ["startSignature", "endSignature", "uploaded", "testVisit", "sessionDayIndex", "hasTakenChronotype", "hasTakenWake"]);
        return super.dictionaryOfAttributes(excludedKeys: ex as NSSet);
    }
    
}
