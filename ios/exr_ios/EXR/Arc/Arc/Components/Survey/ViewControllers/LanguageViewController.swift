//
// LanguageViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public enum ACLocale : String{
/*
     US - English    Nederland - Nederlands    France - Français    España - Español    Argentina - Español    Canada - Français    Deutschland - Deutsche    Italia - Italiano    日本 - 日本語    Brasil - Português    Columbia - Español    Mexico - Español    US - Español    中国 - 简体中文
     
     language_key    en    nl    fr    es    es    fr    de    it    ja    pt    es    es    es    zh
      country_key    US    NL    FR    ES    AR    CA    DE    IT    JP    BR    CO    MX    US    CN
*/
    case en_US
    case en_AU
    case en_GB
    case en_CA
	case en_IE
    case nl_NL
    case fr_FR
    case es_ES
    case es_AR
	case es_419
    case fr_CA
    case de_DE
    case it_IT
    case ja_JP
    case pt_BR
    case es_CO
    case es_MX
    case es_US
    case zh_CN
    
    static func from(description:String) -> ACLocale {
        switch description {
            
        case "US - English": return .en_US
        case "Australia - English": return .en_AU
        case "UK - English": return .en_GB
        case "Canada - English": return .en_CA
		case "Ireland - English": return .en_IE
        case "Nederland - Nederlands": return .nl_NL
        case "France - Français": return .fr_FR
        case "España - Español": return .es_ES
		case "Europe - Spanish": return .es_ES
		case "Europa - Español": return .es_ES

        case "Argentina - Español": return .es_AR
        case "Canada - Français": return .fr_CA
		case "Canada - French": return .fr_CA

        case "Deutschland - Deutsche": return .de_DE
		case "Germany - German": return .de_DE

        case "Italia - Italiano": return .it_IT
        case "日本 - 日本語": return .ja_JP
		case "Japan - Japanese": return .ja_JP

        case "Brasil - Português": return .pt_BR
        case  "Columbia - Español": return .es_CO
		case  "Colombia - Español": return .es_CO

        case "Mexico - Español": return .es_MX
		case "Latin America - Spanish": return .es_419
		case "America Latina - Español": return .es_419

        case "US - Español": return .es_US
        case "中国 - 简体中文": return .zh_CN
        default: return .en_US
            
        }
        
    }
    var string: String {
        return self.rawValue
    }
    var locale:(language:String, region:String) {
        let values = string.components(separatedBy: "_")
        return (values[0], values[1])
    }
	func getLocale() -> Locale {
		return Locale(identifier: string)
	}
    var availablePriceTest:String {
	let value = "prices/\(self.rawValue)/price_sets"
       return value
    }
}

open class ACLanguageViewController : SurveyNavigationViewController {
	
	
    open override func valueSelected(value: QuestionResponse, index: String) {
        if index == "ac_language_1" {
            guard let value = value.text_value else {
                return
            }
            let locale = ACLocale.from(description: value)
            let components = locale.string.components(separatedBy: "_")
            let language = components[0]
            let country = components[1]
            Arc.shared.appController.language = language
            Arc.shared.appController.country = country

            Arc.shared.setLocalization(country:country,
                                       language: language)
            Arc.shared.WELCOME_TEXT = Arc.environment?.welcomeText ?? ""

        }
    }
}
