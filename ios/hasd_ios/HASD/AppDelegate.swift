//
// AppDelegate.swift
//




import UIKit
import CoreData
import Arc
import UserNotifications
#if FIREBASE
import Firebase

#endif
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?
    var environment:ArcEnvironment = HASDEnvironment.production
    
    var backgroundTask:UIBackgroundTaskIdentifier!
    var receivedNotification:UNNotificationResponse?
    var privacyProtectionWindow:UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        #if FIREBASE
        FirebaseApp.configure()
        #endif

        Arc.configureWithEnvironment(environment: environment)
		
		NotificationCenter.default.addObserver(forName: .ACDateChangeNotification, object: nil, queue: .main) { [unowned self](notif) in
			self.dateChangeAlert()
		}
		
		
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.makeKeyAndVisible()
        window?.rootViewController = UIStoryboard(name: "LaunchScreen", bundle: nil).instantiateInitialViewController()!
        ProxyManager.shared.apply()
        
        
        // we ideally need performFetchWithCompletionHandler to be called at least once a day, but since we can't specify when we'd like it called,
        // let's set the minimum time to once every 6 hours, or at most 4 times per day.
       
        
		
        
        UNUserNotificationCenter.current().delegate = self;
        
        application.setMinimumBackgroundFetchInterval(UIApplication.backgroundFetchIntervalMinimum);

        if Arc.shared.appController.isFirstLaunch
        {
            Arc.shared.appController.isFirstLaunch = false;
            Arc.shared.notificationController.clearAllPendingNotifications()
        }
		
		if let report:ErrorReport = Arc.shared.appController.read(key:"error") {
            #warning("A new UI flow should be introduced for errors caught this way.")
            Arc.handleError(report, named: "The app was forced to close:")
            #if DEBUG
            Arc.shared.displayAlert(message: "The app was forced to close: \(report.localizedDescription)", options: [.default("Okay", {
				Arc.shared.appController.delete(forKey: "error")
			})])
            #else
                Arc.shared.appController.delete(forKey: "error")

            #endif

			
		}
		Arc.shared.notificationController.printPendingSystemNotifications()

		Arc.shared.notificationController.isAuthorized(completion: { _ in
//			Arc.shared.appController.isNotificationAuthorized = $0
			OperationQueue.main.addOperation {
				Arc.shared.nextAvailableState(runPeriodicBackgroundTask: false, direction: .fade)
				
			}
			
		})
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
        HMLog("applicationWillResignActive");
        
        Arc.shared.appController.lastClosedDate = Date();
        CoreDataStack.shared.saveContext()
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        HMLog("applicationDidEnterBackground")
        showPrivacyProtectionWindow()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
		HMLog("applicationWillEnterForeground")
        UIApplication.shared.applicationIconBadgeNumber = 0
        hidePrivacyProtectionWindow()
		
		
        
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
		//Check for any survey view controllers
        HMLog("applicationDidBecomeActive")
        if let lastClosed = Arc.shared.appController.lastClosedDate
        {
            if lastClosed.timeIntervalSinceNow > (-1 * Arc.shared.studyController.TEST_TIMEOUT)
            {
                if let vc : CustomViewController<InfoView> = Arc.getTopViewController() {
                    if vc.title != "finished" {
                        return
                    }
                }
                if let vc : UIViewController = Arc.getTopViewController(), vc is ACTutorialViewController {
                    return
                }
            }
        }
        //If we aren't in a test always keep spot in surveys.
        if Arc.shared.currentTestSession == nil {
            if let vc : CustomViewController<InfoView> = Arc.getTopViewController() {
                #warning("refactor this entire method, implement state machine")
                if vc.title != "finished" {
                    return
                }
            }
        }
        if let vc : NotificationsRejectedViewController = Arc.getTopViewController() {
            vc.updateState()
            return
        }
        if let state = Arc.currentState as? HDState, [HDState.onboarding, HDState.auth, HDState.schedule, HDState.changeSchedule].contains(state) && Arc.shared.appController.commitment != .rebuked {
        
            return
        }
        Arc.shared.periodicBackgroundTask(timeout: 20) {
            
        }
        
        // Let's handle any notifications that were recieved through userNotificationCenter:didReceive
       OperationQueue.main.addOperation {
           Arc.shared.nextAvailableState(runPeriodicBackgroundTask: false, direction: .fade)

           self.handleReceivedNotification()
       }
        
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        CoreDataStack.shared.saveContext()
        
    }

    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        
        if(application.applicationState != .background)
        {
            completionHandler(.noData)
            return;
        }
