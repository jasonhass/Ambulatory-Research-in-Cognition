//
// Introduction.swift
//



import Foundation
public struct Introduction : Codable {

	public struct Instruction : Codable {
		var title : String
		var subtitle : String
		var preface : String
		var nextButtonTitle:String?
        var nextButtonImage:String?
		var style:String?
	}
	var instructions:Array<Instruction>?
	
}
