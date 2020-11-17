//
// PriceTest.swift
//



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
        self.id = id
        sections = []
    }
}
