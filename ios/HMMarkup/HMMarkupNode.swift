//
// HMMarkupNode.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

public enum HMMarkupNode {
	case text(String)
	case strong([HMMarkupNode])
	case emphasis([HMMarkupNode])
	case delete([HMMarkupNode])
	case underline([HMMarkupNode])
}

extension HMMarkupNode {
	init?(delimiter: UnicodeScalar, children: [HMMarkupNode]) {
		switch delimiter {
		case "*":
			self = .strong(children)
		case "_":
			self = .emphasis(children)
		case "~":
			self = .delete(children)
		case "`":
			self = .underline(children)
		default:
			return nil
		}
	}
}
