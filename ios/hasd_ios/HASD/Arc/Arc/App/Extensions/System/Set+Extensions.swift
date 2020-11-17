//
// Set+Extensions.swift
//



import Foundation

public extension Set {
    static func uniqueSet(numberOfItems count:Int, maxValue:Int) -> Set<Int> {
        var set:Set<Int> = []
        
        //Create a pool of possible values.
        var indicies:[Int] = [Int](repeating: 0, count: maxValue + 1)
        for index in 0 ... maxValue {
            indicies[index] = index
        }
        
        while set.count < count && indicies.count > 0 {
            
            //Get a random index
            let index = Int(arc4random_uniform(UInt32(indicies.count)))
            //Insert into the array
            set.insert(indicies[index])
            indicies.remove(at: index)
        }
        return set
    }
}
