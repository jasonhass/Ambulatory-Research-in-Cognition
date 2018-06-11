/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
