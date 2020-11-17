//
// NotificationController.swift
//


import Foundation
import UIKit
import UserNotifications
import CoreData

open class NotificationController : MHController
{
    public enum ControllerError : Error {
        case fireDateDeviation(TimeInterval)
    }

	
	open func authenticateNotifications(completion: @escaping (Bool, Error?)->()) {
		let center = UNUserNotificationCenter.current()

		let options: UNAuthorizationOptions = [.alert, .sound, .badge];
		
		center.requestAuthorization(options: options, completionHandler: completion)
	
	}
	open func isAuthorized(completion: @escaping (Bool)->()) {
		let center = UNUserNotificationCenter.current()
		
		let options: UNAuthorizationOptions = [.alert, .sound, .badge];
		
		center.getNotificationSettings { (settings) in
			OperationQueue.main.addOperation {
				completion(settings.authorizationStatus == .authorized)

			}
		}
		
	}

    open func checkNotificationForDeviation(notification:UNNotification, response:UNNotificationResponse? = nil) throws {
        let deliveryDate = notification.date.timeIntervalSince1970
        if let trigger = notification.request.trigger as? UNCalendarNotificationTrigger,
            let triggerDate = Calendar.current.date(from: trigger.dateComponents)?.timeIntervalSince1970 {
            let deviation = deliveryDate - triggerDate
            HMLog("received notification \(notification.request.identifier): \(response?.actionIdentifier), Delivery deviation: \(deviation)");
            if fabs(deviation) > 60 * 10 {
                let error = ControllerError.fireDateDeviation(deviation)
                Arc.handleError(error, named: "Notification Deviation")
                throw error
            }
        }
    }

    @discardableResult
	open func scheduleNotification(date:Date, title:String, body:String, identifierPrefix:String = "") -> NotificationEntry
    {
        print("Scheduling \(identifierPrefix) notification for \(date.localizedString(dateStyle: .short, timeStyle: .medium) )");
        
        let newNotification:NotificationEntry = new()
        
        let identifier = "\(identifierPrefix)-\(UUID().uuidString)";
        newNotification.createdOn = Date()
        newNotification.scheduledAt = date
        newNotification.title = title;
        newNotification.body = body;
        newNotification.notificationIdentifier = identifier;
		
        
		
		let unitFlags: Set<Calendar.Component> = [ .second, .minute, .hour, .day, .month, .year]
		let components = NSCalendar.current.dateComponents(unitFlags, from: date);
		let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
		
		let content = UNMutableNotificationContent()
		content.title = title;
		content.body = body;
		content.sound = UNNotificationSound(named: UNNotificationSoundName(rawValue: "pluck.aiff"));
		let request = UNNotificationRequest(
			identifier: identifier,
			content: content,
			trigger: trigger
		);
		
		
		
		UNUserNotificationCenter.current().add(
		request) {
			error in
			if let error = error {
				HMLog("Scheduling notification produced an error \(error.localizedDescription)")
				dump(error)
                Arc.handleError(error, named: "Scheduling notification produced an error")
			}
		}
            
			
        

       	save();
        return newNotification;
    }
	
