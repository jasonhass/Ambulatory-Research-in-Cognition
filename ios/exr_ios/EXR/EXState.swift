//
// EXState.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import Arc
public enum EXState : String, State, CaseIterable {
	
	case welcome, auth, schedule, scheduleToHome, context, chronotype, wake, gridTest, priceTest, symbolsTest, changeSchedule, contact, rescheduleAvailability, testIntro, thankYou, thankYouToday, thankYouTomorrow, thankYouCycle, thankYouComplete, about, region, language, changeStudyStart, signatureStart, signatureEnd, onboarding, participationDenied, todayProgress, earningsDetail, rebukedCommitment, migration, migrationFailed, cycleProgress, notificationPermission, onboardingNotifications, home, earningsPostTest, earnings
	
	static var startup:[EXState] { return [.auth, .schedule, .home] }
	
	static var configuration: [EXState] {return [.changeSchedule] }
	
	static var surveys:[EXState] { return  [.context, .chronotype, .wake] }
	
	static var tests:[EXState] {return [.gridTest, .priceTest, .symbolsTest] }
	static var testCount = 0
    
    static var userDirected:[EXState] {return [.onboarding, .auth, .schedule, .changeSchedule] }
    static var testDirected:[EXState] {return tests}
    var shouldAutoNavigate:Bool {return EXState.testDirected.contains(self)}
	//Return a view controller w
	public func viewForState() -> UIViewController {

		switch self {
		case .notificationPermission:
			let vc:OnboardingSurveyViewController = OnboardingSurveyViewController(file: "notifications")
			return vc
		case .migration:
			return ACMigrationViewController()
		case .migrationFailed:
			return ACMigrationViewController()
		case .earningsDetail:
			return EarningsDetailViewController()
		case .earnings:
			return EarningsViewController(isPostTest: false)
		case .earningsPostTest:
			return EarningsViewController(isPostTest: true)
		case .todayProgress:
			
			return ACTodayProgressViewController()

        case .welcome:
            let vc:WelcomeViewController = .get()
            return vc
		case .home:
            let sb = UIStoryboard(name: "Main", bundle: nil);
            let vc = sb.instantiateViewController(withIdentifier: "tabBarController");
			return vc
			
		case .auth:
			//To use standard auth uncomment
//			let vc:CRAuthViewController = CRAuthViewController(file: "Auth")
			let vc:AC2FAuthenticationViewController = AC2FAuthenticationViewController(file: "2FAuth")
			vc.shouldNavigateToNextState = false
			return vc
        case .onboarding:
			let vc:OnboardingSurveyViewController = OnboardingSurveyViewController(file: "onboarding")
            return vc
		
		case .onboardingNotifications:
			let vc:OnboardingSurveyViewController = OnboardingSurveyViewController(file: "onboarding-notifications")
			return vc
		case .rebukedCommitment:
			let vc:OnboardingSurveyViewController = OnboardingSurveyViewController(file: "onboarding-rebuked")
			return vc
        case .signatureStart:
            let vc:ACSignatureNavigationController = .get()
            vc.tag = 0
            
            vc.loadSurvey(template: "signature")
            return vc
        case .signatureEnd:
            let vc:ACSignatureNavigationController = .get()
            vc.tag = 1
            vc.loadSurvey(template: "signature")
            return vc
		case .schedule, .changeSchedule:
			let controller:ACScheduleViewController = .init(file: "schedule")
			controller.participantId = Arc.shared.participantId
			
			if (self == .changeSchedule) {
                controller.isChangingSchedule = true
            }
			
            controller.shouldLimitWakeTime = true
        	controller.shouldTestImmediately = false
			return controller
        
        
		case .testIntro:
            ACState.testCount = 1
			return ACState.testIntro.viewForState()
		case .gridTest:
            
            
            let vc:CRGridTestViewController = .get(nib:"GridTestViewController", bundle: Bundle(for: Arc.self))
            
            let controller:InstructionNavigationController = .get()
            controller.nextVc = vc
            controller.titleOverride = "Test \(ACState.testTaken + 1) of 3".localized(ACTranslationKey.testing_header_1)
            .replacingOccurrences(of: "1", with: "\(ACState.testTaken + 1)")
            
            controller.load(instructions: "TestingIntro-Grids")
            return controller
			//return ACState.gridTest.viewForState()

		case .priceTest:
            
            let vc:PricesTestViewController = .get()
            let controller:InstructionNavigationController = .get()
            controller.nextVc = vc
            controller.titleOverride = "Test \(ACState.testTaken + 1) of 3".localized(ACTranslationKey.testing_header_1)
                .replacingOccurrences(of: "1", with: "\(ACState.testTaken + 1)")
                .replacingOccurrences(of: "{Value2}", with: "3")
            
            controller.load(instructions: "TestingIntro-Prices")
            return controller
			//return ACState.priceTest.viewForState()

		case .symbolsTest:
            
            let vc:SymbolsTestViewController = .get()
            let controller:InstructionNavigationController = .get()
            controller.nextVc = vc
            controller.titleOverride = "Test \(ACState.testTaken + 1) of 3".localized(ACTranslationKey.testing_header_1)
				.replacingOccurrences(of: "1", with: "\(ACState.testTaken + 1)")
                .replacingOccurrences(of: "{Value2}", with: "3")
            
            controller.load(instructions: "TestingIntro-Symbols")
            return controller
			//return ACState.symbolsTest.viewForState()
        case .region:
            //let controller:SurveyNavigationViewController = .get()
            let controller:SurveyNavigationViewController = .get()
            controller.participantId = Arc.shared.participantId
            controller.surveyType = .region
            
            controller.loadSurvey(template: "region", surveyId: "hb_region")
            return controller
		
        case .language:
            let controller:ACLanguageViewController = .get()
            controller.surveyType = .language
            controller.loadSurvey(template: Arc.environment?.languageFile ?? "language-production")
            return controller
            
        case .changeStudyStart:
            let controller:StartDateShiftViewController = .init(file: "changeStartDate")
            return controller
        
        case .context:
            let controller:BasicSurveyViewController = .init(file: "context", surveyId: nil, showHelp: false)
			return controller
		case .chronotype:
            let controller:ACWakeSurveyViewController = .init(file:"chronotype")
			
			return controller
		case .wake:
            let controller:ACWakeSurveyViewController = .init(file:"wake")
    
			return controller
			
		case .thankYou:
			return ACState.thankYou.viewForState()
        case .thankYouToday:
			let vc:EXFinishedNavigationController = EXFinishedNavigationController(file:"finishedToday")
            ACState.testCount = 0
            return vc
        case .thankYouTomorrow:
            let vc:EXFinishedNavigationController = EXFinishedNavigationController(file:"finishedTomorrow")
            ACState.testCount = 0
            return vc
        case .thankYouCycle:
            let vc:EXFinishedNavigationController = EXFinishedNavigationController(file:"finishedCycle")
            ACState.testCount = 0
            return vc
        case .thankYouComplete:
            let vc:EXFinishedNavigationController = EXFinishedNavigationController(file:"finishedComplete")
            ACState.testCount = 0
            return vc
		case .about:
			let vc:EXAboutViewController = .get()
			return vc
			
		case .contact:
			let controller:EXContactNavigationController = .get()
			return controller
        
        case .rescheduleAvailability:
            let controller:ACChangeAvailabilityViewController = .get()
            return controller
		case .participationDenied:
			let controller = CustomViewController<ParticipationView>()
			return controller
		default:
			return UIViewController()
		}
	}
	
	public func surveyTypeForState() -> SurveyType {
		switch self {
		case .chronotype:
			return .chronotype
		case .context:
			return .context
		case .wake:
			return .wake
		case .symbolsTest:
			return .symbolsTest
		case .gridTest:
			return .gridTest
		case .priceTest:
			return .priceTest
		default:
			return .unknown
		}
	}
}
