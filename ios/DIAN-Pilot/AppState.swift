//
//  AppState.swift
//  StateMachineNavigation
//
//  Created by Philip Hayes on 5/3/17.
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
    
    confirmArcDate,
    noTest,
    baselineWait,
    
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
    endingVerification
    
    
    
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
            
            
        case .confirmArcDate:
            return (storyboardName: "ArcConfirm", storyboardId: "ArcConfirmViewController");
        case .noTest:
            return (storyboardName: "Main", storyboardId: "NoTestViewController");
        case .baselineWait:
            return (storyboardName: "Main", storyboardId: "BaselineWaitViewController");
            
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
        
        let sbInfo = state.sbInfo();
        
        
        let vc = UIStoryboard(name: sbInfo.storyboardName, bundle: nil).instantiateViewController(withIdentifier: sbInfo.storyboardId);
        
        if let ui = userInfo, let v = vc as? DNViewController
        {
            v.userInfo = ui;
        }
        
        window.rootViewController = vc;
        return window.rootViewController as! DNViewController
    }
    
    
}
