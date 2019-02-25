//
// PriceTest.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation
//For UI configuration only
public struct PriceTest : Codable{
    public struct Item : Codable {
        var item:String
        var price:String
        var alt:String
    }
    var items:Array<Item>
}
//For server communication only
public struct PriceTestResponse : HMTestCodable {
    	public static var dataType: SurveyType = .priceTest

    public struct Choice : Codable {
        var price:String
        var alt_price:String
        var item:String
        var good_price:Int?
        var correct_index:Int?
        var selected_index:Int?
        var question_display_time:TimeInterval?
        var selection_time:TimeInterval?
        var stimulus_display_time:TimeInterval?
        
        init(item:PriceTest.Item) {
            self.price = item.price
            self.alt_price = item.alt
            self.item = item.item
        }
    }
    
    public var id: String?
    public var type: SurveyType? = .priceTest

    public var date:TimeInterval?
    var sections:Array<Choice>
    
    init(id:String) {
        sections = []
    }
}
