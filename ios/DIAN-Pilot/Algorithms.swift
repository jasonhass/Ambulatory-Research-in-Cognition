/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation
import UIKit
/**
 Returns a set of unique numbers

 - parameter count: number of items in the result set
 - parameter maxValue: the largest value possible in the set must be greater 
 than 0

 */
func uniqueSet(count:Int, maxValue:Int?) -> Set<Int> {
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

func findEditingTextField(view:UIView) -> UITextField? {
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

func randomString(length: Int) -> String {
    
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


func deviceIdentifier() -> String
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
