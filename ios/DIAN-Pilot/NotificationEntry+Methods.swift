//
//  NotificationEntry+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 5/18/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import UIKit
import UserNotifications
import CoreData

extension NotificationEntry
{
    @discardableResult
    static func scheduleNotification(date:Date, title:String, body:String, identifierPrefix:String = "") -> NotificationEntry
    {
        DNLog("Scheduling \(identifierPrefix) notification for \(date.localizedString())");
        
        let newNotification:NotificationEntry = NotificationEntry.createIn(context: DNDataManager.backgroundContext);
        
        let identifier = "\(identifierPrefix)-\(UUID().uuidString)";
        newNotification.createdOn = Date() as NSDate;
        newNotification.scheduledAt = date as NSDate;
        newNotification.title = title;
        newNotification.body = body;
        newNotification.notificationIdentifier = identifier;
        
        if #available(iOS 10.0, *) {
            
            let unitFlags: Set<Calendar.Component> = [ .second, .minute, .hour, .day, .month, .year]
            let components = NSCalendar.current.dateComponents(unitFlags, from: date);
            let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
            
            let content = UNMutableNotificationContent()
            content.title = title;
            content.body = body;
            content.sound = UNNotificationSound.default()
            let request = UNNotificationRequest(
                identifier: identifier,
                content: content,
                trigger: trigger
            );
            
            
            
            UNUserNotificationCenter.current().add(
                request, withCompletionHandler: nil);
            
        }
        else
        {
            let notification = UILocalNotification();
            
            notification.alertTitle = title;
            notification.alertBody = body;
            notification.fireDate = date;
            notification.soundName = UILocalNotificationDefaultSoundName;
            notification.userInfo = ["notificationIdentifier": identifier];
            UIApplication.shared.scheduleLocalNotification(notification);
        };
        

        DNDataManager.save();
        return newNotification;
    }
    
    
    // clears all pending notifications
    static func clearNotifications(withIdentifierPrefix identifierPrefix:String)
    {
        
        let notifications = NotificationEntry.getNotifications(withIdentifierPrefix: identifierPrefix, onlyPending: true);
        var identifiers:Array<String> = Array();
        
        for notification in notifications
        {
            identifiers.append(notification.notificationIdentifier!);
            DNDataManager.backgroundContext.delete(notification);
        }
    
        DNDataManager.save();
        
        if #available(iOS 10.0, *)
        {
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: identifiers);
        }
        else
        {
            if let scheduledNotifications = UIApplication.shared.scheduledLocalNotifications
            {
                for notification in scheduledNotifications
                {
                    if let info = notification.userInfo, let identifier = info["notificationIdentifier"] as? String, identifiers.contains(identifier)
                    {
                        UIApplication.shared.cancelLocalNotification(notification);
                    }
                }
            }
        }
    }
    
    static func clearAllPendingNotifications()
    {
        let notifications = NotificationEntry.getNotifications(withIdentifierPrefix: nil, onlyPending: true);
        
        for notification in notifications
        {
            DNDataManager.backgroundContext.delete(notification);
        }
        
        DNDataManager.save();
        
        if #available(iOS 10.0, *)
        {
            UNUserNotificationCenter.current().removeAllPendingNotificationRequests();
        }
        else
        {
            UIApplication.shared.cancelAllLocalNotifications();
        }
    }
    
    // deletes notifications that have already passed
    
    static func clearPastNotifications()
    {
        let request:NSFetchRequest<NotificationEntry> = NSFetchRequest<NotificationEntry>(entityName: "NotificationEntry");
        request.predicate = NSPredicate(format: "scheduledAt < %@", NSDate());
        request.sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
        
        DNDataManager.backgroundContext.perform {
            
            do
            {
                let results = try DNDataManager.backgroundContext.fetch(request);
                
                for r in results
                {
                    DNDataManager.backgroundContext.delete(r);
                }
                
                DNDataManager.save();
                
            }
            catch
            {
                DNLog("error fetching notifications: \(error)");
            }
        }

    }
    
    static func getNotifications(withIdentifierPrefix identifierPrefix:String?, onlyPending:Bool = true) -> [NotificationEntry]
    {
        
        let request:NSFetchRequest<NotificationEntry> = NSFetchRequest<NotificationEntry>(entityName: "NotificationEntry");
        if let prefix = identifierPrefix
        {
            if onlyPending
            {
             request.predicate = NSPredicate(format: "notificationIdentifier beginswith %@ AND scheduledAt > %@", prefix, NSDate());
            }
            else
            {
                request.predicate = NSPredicate(format: "notificationIdentifier beginswith %@", prefix);
            }
        }
        request.sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
        
        do
        {
            let results = try DNDataManager.backgroundContext.fetch(request);
            return results;
        }
        catch
        {
            DNLog("error fetching notifications: \(error)");
        }
        
        return [];
    }
    
    static func printPendingSystemNotifications()
    {
        DNLog("printing pending system notifications");
        if #available(iOS 10.0, *)
        {
            UNUserNotificationCenter.current().getPendingNotificationRequests(completionHandler: { requests in
                for r in requests
                {
                    DNLog("\(r.trigger!) | \(r.content.title)");
                }
            });
        }
        else
        {
            
            if let scheduledNotifications = UIApplication.shared.scheduledLocalNotifications
            {
                for n in scheduledNotifications
                {
                    DNLog("\(n.fireDate!.localizedString()) \(n.alertTitle)");
                }
            }
        }

    }
}
