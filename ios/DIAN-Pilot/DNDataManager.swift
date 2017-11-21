
//  DNDataManager.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/15/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import UserNotifications
import CoreData

class dayTime
{
    var wake:Date?;
    var bed:Date?;
}




public class DNDataManager {
    static public let sharedInstance = DNDataManager()
    static public var backgroundContext:NSManagedObjectContext {
        get {
            //Get a reference to the application delegate and create a context
            return DNDataManager.sharedInstance.managedObjectContext;
        }
    }
    var defaults:UserDefaults! = UserDefaults(suiteName: "com.washu.arc");
    var dailyTestCount:Int = 4; // number of tests scheduled in a day
    var minTestGap:TimeInterval = 2 * 60 * 60; // minimum amount of time between tests (2 hours)
    var isTesting:Bool = false
    
    var currentTestSession:TestSession?;
        
    //MARK: - Core Data Stack
    
    lazy var applicationDocumentsDirectory: URL = {
        // The directory the application uses to store the Core Data store file. This code uses a directory named "com.cadiridris.coreDataTemplate" in the application's documents Application Support directory.
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return urls[urls.count-1]
    }()
    
    lazy var managedObjectModel: NSManagedObjectModel = {
        // The managed object model for the application. This property is not optional. It is a fatal error for the application not to be able to find and load its model.
        let bundle = Bundle(for: DNDataManager.self);
        let modelURL = bundle.url(forResource: "DIAN_Pilot", withExtension: "momd")!
        return NSManagedObjectModel(contentsOf: modelURL)!
    }()
    
    lazy var persistentStoreCoordinator: NSPersistentStoreCoordinator = {
        // The persistent store coordinator for the application. This implementation creates and returns a coordinator, having added the store for the application to it. This property is optional since there are legitimate error conditions that could cause the creation of the store to fail.
        // Create the coordinator and store
        let coordinator = NSPersistentStoreCoordinator(managedObjectModel: self.managedObjectModel)
        let url = self.applicationDocumentsDirectory.appendingPathComponent("DIAN_Pilot.sqlite")
        var failureReason = "There was an error creating or loading the application's saved data."
        do {
            try coordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: url, options: nil)
        } catch {
            // Report any error we got.
            var dict = [String: AnyObject]()
            dict[NSLocalizedDescriptionKey] = "Failed to initialize the application's saved data" as AnyObject?
            dict[NSLocalizedFailureReasonErrorKey] = failureReason as AnyObject?
            
            dict[NSUnderlyingErrorKey] = error as NSError
            let wrappedError = NSError(domain: "YOUR_ERROR_DOMAIN", code: 9999, userInfo: dict)
            // Replace this with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog("Unresolved error \(wrappedError), \(wrappedError.userInfo)")
            abort()
        }
        
