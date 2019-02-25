//
// ACEnvironment.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import Foundation

public protocol ArcEnvironment {
    var mockData:Bool {get}
    var blockApiRequests:Bool {get}
    var baseUrl:String? {get}
    var welcomeLogo:String? {get}
    var welcomeText:String? {get}
    var privacyPolicyUrl:String? {get}
    var arcStartDays:Dictionary<Int, Int>? {get}
   
    var appController:AppController {get}
    
    var authController:AuthController {get}
    
    var sessionController:SessionController {get}
    
    var surveyController:SurveyController {get}
    
    var scheduleController:ScheduleController {get}
    
    var gridTestController:GridTestController {get}
    
    var pricesTestController:PricesTestController {get}
    
    var symbolsTestController:SymbolsTestController {get}
    
    var studyController:StudyController {get}
    
    var notificationController:NotificationController {get}
    
    var appNavigation:AppNavigationController {get}
    
    var controllerRegistry:ArcControllerRegistry {get}
    

}

public extension ArcEnvironment {
    
    //This will trigger a flag that causes coredata to use a mock
    //persistent store, an in-memory database. 
    public var mockData:Bool {return false}
    
    public var appController:AppController {return AppController()}
    
    public var authController:AuthController {return AuthController()}
    
    public var sessionController:SessionController {return SessionController()}
    
    public var surveyController:SurveyController {return SurveyController()}
    
    public var scheduleController:ScheduleController {return ScheduleController()}
    
    public var gridTestController:GridTestController {return GridTestController()}
    
    public var pricesTestController:PricesTestController {return PricesTestController()}
    
    public var symbolsTestController:SymbolsTestController {return SymbolsTestController()}
    
    public var studyController:StudyController {return StudyController()}
    
    public var notificationController:NotificationController {return NotificationController()}
    
    
    public var controllerRegistry:ArcControllerRegistry {return ArcControllerRegistry()}
}
