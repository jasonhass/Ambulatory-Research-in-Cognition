//
// HASDOnboardingSurveyViewController.swift
//




import UIKit
import Arc

public class HASDOnboardingSurveyViewController: OnboardingSurveyViewController {
    var isNextEnabled:Bool = false
    
    override open func didChangeValue() {
        
        let question = questions[currentIndex]
        
        let _ = Arc.shared.surveyController.mark(responseTime: question.questionId,
                                                 question: question.prompt,
                                                 forSurveyResponse: self.surveyId)
        
        if (!isNextEnabled) {
            enableNextButton()
            isNextEnabled = true
        } else {
            disableNextButton()
            isNextEnabled = false
        }
    }
    
    open override func valueSelected(value: QuestionResponse, index: String) {
        super.valueSelected(value: value, index: index)
        if index == "commitment" {
            app.appController.commitment = .committed
            
        }
    }
}
