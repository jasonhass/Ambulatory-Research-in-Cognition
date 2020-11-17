//
// HMCodable.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
/*
 This is simply for objects that will be stored in core data
 as JSONData objects.
 
 */

public protocol HMCodable : Codable {
    var id : String? {get set}
	var type : SurveyType? {get set}
	static var dataType: SurveyType {get}

}

public protocol HMTestCodable : HMCodable {
    var date:TimeInterval? {get set}
}
