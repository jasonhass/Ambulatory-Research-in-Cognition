//
// ScheduleNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit
import Arc

open class ScheduleNavigationController: SurveyNavigationViewController, MHControllerDelegate  {
    //Override this to prepare a controller
	public var isChangingSchedule = false
    var wakeTime:String = ""
    var bedTime:String = ""
    var error:String?

	open func didCatch(errors: Error) {
		dump(errors)
	}
	
    override open func loadSurvey(template:String) {
        survey = Arc.shared.surveyController.load(survey: template)
		shouldShowHelpButton = true

        shouldNavigateToNextState = false
        //Shuffle the questions
        questions = survey.questions
        
		if let entry = Arc.shared.scheduleController.get(allEntriesForId: self.participantId!)?.first {
			wakeTime = entry.availabilityStart ?? ""
			bedTime = entry.availabilityEnd ?? ""
		}

    }
    
    override open func onQuestionDisplayed(input:SurveyInput, index:String) {
        if index == "schedule_1" {
            input.setValue(AnyResponse(type: .time, value: wakeTime))
        } else {
			input.setValue(AnyResponse(type: .time, value: bedTime))
           
        }
        
        if let surveyVC = self.topViewController as? SurveyViewController
        {
            // let's check to see if the currently set value is valid
            
            if let initialValue = input.getValue(), self.isValid(value: initialValue, index: index)
            {
                surveyVC.enableNextButton();
            }
            else
            {
                surveyVC.disableNextButton();
                surveyVC.setError(message:self.error)
                    
            }
            
            // and setup a block to check every time the user changes the time
            
            surveyVC.didChangeValue = {
                [weak self] in
                
                if let newValue = input.getValue(), self?.isValid(value: newValue, index: index) ?? false
                {
                    surveyVC.enableNextButton();
                    
                }
                else
                {
                    surveyVC.disableNextButton();
                    
                }
                surveyVC.setError(message:self?.error)
                
            };
        }
        
    }
    open override func isValid(value: QuestionResponse, index: String) -> Bool {
        error = nil
        guard index == "schedule_2"  else { return true	; }
        guard let value = value.value as? String else { return false}
        // If we're attempting to set a sleep time, we need to check and make sure that it's not
        // too close to the set wake time (and that it's not set to the same exact time)
        
   
        let formatter = DateFormatter()
        formatter.defaultDate = Date();
        formatter.dateFormat = "h:mm a"
        
        if let wake = formatter.date(from: wakeTime),
            var sleep = formatter.date(from: value)
        {
            // If the sleep time is actually "before" the wake time (like they go to sleep at 1am and wake up at 11 am),
            // then we need to add a day to the sleep date, to make the math work right.
            // We can't just look at fabs(sleep.timeIntervalSince(wake)), because it won't take into account the change in day
            // properly. If they, for instance, set their sleep time to 1am, and their wake time to 4am, checking the absolute
            // value would only give us 3 hours, but the reality is that it's 21 hours.
            
            if wake.compare(sleep) == .orderedDescending
            {
                sleep = sleep.addingDays(days: 1);
            }
            
            if sleep.timeIntervalSince(wake) < 28800
            {
                
                error = "Please set a minimum of 8 hours of wake time."
                return false;
            }
        }
        
        return true;
      
    }
    //Override this to write to other controllers
    override open func onValueSelected(value:QuestionResponse, index:String) {
		#warning("provide safer and cleaner mechanism for unwraping")
		guard let value = value.value as? String else {
			return
		}
		
        if index == "schedule_1" {
            wakeTime = value
        } else if index == "schedule_2" {
            bedTime = value
			let _ = Arc.shared.scheduleController.delete(schedulesForParticipant: self.participantId!)
			let _ = Arc.shared.scheduleController.create(entries: wakeTime,
														 endTime: bedTime,
														 weekDays: (WeekDay.sunday ... WeekDay.saturday),
														 participantId: self.participantId!)
			
			let _ = Arc.shared.scheduleController.get(confirmedSchedule: self.participantId!)
			//Probably see where the app wants to go next
			if let top = self.topViewController as? SurveyViewController {
				top.nextButton.showSpinner(color: UIColor(white: 1.0, alpha: 0.8), backgroundColor:UIColor(named:"Primary") )
			}
			
			
//			DispatchQueue.global(qos: .userInteractive).async {
			MHController.dataContext.perform {
					
				
				if self.isChangingSchedule {
					if let studyId = Arc.shared.currentStudy {
						Arc.shared.studyController.clear(upcomingSessions: Int(studyId))
						Arc.shared.studyController.create(testSessions: Int(studyId))
						Arc.shared.notificationController.clear(sessionNotifications: Int(studyId))
						Arc.shared.notificationController.schedule(sessionNotifications: Int(studyId))
						let study = Arc.shared.studyController.get(study: studyId)
						Arc.shared.sessionController.uploadSchedule(studyPeriod: study!)
					}
					
				} else {
					var date = Date()
					
					if let firstTest = Arc.shared.studyController.firstTest {
						
						date = Date(timeIntervalSince1970: firstTest.session_date)
						
					}
					let study = Arc.shared.studyController.createStudyPeriod(forDate: date)
					_ = Arc.shared.studyController.mark(confirmed: Int(study.studyID))
					
					Arc.shared.studyController.create(testSessions: Int(study.studyID))
					if let latestTest = Arc.shared.studyController.latestTest, let session = Int(latestTest.session_id){
						Arc.shared.studyController.delete(sessionsUpTo: session, inStudy: Int(study.studyID))
					}
					Arc.shared.notificationController.clear(sessionNotifications: Int(study.studyID))
					Arc.shared.notificationController.schedule(sessionNotifications: Int(study.studyID))
					study.hasConfirmedDate = true
					
					
					Arc.shared.scheduleController.upload(confirmedSchedule: Int(study.studyID))
					Arc.shared.sessionController.uploadSchedule(studyPeriod: study)
				}
				Arc.shared.studyController.save()

				
				
			}
			let loader:LoadingScreenViewController = .get()
			
			Arc.shared.studyController.progressHandler = loader.didProgress
			
			Arc.shared.appNavigation.navigate(vc: loader, direction: .fade)
			
			
			
				
        }
        
    }
    
	
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
	
	
}
