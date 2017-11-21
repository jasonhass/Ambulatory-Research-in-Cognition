//
//  DNTest.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/15/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

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
