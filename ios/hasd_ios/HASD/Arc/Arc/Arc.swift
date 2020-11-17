//
// Arc.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import UIKit
import HMMarkup
import PDFKit
public protocol ArcApi {
	
}
public extension Notification.Name {
	static let ACDateChangeNotification = Notification.Name(rawValue: "ACDateChangeNotification")
	
}


public enum SurveyAvailabilityStatus: Equatable {
    case available, laterToday, tomorrow, startingTomorrow(String, String), laterThisCycle(String), later(String, String), finished, postBaseline
}
open class Arc : ArcApi {
	
	var ARC_VERSION_INFO_KEY = "CFBundleShortVersionString"
	var APP_VERSION_INFO_KEY = "CFBundleShortVersionString"
    public var APP_PRIVACY_POLICY_URL = ""
    public var WELCOME_LOGO:UIImage? = nil
    public var WELCOME_TEXT = ""
	public var TEST_TIMEOUT:TimeInterval = 300; // 5 minute timeout if the application is closed
	public var TEST_START_ALLOWANCE:TimeInterval = -300; // 5 minute window before actual start time
    public var activeTab:Int = 0
	var STORE_DATA = false
	var FORGET_ON_RESTART = false
    lazy var arcInfo: NSDictionary? = {
        if let path = Bundle(for: Arc.self).path(forResource: "Info", ofType: "plist") {
            return NSDictionary(contentsOfFile: path)
            
        }
        return nil
    }()
	lazy var info: NSDictionary? = {
		if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
			return NSDictionary(contentsOfFile: path)
			
		}
		return nil
	}()
    lazy public var translation:ArcTranslation? = {
        do {
            guard let asset = NSDataAsset(name: "translation") else {
                return nil
            }
            let translation:ArcTranslation = try JSONDecoder().decode(ArcTranslation.self, from: asset.data)
        
            return translation
        } catch {
            dump(error)
        }
        return nil
    }()
    public var translationIndex = 1
	lazy public var deviceString = {deviceInfo();}()
	lazy public var deviceId = AppController().deviceId
	lazy public var versionString = {info?[APP_VERSION_INFO_KEY] as? String ?? ""}()
	lazy public var arcVersion = {arcInfo?[ARC_VERSION_INFO_KEY] as? String ?? "";}()
    //A map of all of the possible states in the application
	
    static public let shared = Arc()
	
	public var appController:AppController = AppController()
	
	public var authController:AuthController = AuthController()
	
	public var sessionController:SessionController = SessionController()
	
	public var surveyController:SurveyController = SurveyController()
        
	public var scheduleController:ScheduleController = ScheduleController()
    
	public var gridTestController:GridTestController = GridTestController()
    
	public var pricesTestController:PricesTestController = PricesTestController()
    
	public var symbolsTestController:SymbolsTestController = SymbolsTestController()
	
	public var studyController:StudyController = StudyController()
    	
	public var notificationController:NotificationController = NotificationController()
	
	public var appNavigation:AppNavigationController = BaseAppNavigationController()
	
	public var controllerRegistry:ArcControllerRegistry = ArcControllerRegistry()
	
	public var earningsController:EarningsController = EarningsController()
	//Back this value up with local storage.
	//When the app terminates this value is released,
	//This will cause background processes to crash when fired.
	public var participantId:Int? {
		get {
			return appController.participantId
		}
		set {
			appController.participantId = newValue
		}
	}
    
	public var currentStudy:Int?
	public var availableTestSession:Int?
	public var currentTestSession:Int?
	static public var currentState:State?
    static public var environment:ArcEnvironment?
	static public var debuggableStates:[State] = []
	
	public init() {
		controllerRegistry.registerControllers()
	}
    static public func configureWithEnvironment(environment:ArcEnvironment) {
        self.environment = environment

		Arc.debuggableStates = environment.debuggableStates
        HMAPI.baseUrl = environment.baseUrl ?? ""
		CoreDataStack.useMockContainer = environment.mockData

        _ = MHController.dataContext
        
        _ = HMAPI.shared
        HMRestAPI.shared.blackHole = environment.blockApiRequests
        Arc.shared.appNavigation = environment.appNavigation
        Arc.shared.studyController = environment.studyController
        Arc.shared.authController = environment.authController
        Arc.shared.appController = environment.appController
        
        Arc.shared.surveyController = environment.surveyController
        Arc.shared.notificationController = environment.notificationController
        Arc.shared.scheduleController = environment.scheduleController
        Arc.shared.sessionController = environment.sessionController
        Arc.shared.gridTestController = environment.gridTestController
        Arc.shared.pricesTestController = environment.pricesTestController
        Arc.shared.symbolsTestController = environment.symbolsTestController
        
        let locale = Locale.current

        
        Arc.shared.setLocalization(country: Arc.shared.appController.country ?? locale.regionCode,
                                   language: Arc.shared.appController.language ?? locale.languageCode)

        
        
        Arc.shared.WELCOME_LOGO =  UIImage(named: environment.welcomeLogo ?? "")
        Arc.shared.WELCOME_TEXT = environment.welcomeText ?? ""
        Arc.shared.APP_PRIVACY_POLICY_URL = environment.privacyPolicyUrl ?? ""
		
        
        if let arcStartDays = environment.arcStartDays {
            Arc.shared.studyController.ArcStartDays = arcStartDays
        }
        environment.configure()

    }
    public func nextAvailableState(runPeriodicBackgroundTask:Bool = false, direction:UIWindow.TransitionOptions.Direction = .toRight) {
		let state = appNavigation.nextAvailableState(runPeriodicBackgroundTask: runPeriodicBackgroundTask)
		HMLog("Navigating to:  \(state)")
		Arc.currentState = state
        appNavigation.navigate(state: state, direction: direction)
	}
	public func nextAvailableSurveyState() {
		
		let state = appNavigation.nextAvailableSurveyState() ?? appNavigation.defaultState()
		Arc.currentState = state
		appNavigation.navigate(state: state, direction: .toRight)
	}
	
	//A friend of a friend is a stranger.
	public static func store<T:Codable>(value:T?, forKey key:String) {
		Arc.shared.appController.store(value: value, forKey: key)

	}
	public static func delete(forKey key:String) {
		Arc.shared.appController.delete(forKey: key)

	}
	public static func read<T:Codable>(key:String) -> T? {
		return Arc.shared.appController.read(key: key)

	}

    ///Errors passed into this function will be uploaded using the crash reporter library. Choosing to raise
    ///the error will cause execution of the application to abort.
    ///Calling this function in try/catch handlers is recommended if you wish to log aberrant behavior
    ///or track when later maintenance changes the internal state of the application unexpectedly.
    /// - parameters:
    ///     - error: The error thrown.
    ///     - name: The name to give to the exception that will be uploaded.
    ///     - shouldRaise: a flag telling the application to end execution immediately.
    /// - attention: Choosing to raise an error will prevent the error from uploading immediately.
    /// it will, however, trigger the default crash reporter behavior and save the report. Then it will upload on
    /// next run of the application.
    /// - note: the should raise flag is especially useful for DEV and QA builds.
    /// - warning: Never raise during Production, fail safely and return the ui to a functional state.
    /// - version: 0.1
    public static func handleError(_ error:Error, named name:String, shouldRaise:Bool = false, userInfo:[AnyHashable:Any]? = nil) {
        let description = error.localizedDescription
        let exception = NSException(name: NSExceptionName(name),
                                    reason: description,
                                    userInfo: nil)
        handleException(exception)
    }
    public static func handleError(_ error:ErrorReport, named name:String, shouldRaise:Bool = false, userInfo:[AnyHashable:Any]? = nil) {
        let description = error.localizedDescription
        let exception = NSException(name: NSExceptionName(name),
                                    reason: description,
                                    userInfo: nil)
        handleException(exception)
    }
    private static func handleException(_ exception:NSException, raise:Bool = false) {
        // Handle exceptions

    }
    @discardableResult
    public func displayAlert(message:String, options:[MHAlertView.ButtonType], isScrolling:Bool = false) -> MHAlertView {
        let view:MHAlertView = (isScrolling) ? .get(nib: "MHScrollingAlertView") : .get()
        view.alpha = 0
        
        guard let window = UIApplication.shared.keyWindow else {
            return view
        }
        
        guard let _ = window.rootViewController else {
            
            return view
        }
        window.constrain(view: view)
        view.set(message: message, buttons: options)
        UIView.animate(withDuration: 0.35, delay: 0.1, options: .curveEaseOut, animations: {
            view.alpha = 1
        }) { (_) in
            
        }
		return view
    }
    public func setCountry(key:String?) {
        appController.country = key
    }
    public func setLanguage(key:String?) {
        appController.language = key
    }
	public func setLocalization(country:String?, language:String?, shouldTranslate:Bool = true) {
        
        let matchesBoth = Arc.shared.translation?.versions.filter {
            $0.map?["country_key"] == country && $0.map?["language_key"] == language
        }
        let matchesCountry = Arc.shared.translation?.versions.filter {
            $0.map?["country_key"] == country
        }
        let matchesLanguage = Arc.shared.translation?.versions.filter {
            $0.map?["language_key"] == language
        }
        
        var config = HMMarkupRenderer.Config()
        config.shouldTranslate = shouldTranslate
        switch (country, language) {
        
        case (nil, _):
			
            config.translation = matchesLanguage?.first?.map

            break

        case (_, nil):
            config.translation = matchesCountry?.first?.map

            break
        case (_, _):
            
            config.translation = matchesBoth?.first?.map

            break
        
        }
        HMMarkupRenderer.config = config

    }
    public func setLocalization(index:Int = 1) {
       
            var config = HMMarkupRenderer.Config()
            config.shouldTranslate = true
            config.translation = Arc.shared.translation?.versions[index].map
            HMMarkupRenderer.config = config
      
    }
	public func deviceInfo() -> String
	{
		let deviceString = " \(UIDevice.current.systemName)|\(deviceIdentifier())|\(UIDevice.current.systemVersion)";
		return deviceString;
	}
    
    public func uploadTestData() {
		MHController.dataContext.perform {
			Arc.shared.sessionController.sendFinishedSessions()
			Arc.shared.sessionController.sendMissedSessions()
			Arc.shared.sessionController.sendSignatures()
			Arc.shared.sessionController.clearUploadedSessions()
			
			if !Arc.shared.appController.testScheduleUploaded{
				let studies = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID})
				Arc.shared.sessionController.uploadSchedule(studyPeriods: studies)
			}
			if !Arc.shared.appController.wakeSleepUploaded {
				if let study = Arc.shared.studyController.getAllStudyPeriods().sorted(by: {$0.studyID < $1.studyID}).first {
					Arc.shared.scheduleController.upload(confirmedSchedule: Int(study.studyID));
				}
				
			}
			
		}
		
	}
	
	public func sendHeartBeat() {
		guard !HMRestAPI.shared.blackHole else {
			return
		}
		HMAPI.deviceHeartbeat.execute(data: HeartbeatRequestData()) { (response, data, _) in
			HMLog("received response \(data?.toString() ?? "") on \(Date())")

		}
	}
  
	public func periodicBackgroundTask(timeout:TimeInterval = 20, completion: @escaping()->Void)
	{
		let now = Date();
		// check to see if we need to schedule any notifications for upcoming Arcs
		// If the participant hasn't confirmed their start date, we should send notifications periodically in the weeks leading up
		// to the Arc.
        let app = Arc.shared
        
        //Check for participant setup
        if app.participantId == nil {
            
            //If none set up go to auth
            guard let id = app.authController.checkAuth() else {
                completion();
                return
            }
            
            //set the id we can skip past this once set
            app.participantId = Int(id)
        }
        MHController.dataContext.performAndWait {
            if app.appController.timePeriodPassed(key: "heartBeat",
                                                  date: Date().addingHours(hours: -6)) {
                Arc.shared.appController.lastFetched["heartBeat"] = Date().timeIntervalSince1970
                HMAPI.deviceHeartbeat.execute(data: HeartbeatRequestData())

                HMAPI.getContactInfo.execute(data: nil, completion: { (res, obj, err) in
                    guard err == nil && obj?.errors.isEmpty ?? true else {
                        return;
                    }
                    guard let contact_info = obj?.response?.contact_info else {
                        return;
                    }
                    DispatchQueue.main.async{

                        if let json = try? JSONEncoder().encode(contact_info)
                        {
                            CoreDataStack.currentDefaults()?.set(json, forKey: "contact_info");
                        }

                    }
                });
                uploadTestData()
            }
		
        }
		
        if let study = studyController.getCurrentStudyPeriod()
        {
            let studyId = Int(study.studyID)
            MHController.dataContext.performAndWait {
                self.studyController.markMissedSessions(studyId: studyId)
                Arc.shared.notificationController.save()
            }
  
        }
        
		
		MHController.dataContext.performAndWait {
			Arc.shared.notificationController.clear(sessionNotifications: 0)
			Arc.shared.notificationController.schedule(upcomingSessionNotificationsWithLimit: 32)
            Arc.shared.notificationController.manageDeletePresentedSessionNotifications("TestSession") {}
            Arc.shared.notificationController.manageDeletePresentedSessionNotifications("MissedTest", expiredAge: 4) {}

			Arc.shared.notificationController.save()
		}

		
		
		if let study = studyController.getUpcomingStudyPeriod()
		{
			let studyId = Int(study.studyID)
            if Arc.environment?.shouldDisplayDateReminderNotifications ?? false {
                _ = Arc.shared.notificationController.scheduleDateConfirmationsForUpcomingStudy()
            }
			if  let startDate = study.userStartDate as Date?
			{
				if study.hasConfirmedDate == false
				{

					if notificationController.has(scheduledDateReminder: studyId) == false
					{
//						notificationController.schedule(dateRemdinderNotification: study);
					}

					if notificationController.has(scheduledConfirmationReminders: studyId) == false
					{
//						study.scheduleConfirmationReminders();

					}
				}
				else
				{
					
//					study.clearConfirmationReminders();
//					study.clearDateReminderNotification();
				}

				// if we're one day away, and we haven't scheduled sessions yet, do so now.
				if startDate.daysSince(date: now) == 1 &&
					study.hasScheduledNotifications == false
				{
//					study.createTestSessions();
//					study.scheduleSessionNotifications();
					//notificationController.schedule(sessionNotifications: studyId)
					notificationController.schedule(upcomingSessionNotificationsWithLimit: 32)
				}
			}
		}
		
		
		// Now check if we have any past visits  that need to be marked as missed
		
		let studies  = studyController.getPastStudyPeriods()
		
		for study in studies
		{
			MHController.dataContext.performAndWait {
				self.studyController.markMissedSessions(studyId: Int(study.studyID));

			}
			if STORE_DATA == false
			{
				// delete any past Arcs that have had all of their data uploaded successfully
				let sessions = studyController.get(allSessionsForStudy: Int(study.studyID));
				
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
					MHController.dataContext.performAndWait {
						
						HMLog("Deleting Visit \(study.studyID)");
						for session in sessions
						{
                            session.clearData()
						}
						
						self.studyController.save();
					}
				}
			}
		}
		uploadTestData()
	
		
		completion();
		
	}
	public func startTestIfAvailable() -> Bool {
		let app = Arc.shared
		
		guard let study = app.studyController.getCurrentStudyPeriod() else {
			return false
		}
		//If we have a current session store it here.
		guard let id = app.studyController.get(availableTestSession: Int(study.studyID))?.sessionID else {
			app.availableTestSession = nil
			return false
		}
		app.currentStudy = Int(study.studyID)
		app.currentTestSession = Int(id)
		app.studyController.mark(started: Int(id), studyId: Int(study.studyID))
		Arc.shared.appController.lastClosedDate = nil;
		
		return true
		
			
		
	}
    
   public static func getTopViewController<T:UIViewController>() -> T?{
	   guard let window = UIApplication.shared.keyWindow else {

		   return nil
	   }

	   guard let view = window.rootViewController else {
		   
		   return nil
	   }
	   if let nav = view as? UINavigationController {
		   return nav.topViewController as? T
	   }
	   if let tabs = view as? UITabBarController {
		   return tabs.selectedViewController as? T
	   }
	   return view as? T
   }
    open func getSurveyStatus() -> SurveyAvailabilityStatus {
        var upcoming:Session?
        var session:Int?
        if let s = currentStudy {
            //Get upcomming sessions ensuring that only valid test are considered
            upcoming = studyController.get(upcomingSessions: Int(s))
				.filter {$0.missedSession == false}
				.filter {$0.completeTime == nil}
				.filter {$0.uploaded == false}
				.first
            
            if let sess = availableTestSession {
                session = sess
            }
        }
		//Our first check looked into the current studyCycle, this check
		//Is intended to grab the next upcoming session regardless of studyPeriod.
		//It will be used to determine if and when
        if upcoming == nil{
            upcoming = studyController.getUpcomingSessions(withLimit: 1).first
        }
        // Do any additional setup after loading the view.
        if let _ = session {
            
            return .available
        } else {
            
            if let upcoming = upcoming {
                // let d = DateFormatter()
                let date = upcoming.sessionDate ?? Date()
                
                if date.isToday() {
                    
                    return .laterToday
                } else if date.isTomorrow() {
                    if Arc.shared.studyController.studyState == .baseline {
                        return .postBaseline
                    }
                    
                    if Arc.shared.studyController.getCurrentStudyPeriod() == nil {
                        let dateString = date.localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue, options: 0, locale: nil)
                        let endDateString = date.addingDays(days: 6).localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue, options: 0, locale: nil)
                        return .startingTomorrow(dateString, endDateString)
                    }
                    return .tomorrow
                }
//				else if date < upcoming.study?.endDate ?? Date() {
//					let dateString = date.localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue, options: 0, locale: nil)
//					return .laterThisCycle(dateString)
//				}
				else {
                    let dateString = date.localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue, options: 0, locale: nil)
                    let endDateString = date.addingDays(days: 6).localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue, options: 0, locale: nil)
                    return .later(dateString, endDateString)
                }
            } else {
                
                return .finished
            }
            
            
        }
    }
	fileprivate var dataPage:Int = 0

	public func debugData() {
		guard let study = studyController.getCurrentStudyPeriod() else {
			return
		}
		guard let sessions = study.sessions?.array as? [Session] else {return}
		let data = sessions.map {
			return FullTestSession(withSession: $0)
		}
		if dataPage > data.count {
			dataPage = 0
		}
		if dataPage < 0 {
			dataPage = data.count - 1
		}
		let view = displayAlert(message: data[dataPage].toString(),
			options:  [
				.default("Next Page", {
					[weak self] in
					self?.dataPage += 1
					
					self?.debugData()
					
				}),
				.default("Previous Page", {[weak self] in
					self?.dataPage -= 1

					self?.debugData()
					
				}),

				.default("Notifications", {[weak self] in self?.debugNotifications()}),
				.default("Schedule", {[weak self] in self?.debugSchedule()}),
																   .cancel("Close", {})],
			isScrolling: true)
		view.messageLabel.textAlignment = .left
		view.messageLabel.font = UIFont.systemFont(ofSize: 12)

		
	}
	public func debugScreens() {
		guard let url = Arc.screenShotApp(states:Arc.debuggableStates) else {
			return
		}
		dump(url)
		
		guard let window = UIApplication.shared.keyWindow else {
            return
        }
		let pdfViewer = ACPDFViewController()
		pdfViewer.modalPresentationStyle = .pageSheet
		pdfViewer.setDocument(url: url)
		window.rootViewController?.present(pdfViewer, animated: true, completion: nil)
       
	}
	public func debugSchedule(states:[State]? = nil) {
        let dateFrame = studyController.getCurrentStudyPeriod()?.userStartDate ?? Date()
        let lastFetch = appController.lastBackgroundFetch?.localizedFormat()
        let list = studyController.getUpcomingSessions(withLimit: 32, startDate: dateFrame as NSDate)
            .map({
                " w:\($0.week) d:\($0.day)\n\($0.study?.studyID ?? -1)-\($0.sessionID): \($0.sessionDate?.localizedString() ?? "") \(($0.finishedSession) ?  "√" : "\(($0.missedSession) ? "x" : "\(($0.startTime == nil) ? "-" : "o")")") \($0.uploaded ? "∆" : "") "
            }).joined(separator: "\n")
        
        
        displayAlert(message:  """
            Study: \(currentStudy ?? -1)
            
            Test: \(availableTestSession ?? -1)
            
            Last background Fetch:
            \(String(describing: (lastFetch != nil) ? lastFetch : "None"))
            
            Last flagged missed test count: \(appController.lastFlaggedMissedTestCount)
            
            \(list)
            """, options:  [
                            .default("Notifications", {[weak self] in self?.debugNotifications()}),
							.default("Data", {[weak self] in self?.debugData()}),
							.default("Screens", {[weak self] in self?.debugScreens()}),
                            .cancel("Close", {})],
                 isScrolling: true)
    }
    public func debugNotifications() {
        
        let list = notificationController.getNotifications(withIdentifierPrefix: "TestSession").map({"\($0.studyID)-\($0.sessionID): \($0.scheduledAt!.localizedString())\n"}).joined()
        let preTestNotifications = notificationController.getNotifications(withIdentifierPrefix: "DateReminder").map({"\($0.studyID)-\($0.sessionID): \($0.scheduledAt!.localizedString())\n"}).joined()
        
        displayAlert(message:  """
            Study: \(currentStudy ?? -1)
            
            Test: \(availableTestSession ?? -1)
            
            \(list)
            Date Reminders:
            \(preTestNotifications)
            """, options:  [.default("Schedule", {[weak self] in self?.debugSchedule()}),
							.default("Data", {[weak self] in self?.debugData()}),
                            .cancel("Close", {})],
                 isScrolling: true)
    }
	
	public static func get(flag:ProgressFlag) -> Bool {
		return Arc.shared.appController.flags[flag.rawValue] ?? false
	}
	public static func set(flag:ProgressFlag) {
		Arc.shared.appController.flags[flag.rawValue] = true
	}
	public static func remove(flag:ProgressFlag) {
		Arc.shared.appController.flags[flag.rawValue] = false
	}
	
	public static func apply(forVersion version:String) {
		let components = version.components(separatedBy: ".")
		let major:Int = Int(components[0]) ?? 0
		let minor:Int = Int(components[1]) ?? 0
		let patch:Int = Int(components[2]) ?? 0
		for flag in ProgressFlag.prefilledFlagsFor(major: major, minor: minor, patch: patch) {
			set(flag: flag)
		}
	}
	public static func displayDateShift(state:State) {
		let longFormat = ACDateStyle.longWeekdayMonthDay.rawValue
		let upComingStudy = Arc.shared.studyController.getUpcomingStudyPeriod()
		guard let upcoming = upComingStudy, let userDate = upcoming.userStartDate else {
			return
		}
		let startDate = userDate.localizedFormat(template:longFormat)
		let endDate = userDate.addingDays(days: 6).localizedFormat(template:longFormat)
		let message = "*Your next testing cycle will be \(startDate) to \(endDate)*.\n\nPlease confirm these testing dates or adjust your schedule.".localized(ACTranslationKey.overlay_nextcycle)
			.replacingOccurrences(of: "{DATE1}", with: startDate)
			.replacingOccurrences(of: "{DATE2}", with: endDate)
		Arc.shared.displayAlert(message: message,
			options: [.default("CONFIRM".localized(ACTranslationKey.button_confirm), {}),
					  .default("ADJUST SCHEDULE".localized(ACTranslationKey.button_adjustschedule), {
						Arc.shared.appNavigation.navigate(state: state, direction: .toRight)
						
					}
				)
			]
		)
	}
	public static func screenShotApp(states:[State]) -> URL? {
		var urls:[URL] = []
			
		let results = states
		.lazy
			.compactMap(Arc.screenShot)
			
		var images:[UIImage] = []
		for result in results {
			images.append(contentsOf: result)
		}
		
		
		return createPDFDataFromImage(images: images)
	}
	
	public static  func screenShot(state:State) -> [UIImage]? {
		let vc = state.viewForState()
		var images = [UIImage]()
		print("Snapshotting \(state)")
		if let v = vc as? BasicSurveyViewController {
			v.autoAdvancePageIndex = false
			for question in (v.questions + (v.subQuestions ?? [])).enumerated() {
				
				
				//This controller uses internal indexing, a side effect is triggered by addController
				
				v.addController(v.customViewController(forQuestion: question.element))
				v.currentIndex = question.offset
				v.display(question: question.element)
				guard let image = imageFromView(view: v.view) else {
					continue
				}
				images.append(image)
			}
			return images
		}
		if 	let v = vc as? CustomViewController<ACTemplateView>,
			let image = imageFromView(view: v.customView.root.subviews[0]) {
			return [image]
		}
		return nil

	}
	public static  func screenShot(viewController:UIViewController) -> UIView? {
		
		return viewController.view
	}
	public static  func imageFromView(view:UIView) -> UIImage? {
		let renderer = UIGraphicsImageRenderer(size: view.bounds.size)
		print("Drawing image")
		let image = renderer.image { ctx in
			view.drawHierarchy(in: view.bounds, afterScreenUpdates: true)
		}
		
		return image
	}
	public static  func save(image:UIImage, withName name:String) -> URL? {
		if let data = image.pngData() {
			let filename = getCachesDirectory().appendingPathComponent(name)
			try? data.write(to: filename)
			return filename
		}
		return nil
	}
	
	public static func createPDFDataFromImage(images: [UIImage]) -> URL? {
		let pdfData = NSMutableData()
		guard let window = UIApplication.shared.keyWindow else {
			assertionFailure("No Keywindow")
			
			return nil
		}
		let imageRect = CGRect(x: 0, y: 0, width: window.frame.width + 200, height: window.frame.height + 200)
		UIGraphicsBeginPDFContextToData(pdfData, imageRect, nil)
		for image in images {
			UIGraphicsBeginPDFPage()
			let context = UIGraphicsGetCurrentContext()
			context?.setFillColor(UIColor.black.cgColor)
			context?.fill(imageRect)
			
			image.draw(in: CGRect(x: 100, y: 50, width: image.size.width, height: image.size.height))
		}
		UIGraphicsEndPDFContext()

		//try saving in doc dir to confirm:
		let dir = getCachesDirectory()
		let path = dir.appendingPathComponent("ScreenShots.pdf")

		do {
				try pdfData.write(to: path, options: NSData.WritingOptions.atomic)
		} catch {
			print("error catched")
		}

		return path
	}
	public static func getDocumentsDirectory() -> URL {
		let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
		return paths[0]
	}
	public static func getCachesDirectory() -> URL {
		let paths = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)
		return paths[0]
	}
	
}

