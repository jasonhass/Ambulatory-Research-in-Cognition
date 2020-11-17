//
// Roboto.swift
//



import UIKit
import HMMarkup

public struct Text {
	public static func replaceIn(_ text:String?, withTemplate template:[String:String]) -> String {
		guard var text = text else {
			return ""
		}
		for (key, value) in template {
			text = text.replacingOccurrences(of: "{\(key)}", with: value)
		}
		return text
	}
}
public struct Roboto {
	public static let family = "Roboto"
	
	
	public struct Face {
		public static let regular = "Regular"
		public static let black = "Black"
		public static let light = "Light"
		public static let lightItalic = "LightItalic"
		public static let thin = "Thin"
		public static let mediumItalic = "MediumItalic"
		public static let medium = "Medium"
		public static let blackItalic = "BlackItalic"
	}
	public struct Font {
		public static let body = UIFont(name: "Roboto", size: 17)!
			
			.family(Roboto.family)
			.face(Roboto.Face.regular)
			.size(17)
		public static let medium = UIFont(name: "Roboto-Medium", size: 17)!

		public static let subBody = UIFont(name: "Roboto", size: 16)!
			
			.family(Roboto.family)
			.face(Roboto.Face.regular)
			.size(16)
		public static let disclaimer = UIFont(name: "Roboto", size: 14)!
			
			.family(Roboto.family)
			.face(Roboto.Face.regular)
			.size(14)
		public static let badge = UIFont(name: "Roboto", size: 16)!
			
			.family(Roboto.family)
			.face(Roboto.Face.regular)
			.size(16)
		
		public static let bodyBold = Font.body
			.boldFont()
			
		public static let heading = UIFont(name: "Roboto", size: 26)!

		public static let headingMedium = UIFont(name: "Roboto-Medium", size: 26)!
		public static let earningsBold = UIFont(name: "Roboto-Medium", size: 32)!
		
		public static let headingBlack = UIFont(name: "Roboto", size: 26)!
			.family(Roboto.family)
			.face(Roboto.Face.black)
			.size(26)
		public static let headingBold = UIFont(name: "Roboto", size: 26)!
			.family(Roboto.family)
			.boldFont()
			.size(26)
		public static let subHeading = UIFont(name: "Roboto", size: 22)!
			.family(Roboto.family)
			.size(22)
		public static let subHeadingBold = UIFont(name: "Roboto", size: 22)!
			.family(Roboto.family)
			.boldFont()
			.size(22)
		public static let goalHeading = UIFont(name: "Roboto-Medium", size: 20)!
//			.family(Roboto.family)
//			.face(Roboto.Face.medium)
//			.size(20)
		public static let goalRewardBold = UIFont(name: "Roboto", size: 16)!
			.family(Roboto.family)
			.boldFont()
			.size(16)
		public static let goalReward = UIFont(name: "Roboto", size: 16)!
			.family(Roboto.family)
			.size(16)
		public static let italic = UIFont(name: "Roboto", size: 17)!
			.family(Roboto.family)
			.italicFont()
			.size(17)
        public static let prompt = UIFont(name: "Roboto-Regular", size: 26)!
            .family(Roboto.family)
            .face(Roboto.Face.regular)
            .size(26)
        public static let introHeading = UIFont(name: "Roboto-Medium", size: 32)!
	}
	
	
	public struct Style {
		