//        self.backgroundTask = application.beginBackgroundTask {
//            application.endBackgroundTask(self.backgroundTask)
//            self.backgroundTask = .invalid
//        }
        HMLog("performFetchWithCompletionHandler \(Date().localizedFormat())");
        Arc.configureWithEnvironment(environment: environment)
        Arc.shared.notificationController.manageDeletePresentedSessionNotifications("TestSession") {}

        Arc.shared.appController.lastBackgroundFetch = Date()
        Arc.shared.periodicBackgroundTask(timeout: 20) {
//            UIApplication.shared.endBackgroundTask(self.backgroundTask)
//            self.backgroundTask = nil
            completionHandler(.newData);
        }
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void)
    {
        HMLog("-=-==-==-=-=-=-=-=-=-=-presenting notification");
        if notification.request.identifier.hasPrefix("MissedTests") {
            Arc.shared.notificationController.clearNotifications(withIdentifierPrefix: "MissedTests")
            Arc.shared.notificationController.save()
        }

        if notification.request.identifier.hasPrefix("Cleanup") {
            Arc.shared.notificationController.manageDeletePresentedSessionNotifications("TestSession") {
            }

        }
        completionHandler([]);
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void)
    {
        
        UIApplication.shared.applicationIconBadgeNumber = 0;
        Arc.configureWithEnvironment(environment: environment)
        do {
            try Arc.shared.notificationController.checkNotificationForDeviation(notification: response.notification, response: response)
        } catch {
            
        }

        // Store the recived notification. If the app is active, let's go ahead and process it,
        // otherwise, we'll let applicationDidBecomeActive handle it.
        
        self.receivedNotification = response
        if UIApplication.shared.applicationState == .active
        {
            self.handleReceivedNotification()
        }

        completionHandler();
    }
    
    func handleReceivedNotification()
    {
        guard let response = self.receivedNotification else { return; }
        if response.notification.request.identifier.hasPrefix("MissedTests") {
            Arc.shared.notificationController.clearNotifications(withIdentifierPrefix: "MissedTests")
            Arc.shared.notificationController.save()
        }
        if response.notification.request.identifier.hasPrefix("DateReminder") {
            dateChangeAlert()
        }
        
        self.receivedNotification = nil
    }
    
	func dateChangeAlert() {
		let longFormat = ACDateStyle.longWeekdayMonthDay.rawValue
		let upComingStudy = Arc.shared.studyController.getUpcomingStudyPeriod()
		guard let upcoming = upComingStudy, let userDate = upcoming.userStartDate else {
			return
		}
		let startDate = userDate.localizedFormat(template:longFormat)
		let endDate = userDate.addingDays(days: 6).localizedFormat(template:longFormat)
		let message = "*Your next testing cycle will be \(startDate) to \(endDate)*.\n\nPlease confirm these testing dates or adjust your schedule.".localized("notification_confirm_adjust_1")
			.replacingOccurrences(of: "{DATE1}", with: startDate)
			.replacingOccurrences(of: "{DATE2}", with: endDate)
		Arc.shared.displayAlert(message: message,
			options: [.default("CONFIRM".localized("button_confirm"), {}),
					  .default("ADJUST SCHEDULE".localized("button_adjust_sched"), {
						Arc.shared.appNavigation.navigate(state: HDState.changeStudyStart, direction: .toRight)
						
					}
				)
			]
		)
	}
    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        return true;
    }


    private func showPrivacyProtectionWindow() {
        let vc = UIStoryboard(name: "LaunchScreen", bundle: nil).instantiateInitialViewController()

        privacyProtectionWindow = UIWindow()
        privacyProtectionWindow?.rootViewController = vc
        privacyProtectionWindow?.windowLevel = .alert + 1
        privacyProtectionWindow?.makeKeyAndVisible()
    }

    private func hidePrivacyProtectionWindow() {
        privacyProtectionWindow?.isHidden = true
        privacyProtectionWindow = nil
    }
}

