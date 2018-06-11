/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
import CoreData
import UserNotifications
import Zip


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?
    var splashScreen:UIView?;
    
    let data:DNDataManager = DNDataManager.sharedInstance
    static var _window: UIWindow?
    var closeKeyboardRecognizer: UITapGestureRecognizer?;
    @discardableResult
    class func go(state:AppState, userInfo:Dictionary<String, Any>? = nil) -> DNViewController{
        let router = AppRouter()
        
        DNLog("going to state: \(state)");
        return router.go(state: state, userInfo: userInfo);
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        DNStyle.applyStyle()
        _ = DNDataManager.sharedInstance.translation
        NotificationCenter.default.addObserver(self, selector: #selector(AppDelegate.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(AppDelegate.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)

        
        #if DBG
            DNLogManager.setLogToFile(log: true);
            DNLog("--------------------------------");
            DNLog("Application did finish launching");
            DNRestAPI.shared.storeZips = true;
            DNRestAPI.shared.sendData = false;
            DNRestAPI.shared.requireRegistration = true;
        #elseif QA
            DNLogManager.setLogToFile(log: true);
            DNLog("--------------------------------");
            DNLog("Application did finish launching");
            DNRestAPI.shared.storeZips = true;
            DNRestAPI.shared.sendData = true;
            DNRestAPI.shared.requireRegistration = true;
        #endif
        
        #if CS
            DNLogManager.setLogToFile(log: false);
            DNRestAPI.shared.storeZips = false;
            DNRestAPI.shared.sendData = true;
            DNRestAPI.shared.requireRegistration = false;
            DNRestAPI.shared.sendPing = false;
        #elseif TU
            DNLogManager.setLogToFile(log: false);
            DNRestAPI.shared.storeZips = false;
            DNRestAPI.shared.sendData = true;
        #elseif EXR
            DNRestAPI.shared.requireRegistration = false;
            DNLogManager.setLogToFile(log: false);
            DNRestAPI.shared.storeZips = false;
            DNRestAPI.shared.sendData = true;
            DNRestAPI.shared.skipBaseline = true;
        #endif
        
        #if EXR_QA
            DNLogManager.setLogToFile(log: true);
            DNRestAPI.shared.storeZips = true;
        #endif
        
        // DEBUG Compilation Condition is set for non DBG/QA conditions when running from xcode.
        // When you build for archiving, DEBUG will not be set.
        
        #if DEBUG
            DNLogManager.setLogToFile(log: true);
            DNRestAPI.shared.storeZips = true;
            DNRestAPI.shared.sendData = false;
            DNRestAPI.shared.PRINT_REQUESTS = false;
            DNRestAPI.shared.requireRegistration = false;
        #endif
        
        
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().requestAuthorization(
                options: [.alert,.sound,.badge],
                completionHandler: { (granted,error) in
            })
            UNUserNotificationCenter.current().delegate = self;
        }
        else
        {
            application.registerUserNotificationSettings(
                UIUserNotificationSettings(
                    types:[UIUserNotificationType.alert, .badge, .sound],
                    categories: nil
                )
            )
        };
        
        // we ideally need performFetchWithCompletionHandler to be called at least once a day, but since we can't specify when we'd like it called,
        // let's set the minimum time to once every 6 hours, or at most 4 times per day.
        
        application.setMinimumBackgroundFetchInterval(6*60*60);
        
        
        //initialize the CoreData stack
        _ = DNDataManager.backgroundContext
        
        defer {
        
            if DNDataManager.sharedInstance.isFirstLaunch
            {
                DNDataManager.sharedInstance.isFirstLaunch = false;
                NotificationEntry.clearAllPendingNotifications();
            }
            AppDelegate.chooseDisplay(runPeriodicBackgroundTask: true);
        }
        
        
        return true
    }
    func topViewcontroller()->UIViewController? {
        if var topController = window?.rootViewController {

            while let presented = topController.presentedViewController {
                topController = presented
            }
            return topController
        }
        return nil
    }
    
    func keyboardWillShow(notification: NSNotification) {
        
        if let c = self.closeKeyboardRecognizer
        {
            self.window?.removeGestureRecognizer(c);
        }
        self.closeKeyboardRecognizer = UITapGestureRecognizer(target: self, action: #selector(tappedView(gesture:)));
        
        self.window?.addGestureRecognizer(self.closeKeyboardRecognizer!);
        
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.window?.frame.origin.y == 0{
                    OperationQueue.main.addOperation {
                    let top =  (self.topViewcontroller())!
                    if let e = findEditingTextField(view:top.view) {
                        if let textFieldBottom = e.superview?.convert((e.frame.origin), to: nil).y {
                            let bottom = textFieldBottom + e.frame.height
                            let limit = (self.window?.frame.height)! - keyboardSize.height
                            let offset = bottom - limit
                            if textFieldBottom + e.frame.height >  limit{
                                UIView.animate(withDuration: 0.2, animations: {
                                    self.window?.frame.origin.y -= offset + 10
                                    
                                })
                            }
                        }
                    }
                    

                }
            }
        }
    }
    
    func tappedView(gesture:UITapGestureRecognizer){
        self.window?.resignFirstResponder();
        self.resignFirstResponder()
        self.window?.endEditing(true);
    }
    
    func keyboardWillHide(notification: NSNotification) {
        
        if let c = self.closeKeyboardRecognizer
        {
            self.window?.removeGestureRecognizer(c);
        }
        
        self.closeKeyboardRecognizer = nil;
        
        if ((notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue) != nil {
            if self.window?.frame.origin.y != 0{
                
                
                OperationQueue.main.addOperation {
                    UIView.animate(withDuration: 0.2, animations: {

                        self.window?.frame.origin.y = 0

                    })
                }
            }
        }
    }
    
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
        DNDataManager.sharedInstance.lastClosedDate = Date();
        
        
        
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.

        DNLog("applicationDidEnterBackground");
//        self.splashScreen = Bundle.main.loadNibNamed("SplashScreen", owner: self, options: nil)?[0] as? UIView;
//        
//        if let s = splashScreen, let w = self.window
//        {
//            s.frame = w.bounds;
//            w.addSubview(s);
//        }
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        
        DNLog("applicationWillEnterForeground");
        
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
        
        splashScreen?.removeFromSuperview();
        splashScreen = nil;
        
        AppDelegate.chooseDisplay(runPeriodicBackgroundTask: true);
        UIApplication.shared.applicationIconBadgeNumber = 0

    }

    func applicationDidBecomeActive(_ application: UIApplication) {

    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        DNDataManager.save();
    }
    
    
    
    
    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
    
        if(application.applicationState != .background)
        {
            return;
        }
        
        DNLog("performFetchWithCompletionHandler");
        DNDataManager.sharedInstance.periodicBackgroundTask(timeout: 20) { 
            completionHandler(.newData);
        }
    }
    
    