	open func get(notificationsWithIdentifierPrefix identifierPrefix:String?, onlyPending:Bool = true) -> [NotificationEntry]
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
		
		
		return fetch(predicate: request.predicate, sort: request.sortDescriptors) ?? []
		
	}
    
	// creates notifications for upcoming TestSessions
	open func schedule(sessionNotifications studyId:Int)
	{
		guard let study = Arc.shared.studyController.get(study: studyId) else {
			fatalError("Invalid study ID")
		}
		let tests = Arc.shared.studyController.get(upcomingSessions: studyId)
		for test in tests
		{
			
			let body = notificationTitle(for: test)
			let date = test.sessionDate! as Date
			
			let newNotification = scheduleNotification(date: date, title: "", body: body, identifierPrefix: "TestSession");
			
			newNotification.studyID = study.studyID;
			newNotification.sessionID = test.sessionID;
		}
		
		study.hasScheduledNotifications = true;
		
//		save();
	}
    
    // Note: this function needs to be overriden when implementing an app that exhibits behavior other than this.
    func notificationTitle(for test: Session) -> String {
        var title = ""
        
        switch test.session {
        case 0:
            switch test.day {
            case 0:     //first test. Day 1, test 1
                title = "It's time for the first test of the week! Start your streak by completing this test by {TIME}.".localized(ACTranslationKey.notification1_firstday)
            case 4 where test.study?.studyID == 0, 3:     //halfway point. Day 4, test 1
                title = "You're halfway through the week. Keep it up! Today's first test will be available until {TIME}.".localized(ACTranslationKey.notification1_halfway)
            case 6 where test.study?.studyID != 0, 7:     //beginning of the final day. Day 7, test 1
				title = "You're on the last day of testing this week! Your first test of the day will be available until {TIME}.".localized(ACTranslationKey.notification1_lastday)
            default:
                title = "It's time for the first test of the day! This test will be avilable until {TIME}.".localized(ACTranslationKey.notification1_default)
            }
        case 1:
            title = "It's time for today's second test! This test will be available until {TIME}.".localized(ACTranslationKey.notifications2_default)
        case 2:
            title = "Your third test of the day is now available. Please complete this test by {TIME}.".localized(ACTranslationKey.notification3_default)
        default:        //session 3
            switch test.day {
            case 6 where test.study?.studyID != 0, //final test of final day. if the studyId is not 0 Day 7, test 4
				 7: //Or if the studyID == 0 which is the baseline. Then there are 8 days instead of 7
                title = "It's time to take your last test of the week! Please finish it by {TIME}. Your coordinator will contact you about your earnings shortly.".localized(ACTranslationKey.notification4_lastday)
            default:
                title = "The last test of the day is here! Finish strong by completing this test by {TIME}.".localized(ACTranslationKey.notification4_default)
            }
        }
        
        if let time = test.expirationDate?.localFormat() {
            title = title.replacingOccurrences(of: "{TIME}", with: time)
        }
        
        return title
    }
    
    // creates notifications for upcoming TestSessions
    open func schedule(upcomingSessionNotificationsWithLimit limit:Int)
    {
        
        //Just get the upcoming 32 sessions reverse them and schedule them
        //Reversing the incoming list will cause the latest tests to be remove instead of the recent
        let tests = Arc.shared.studyController.getUpcomingSessions(withLimit: limit).reversed()
        for test in tests
        {
            guard test.startTime == nil && test.missedSession == false else {
                continue
            }
            let body = notificationTitle(for: test);
            let date = test.sessionDate! as Date
            
            let newNotification = scheduleNotification(date: date, title: "", body: body, identifierPrefix: "TestSession");
            if let study = test.study {
                newNotification.studyID = study.studyID;
                study.hasScheduledNotifications = true;
            } else {
                assertionFailure("Invalid test session, No study ID")
            }
            newNotification.sessionID = test.sessionID;
        }
        
        
        //        save();
    }

    open func scheduleMissedTestNotification() {
        self.clearMissedTestNotifications()
        let sessions = Arc.shared.studyController.getUpcomingSessions(withLimit: 32)
        var count = 1
        for session in sessions {
            if count%4 == 0 {
                // get session time
                guard let testTime = session.expirationDate else { continue }
                // schedule missed test notif for new time
                let body = "You've missed your tests. If you're unable to finish this week, please contact your site coordinator.".localized(ACTranslationKey.notification_missedtests) //comment: "Notification for missed test sessions"
                scheduleNotification(date: testTime, title: "", body: body, identifierPrefix: "MissedTestNotification")
            }
            count += 1
        }
    }
    
    fileprivate func clearMissedTestNotifications() {
        clearNotifications(withIdentifierPrefix: "MissedTestNotification")
    }
    
    func clearDateReminders()
    {
        clearNotifications(withIdentifierPrefix: "DateReminder");
    }
	func clear(confirmationReminders studyId:Int)
	{
		clearNotifications(withIdentifierPrefix: "DateConfirmation-\(studyId)");
	}
	
	func clear(confirmationReminderForWeek week:Int, studyId:Int)
	{
		clearNotifications(withIdentifierPrefix: "DateConfirmation-\(studyId)-\(week)");
	}
    func clear(missedTestReminder studyId:Int)
    {
        clearNotifications(withIdentifierPrefix: "MissedTests-\(studyId)");
    }
	
	// clear notifications for upcoming TestSessions
	open func clear(sessionNotifications studyId:Int)
	{
//		guard let study = Arc.shared.studyController.get(study: studyId) else {
//			return
//		}
		clearNotifications(withIdentifierPrefix: "TestSession");
//		study.hasScheduledNotifications = false;
//		save();
	}
    // clears all pending notifications
	open func clearNotifications(withIdentifierPrefix identifierPrefix:String)
    {
        
		let notifications = getNotifications(withIdentifierPrefix: identifierPrefix, onlyPending: false)
		
		manageDeleteNotifications(notifications: notifications);
    }
    
	open func manageDeleteNotifications(notifications: Array<NotificationEntry>)
    {
		guard notifications.count > 0 else {
			return
		}
		var identifiers:Array<String> = Array();
		
		for notification in notifications
		{
			if let ident = notification.notificationIdentifier {
				identifiers.append(ident);
			}
			self.delete(notification);
		}
		
		self.save();
		
		UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: identifiers);
		
    }
    open func manageDeletePresentedSessionNotifications(_ prefix:String? = nil, expiredAge:Int = 2, completion:(()->())?)
    {
        UNUserNotificationCenter.current().getDeliveredNotifications {[weak self] (notifs) in
            let notifsToRemove: [String] = notifs.compactMap{
                guard let trigger:UNCalendarNotificationTrigger = $0.request.trigger as? UNCalendarNotificationTrigger else {
                    return nil
                }
                let dateComponents = trigger.dateComponents
                guard let date = Calendar.current.date(from: dateComponents) else {
                    return nil
                }
                try? self?.checkNotificationForDeviation(notification: $0)
                guard date.compare(Date().addingHours(hours: -expiredAge)) == .orderedAscending else {
                    print("\(date.localizedFormat()) is greater than \(Date().addingHours(hours: -expiredAge).localizedFormat())")
                    return nil
                }

                if let p =  prefix {
                    guard $0.request.identifier.hasPrefix(p) else {
                        print("does not have prefix \(p)")

                        return nil
                    }
                }
                HMLog("Removing \($0.request.identifier)")

                return $0.request.identifier
            }
                UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: notifsToRemove);
            completion?()
        }

    }
    //Delete notifications that could have been created by previous
	//Installations of the application, or remove them for clean up sake
	open func clearAllPendingNotifications()
    {
		let notifications = getNotifications(withIdentifierPrefix: nil, onlyPending: true)
        
        for notification in notifications
        {
			delete(notification)
        }
        
        save();
		
		UNUserNotificationCenter.current().removeAllPendingNotificationRequests();

    }
    
    // deletes notifications that have already passed
    open func removePresentedNotifications() {
       
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }

	open func clearPastNotifications()
    {
        let predicate = NSPredicate(format: "scheduledAt < %@", NSDate());
        let sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
		
		guard let results:[NotificationEntry] = self.fetch(predicate: predicate, sort: sortDescriptors) else {
			return
		}

		
        MHController.dataContext.perform {
            

			
			for r in results
			{
				self.delete(r);
			}
			
			self.save();
                
				
        }

    }
	open func clearNotifications(sessionId:Int, studyId:Int)
	{
		
		
		let predicate = NSPredicate(format: "notificationIdentifier beginswith %@ AND studyID = %i AND sessionID = %i", "Session", studyId, sessionId);
		let sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
		let notifications:[NotificationEntry] = fetch(predicate: predicate, sort: sortDescriptors) ?? []
		manageDeleteNotifications(notifications: notifications);
	}
	open func getNotifications(withIdentifierPrefix identifierPrefix:String?, onlyPending:Bool = true) -> [NotificationEntry]
    {
		var predicate:NSPredicate?
        if let prefix = identifierPrefix
        {
            if onlyPending
            {
				predicate = NSPredicate(format: "notificationIdentifier beginswith %@ AND scheduledAt > %@", prefix, NSDate());
            }
            else
            {
				predicate = NSPredicate(format: "notificationIdentifier beginswith %@", prefix);
            }
        }
        let sortDescriptors = [NSSortDescriptor(key:"scheduledAt", ascending:true)];
        
        
        return fetch(predicate: predicate, sort: sortDescriptors) ?? []
       
    }
	
	open func has(scheduledDateReminder studyId:Int) -> Bool
	{
		var maybeNotifications = getNotifications(withIdentifierPrefix: "DateReminder-\(studyId)", onlyPending: false);
        let group = DispatchGroup()
        group.enter()
        UNUserNotificationCenter.current().getPendingNotificationRequests { (reqs) in
            maybeNotifications = maybeNotifications.filter({ (entry) -> Bool in
                return reqs.contains(where: { (req) -> Bool in
                    entry.notificationIdentifier == req.identifier
                })
            })
            group.leave()
        }
        group.wait()
		return maybeNotifications.count > 0;
	}
    open func scheduleDateConfirmationsForUpcomingStudy(force:Bool = false) -> Bool {
        guard let study = Arc.shared.studyController.getUpcomingStudyPeriod() else {
            return false
        }
        if !has(scheduledDateReminder: Int(study.studyID)) || force == true {
            schedule(dateConfirmationsForStudy: Int(study.studyID), force: force)
        }
        return true
    }
    open func schedule(dateConfirmationsForStudy studyId:Int, force:Bool = false) {
    
        guard let study = Arc.shared.studyController.get(study: studyId) else {
            fatalError("Invalid study ID")
        }
//        if !force {
//            guard has(scheduledConfirmationReminders: studyId) == false else {
//                return
//            }
//        }
        guard let studyDate = study.userStartDate else {
            fatalError("No user study date set")
        }
        clearDateReminders()
        //One month
        let format = ACDateStyle.longWeekdayMonthDay.rawValue
        
        let formattedDate = studyDate.localizedFormat(template: format)
        //Today must be before the target date
        guard Date().compare(studyDate.addingDays(days: -1).addingHours(hours: 12)) == .orderedAscending ||
        Date().compare(studyDate.addingDays(days: -1).addingHours(hours: 12)) == .orderedSame else {
            return
        }
        let day = scheduleNotification(date: studyDate.addingDays(days: -1).addingHours(hours: 12),
                                       title: "",
                                       body: "Reminder: Your next testing cycle begins tomorrow.\nTap to confirm or reschedule.".localized(ACTranslationKey.notification_daybefore),
                                       identifierPrefix: "DateReminder-\(studyId)")
        day.studyID = Int64(studyId)

        guard Date().compare(studyDate.addingWeeks(weeks: -1).addingHours(hours: 12)) == .orderedAscending ||
            Date().compare(studyDate.addingWeeks(weeks: -1).addingHours(hours: 12)) == .orderedSame else {
                save()
                return
        }
        
        let week = scheduleNotification(date: studyDate.addingWeeks(weeks: -1).addingHours(hours: 12),
                                        title: "",
                                        body: "Reminder: Your next testing cycle starts in one week.\nTap to confirm or reschedule.".localized(ACTranslationKey.notification_weekbefore),
                                        identifierPrefix: "DateReminder-\(studyId)")
        week.studyID = Int64(studyId)

        guard Date().compare(studyDate.addingMonths(months: -1).addingHours(hours: 12)) == .orderedAscending ||
            Date().compare(studyDate.addingMonths(months: -1).addingHours(hours: 12)) == .orderedSame else {
                save()
                return
        }
        
        let monthBody = "Your next week of testing begins in one month on \(formattedDate).\nYou may adjust this schedule up to 7 days."
            .localized(ACTranslationKey.notification_monthbefore)
            .replacingOccurrences(of: "{DATE}", with: formattedDate)
        let month = scheduleNotification(date: studyDate.addingMonths(months: -1).addingHours(hours: 12),
                                         title: "",
                                         body:monthBody,
                                         identifierPrefix: "DateReminder-\(studyId)")
        
        month.studyID = Int64(studyId)

        save()
        
    }
    
	open func schedule(dateRemdinderNotification study:StudyPeriod)
	{
		clearDateReminders()
		let studyID = Int(study.studyID)
		if let d = study.userStartDate as Date?
		{
			var fireDate:Date = d.addingDays(days: -7 * 4); // 4 weeks prior to start date
			
			if let wakeSleep = Arc.shared.scheduleController.get(entriesForDay: WeekDay.getDayOfWeek(fireDate),
																	 forParticipant: Arc.shared.participantId!)?.first {
				
				let start = wakeSleep.startTimeOn(date: fireDate)
				let end = wakeSleep.endTimeOn(date: fireDate)
				var midDay = start!.addingTimeInterval(end!.timeIntervalSince(start!) / 2.0);
				if midDay.compare(fireDate) == .orderedAscending
				{
					midDay = midDay.addingDays(days: 1);
				}
				fireDate = midDay;
			}
			
			var body = "Your next test will be on {DATE}." //"Notification text for one-month reminder"
			
			let formattedDate = DateFormatter.localizedString(from: d, dateStyle: .short, timeStyle: .none);
			
			body = body.replacingOccurrences(of: "{DATE}", with: formattedDate);
			
			let newNotification = scheduleNotification(date: fireDate, title: "", body: body, identifierPrefix: "DateReminder-\(studyID)");
			newNotification.studyID = study.studyID;
			save();
		}
	}
	open func has(ScheduledMissedTestsNotification studyId:Int) -> Bool
	{
		
        var maybeNotifications = getNotifications(withIdentifierPrefix: "MissedTests-\(studyId)", onlyPending: false);
        let group = DispatchGroup()
        group.enter()
        UNUserNotificationCenter.current().getPendingNotificationRequests { (reqs) in
            maybeNotifications = maybeNotifications.filter({ (entry) -> Bool in
                return reqs.contains(where: { (req) -> Bool in
                    entry.notificationIdentifier == req.identifier
                })
            })
            group.leave()
        }
        group.wait()
        
		return maybeNotifications.count > 0;
	}
	
	open func has(scheduledConfirmationReminderForWeek week:Int, studyId:Int) -> Bool
	{
		let maybeNotifications = getNotifications(withIdentifierPrefix: "DateConfirmation-\(studyId)-\(week)", onlyPending: false);
		return maybeNotifications.count > 0;
	}
	
	open func has(scheduledConfirmationReminders studyId:Int) -> Bool
	{
		let maybeNotifications = getNotifications(withIdentifierPrefix: "DateConfirmation-\(studyId)", onlyPending: false);
		return maybeNotifications.count > 0;
	}
	
	open func printPendingSystemNotifications()
    {
        HMLog("printing pending system notifications");
		
		UNUserNotificationCenter.current().getPendingNotificationRequests(completionHandler: { requests in
			HMLog("\(requests.count) requests.")
			for r in requests
			{
				HMLog("\(r.trigger!) | \(r.identifier) | \(r.content.title) | \(r.content.body)");
			}
		});
	}
}
