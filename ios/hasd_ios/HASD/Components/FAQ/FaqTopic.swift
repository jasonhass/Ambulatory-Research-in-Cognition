//
// FaqTopic.swift
//




import Foundation


class FaqTopic: Codable
{
    var topic_name: String;
    var questions: Array<FaqItem>;
    

    init()
    {
        self.topic_name = "";
        self.questions = [];
    }
}
