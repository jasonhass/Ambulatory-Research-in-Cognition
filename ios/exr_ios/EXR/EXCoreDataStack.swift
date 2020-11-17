//
// EXCoreDataStack.swift
//



import Foundation
import CoreData
import Arc


open class EXCoreDataStack {
	
	
	// MARK: - Core Data stack
	static public var isSaving:Bool = false
	static public let shared = EXCoreDataStack()
	
	
	static public var legacyDefaults = UserDefaults(suiteName: "com.washu.arc")
	
	static public func currentDefaults() -> UserDefaults? {
		
		return legacyDefaults
	}
	
	lazy var applicationDocumentsDirectory: URL = {
		// The directory the application uses to store the Core Data store file. This code uses a directory named "com.cadiridris.coreDataTemplate" in the application's documents Application Support directory.
		let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
		return urls[urls.count-1]
	}()
	
	lazy var legacyManagedObjectModel: NSManagedObjectModel = {
		// The managed object model for the application. This property is not optional. It is a fatal error for the application not to be able to find and load its model.
		let bundle = Bundle(for: EXCoreDataStack.self);
		let modelURL = bundle.url(forResource: "DIAN_Pilot", withExtension: "momd")!
		return NSManagedObjectModel(contentsOf: modelURL)!
	}()
	
	lazy var managedObjectContext: NSManagedObjectContext = {
		// Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.) This property is optional since there are legitimate error conditions that could cause the creation of the context to fail.
		let coordinator = self.legacyPersistentContainer
		var managedObjectContext = NSManagedObjectContext(concurrencyType: .mainQueueConcurrencyType)
		managedObjectContext.persistentStoreCoordinator = coordinator
		managedObjectContext.mergePolicy = NSMergeByPropertyObjectTrumpMergePolicy
		return managedObjectContext
	}()
	
