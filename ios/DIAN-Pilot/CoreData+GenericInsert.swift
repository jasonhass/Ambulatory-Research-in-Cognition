//
//  CoreData+GenericInsert.swift
//  ARC
//
//  Created by Philip Hayes on 5/9/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import CoreData

extension NSManagedObject {
    class func createIn<T>(context:NSManagedObjectContext) -> T {
        
        return NSEntityDescription.insertNewObject(forEntityName: NSStringFromClass(T.self as! AnyClass), into: context) as! T
    }
    
    func dictionaryOfAttributes(excludedKeys:NSSet = NSSet()) -> AnyObject{
        
        var data = MutableOrderedDictionary()
        let attributes:NSDictionary = self.entity.attributesByName as NSDictionary
        let relationships = self.entity.relationshipsByName as NSDictionary
        for key in attributes.allKeys.sorted(by: { (a, b) -> Bool in
            if let aS = a as? String, let bS = b as? String
            {
                return aS.compare(bS) == .orderedAscending;
            }
            return true;
        }) {
            
            if excludedKeys.contains(key){
                continue
            }
            guard var value = self.value(forKey: key as! String) else {
                continue
            }
            
            if value is Date {
                value = (value as! Date).JSONDate()
                
            }
            
            data.setObject(value, forKey: key as! String);
            
        }
        
        for k in relationships.allKeys.sorted(by: { (a, b) -> Bool in
            if let aS = a as? String, let bS = b as? String
            {
                return aS.compare(bS) == .orderedAscending;
            }
            return true;
        }) {
            
            let key = k as! String;
            let desc = relationships[key] as! NSRelationshipDescription;
            
            if excludedKeys.contains(key){
                continue
            }
            guard var value = self.value(forKey: key) else {
                continue
            }
            if value is NSManagedObject {
                var set:Set<String> = []
                if let inv = desc.inverseRelationship{
                    set.insert(inv.name)
                }
                value = (value as! NSManagedObject).dictionaryOfAttributes(excludedKeys: set as NSSet)
            }
            if value is NSSet {
                var set:Set<String> = []

                if let inv = desc.inverseRelationship{
                    set.insert(inv.name)
                }
                value = (value as! [NSManagedObject]).map({ (obj) in
                    return obj.dictionaryOfAttributes(excludedKeys: set as NSSet)
                })
            }
            if value is NSOrderedSet {
                var set:Set<String> = []

                if let inv = desc.inverseRelationship{
                    set.insert(inv.name)
                }
                value = (value as! NSOrderedSet).map({ (obj) in
                    return (obj as! NSManagedObject).dictionaryOfAttributes(excludedKeys: set as NSSet)
                })
            }
            data.setObject(value, forKey: key);
        }
        data.alphabetized();
        return data;
    }
}
