//
// FaqItem.swift
//




import Foundation

class FaqItem: Codable
{
    var question:String;
    var answer: String;
    
    init()
    {
        self.question = "";
        self.answer = "";
    }
}