	lazy var legacyPersistentContainer: NSPersistentStoreCoordinator = {
		// The persistent store coordinator for the application. This implementation creates and returns a coordinator, having added the store for the application to it. This property is optional since there are legitimate error conditions that could cause the creation of the store to fail.
		// Create the coordinator and store
		let coordinator = NSPersistentStoreCoordinator(managedObjectModel: self.legacyManagedObjectModel)
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

	public func migrate(onComplete:@escaping (Result<Void, Error>)->()) {
		OperationQueue().addOperation {
			//If we don't have a legacy id then we're done, no migration needed.
			guard let id = EXCoreDataStack.legacyDefaults?.value(forKey: "participantId") as? String else {
				DispatchQueue.main.async {
					onComplete(.success(()))
				}
				return
			}
			Arc.shared.appController.flags["migration_in_progress"] = true
			//If we do, update our current app, this way the user will not be shown
			//a login screen.
			Arc.shared.participantId = Int(id)
			
			
			//If there aren't any files then we're done, we only check in case there was a failure
			//or it hasn't been done before.
			guard let urls = FileManager.default.urls(for: .documentDirectory) else {
				DispatchQueue.main.async {
					onComplete(.success(()))
				}
				return
			}
		
			//Perform a series of steps, throwing erros and aborting the task on failure.
			//Errors thrown are handled by the caller. (Update ui, alert the system of failure)
		
			let oldId = UIDevice.current.identifierForVendor!.uuidString
			let newId = Arc.shared.deviceId
			
			if Arc.shared.appController.flags["migration_deviceId"] != true
			{

				//Update the device id if we fail here, the rest of the calls will fail.
				switch Await(DataMigration.deviceIdUpdate).execute((oldId, newId)) {
				case .success():
					Arc.shared.appController.flags["migration_deviceId"] = true

					break
				case .failure(let error):
					DispatchQueue.main.async {
						onComplete(.failure(error))
					}
					return
				}
			}
			
			//Set their commitment to committed by default. 
			if Arc.shared.appController.flags["migration_commitment"] != true {
				Arc.shared.appController.commitment = .committed
				Arc.shared.appController.flags["migration_commitment"] = true
				
				Arc.apply(forVersion:"1.9.5")
			}
			
			//This creates a list that will only do work if a subscript is accessed.
			let responses = urls
				//Creates a lazy collection
				.lazy
				//Anything that makes it past here will be processed
				.filter { $0.pathExtension == "zip"}
				
				//The input $0, a URL, will run in upload and produce an output Result<URL, MigrationError>
				//Since this is the last step, the array of URL will become an array of lazily
				//generated .sucess or .failure objects.
				.map {Await(DataMigration.upload).execute($0)}
			
			//Every time an iteration is triggered it will only
			//execute the code within the loop if it passes .filter { $0.pathExtension == "zip"} on line 120
			//(That code has not executed yet.)
			let count = responses.count
			
			if count > 0 {
				var current = 0
				var progress = Double(current) / Double(count)
				NotificationCenter.default.post(name: .ACMigrationElementCompleted, object: progress)

				for response in responses {
					switch response {
					case .success(let url):
							current += 1
							progress = Double(current) / Double(count)
						_ = DataMigration.delete(file: url)
						HMLog("Uploaded and deleted: \(url)")
						NotificationCenter.default.post(name: .ACMigrationElementCompleted, object: progress)
					case .failure(let error):
						HMLog("Failed with error: \(error)")
						DispatchQueue.main.async {
							onComplete(.failure(error))
						}
						return
						
					}
					
				}
			}
            
			if Arc.shared.appController.flags["migration_sessionInfo"] != true
			{
				switch Await(DataMigration.updateSessionInfo).execute(()){
				case .success():
					Arc.shared.appController.flags["migration_sessionInfo"] = true
					
					break
				case .failure(let error):
					DispatchQueue.main.async {
						onComplete(.failure(error))
					}
					return
				}
			}
			NotificationCenter.default.post(name: .ACMigrationElementCompleted, object: 1.0)
			//Return to the main queue as a courtesy to api users.
			DispatchQueue.main.async {
				onComplete(.success(()))

			}
			
			
		}
		
	}
	
	func getDocumentsDirectory() -> URL {
		let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
		let documentsDirectory = paths[0]
		return documentsDirectory
	}

	public func fetch<T:NSManagedObject>(predicate:NSPredicate? = nil, sort:[NSSortDescriptor]? = nil, limit:Int? = nil) -> [T]? {
		do {
			if let fetchRequest:NSFetchRequest<T> = T.fetchRequest() as? NSFetchRequest<T> {
				fetchRequest.predicate = predicate
				fetchRequest.sortDescriptors = sort
				if let limit = limit {
					fetchRequest.fetchLimit = limit
				}
				let results = try  self.managedObjectContext.fetch(fetchRequest)
				return results
			}
		}  catch {
			print(error)
		}
		return nil
	}
}
extension FileManager {
	func urls(for directory: FileManager.SearchPathDirectory, skipsHiddenFiles: Bool = true ) -> [URL]? {
		let documentsURL = urls(for: directory, in: .userDomainMask)[0]
		let fileURLs = try? contentsOfDirectory(at: documentsURL, includingPropertiesForKeys: nil, options: skipsHiddenFiles ? .skipsHiddenFiles : [] )
		return fileURLs
	}
}

struct DataMigration {
	
	enum MigrationError:Error {
		case noFile(URL)
		case noManagedObject
		case noVisitFound
		case errorFetchingData(Error)
		case noResponse
		case statusFailure(Int, [String:[String]])
		
		var localizedDescription: String {
			switch self {
			case .noFile(let url):
				return "No file found for \(url)"
			
			case .noManagedObject:
				return "No object retrieved to work with."
			case .noVisitFound:
				return "No visits found when checking legacy database."
			
			case .errorFetchingData(let error):
				return "Error fetching Data: \(error.localizedDescription)"
			
			case .noResponse:
				return "No response from server."
			
			case .statusFailure(let status, let errors):
				return "recieved status: \(status) with errors from server: \(errors)."
			default:
				return "Unknown error."
			}
		}
	}
	struct DeviceUpdate : Codable {
		let new_device_id:String
	}
	
