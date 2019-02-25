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
	public var testCount:Int = 0
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
}
