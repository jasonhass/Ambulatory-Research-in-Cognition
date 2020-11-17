//
// ACEarningsDetailView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import ArcUIKit

public class ACEarningsDetailView : ACTemplateView {
	enum GoalDisplayName : String {
		case testSession = "test-session"
		case fourOfFour = "4-out-of-4"
		case twoADay = "2-a-day"
		case totalSessions =  "21-sessions"
		
		func getName() ->String {
			switch self {
			
			
			case .testSession:
				return "progress_earnings_status1".localized(ACTranslationKey.progress_earnings_status1)
			case .fourOfFour:
				return "progress_earnings_status3".localized(ACTranslationKey.progress_earnings_status2)
			case .twoADay:
				return "progress_earnings_status4".localized(ACTranslationKey.progress_earnings_status3)
			case .totalSessions:
				return "progress_earnings_status2".localized(ACTranslationKey.progress_earnings_status4)
			
			}
		}
	}
	
	
	var detailGroups:[EarningsDetailGroup] = []
	var thisStudy:ThisStudyExpressible = Arc.shared.studyController
	var thisWeek:ThisWeekExpressible = Arc.shared.studyController
	weak var studyTotalLabel:ACLabel!
	weak var syncLabel:ACLabel!
	weak var content:UIStackView!
	
	
	
	public override func content(_ view: UIView) {
		backgroundColor = ACColor.primaryInfo
		if let v = view as? UIStackView {
			v.layoutMargins = .zero
		}
		 view.stack { [unowned self] in
			$0.axis = .vertical
			$0.spacing = 20
//			$0.stack {
//				$0.axis = .vertical
//                
//				$0.alignment = .leading
//				$0.button {
//					$0.layout {
//						$0.width == 80 ~ 999
//						$0.height == 32 ~ 999
//                        $0.leading == safeAreaLayoutGuide.leadingAnchor + 10
//					}
//					$0.tintColor = .white
//					$0.backgroundColor = UIColor(red:1, green:1, blue:1, alpha:0.15)
//					$0.layer.cornerRadius = 16
//					$0.setTitle("".localized(ACTranslationKey.button_back), for: .normal)
//					$0.setImage(UIImage(named:"cut-ups/icons/arrow_left_white"), for: .normal)
//				}
//			}
			$0.stack {
				$0.isLayoutMarginsRelativeArrangement = true
				$0.layoutMargins = UIEdgeInsets(top: 0, left: 24, bottom: 0, right: 24)
				$0.axis = .vertical
				$0.spacing = 20
				$0.acLabel {
					Roboto.Style.headingMedium($0, color:.white)
					$0.text = "".localized(ACTranslationKey.earnings_details_header)
					
				}
				
				$0.acHorizontalBar {
					
					$0.relativeWidth = 0.15
					$0.color = UIColor(named: "HorizontalSeparator")
					$0.layout {
						$0.height == 2 ~ 999
					}
				}
				
				let v = $0.acLabel {
					Roboto.Style.body($0, color:ACColor.highlight)
					$0.text = "".localized(ACTranslationKey.earnings_studytotal)
				}
				$0.setCustomSpacing(8, after: v)
				self.studyTotalLabel = $0.acLabel {
					Roboto.Style.earningsBold($0, color:.white)
					$0.text = ""

				}
				

				self.syncLabel = $0.acLabel {
					
					Roboto.Style.subBody($0, color:UIColor(red:0.71, green:0.73, blue:0.8, alpha:1))
					$0.text = "".localized(ACTranslationKey.earnings_sync)
				}
				
			}
			
			self.content = $0.stack {
				$0.axis = .vertical
				
			}
		}
	}
	
	public func set(studyTotal:String) {
		studyTotalLabel.text = studyTotal
	}
	public func set(synched:TimeInterval) {
		var time = ""
		if #available(iOS 13.0, *) {
			//TODO: Update for iOS 13, right now, this is commented out because
			//it does not build in xcode 10. 
			let dateFormatter:RelativeDateTimeFormatter = RelativeDateTimeFormatter()
			dateFormatter.locale = Arc.shared.appController.locale.getLocale()

			time = dateFormatter.localizedString(for: Date(timeIntervalSince1970: synched), relativeTo:  Date())
		} else {
			// Fallback on earlier versions
			let dateFormatter:DateFormatter = DateFormatter()
			dateFormatter.locale = Arc.shared.appController.locale.getLocale()
			dateFormatter.dateFormat = "MMM dd 'at' hh:mm a"
			time = dateFormatter.string(from: Date(timeIntervalSince1970: synched))
		}
		
		syncLabel.text = "\("".localized(ACTranslationKey.earnings_sync)) \(time)"
	}
	public func clear() {
		content.removeSubviews()
	}
	public func add(cycle:EarningDetail.Response.Earnings.Cycle) {
		let start = Date(timeIntervalSince1970: cycle.start_date).localizedFormat(template: ACDateStyle.mediumWeekDayMonthDay.rawValue)
		
		let end = Date(timeIntervalSince1970: cycle.end_date).localizedFormat(template: ACDateStyle.mediumWeekDayMonthDay.rawValue)
			content.earningsDetailGroup {
			
			if thisStudy.week == cycle.cycle {
				switch thisStudy.studyState {
				case .active, .activeBaseline, .baseline:
					$0.set(header: "This Week".localized(ACTranslationKey.earnings_details_subheader1))
                    $0.set(badge: "Ongoing".localized(ACTranslationKey.status_ongoing))
				case .inactive, .complete:
					$0.set(header: "Completed Testing Cycle")
					$0.set(badge: nil)

					
				case .unknown:
					$0.set(header: "Completed Testing Cycle".localized(ACTranslationKey.earnings_details_subheader2))
					$0.set(badge: nil)
				}
			} else {
				$0.set(header: "Completed Testing Cycle".localized(ACTranslationKey.earnings_details_subheader2))
				$0.set(badge: nil)

			}
			$0.set(subHeader: "\(start) - \(end)")
			$0.set(cycleTotal: cycle.total)
			for detail in cycle.details {
				let name = GoalDisplayName(rawValue: detail.name)?.getName() ?? "Goal Name"
				var count = ""
				if detail.count_completed == 1 {
					count = " (\(detail.count_completed) time)"
				}
				if detail.count_completed > 1 {
					count = " (\(detail.count_completed) times)"

				}
				$0.add(section:(body: "*\(name)*\n\(detail.value)\(count)", price: detail.amount_earned))
			}
		}
		
	}
	
}

