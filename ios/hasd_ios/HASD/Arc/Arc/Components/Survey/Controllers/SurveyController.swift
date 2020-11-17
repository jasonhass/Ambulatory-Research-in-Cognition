//
// SurveyController.swift
//



import Foundation
import UIKit
import CoreData
open class SurveyController : MHController {
    private var loadedSurvey:Survey?
	open func load(survey template:String, bundle:Bundle = .main) -> Survey {
		
		guard let asset = NSDataAsset(name: template, bundle:bundle) else {
			fatalError("Missing data asset: \(template)")
		}
		do {
			let survey = try JSONDecoder().decode(Survey.self, from: asset.data)
			
			loadedSurvey = survey
			return survey
		} catch {
			fatalError("Invalid asset format: \(template) - Error: \(error)")
			
		}
		
	}
    
    open func get(question: String) -> Survey.Question {
		
        guard let questions = loadedSurvey?.questions else {
            fatalError("Survey not loaded")
        }
		
		if let q = questions.first(where: { (obj) -> Bool in
			return obj.questionId == question
		}) {
			return q
		}
		if let q = loadedSurvey?.subQuestions?.first(where: { (obj) -> Bool in
			return obj.questionId == question
		}) {
			return q
		} else {
			fatalError("Invalid question id: \(question), supplied")
		}
		
    }
    
    
    open func create(surveyResponse id:String = UUID().uuidString, type:SurveyType? = nil)  -> String {
        
        let surveyResponse = self.createSurveyResponse(surveyResponse: id, type: type)
        return save(id: id, obj: surveyResponse).id!

    }
    
    open func createSurveyResponse(surveyResponse id:String = UUID().uuidString, type:SurveyType? = nil) -> SurveyResponse
    {
        let surveyResponse = SurveyResponse(id: id, type: type ?? .unknown)
        return surveyResponse;
    }
    
    
    open func get(surveyResponse responseId:String) -> SurveyResponse? {
        
        do {
            let response:SurveyResponse? = try get(id: responseId)
        
            return response
        } catch {
            delegate?.didCatch(errors: error)
        }
        return nil
    }
	open func mark(startDate surveyId:String) {
		if var surveyResponse = get(surveyResponse: surveyId) {
			surveyResponse.start_date = Date().timeIntervalSince1970
			_ = save(id: surveyResponse.id!, obj: surveyResponse)
		} else {
			fatalError("Invalid survey id '\(surveyId)' supplied.")
		}
		
	}
    open func set(response: QuestionResponse, questionId:String, question:String, forSurveyId surveyId:String) -> SurveyResponse {
        
        if var surveyResponse = get(surveyResponse: surveyId) {
            if let index = surveyResponse.questions?.firstIndex(where: { (question) -> Bool in
                return questionId == question.question_id
            }), var question = surveyResponse.questions?[index] {
                question.type = response.type
                question.value = response.value
				question.text_value = response.text_value
                if question.response_time == nil {
                    question.response_time = Date().timeIntervalSince1970
                }
                surveyResponse.questions?.replaceSubrange((index...index), with: [question])
                
            } else {
				
				var q = SurveyResponse.Question(question_id: questionId,
										question: question,
										response: response.value)
				q.type = response.type
				q.text_value = response.text_value

                if q.response_time == nil {
                    q.response_time = Date().timeIntervalSince1970
                }
                surveyResponse.questions?.append(q)
            }
            
            
            //Force unwrap the id to ensure that nil ID's aren't being passed in
            
            return save(id: surveyResponse.id!, obj: surveyResponse)
            
        } else {
            fatalError("Invalid survey id '\(surveyId)' supplied.")
        }
        
    }
	
    
    open func mark(responseTime questionId: String, question:String, forSurveyResponse surveyId:String) {
        if var surveyResponse = get(surveyResponse: surveyId) {
            
            if let index = surveyResponse.questions?.firstIndex(where: { (question) -> Bool in
                return questionId == question.question_id
            }), var question = surveyResponse.questions?[index] {
                
                question.response_time = Date().timeIntervalSince1970
                
                surveyResponse.questions?.replaceSubrange((index...index), with: [question])
                
            } else {
                
                var q = SurveyResponse.Question(question_id: questionId,
                                                question: question,
                                                response: nil)
                    q.response_time = Date().timeIntervalSince1970
                
                surveyResponse.questions?.append(q)
            }
            
            
            //Force unwrap the id to ensure that nil ID's aren't being passed in
            
            _ = save(id: surveyResponse.id!, obj: surveyResponse)
            
        } else {
            fatalError("Invalid survey id '\(surveyId)' supplied.")
        }
    }
    
	open func mark(displayTime questionId: String, question:String, forSurveyResponse surveyId:String) {
		if var surveyResponse = get(surveyResponse: surveyId) {
			if let index = surveyResponse.questions?.firstIndex(where: { (question) -> Bool in
				return questionId == question.question_id
			}), var question = surveyResponse.questions?[index] {
				if question.display_time == nil {
					question.display_time = Date().timeIntervalSince1970
				}
				surveyResponse.questions?.replaceSubrange((index...index), with: [question])
				
			} else {
				
				var q = SurveyResponse.Question(question_id: questionId,
												question: question,
												response: nil)
				if q.display_time == nil {
					q.display_time = Date().timeIntervalSince1970
				}
				
				surveyResponse.questions?.append(q)
			}
			
			
			//Force unwrap the id to ensure that nil ID's aren't being passed in
			
			_ = save(id: surveyResponse.id!, obj: surveyResponse)
			
		} else {
			fatalError("Invalid survey id '\(surveyId)' supplied.")
		}
	}
	open func getValue<T>(forQuestion questionId:String, fromSurveyResponse surveyId:String) -> T? {
		return getResponse(forQuestion: questionId, fromSurveyResponse: surveyId)?.getValue()
	}
    open func getResponse(forQuestion questionId:String, fromSurveyResponse surveyId:String) -> QuestionResponse? {
        
        if let surveyResponse = get(surveyResponse: surveyId) {
            
//            print(surveyResponse.toString())
            //Force unwrap the id to ensure that nil ID's aren't being passed in
            let response =  surveyResponse.questions?.filter({ (question) -> Bool in
                return questionId == question.question_id
            }).first
            
            return response
        }
        
        return nil
    }
	
	open func delete(response questionId:String, fromSurveyResponse surveyId:String) -> SurveyResponse {
		if var surveyResponse = get(surveyResponse: surveyId) {
			
//			print(surveyResponse.toString())
			//Force unwrap the id to ensure that nil ID's aren't being passed in
			let response =  surveyResponse.questions?.filter({ (question) -> Bool in
				return questionId != question.question_id
			})
			surveyResponse.questions = response
			return save(id: surveyId, obj: surveyResponse)
		} else {
			fatalError("Invalid survey id '\(surveyId)' supplied.")
		}
		
	}
	
    //A catch all update function
    open func update(surveyResponse: SurveyResponse) -> SurveyResponse {
        
        return save(id: surveyResponse.id!, obj: surveyResponse)
        
    }
    open func deleteSurvey(survey:SurveyResponse) -> Bool {
        
        return delete(data: survey)
    }
}