        return coordinator
    }()
    
    lazy var managedObjectContext: NSManagedObjectContext = {
        // Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.) This property is optional since there are legitimate error conditions that could cause the creation of the context to fail.
        let coordinator = self.persistentStoreCoordinator
        var managedObjectContext = NSManagedObjectContext(concurrencyType: .mainQueueConcurrencyType)
        managedObjectContext.persistentStoreCoordinator = coordinator
        managedObjectContext.mergePolicy = NSMergeByPropertyObjectTrumpMergePolicy
        return managedObjectContext
    }()
    
    
    
    
    
    
    static func save()
    {
        DNDataManager.backgroundContext.performAndWait {
            
            do
            {
                try DNDataManager.backgroundContext.save();
            }
            catch
            {
                print("error saving bg context: \(error)");
            }
        }
    }
    
    //Mark: - UserDefaults-backed data
    
    var arcCount:Int
    {
        get {
            return (defaults.value(forKey:"arcCount") as? Int) ?? 0;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "arcCount");
            defaults.synchronize();
        }
    }
    

    var participantId:String? {
        get {
            return defaults.value(forKey: "participantId") as? String;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "participantId");
            defaults.synchronize()

        }
    }
        
    var isFirstLaunch:Bool {
        get {
            if let hasLaunched = defaults.value(forKey:"hasLaunched") as? Bool
            {
                return false;
            }
            return true;
        }
        set (newVal)
        {
            defaults.setValue(true, forKey:"hasLaunched");
            defaults.synchronize();
        }
    }
    
    // the Date that the application was sent to background. This isn't backed by UserDefaults, because we don't want
    // this to persist if the application is terminated.
    var lastClosedDate:Date?
    
    func hasParticipantId() -> Bool
    {
        return self.participantId != nil;
    }
    
    func hasSleepWakeTimes() -> Bool
    {
        for i in 0..<7
        {
            if self.getTimes(dayOfWeek: i) == nil
            {
                return false;
            }
        }
        
        return true;
    }
    
    // returns actual Dates for the wake/sleep time for the given Date.
    // If the sleep time is < wake time (ie the participant wakes up at 2:00 PM, but goes to sleep at 10:00 AM), then
    // the resulting sleep Date will be for the next day (ie the participant wakes up on Tuesday at 2:00 PM and goes to sleep Wednesday at 10:00 AM)
    
    func getWakeSleepTimes(forDate date:Date) -> dayTime?
    {
        let calendar:Calendar = Calendar(identifier: .gregorian);
        let components = calendar.dateComponents([.year,.weekday, .month, .day, .weekOfMonth], from: date);
        
        if let wakeSleep = self.getTimes(dayOfWeek:components.weekday! - 1)
        {
            let wake = Date.combine(day: date, time: wakeSleep.wake!);
            var sleep = Date.combine(day: date, time: wakeSleep.bed!);
            
            if wakeSleep.bed!.compare(wakeSleep.wake!) == .orderedAscending
            {
                sleep = Date.combine(day: date.addingDays(days: 1), time: wakeSleep.bed!);
            }
            
            let ws = dayTime();
            ws.wake = wake;
            ws.bed = sleep;
            
            return ws;
        }
        
        return nil;
    }
    
    
    // returns Date objects with the wake/sleep times set for the given dayOfWeek.
    // Note that the Dates returned should only be used for their hour/minute/second information, 
    // the day/month/year is not set to a usable value.
    func getTimes(dayOfWeek:Int) -> dayTime?
    {
        if let l = defaults.value(forKey: String(format:"%dTimes", dayOfWeek)) as? Dictionary<String, Date>
        {
            let t = dayTime();
            t.wake = l["wake"];
            t.bed = l["bed"];
            return t;
        }
        
        return nil
    }
    
    func setTimes(wake:Date, bed:Date, dayOfWeek:Int)
    {
        var times:Dictionary<String, Date> = Dictionary();
        times["wake"] = wake;
        times["bed"] = bed;
        defaults.setValue(times, forKey: String(format:"%dTimes", dayOfWeek));
        defaults.synchronize()
    }
    
    
    //MARK: - background tasks
    
    func periodicBackgroundTask(timeout:TimeInterval = 20, completion: @escaping()->Void)
    {
        let now = Date();
        // check to see if we need to schedule any notifications for upcoming Arcs
        // If the participant hasn't confirmed their start date, we should send notifications periodically in the weeks leading up
        // to the Arc.
        
        DNRestAPI.shared.sendDailyPing();
        
        
        
        if let arc = TestArc.getCurrentArc()
        {
            arc.markMissedSessions();
            
            
            // we don't want to fire off the missed test notification while the app is open,
            // so we have to check to make sure it's in the background
            if UIApplication.shared.applicationState == .background
                && arc.consecutiveMissedSessionCount() >= 4
                && arc.hasScheduledMissedTestsNotification() == false
            {
                arc.scheduleMissedTestsNotification();
            }
        }
        
        if let arc = TestArc.getUpcomingArc()
        {
            if  let startDate = arc.userStartDate as Date?
            {
                if arc.hasConfirmedDate == false
                {
                    
                    if arc.hasScheduledDateReminder() == false
                    {
                        arc.scheduleDateRemdinderNotification();
                    }
                    
                    if arc.hasScheduledConfirmationReminders() == false
                    {
                        arc.scheduleConfirmationReminders();
                    }
                }
                else
                {
                    arc.clearConfirmationReminders();
                    arc.clearDateReminderNotification();
                }
                
                // if we're one day away, and we haven't scheduled sessions yet, do so now.
                if startDate.daysSince(date: now) == 1 && arc.hasScheduledTestSessions() == false
                {
                    arc.createTestSessions();
                    arc.scheduleSessionNotifications();
                }
            }
        }
        
        
        // Now check if we have any past arcs that need to be marked as missed
        
        let arcs = TestArc.getPastArcs()
        
        for arc in arcs
        {
            arc.markMissedSessions();
            if DNRestAPI.shared.storeZips == false
            {
                // delete any past Arcs that have had all of their data uploaded successfully
                let sessions = arc.getAllSessions();
                
                var hasUploadedAll:Bool = true;
                for session in sessions
                {
                    if session.uploaded == false
                    {
                        hasUploadedAll = false;
                        break;
                    }
                }
                
                if hasUploadedAll
                {
                    DNDataManager.backgroundContext.perform {
                        
                        DNLog("Deleting Arc \(arc.arcID)");
                        for session in sessions
                        {
                            DNDataManager.backgroundContext.delete(session);
                        }
                        
                        DNDataManager.backgroundContext.delete(arc);
                        DNDataManager.save();
                    }
                }
            }
        }
        
                
        // now, send any unfinished tests
        sendFinishedSessions();
        sendMissedSessions();
        

        
        completion();
        
    }
    
    func sendFinishedSessions()
    {
        
        self.managedObjectContext.perform {
            let sessions = TestSession.getFinishedSessionsForUploading();
            
            for i in 0..<sessions.count
            {
                let session = sessions[i];
                DNRestAPI.shared.sendFinishedTestSession(session: session, delay: 1 * TimeInterval(i));
            }
        }
    }
    
    func sendMissedSessions()
    {
        self.managedObjectContext.perform {
            
            let sessions = TestSession.getMissedSessionsForUploading();
            
            for i in 0..<sessions.count
            {
                let session = sessions[i];
                DNRestAPI.shared.sendMissedTestSession(session: session, delay: 1 * TimeInterval(i));
            }
        }
    }
    
}
