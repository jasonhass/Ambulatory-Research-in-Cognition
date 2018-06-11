/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import Foundation

public class DNTest {
    public var startTime:Date
    public var endTime:Date?
    private var isComplete:Bool = false
    private var timelimted:Bool = false
    private var duration:TimeInterval = 0.0
    private var timer:Timer?
    internal var selectedValue:AnyObject? = nil
    var session = 0


    func getTestDescription()->DNTestDescription{
        return DNTestDescription(title: "", storyBoardName:"", pages: [])
    }

    /** 
     If you supply a duration greater than 0 the test will be time limited.
     You can also just pass in nil for unlimited time.

    */
    public init() {
        startTime = Date()

    }
    
    public func startTest(){
        startTime = Date()

    }
    public func nextStep(){
        
    }
    public func selectValue(option:AnyObject?) {

    }

    @objc public func endTest(){
        if !isComplete {
            isComplete = true
            endTime = Date()
        }
    }

    public func getIsComplete() -> Bool {
        return isComplete
    }
    
    public func saveData(testSession:TestSession)
    {
        print("HEY OVERRIDE ME!");
    }
}

public struct DNTestDescription {
    var title:String
    var storyBoardName:String
    var pages:[String]
    
}
