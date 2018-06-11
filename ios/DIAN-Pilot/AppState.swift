//
//  AppState.swift
//  StateMachineNavigation
//

//  Copyright © 2017 Philip Hayes. All rights reserved.
//

import Foundation
import UIKit

enum AppState {
  
    
    //These views have no parameters that need to be passed in, just display.
    //This is similar to home/
    case
    setupUser,
    setupTime,
    setupArcDate,
    setupLanguage,
    
    confirmArcDate,
    noTest,
    baselineWait,
    endProject,
    
    testStart,
    testOverview,
    testSession,
    testInstructions,
    
    surveyChronotype,
    surveyWake,
    surveyContext,
    surveyInterrupted,
    surveyFinalTest,
    surveyUpgradePhone,
    
    beginningVerification,
    endingVerification,
    
    twoFactorEnterCode,
    twoFactorResendCode,
    twoFactorThankYou
    
    
    
    
    func sbInfo() -> (storyboardName:String, storyboardId:String)
    {
        switch self
        {
            
        case .setupUser:
            return (storyboardName: "Setup", storyboardId: "UserSetupViewController");
        case .setupTime:
            return (storyboardName: "Setup", storyboardId: "TimeSetupViewController");
        case .setupArcDate:
            return (storyboardName: "Setup", storyboardId: "DateSelectionViewController");
        case .setupLanguage:
            return (storyboardName: "Setup", storyboardId: "Language");
            
            
        case .confirmArcDate:
            return (storyboardName: "ArcConfirm", storyboardId: "ArcConfirmViewController");
        case .noTest:
            return (storyboardName: "Main", storyboardId: "NoTestViewController");
        case .baselineWait:
            return (storyboardName: "Main", storyboardId: "BaselineWaitViewController");
        case .endProject:
            return (storyboardName: "Main", storyboardId: "EndProjectViewController");
            
        case .testStart:
            return (storyboardName: "Security", storyboardId: "SignatureViewController");
        case .testOverview:
            return (storyboardName: "TestOverview", storyboardId: "SurveyOverviewViewController");
        case .testSession:
            return (storyboardName: "TestSession", storyboardId: "DNTestHostViewController");
        case .testInstructions:
            return (storyboardName: "TestOverview", storyboardId: "TestInstructionsViewController");
        
        case .surveyChronotype:
            return (storyboardName: "Chronotype", storyboardId: "chronotypeStart");
        case .surveyWake:
            return (storyboardName: "Wake", storyboardId: "wake1");
        case .surveyContext:
            return (storyboardName: "ContextSurvey", storyboardId: "ContextSurveyViewController");
        
        case .surveyInterrupted:
            return (storyboardName: "FinalSurvey", storyboardId: "FinalQuestionsViewController");
        case .surveyFinalTest:
            return (storyboardName: "FinalSurvey", storyboardId: "FinalSessionQuestionsViewController");
        case .surveyUpgradePhone:
            return (storyboardName: "ConfirmPhone", storyboardId: "ConfirmPhoneViewController");
        
        case .beginningVerification:
            return (storyboardName: "Security", storyboardId: "SignatureViewController");
        case .endingVerification:
            return (storyboardName: "Security", storyboardId: "EndSignatureViewController");
            
        case .twoFactorEnterCode:
            return (storyboardName: "Setup", storyboardId: "EnterCodeViewController");
        case .twoFactorResendCode:
            return (storyboardName: "Setup", storyboardId: "ResendCodeViewController");
        case .twoFactorThankYou:
            return (storyboardName: "Setup", storyboardId: "ResendCodeVerificationViewController");
        }
    }
        
}
class AppRouter {
    
    init(){
        
    }
    
    @discardableResult
    func go(state:AppState, userInfo:Dictionary<String, Any>? = nil)->DNViewController {
        var appDelegate:AppDelegate = UIApplication.shared.delegate as! AppDelegate

        let window = UIApplication.shared.keyWindow ?? UIWindow(frame: UIScreen.main.bounds)
        appDelegate.window = window

        defer {
            if !window.isKeyWindow {
                window.makeKeyAndVisible()
            }
        }
        
        let vc = get(state: state, userInfo: userInfo)
        
        window.rootViewController = vc;
        return window.rootViewController as! DNViewController
    }
    
    func get(state:AppState, userInfo:Dictionary<String, Any>? = nil) -> DNViewController {
        let sbInfo = state.sbInfo();
        
        
        let vc = UIStoryboard(name: sbInfo.storyboardName, bundle: nil).instantiateViewController(withIdentifier: sbInfo.storyboardId);
        
        if let ui = userInfo, let v = vc as? DNViewController
        {
            v.userInfo = ui;
        }
        return vc as! DNViewController
    }
    
}
