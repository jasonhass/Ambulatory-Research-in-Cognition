//
// ArcTranslation.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import HMMarkup
public protocol TranslationKey {
	var rawValue:String { get }
}
public struct ArcTranslation : Codable  {
    public struct Map : Codable {
        public var map:Dictionary<String, String>?
    }
    public var versions:Array<Map>
}

public extension String {
    func localized(_ key:String) -> String {
        if let config = HMMarkupRenderer.config,
            config.shouldTranslate,
            let translation = config.translation
        {
            return translation[key] ?? self
        }
        return self
    }
	func localized(_ key:TranslationKey) -> String {
		let key = key.rawValue
		if let config = HMMarkupRenderer.config,
			config.shouldTranslate,
			let translation = config.translation
		{
			return translation[key] ?? self
		}
		return self
	}
}


