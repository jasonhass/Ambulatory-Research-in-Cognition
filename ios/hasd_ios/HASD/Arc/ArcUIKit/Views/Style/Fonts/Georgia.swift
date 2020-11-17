//
// Georgia.swift
//



import UIKit

public struct Georgia {
	static public let family = "Georgia"
	public struct Face {
		static public let blackItalic = "BlackItalic"
	}
	public struct Font {
		static public let titleItalic = UIFont(name: "Georgia", size: 22.5)!
			.family(Georgia.family)
			.italicFont()
			.size(22.5)
		static public let bodyItalic = UIFont(name: "Georgia", size: 18)!
			.family(Georgia.family)
			.italicFont()
			.size(18)
		
		static public let largeTitle = UIFont(name: "Georgia", size: 42)!
			.family(Georgia.family)
			.size(42)
		static public let veryLargeTitle = UIFont(name: "Georgia", size: 42)!
			.family(Georgia.family)
			.size(96)

		
	}
	public struct Style {
		static public func title(_ label:UILabel, color:UIColor = #colorLiteral(red: 0.2349999994, green: 0.2349999994, blue: 0.2349999994, alpha: 1)) {
			label.font = Georgia.Font.titleItalic
			label.numberOfLines = 0
			label.textColor = color

		}
		static public func subtitle(_ label:UILabel, color:UIColor = #colorLiteral(red: 0.2349999994, green: 0.2349999994, blue: 0.2349999994, alpha: 1)) {
			label.font = Georgia.Font.bodyItalic
			label.numberOfLines = 0
			label.textColor = color
			
		}
		static public func largeTitle(_ label:UILabel, color:UIColor = #colorLiteral(red: 0.2349999994, green: 0.2349999994, blue: 0.2349999994, alpha: 1)) {
			label.font = Georgia.Font.largeTitle
			label.numberOfLines = 0
			label.textColor = color
			
		}
		static public func veryLargeTitle(_ label:UILabel, color:UIColor = .primaryInfo) {
			label.font = Georgia.Font.veryLargeTitle
			label.numberOfLines = 0
			label.textColor = color
			
		}
	}
}
