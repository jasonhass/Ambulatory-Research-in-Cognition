//
//  MutableOrderedDictionary.swift
//  ARC
//
//  Created by Michael Votaw on 11/8/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class MutableOrderedDictionary: NSDictionary {
    let _values: NSMutableArray = []
    let _keys: NSMutableOrderedSet = []
    
    override var count: Int {
        return _keys.count
    }
    override func keyEnumerator() -> NSEnumerator {
        return _keys.objectEnumerator()
    }
    override func object(forKey aKey: Any) -> Any? {
        let index = _keys.index(of: aKey)
        if index != NSNotFound {
            return _values[index]
        }
        return nil
    }
    func setObject(_ anObject: Any, forKey aKey: String) {
        let index = _keys.index(of: aKey)
        if index != NSNotFound {
            _values[index] = anObject
        } else {
            _keys.add(aKey)
            _values.add(anObject)
        }
    }
    
    func alphabetized()
    {
        let sortedKeys = self._keys.sorted { (a, b) -> Bool in
            if let aS = a as? String, let bS = b as? String
            {
                return aS.compare(bS) == .orderedAscending;
            }
            
            return false;
        }
        let oldValues = self._values.copy() as! NSArray;
        let oldKeys = self._keys.copy() as! NSOrderedSet;
        self._keys.removeAllObjects();
        self._values.removeAllObjects();
        
        for i in 0..<sortedKeys.count
        {
            let key = sortedKeys[i];
            let oldIndex = oldKeys.index(of: key);
            
            self.setObject(oldValues[oldIndex], forKey: key as! String);
        }
        
    }
}
