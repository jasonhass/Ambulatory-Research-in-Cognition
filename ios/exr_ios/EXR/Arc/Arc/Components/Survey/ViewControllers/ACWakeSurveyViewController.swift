//
// ACWakeSurveyViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

open class ACWakeSurveyViewController: BasicSurveyViewController {
    enum WakeSurveyQuestion : String {
        case bedTimeLastNight = "wake_1"
        case sleepTimeLastNight = "wake_2"
        case wakeTimeThisMorning = "wake_4"
        case outOfBedTimeThisMorning = "wake_5"
        case workSleep = "chronotype_3"
        case workWake = "chronotype_4"
        case nonworkSleep = "chronotype_5"
        case nonworkWake = "chronotype_6"
        case nonworkSleep2 = "chronotype_7"
        case nonworkWake2 = "chronotype_8"

        case other
    }
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        shouldShowHelpButton = false
    }
	
    open override func didPresentQuestion(input: SurveyInput?, questionId: String)
    {
		super.didPresentQuestion(input: input, questionId: questionId)
        guard let input = input else { return; }
        
        let question = WakeSurveyQuestion(rawValue: questionId) ?? .other
        
        guard question != .other else {return}
        
        let date = Date()
        
        let day = WeekDay.getDayOfWeek(date)
        
        guard let today = Arc.shared.scheduleController.get(entriesForDay: day, forParticipant: Arc.shared.participantId ?? 0)?.first else {return}
        guard let yesterday = Arc.shared.scheduleController.get(entriesForDay: day.advanced(by: -1), forParticipant: Arc.shared.participantId ?? 0)?.first else {return}

        switch question {
        
        case .bedTimeLastNight, .sleepTimeLastNight:
            
            guard getAnswerFor(question: question) == nil else {return}

            input.setValue(AnyResponse(type: .time, value: yesterday.availabilityEnd))
            
            enableNextButton()
        
        
        
        case .wakeTimeThisMorning, .outOfBedTimeThisMorning, .workWake:
            
            guard getAnswerFor(question: question) == nil else {return}
            
            input.setValue(AnyResponse(type: .time, value: today.availabilityStart))
            
            enableNextButton()
        
        
        
        case .workSleep:
            
            guard getAnswerFor(question: question) == nil else {return}
            
            input.setValue(AnyResponse(type: .time, value: today.availabilityEnd))
            
            enableNextButton()
        
        
        
        case .nonworkWake, .nonworkWake2:
            
            guard getAnswerFor(question: question) == nil else {return}
            
            guard let saturday = Arc.shared.scheduleController.get(entriesForDay: .saturday, forParticipant: Arc.shared.participantId ?? 0)?.first else {return}
            
            input.setValue(AnyResponse(type: .time, value: saturday.availabilityStart))
            
            enableNextButton()
        
        
        
        case .nonworkSleep, .nonworkSleep2:
            
            guard getAnswerFor(question: question) == nil else {return}
            
            guard let saturday = Arc.shared.scheduleController.get(entriesForDay: .saturday, forParticipant: Arc.shared.participantId ?? 0)?.first else {return}
            
            input.setValue(AnyResponse(type: .time, value: saturday.availabilityEnd))
            
            enableNextButton()
        
        default:
            return
        }
    }
    
    
    private func getAnswerFor(question:WakeSurveyQuestion) -> String? {
        let question = question.rawValue
        
        guard let answer = Arc.shared.surveyController.getResponse(forQuestion: question, fromSurveyResponse: surveyId) else {
            return nil
        }
        
        guard let value = answer.value as? String else {return nil}
        
        return value
    }
}
