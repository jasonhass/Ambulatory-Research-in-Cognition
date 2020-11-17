//
// SymbolsTest.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.



/*
public struct DNSymbolSet {
    
    var symbols:[Int]
    
}

public struct DNSymbolDataSet{
    
    var options:[DNSymbolSet]
    var choices:[DNSymbolSet]
    var correct:Int
}
public struct DNSymbolInputData {
    var choice:Int
    var timeTouched:NSDate
    var referenceTime:NSDate
    var touchLocation:CGPoint = CGPoint.zero
    
    init() {
        choice = -1
        timeTouched = NSDate()
        referenceTime = NSDate()
    }
}
 */
import Foundation
//For UI configuration
public struct SymbolsTest : Codable {
    public struct SymbolSet: Codable, Equatable {
        var symbols:[Int]
        
    }

    public struct Section : Codable {
        var options:Array<SymbolSet>
        var choices:Array<SymbolSet>
        var correct:Int
        
    }
    var sections:Array<Section> = []
    
    
}


//For server communication
public struct SymbolsTestResponse : HMTestCodable {
	public static var dataType: SurveyType = .symbolsTest

    public struct Section : Codable {
        
        var appearance_time:TimeInterval?
        var selection_time:TimeInterval?
        var correct:Int?
        var selected:Int = -1
        var choices:Array<Array<String>>? = []
        var options:Array<Array<String>>? = []
    }
    
    public var id: String?
    
    public var type: SurveyType? = .symbolsTest
    
    public var date:TimeInterval?
    
    var sections:Array<Section> = []
    
    init(id:String) {
        self.id = id
    }
    
}
