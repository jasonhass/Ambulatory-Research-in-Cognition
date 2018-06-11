/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

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
    
    func removeObject(forKey aKey:String)
    {
        let index = _keys.index(of: aKey);
        if index != NSNotFound
        {
            _keys.removeObject(at: index);
            _values.removeObject(at: index);
            
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
