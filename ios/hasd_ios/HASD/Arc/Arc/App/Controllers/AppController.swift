//
// AppController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
open class AppController : MHController {
	public enum Commitment : String, Codable {
		case committed, rebuked
	}
	
	public var testCount:Int = 0
	
	public func store<T:Codable>(value:T?, forKey key:String) {
		defaults.setValue(value?.encode(), forKey:key);
		defaults.synchronize();
	}
	public func delete(forKey key:String) {
		defaults.removeObject(forKey: key)
		defaults.synchronize();
	}
	public func read<T:Codable>(key:String) -> T? {
		guard let value:T =  defaults.data(forKey: key)?.decode() else {
			
			return nil
		}
		return value
	}
	public var isParticipating:Bool {
		get {
			if let value = (defaults.value(forKey:"isParticipating") as? Bool)
			{
				return value;
			}
			return false;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"isParticipating");
			defaults.synchronize();
		}
	}
	public var isNotificationAuthorized:Bool {
		get {
			if let value = (defaults.value(forKey:"isNotificationAuthorized") as? Bool)
			{
				return value;
			}
			return false;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"isNotificationAuthorized");
			defaults.synchronize();
		}
	}
	public var flags:[String:Bool] {
		get {
			
			guard let value =  defaults.dictionary(forKey:"applicationFlags") as? [String : Bool] else {
				defaults.setValue([:], forKey:"applicationFlags");

				return defaults.dictionary(forKey:"applicationFlags") as! [String : Bool];
			}
			
			return value
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"applicationFlags");
			defaults.synchronize();
		}
	}
	
	//Use this to track the last time something had been fetched.
	//Api calls, periodic checks, event triggers
	public var lastFetched:[String:TimeInterval] {
		get {
			
			guard let value =  defaults.dictionary(forKey:"lastFetched") as? [String : TimeInterval] else {
				defaults.setValue([:], forKey:"lastFetched");
				
				return defaults.dictionary(forKey:"lastFetched") as! [String : TimeInterval];
			}
			
			return value
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"lastFetched");
			defaults.synchronize();
		}
	}
    public var locale:ACLocale {
        return ACLocale(rawValue: "\(language ?? "en")_\(country ?? "US")") ?? .en_US
    }
    public var language:String? {
        get {
            
            return defaults.string(forKey:"language");
            
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"language");
            defaults.synchronize();
        }
    }
    public var country:String? {
        get {
            
            return defaults.string(forKey:"country");
            
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"country");
            defaults.synchronize();
        }
    }
	public var participantId:Int? {
		get {

			if let id = defaults.value(forKey:"participantId") as? Int
			{
				return id;
			}
			return nil;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"participantId");
			defaults.synchronize();
		}
	}
    public var lastFlaggedMissedTestCount:Int {
        get {
            
            if let id = defaults.value(forKey:"lastFlaggedMissedTestCount") as? Int
            {
                return id;
            }
            return 0;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"lastFlaggedMissedTestCount");
            defaults.synchronize();
        }
    }
    public var isFirstLaunch:Bool {
        get {
            if (defaults.value(forKey:"hasLaunched") as? Bool) != nil
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
    public var commitment:Commitment? {
        get {
			return Commitment(rawValue: defaults.string(forKey: "commitment") ?? "")
			
        }
        set (newVal)
        {
			defaults.setValue(newVal?.rawValue, forKey:"commitment");
			defaults.synchronize();
        }
    }
    public var deviceId:String {
        get {
            if let value = (defaults.value(forKey:"deviceId") as? String)
            {
                return value;
            }
            let id = UUID().uuidString;
            defaults.setValue(id, forKey:"deviceId");
            defaults.synchronize();
            return id
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"deviceId");
            defaults.synchronize();
        }
    }
	
	public var wakeSleepUploaded:Bool {
		get {
			if let value = (defaults.value(forKey:"wakeSleepUploaded") as? Bool)
			{
				return value;
			}
			return false;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"wakeSleepUploaded");
			defaults.synchronize();
		}
	}
	public var testScheduleUploaded:Bool {
		get {
			if let value = (defaults.value(forKey:"testScheduleUploaded") as? Bool)
			{
				return value;
			}
			return false;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"testScheduleUploaded");
			defaults.synchronize();
		}
	}

	public var lastClosedDate:Date?

	
	public var lastUploadDate:Date? {
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
    public var lastBackgroundFetch:Date? {
        get {
            if let _lastUploadDate = defaults.value(forKey:"lastBackgroundFetch") as? Date
            {
                return _lastUploadDate;
            }
            return nil;
        }
        set (newVal)
        {
            defaults.setValue(newVal, forKey:"lastBackgroundFetch");
            defaults.synchronize();
        }
    }
	public var lastWeekScheduled:Date? {
		get {
			if let _lastUploadDate = defaults.value(forKey:"lastWeekScheduled") as? Date
			{
				return _lastUploadDate;
			}
			return nil;
		}
		set (newVal)
		{
			defaults.setValue(newVal, forKey:"lastWeekScheduled");
			defaults.synchronize();
		}
	}
    public func fetch(signature sessionId:Int64, tag:Int32) -> Signature?{
        var signature:Signature?
        MHController.dataContext.performAndWait {
            let predicate = NSPredicate(format: "tag == \(tag) AND sessionId == \(sessionId)")
            signature = fetch(predicate: predicate, sort: nil, limit: 1)?.first
        }
        
        return signature
    }
    public func save(signature image:UIImage, sessionId:Int64, tag:Int32) -> Bool {
        let signature:Signature = new()
        guard let data = image.pngData() else {
            return false
        }
        signature.data = data
        signature.sessionId = sessionId
        signature.tag = tag
        return true
    }

    public func timePeriodPassed(key:String, date:Date) -> Bool {
        guard let t = Arc.shared.appController.lastFetched[key] else {
            return true
        }
        return t < date.timeIntervalSince1970
    }
}