/*
 
     chooseDisplay() acts as the "home" display, choosing between several different default states:
     
     1: New Participant setup
     2: Currently in a Test Session
     3: Test Session is available
     4: No Test Session at this time
     5: Not currently in an Arc
*/
    
    
    class func chooseDisplay(runPeriodicBackgroundTask:Bool = false)
    {
        
        if runPeriodicBackgroundTask
        {
            DNDataManager.sharedInstance.periodicBackgroundTask(timeout: 20) {
            }
        }
        
        DNLog("chooseDisplay");
        
        
        
        if DNDataManager.sharedInstance.languageKey == "" {
            
            #if CS
                DNDataManager.sharedInstance.languageKey = "United States - English";
            #elseif EXR
                DNDataManager.sharedInstance.languageKey = "United States - English";
            #else // TU, QA, DBG
            go(state: .setupLanguage);
            return;
            #endif
        }
        
        
        
        //1: Participant setup
        if !DNDataManager.sharedInstance.hasParticipantId()
        {
            go(state: .setupUser);
            return;
        }
        
        #if EXR
        
            if DNDataManager.sharedInstance.hasAuthenticated == false
            {
                go(state:.twoFactorEnterCode);
                return;
            }
        #endif
        
        if !DNDataManager.sharedInstance.hasSleepWakeTimes()
        {
            go(state: .setupTime);
            return;
        }
        
        //2: Currently in a Test Session.
        // If the application is currently testing, and the test has not timed out,
        // just let them continue the test.
        
        
        if DNDataManager.sharedInstance.isTesting
        {
            if let lastClosed = DNDataManager.sharedInstance.lastClosedDate
            {
                DNDataManager.sharedInstance.lastClosedDate = nil;
                if lastClosed.timeIntervalSinceNow > (-1 * TEST_TIMEOUT)
                {
                    return;
                }
                else
                {
                    DNDataManager.sharedInstance.isTesting = false;
                    DNDataManager.sharedInstance.currentTestSession = nil;
                    if let visit = TestVisit.getCurrentVisit(), let test = visit.getCurrentTestSession()
                    {
                        test.markMissed();
                    }
                }
            }
        }
        
        if let visit = TestVisit.getCurrentVisit()
        {
            
            //3: Test Session is available, but has not been started yet
            
            if visit.getAvailableTestSession() != nil
            {
                go(state:.testStart);
                return;
            }
            else    //4: no test at this time
            {
                go(state: .noTest);
                return;
            }
        }
        else    //5: no Arc at this time
        {
            #if CS
                
                //just show them a "thanks for participating" screen.
                go(state: .endProject);
                
            #elseif EXR
            
            go(state:.confirmArcDate);
            
            #else // TU, QA, DEBUG
            
            if DNDataManager.sharedInstance.visitCount == 1
            {
                go(state: .baselineWait);
            }
            else
            {
                go(state:.confirmArcDate);
            }
            return;
                
            #endif
        }
    }
    
    //MARK: - UserNotificationCenter delegate
    
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void)
    {
        DNLog("presenting notification");
        completionHandler([.alert,.sound,.badge]);
    }
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void)
    {
     

        UIApplication.shared.applicationIconBadgeNumber = 0;
        DNLog("received notification");
        
        if DNDataManager.sharedInstance.isTesting == false
        {
            AppDelegate.chooseDisplay();
        }
        
        completionHandler();
    }
}

