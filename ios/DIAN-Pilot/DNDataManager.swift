
//  DNDataManager.swift
//  DIAN-Pilot
/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
    
    lazy var info: NSDictionary? = {
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            return NSDictionary(contentsOfFile: path)
            
        }
        return nil
    }()
    
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
        let mOptions = [NSMigratePersistentStoresAutomaticallyOption: true,
                        NSInferMappingModelAutomaticallyOption: true]
        var failureReason = "There was an error creating or loading the application's saved data."
        do {
            try coordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: url, options: mOptions)
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
    
    
    
    var languageKey:String {
        get {
            return (defaults.value(forKey:"setupLanguage") as? String) ?? "";
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "setupLanguage");
            defaults.synchronize();
        }
    }
    
    var availableLanguages:[String] = []
    
    // "normal" translation, uses the actual translation keys for selecting a translation.
    
    lazy var translation:Dictionary<String, Dictionary<String, String>> = {
        
        
        let stream = InputStream(fileAtPath: Bundle.main.path(forResource:"translation", ofType: "csv")!)!
        let csv = try! CSVReader(stream: stream, hasHeaderRow: true, trimFields: true, delimiter: ",", whitespaces: .whitespaces)
        let header = csv.headerRow ?? []
        var dict = Dictionary<String, Dictionary<String, String>>()
        
        for c in header {
            DNDataManager.sharedInstance.availableLanguages.append(c)
            dict[c] = [:]
        }
        
        while let row = csv.next() {
            for i in 0 ..< header.count {
                
                dict[header[i]]?[row[0].lowercased()] = row[i]
            }
        }
        return dict
    }()
    
    // so-called "keyless translation". This allows us to select a translation
    // based on the English value as the translation key.
    
    lazy var keylessTranslation:Dictionary<String, Dictionary<String, String>> = {
        
        
        let stream = InputStream(fileAtPath: Bundle.main.path(forResource:"translation", ofType: "csv")!)!
        let csv = try! CSVReader(stream: stream, hasHeaderRow: true, trimFields: true, delimiter: ",", whitespaces: .whitespaces)
        let header = csv.headerRow ?? []
        var dict = Dictionary<String, Dictionary<String, String>>()
        
        for c in header {
            dict[c] = [:]
        }
        
        while let row = csv.next() {
            for i in 1 ..< header.count {
                //This bases the keys on the english column of the translation doc
                let k = row[1].lowercased().replacingOccurrences(of: "\'", with: "")

                dict[header[i]]?[k] = row[i]
            }
        }
        return dict
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
    
    var visitCount:Int
    {
        get {
            return (defaults.value(forKey:"visitCount") as? Int) ?? 0;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "visitCount");
            defaults.synchronize();
        }
    }
    

    var arcId:String? {
        get {
            return defaults.value(forKey: "participantId") as? String;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey: "participantId");
            defaults.synchronize()

        }
    }
    
    var hasAuthenticated:Bool {
        get {
            if let auth = defaults.value(forKey:"hasAuthenticated") as? Bool
            {
                return auth;
            }
            return false;
        }
        set (newVal)
        {
            defaults.setValue(true, forKey:"hasAuthenticated");
            defaults.synchronize();
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
    
    var lastUploadDate:Date? {
        get {
            if let _lastUploadDate = defaults.value(forKey:"lastUploadDate") as? Date
            {
                return _lastUploadDate;
            }
            return nil;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"lastUploadDate");
            defaults.synchronize();
        }
    }
    // the Date that the application was sent to background. This isn't backed by UserDefaults, because we don't want
    // this to persist if the application is terminated.
    var lastClosedDate:Date?
    
    func hasParticipantId() -> Bool
    {
        return self.arcId != nil;
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
        if !self.didCompleteInitialTimeSetup() {
            return false;
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
    
    func initialTimeSetupComplete() {
        defaults.setValue(true, forKey: "initialTimeSetup")
        defaults.synchronize()
    }
    func didCompleteInitialTimeSetup() -> Bool {
        if let _ = defaults.value(forKey: "initialTimeSetup") {
            return true
        }
        return false
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
        DNRestAPI.shared.processEnqueuedFiles();
        
        
        if let visit = TestVisit.getCurrentVisit()
        {
            visit.markMissedSessions();
            
            
            // we don't want to fire off the missed test notification while the app is open,
            // so we have to check to make sure it's in the background
            if UIApplication.shared.applicationState == .background
                && visit.consecutiveMissedSessionCount() >= 4
                && visit.hasScheduledMissedTestsNotification() == false
            {
                visit.scheduleMissedTestsNotification();
            }
        }
        
        if let visit = TestVisit.getUpcomingVisit()
        {
            if  let startDate = visit.userStartDate as Date?
            {
                if visit.hasConfirmedDate == false
                {
                    
                    if visit.hasScheduledDateReminder() == false
                    {
                        visit.scheduleDateRemdinderNotification();
                    }
                    
                    if visit.hasScheduledConfirmationReminders() == false
                    {
                        visit.scheduleConfirmationReminders();
                    }
                }
                else
                {
                    visit.clearConfirmationReminders();
                    visit.clearDateReminderNotification();
                }
                
                // if we're one day away, and we haven't scheduled sessions yet, do so now.
                if startDate.daysSince(date: now) == 1 && visit.hasScheduledTestSessions() == false
                {
                    visit.createTestSessions();
                    visit.scheduleSessionNotifications();
                }
            }
        }
        
        
        // Now check if we have any past visits  that need to be marked as missed
        
        let visits  = TestVisit.getPastVisits()
        
        for visit in visits 
        {
            visit.markMissedSessions();
            if DNRestAPI.shared.storeZips == false
            {
                // delete any past Arcs that have had all of their data uploaded successfully
                let sessions = visit.getAllSessions();
                
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
                        
                        DNLog("Deleting Visit \(visit.visitID)");
                        for session in sessions
                        {
                            DNDataManager.backgroundContext.delete(session);
                        }
                        
                        DNDataManager.backgroundContext.delete(visit);
                        DNDataManager.save();
                    }
                }
            }
        }
        
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
                DNRestAPI.shared.sendFinishedTestSession(session: session, delay: 3 * TimeInterval(i));
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
                DNRestAPI.shared.sendMissedTestSession(session: session, delay: 3 * TimeInterval(i));
            }
        }
    }
    
}