	static public let uploadZip:HMAPIRequest<Data, HMResponse> = .post("/submit-zip")
	static public let updateDevice:HMAPIRequest<DeviceUpdate, HMResponse> = .post("/update-device-id")
	
	
	static public func delete(file:URL) -> Bool {
		let manager = FileManager.default
		if manager.fileExists(atPath: file.path) && manager.isDeletableFile(atPath: file.path) {
			do {
				try manager.removeItem(at: file)
				return true
			} catch {
				HMLog("\(error)")
				return false
			}
		}
		return false
	}
	static public func dataFor(file:URL) -> Data? {
		if FileManager.default.fileExists(atPath: file.path) == false
		{
			HMLog("Error sending file, file \(file.lastPathComponent) does not exist");
			return nil
		}
		do {
			return try Data(contentsOf: file)
		} catch {
			return nil
		}
	}
    
    
	static public func updateSessionInfo(input:Void, didFinish:@escaping ((Result<Void, MigrationError>)->Void)) {
		
        
		var currentVisitId:Int64 = 1
        var currentVisitUserStartDate:Date? = nil;
        
		var didFail = false
		let legacyContext = EXCoreDataStack.shared.managedObjectContext
		legacyContext.performAndWait {
            
            let allVisits:[TestVisit] = EXCoreDataStack.shared.getAllVisits()
            var latestSessionData = SessionInfoResponse.TestState()
            let now = Date()
            if(allVisits.count == 0)
            {
                didFail = true
                return
            }
            
            // Let's look through all of the visits, to best determine where we need to start.
            // allVisits is ordered on visitStartDate ascending, so we know that each visit will be later than the last.
            // Realistically, we should only ever expect to have one or maybe two visits.
            
            
            for (i,visit) in allVisits.enumerated()
            {
                let visitId = max(0, visit.visitID - 1)
                let visitStartDate = visit.visitStartDate!
                let userStartDate = visit.userStartDate ?? visitStartDate
                let userEndDate = visit.userEndDate ?? visit.visitEndDate!
                let studyBeginDate = visitStartDate.addingWeeks(weeks: -26 * Int(visitId)).addingDays(days: -1)
                // The first visit is probably the most likely to be accurate, so let's use it to estimate when their real start date
                // should have been.
                if(i == 0)
                {
                    Arc.shared.studyController.beginningOfStudy = studyBeginDate
                }
                
                // We want to get the most recently passed session_id for each visit.
                // If the visit has any upcoming sessions, we need get the proper session id. So get the starting session id for the visit,
                // and add that to the legacy session id. The legacy sessions always start over at 0 for each visit.
                
                if userStartDate <= now, userEndDate >= now, let session = visit.getUpcomingSessions().first
                {
                    latestSessionData.session_date = session.sessionDate!.timeIntervalSince1970
                    let session_id = Int(session.sessionID) + EXPhase.startingSessionId(forStudyId: Int(visitId))
                    latestSessionData.session_id = "\(session_id)"
                }
                // Or if it's a past visit, let's get the lsat session id.
                else if userEndDate <= now
                {
                    latestSessionData.session_date = userEndDate.timeIntervalSince1970
                    latestSessionData.session_id = "\(EXPhase.startingSessionId(forStudyId: Int(visitId + 1)) - 1)"
                }
                // Otherwise, if this is an upcoming visit, let's figure out roughly when the last visit would have been, and set
                // the last test session appropriately
                else if userStartDate > now
                {
                    latestSessionData.session_date = userEndDate.addingWeeks(weeks: -26).timeIntervalSince1970
                    latestSessionData.session_id = "\(EXPhase.startingSessionId(forStudyId: Int(visitId)) - 1)"
                }
                
                // If this is a current visit, or a past visit, let's hold onto the start date and visit id.
                // Since the visits are sorted by their start date, we don't really need to worry about the start dates
                // contradicting
                
                if userEndDate < now || (userStartDate <= now && userEndDate >= now)
                {
                    currentVisitId = visitId
                    currentVisitUserStartDate = userStartDate
                }
            }

			Arc.shared.studyController.latestTest = latestSessionData
			
		}
        
		if didFail {
			didFinish(.failure(.noVisitFound))
			return
		}
			
		MHController.dataContext.performAndWait {
			//Get the begining of the study to to set the start date
			//This value is offset by one day to account for the extra day in EXR
			let beginningOfStudy = 	Arc.shared.studyController.beginningOfStudy
			HMLog("Start date updated to: \(beginningOfStudy.localizedFormat())")

			//Create an artificial first test value to reference later when the user decides to reschedule
			var first = SessionInfoResponse.TestState()
			first.session_date = beginningOfStudy.timeIntervalSince1970
			Arc.shared.studyController.firstTest = first
			
			//Pick a basic start date-time, we're only using the time to set
			//start and end points to schedule by
			let start = Date.time(year: 2020, month: 1, day: 1, hour: 7, minute: 0)
			let end = start.addingHours(hours: 10)
			
			let _ = Arc.shared.scheduleController.generateSchedule(start: start,
										 end: end)
			
			//Check for existing studies, there could have been an error.
			let studyController = Arc.shared.studyController
			var studies = studyController.getAllStudyPeriods()
			
			//Make sure we start with no studies before creating more.
			for study in studies {
				studyController.delete(study)
			}
			
			//Create all of our study periods starting from the begining of the study.
			studies = studyController.createAllStudyPeriods(startingID: 0, startDate: beginningOfStudy)
			
			//Update our user startdate if it has been changed in the old app
			if currentVisitId != 0, let visitStartDate = currentVisitUserStartDate, let study = studies.filter({
				$0.studyID == currentVisitId
			}).first {
				study.userStartDate = visitStartDate
			}
			
			//Create test sessions for each study
			//This will internally use the schedule we've created earlier to layout our tests.
			
			for i in 0 ..< studies.count {
				let study = studies[i]
			
				studyController.createTestSessions(studyId: Int(study.studyID), isRescheduling: false);
				
			}
			
            // We want to mark tests up to the latest session id as missed and uploaded, so we don't accidentally
            // re-upload them as missed data.
			//Mark tests missed up to the last test the user took in the old app.
            var latestSessionData = Arc.shared.studyController.latestTest ?? SessionInfoResponse.TestState()
            var latestSessionId:Int64 = Int64(latestSessionData.session_id) ?? -1
			HMLog("Latest session Id: \(latestSessionId)")
			
			studyController.markMissed(sessionsUpTo: Int(latestSessionId))
			studyController.markUploaded(sessionsUpTo: Int(latestSessionId))

            // If there are any tests scheduled for today, we need to mark them as missed and uploaded as well.
			let day = studyController.day
			HMLog("Getting sessions from day \(day)")
			let todaysSessions = Arc.shared.studyController.get(sessionsFromDayIndex: Int(day), studyId: Int(currentVisitId))
			
			for session in todaysSessions {
				Arc.shared.studyController.mark(missed: Int(session.sessionID), studyId: Int(session.study!.studyID))
				Arc.shared.studyController.mark(uploaded: Int(session.sessionID), studyId: Int(session.study!.studyID))

                //Only update if we pass the latest test
				if latestSessionId < session.sessionID {
                    latestSessionId = session.sessionID
                    latestSessionData.session_date = session.sessionDate!.timeIntervalSince1970
                    latestSessionData.session_id = "\(session.sessionID)"
                    
				}
			}
            
            Arc.shared.studyController.latestTest = latestSessionData
            HMLog("Updated latest session id to \(latestSessionId)")

		}
        
		didFinish(.success(()))
			

	}

	
	static public func deviceIdUpdate(ids:(oldId:String, newId:String), didFinish:@escaping ((Result<Void, MigrationError>)->Void)) {
		
		updateDevice.execute(data: DeviceUpdate(new_device_id: ids.newId), params: ["device_id":ids.oldId]) {
			(urlRes, data, err) in
			if let err = err {
				didFinish(.failure(.errorFetchingData(err)))
				return
			}
			guard let response = urlRes as? HTTPURLResponse else {
				didFinish(.failure(.noResponse))
				return
			}
			if !(200 ... 299).contains(response.statusCode) {
				didFinish(.failure(.statusFailure(response.statusCode, data?.errors ?? [:])))
				return
			}
			didFinish(.success(()))
		}
	}
	static public func upload(zip:URL, didFinish:@escaping ((Result<URL, MigrationError>)->Void)) {
		HMLog("Preparing to upload \(zip)")
		guard let data = dataFor(file: zip) else {
			didFinish(.failure(.noFile(zip)))
			return
		}
		
		uploadZip.executeZip(data: data) { (urlRes, data, err) in
			if let err = err {
				didFinish(.failure(.errorFetchingData(err)))
				return 
			}
			guard let response = urlRes as? HTTPURLResponse else {
				didFinish(.failure(.noResponse))
				return
			}
			if !(200 ... 299).contains(response.statusCode) {
				didFinish(.failure(.statusFailure(response.statusCode, data?.errors ?? [:])))
				return
			}
			didFinish(.success(zip))
			
		}
		
		
	}
	
	
}
extension EXCoreDataStack {
	func fetch<T:NSManagedObject>(predicate:NSPredicate? = nil, sort:[NSSortDescriptor]? = nil, limit:Int? = nil) -> Result<[T], Error> {
		do {
			guard let fetchRequest:NSFetchRequest<T> = (T.fetchRequest() as? NSFetchRequest<T>) else {
				return .failure(DataMigration.MigrationError.noManagedObject)
			}
			fetchRequest.predicate = predicate
			fetchRequest.sortDescriptors = sort
			if let limit = limit {
				fetchRequest.fetchLimit = limit
			}
			let results = try  self.managedObjectContext.fetch(fetchRequest)
			return .success(results)
			
		}  catch {
			return .failure(error)
		}
	}
	func getTest(session:Int64) -> TestSession?
	{
		let request:NSFetchRequest<TestSession> = NSFetchRequest<TestSession>(entityName: "TestSession");
		
		request.predicate = NSPredicate(format: "sessionID>=%i",session);
		request.sortDescriptors = [NSSortDescriptor(key:"sessionID", ascending:true)];
		do
		{
			let results = try  self.managedObjectContext.fetch(request)
			
			if(results.count > 0)
			{
				let firstResult = results[0] as TestSession;
				
				return firstResult;
			}
		}
		catch
		{
			HMLog("error retrieving cached data: \(error)");
		}
		
		return nil;
	}
	func getCurrentVisit() -> TestVisit?
	{
		let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
		
		let now = NSDate();
		request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
		
		do
		{
			let results = try  self.managedObjectContext.fetch(request)
			
			if(results.count > 0)
			{
				let firstResult = results[0] as TestVisit;
				
				return firstResult;
			}
		}
		catch
		{
			HMLog("error retrieving cached data: \(error)");
		}
		
		return nil;
	}
	func getAllVisits() -> [TestVisit]
	{
		let request:NSFetchRequest<TestVisit> = NSFetchRequest<TestVisit>(entityName: "TestVisit");
        request.sortDescriptors = [NSSortDescriptor(key:"userStartDate", ascending:true)];
		do
		{
			let results:[TestVisit] = try self.managedObjectContext.fetch(request)
			
			return results
		}
		catch
		{
			HMLog("error retrieving cached data: \(error)");
		}
		
		return [];
	}
}
