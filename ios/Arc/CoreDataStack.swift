//
// CoreDataStack.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
import CoreData

open class CoreDataStack {
    // MARK: - Core Data stack
    static public let shared = CoreDataStack()
    static public var useMockContainer = false {
        didSet {
            if useMockContainer {
                CoreDataStack.mockDefaults?.removePersistentDomain(forName: "com.healthyMedium.arcMockDefaults")

            }
        }
    }
    static public var defaults = UserDefaults(suiteName: "com.healthyMedium.arcdefaults");
    static public var mockDefaults = UserDefaults(suiteName: "com.healthyMedium.arcMockDefaults");
    
    static public func currentDefaults() -> UserDefaults? {
        if useMockContainer {
            return mockDefaults
        }
        return defaults
    }
    
    lazy public var mockPersistantContainer: NSPersistentContainer = {
        
        let container = NSPersistentContainer(name: "arc", managedObjectModel: self.managedObjectModel)
        let description = NSPersistentStoreDescription()
        description.type = NSInMemoryStoreType
        description.shouldAddStoreAsynchronously = false // Make it simpler in test env
        
        container.persistentStoreDescriptions = [description]
        container.loadPersistentStores { (description, error) in
            // Check if the data store is in memory
            precondition( description.type == NSInMemoryStoreType )
            
            // Check if creating container wrong
            if let error = error {
                fatalError("Create an in-mem coordinator failed \(error)")
            }
        }
        return container
    }()
    
    lazy public var persistentContainer: NSPersistentContainer = {
		
		if ProcessInfo.processInfo.environment["XCTestConfigurationFilePath"] != nil || CoreDataStack.useMockContainer{
			// Code only executes when tests are running
			return mockPersistantContainer
		}
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
		let container = NSPersistentContainer(name: "arc", managedObjectModel: self.managedObjectModel)
		if let description = container.persistentStoreDescriptions.first {
		
			description.shouldInferMappingModelAutomatically = true
			description.shouldMigrateStoreAutomatically = true
			
			container.persistentStoreDescriptions = [description]
		}
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
				
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    // MARK: - Core Data Saving support
    lazy public var managedObjectModel: NSManagedObjectModel = {
        let managedObjectModel = NSManagedObjectModel.mergedModel(from: [Bundle(for: type(of: self))] )!
        return managedObjectModel
    }()
    public func saveContext () {
        var context = persistentContainer.viewContext
        if context.hasChanges {
//			context.perform {

				do {
						try context.save()

					
				} catch {
					// Replace this implementation with code to handle the error appropriately.
					// fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
					let nserror = error as NSError
					assertionFailure("Unresolved error \(nserror), \(nserror.userInfo)")
				}
//			}
        }
		context = MHController.dataContext
		if context.hasChanges {
//			context.perform {

				do {

						try context.save()
					
				} catch {
					// Replace this implementation with code to handle the error appropriately.
					// fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
					let nserror = error as NSError
					assertionFailure("Unresolved error \(nserror), \(nserror.userInfo)")
				}
//			}
		}
    }
}
