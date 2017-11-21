//
//  TestSession+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 5/17/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

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
        if self.testArc == nil || self.testArc!.testSessions == nil
        {
            return false;
        }
        
        return self.testArc!.testSessions!.index(of: self) == self.testArc!.testSessions!.count - 1;
    }
    
    func isFirstSession() -> Bool
    {
        if self.testArc == nil || self.testArc!.testSessions == nil
        {
            return false;
        }
        
        return self.testArc!.testSessions!.index(of: self) == 0;
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
            if name == "testArc"
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
    
    override func dictionaryOfAttributes(excludedKeys: NSSet) -> AnyObject {
        let ex = excludedKeys.addingObjects(from: ["startSignature", "endSignature", "uploaded", "testArc", "sessionDayIndex", "hasTakenChronotype", "hasTakenWake"]);
        return super.dictionaryOfAttributes(excludedKeys: ex as NSSet);
    }
    
}
