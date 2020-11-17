//
// HMMarkupNode.swift
//



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
		case "^":
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
