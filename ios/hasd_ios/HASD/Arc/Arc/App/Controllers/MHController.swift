//
// MHController.swift
//


/**
 
 
    If we want to save and fetch objects from some other source then we can do that here.
 
 
 
 */



import Foundation
import CoreData
public enum ACResult<T> {
    case success(T)
    case error(Error)
}
public protocol MHControllerDelegate {
    func didCatch(errors : Error)
}
open class MHController {
    public enum ControllerError : Error {
        case NotFound
    }
    public var container:NSPersistentContainer
	static public var dataContext:NSManagedObjectContext = {
			CoreDataStack.shared.persistentContainer.newBackgroundContext()
	}()
    public var delegate:MHControllerDelegate?
	
	open var defaults:UserDefaults! = CoreDataStack.currentDefaults()

    public init(container:NSPersistentContainer = CoreDataStack.shared.persistentContainer) {
        self.container = container
    }
	public func new<T:NSManagedObject>() -> T {
		return T(context: MHController.dataContext)
	}
    public func save<T:HMCodable>(id:String, obj:T) -> T {
        //Enforce id's on objects
        do {
        var saved = obj
        saved.id = id
        let savedData = try JSONEncoder().encode(saved)
        
        let json:JSONData = fetch(id:id) ?? JSONData(context: MHController.dataContext)
        json.id = id
        json.type = "\(obj.type!.rawValue)"
        json.data = savedData
        save()
        return saved
        } catch {
            fatalError("Invalid value : \(error)")
        }
    }
    
    public func createNewJSONData<T:HMCodable>(id:String, obj:T) -> JSONData
    {
        do {
            var saved = obj
            saved.id = id
            let savedData = try JSONEncoder().encode(saved)
            
            let json:JSONData = JSONData(context: MHController.dataContext)
            json.id = id
            json.type = "\(obj.type!.rawValue)"
            json.data = savedData
            return json;
        } catch {
            fatalError("Invalid value : \(error)")
        }
    }
    
    public func fetch(id:String) -> JSONData? {
        let fetch:NSFetchRequest<JSONData> = JSONData.fetchRequest()
        fetch.predicate = NSPredicate(format: "id == %@", id)
			
			
        var results:[JSONData]? = nil
		
		MHController.dataContext.performAndWait {
			do {
				results = try MHController.dataContext.fetch(fetch)
			}  catch {
				delegate?.didCatch(errors: error)
				fatalError("Could not fetch: \(error)")
			}
		}
		
        return results?.first
			
    }
	public func mark(filled id:String) -> JSONData? {
		guard let item = fetch(id: id) else {
			return nil
		}
		item.isFilledOut = true
		save()
		return item
	}
    public func fetch<T:NSManagedObject>(predicate:NSPredicate? = nil, sort:[NSSortDescriptor]? = nil, limit:Int? = nil) -> [T]? {
		var results:[T]? = nil
		
		if let fetchRequest:NSFetchRequest<T> = T.fetchRequest() as? NSFetchRequest<T> {
			fetchRequest.predicate = predicate
			fetchRequest.sortDescriptors = sort
			if let limit = limit {
				fetchRequest.fetchLimit = limit
			}
			MHController.dataContext.performAndWait {
				do {
					results = try MHController.dataContext.fetch(fetchRequest)
				}  catch {
						delegate?.didCatch(errors: error)
				}
			}
		}
			
			
		
		return results
		
    }
    
    public func get<T:HMCodable>(id:String) throws -> T {
        guard let result = fetch(id: id), let value:T = result.get() else {
            throw MHController.ControllerError.NotFound
        }
        return value
    }
    
    @discardableResult public func delete<T:HMCodable>(data:T) -> Bool {
        var success = false
        
        guard let id = data.id else {
            return success
        }
        let fetch:NSFetchRequest<JSONData> = JSONData.fetchRequest()
        fetch.predicate = NSPredicate(format: "id == %@", id)
        do {
        if let result = try MHController.dataContext.fetch(fetch).first {
            MHController.dataContext.delete(result)
            success = true
            save()
        }
        } catch {
            delegate?.didCatch(errors: error)
        }
        return success

    }
	
	public func delete(_ obj:NSManagedObject) {
		MHController.dataContext.delete(obj)
		save()
	}
	public func delete(_ objs:[NSManagedObject]) {
		for obj in objs {
			MHController.dataContext.delete(obj)
		}
		save()
	}
    public func save() {
		CoreDataStack.shared.saveContext()
			
    }
}