		public static func error(_ label:UILabel) {
			label.font = Roboto.Font.italic
			label.numberOfLines = 0
			label.textColor = #colorLiteral(red: 0.6000000238, green: 0, blue: 0, alpha: 1)
			
		}
		public static func body(_ label:UILabel, color:UIColor? = UIColor(named: "primary Text")) {
			label.backgroundColor = .clear

			label.font = Roboto.Font.body
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func medium(_ label:UILabel, color:UIColor? = .primaryText) {
			label.backgroundColor = .clear
			
			label.font = Roboto.Font.medium
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func subBody(_ label:UILabel, color:UIColor? = UIColor(named: "primary Text")) {
			label.backgroundColor = .clear
			
			label.font = Roboto.Font.subBody
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func disclaimer(_ label:UILabel, color:UIColor? = UIColor(named: "Primary Text")) {
			label.font = Roboto.Font.disclaimer
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func badge(_ label:UILabel, color:UIColor? = .badgeText) {
			label.backgroundColor = .badgeBackground
			label.layer.cornerRadius = 4
			label.clipsToBounds = true
			label.font = Roboto.Font.goalReward
			label.numberOfLines = 0
			label.textColor = color
		}
		
		public static func bodyBold(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.bodyBold
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func bodyBold(_ label:UIButton, color:UIColor? = .primaryText) {
			label.titleLabel!.font = Roboto.Font.bodyBold
			label.setTitleColor(color, for: .normal)
		}
		public static func earningsBold(_ label:UILabel, color:UIColor? = .white) {
			label.font = Roboto.Font.earningsBold
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func subHeading(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.subHeading
			label.numberOfLines = 0
			
			label.textColor = color
		}
		public static func goalHeading(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.goalHeading
			label.numberOfLines = 0
			label.textColor = color
		}
		
		public static func goalRewardBold(_ label:UILabel, color:UIColor? = UIColor(named: "primary Text")) {
			label.font = Roboto.Font.goalRewardBold
			label.numberOfLines = 1
			label.textColor = color
			label.textAlignment = .center
		}
		public static func goalReward(_ label:UILabel, color:UIColor? = UIColor(named: "Primary Text")) {
			label.font = Roboto.Font.goalReward
			label.numberOfLines = 1
			label.textColor = color
			label.textAlignment = .center
		}
		public static func subHeadingBold(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.subHeadingBold
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func heading(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.heading
			label.numberOfLines = 0
			
			label.textColor = color
		}
		public static func headingMedium(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.headingMedium
			label.numberOfLines = 0
			
			label.textColor = color
		}
		public static func headingBold(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.headingBold
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func headingBlack(_ label:UILabel, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.headingBlack
			label.numberOfLines = 0
			label.textColor = color
		}
		public static func body(_ label:UITextView, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.body
			label.textColor = color
		}
		public static func bodyBold(_ label:UITextView, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.bodyBold
			label.textColor = color
		}
		public static func heading(_ label:UITextView, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.headingMedium
			
			label.textColor = color
		}
		public static func headingBold(_ label:UITextView, color:UIColor? = UIColor(named:"Primary Text")) {
			label.font = Roboto.Font.headingBold
			label.textColor = color
		}
        public static func prompt(_ label:UILabel, color:UIColor? =
            UIColor(named:"Primary Text")) {
            label.font = Roboto.Font.prompt
            label.numberOfLines = 0
            label.textColor = color
        }
        public static func introHeading(_ label:UILabel, color:UIColor? = .white) {
            label.font = Roboto.Font.introHeading
            label.numberOfLines = 0
            label.textColor = color
        }
	}
	///Attributes for various uses
	public struct Attributes {
		public static let link = [ NSAttributedString.Key.underlineStyle: NSUnderlineStyle.single.rawValue, NSAttributedString.Key.foregroundColor: UIColor(named: "Primary") ?? .blue, NSAttributedString.Key.font: UIFont.systemFont(ofSize: 15, weight: UIFont.Weight.medium) ] as [NSAttributedString.Key : Any]
	}
	
	
	/// Use post processors after you make changes to text in a particular view
	public struct PostProcess {
		public static func renderMarkup (_ label:UILabel, template:[String:String] = [:]) {
			let renderer:HMMarkupRenderer = HMMarkupRenderer(baseFont: label.font)
			
			
			let markedUpString = NSMutableAttributedString(attributedString:  renderer.render(text: label.text ?? "", template:template))
			
			markedUpString.addAttribute(NSAttributedString.Key.foregroundColor, value: label.textColor ?? .black, range: NSMakeRange(0, markedUpString.length))
			label.attributedText = markedUpString
			
			lineHeight(label)
		}
		
		public static func renderMarkup (_ textView:UITextView, template:[String:String] = [:]) {
			let renderer:HMMarkupRenderer = HMMarkupRenderer(baseFont: textView.font ?? Roboto.Font.body)
			
			
			let markedUpString = NSMutableAttributedString(attributedString:  renderer.render(text: textView.text ?? "", template:template))
			
			markedUpString.addAttribute(NSAttributedString.Key.foregroundColor, value: textView.textColor ?? .black, range: NSMakeRange(0, markedUpString.length))
			
			textView.attributedText = markedUpString
			
			lineHeight(textView)
		}
		
		/// Adds an underline to previously formatted text
		///
		/// - Parameter label: make sure you use this call after every time you set your text.
		public static func link (_ label:UILabel) {
			var attributes = Attributes.link
			attributes[NSAttributedString.Key.foregroundColor] = label.textColor
			attributes[NSAttributedString.Key.font] = label.font!
			let attrString = NSMutableAttributedString(string:label.text ?? "", attributes: attributes)
			
			label.attributedText = attrString
			
		}
		public static func link (_ button:UIButton) {
			var attributes = Attributes.link
			attributes[NSAttributedString.Key.foregroundColor] = button.titleLabel!.textColor
			attributes[NSAttributedString.Key.font] = button.titleLabel!.font!
			let attrString = NSAttributedString(string:button.title(for: .normal) ?? "", attributes: attributes)
			
			button.setAttributedTitle(attrString, for: .normal)
		}
		public static func lineHeight (_ label:UILabel) {
			
			let attributedString = NSMutableAttributedString(attributedString: label.attributedText ?? NSAttributedString(string: label.text ?? ""))
			
			
			let paragraphStyle = NSMutableParagraphStyle()
			paragraphStyle.lineSpacing = 5.5
			attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
			label.attributedText = attributedString
		}
		public static func lineHeight (_ textView:UITextView) {
			
			let attributedString = NSMutableAttributedString(attributedString: textView.attributedText ?? NSAttributedString(string: textView.text ?? ""))
			
			
			let paragraphStyle = NSMutableParagraphStyle()
			paragraphStyle.lineSpacing = 5.5
			attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
			textView.attributedText = attributedString
		}
	}
	
}
