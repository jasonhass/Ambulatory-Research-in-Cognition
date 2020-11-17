//
// EXEnvironment.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import Arc
import HMMarkup
enum EXEnvironment : ArcEnvironment {
    
    

    case none, dev, qa, production, userTest, unitTest
    
    var arcStartDays: Dictionary<Int, Int>? {
        switch self {
            
        default:
            return [0: 0,   // Test Cycle A
                    1: 182,  // Test Cycle B
                    2: 182 * 2, // Test Cycle C
                    3: 182 * 3, // Test Cycle D
                    4: 182 * 4, // Test Cycle E
                    5: 182 * 5, // Test Cycle F
                    6: 182 * 6, // Test Cycle G
                    7: 182 * 7, // Test Cycle H
                    8: 182 * 8, // Test Cycle I
					9: 182 * 9  // Test Cycle I
            ];
            
        }
    }
	var debuggableStates:[State] {
		return EXState.allCases
	}
    var protectIdentity: Bool { false }
    var crashReporterApiKey: String? {
        switch self {
        case .userTest, .unitTest, .qa, .dev:
            return "HmnfQU2uJpIOx23kQ2DaeXm6uWa8968r"
        case .production:
            return "0RX3JDXOaxcq7vgvsrKKsNxY4CKUzHKC"
        default:
            return nil
        }
    }
	var hidesChangeAvailabilityDuringTest:Bool {return true}

	var isDebug: Bool {
		switch self {
		case .userTest, .qa, .dev, .unitTest:
			return true
		default:
			return false
		}
	}
    var mockData: Bool {
        switch self {
        case .userTest:
            return false
		case .unitTest:
			return true
        default:
            return false
        }
    }
    
    var blockApiRequests: Bool {
        switch self {
        case .userTest:
            return true
		case .unitTest:
			return true
        default:
            return false
            
        }
    }
    
    var baseUrl: String? {
        
        // Return base url
        return "";
    }
    
    var welcomeLogo: String? {
        
        switch self {
        case .userTest:
            return "cut-ups/icons/user-test"
        default:
            return "cut-ups/icons/_logo/EXR_logo"
        }
    }
    
    var welcomeText: String? {
        switch self {
        case .qa:
            return "Welcome to the\n*EXR app*".localized(ACTranslationKey.gen_welcome_key)
        case .production:
            return "Welcome to the\n*EXR app*".localized(ACTranslationKey.gen_welcome_key)
        case .userTest:
            return "Welcome to the\n*EXR app*".localized(ACTranslationKey.gen_welcome_key)
        default:
            return "Welcome to the\n*EXR app*".localized(ACTranslationKey.gen_welcome_key)
        }
    }
    /**
     - Important: Update language configs before release as needed
     */
    var languageFile: String? {
        switch self {

        case .qa, .userTest, .dev, .unitTest:
            return "language-development"
        default:
            return "language-production"
        }
    }
    
    var privacyPolicyUrl: String? {
        switch self {
        default:
            return "https://wustl.edu/about/compliance-policies/computers-internet-policies/internet-privacy-policy/"
            
        }
    }
    var shouldDisplayDateReminderNotifications: Bool {
        return true
    }

    /*
     adopt variables to override which controllers are supplied in the environment
     */
    var appNavigation: AppNavigationController {
        switch self {
        default:
            //
            return EXAppNavigationController()
            
        }
    }
    var appController: AppController {
        return EXAppController()
    }
    
    var studyController: StudyController {
        return EXStudyController()
    }
    
    var authController: AuthController {
        return EXAuthController()
    }
    func configure() {
        //Use this to set class variables or perform setup before the app runs
        SliderView.hideSelection = true
        
       
        
        
    }
}
