//
// Algorithms.swift
//


import Foundation
import UIKit
/**
 Returns a set of unique numbers

 - parameter count: number of items in the result set
 - parameter maxValue: the largest value possible in the set must be greater 
 than 0

 */
public func uniqueSet(count:Int, maxValue:Int?) -> Set<Int> {
    var set:Set<Int> = []

    //Create a pool of possible values.
    var indicies:[Int] = [Int](repeating: 0, count: (maxValue ?? count) + 1)
    for index in 0 ... (maxValue ?? count) {
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

public func findEditingTextField(view:UIView) -> UITextField? {
//    print("\n\nViews", view.subviews, "\n\n\n")
    var textField:UITextField! = nil
    for subView in view.subviews {
        if let tf = subView as? UITextField {
//            print("FOUND", tf)

            if tf.isEditing {
                textField = tf
                return textField
            } else {
            }
        } else {
//            print("Checking", subView)
            if let tf = findEditingTextField(view: subView) {
                textField = tf
                break
            }
        }
        
    }
    return textField
}

public func randomString(length: Int) -> String {
    
    let letters : NSString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    let len = UInt32(letters.length)
    
    var randomString = ""
    
    for _ in 0 ..< length {
        let rand = arc4random_uniform(len)
        var nextChar = letters.character(at: Int(rand))
        randomString += NSString(characters: &nextChar, length: 1) as String
    }
    
    return randomString
}


public func deviceIdentifier() -> String
{
    var systemInfo = utsname()
    uname(&systemInfo)
    let machineMirror = Mirror(reflecting: systemInfo.machine)
    let identifier = machineMirror.children.reduce("") { identifier, element in
        guard let value = element.value as? Int8, value != 0 else { return identifier }
        return identifier + String(UnicodeScalar(UInt8(value)))
    }
    
    return identifier;
}
