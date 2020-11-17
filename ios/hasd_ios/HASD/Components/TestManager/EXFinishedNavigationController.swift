//
// EXFinishedNavigationController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit
import Arc

class HASDFinishedNavigationController: FinishedNavigationController {

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "finished"
        shouldShowHelpButton = false
        displayHelpButton(shouldShowHelpButton)
		//shouldShowBackButton = false
        // Do any additional setup after loading the view.
    }
    
    
	override open func templateForQuestion(id: String) -> Dictionary<String, String> {
        var dict:Dictionary<String, String> = [:]
        if let upcoming = Arc.shared.studyController.getUpcomingStudyPeriod()?.sessions?.firstObject as? Session {
            let d = DateFormatter()
            d.dateFormat = "MM/dd/yy"
            d.locale = Locale(identifier: Arc.shared.appController.locale.rawValue)
            let date = upcoming.sessionDate ?? Date()
            let enddate = upcoming.sessionDate?.addingDays(days: 6) ?? Date()

            let dateString = date.localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue)
            let endDateString = enddate.localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue)
            dict["next_cycle"] = dateString
            dict["DATE1"] = dateString
            dict["DATE2"] = endDateString
        }
        return dict
    }
    
	
    //Override this to write to other controllers
    override open func valueSelected(value:QuestionResponse, index:String) {
        
        guard let session = Arc.shared.currentTestSession else {return}
        guard let study = Arc.shared.currentStudy else {return}
        if let v = value.value as? Int {
            if v == 0 {
                Arc.shared.studyController.mark(interrupted:true, sessionId: session, studyId: study)
            } else if v == 1 {
                Arc.shared.studyController.mark(interrupted:false, sessionId: session, studyId: study)
                
            }
        }
        
        if let value = value.value as? UIImage {
            if Arc.shared.appController.save(signature: value, sessionId: Int64(session), tag: 1) {
                print("saved")
            }
           
            
        }
        
    }
//    func formatQuestion(_ questionId:String) -> Dictionary<String, String> {
//
//        guard QuestionIndex.init(rawValue: questionId) == .schedule_3 else {
//            return [:]
//        }
//
//        let wake_time = wakeSleeptimes[.schedule_1]?.time
//        let sleep_time = wakeSleeptimes[.schedule_2]?.time
//
//        return ["wake_time":wake_time!, "sleep_time":sleep_time!]
//    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
