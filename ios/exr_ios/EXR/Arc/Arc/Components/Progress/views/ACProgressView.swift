//
// ACProgressView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
class ACProgressView: ACTemplateView {
	private var startDay:Date?
	
	//Today section variables
	var todaySection:UIView!
	var headerLabel:ACLabel!
	var progressViews:ACCircularProgressGroupStackView!
	var progressViewStack:UIStackView!
	var todaysSessionCompletionLabel:ACLabel!
	var todaysSessionRemainingLabel:ACLabel!
    var sessionRemainingView:UIView!
	//This week section variables
	var weekSection:UIView!
	var weekHeaderLabel:ACLabel!
	var weekProgressView:ACWeekStepperView!
	var noticeLabel:ACLabel!
	var dayOfWeekLabel:ACLabel!
	var startDateLabel:ACLabel!
	var endDateLabel:ACLabel!
	
	//This study section variables
	var studySection:UIView!
	var studyHeaderLabel:ACLabel!
	var weekOfStudyLabel:ACLabel!
	var blockProgressView:BlockProgressview!
	var joinDateLabel:ACLabel!
	var finishDateLabel:ACLabel!
	var timeBetweenTestWeeksLabel:ACLabel!
	var nextWeekStack:UIStackView!
	var nextTestingCycle:ACLabel!
	var viewFaqButton:ACButton!

	override func content(_ view: UIView) {
		if let v = view as? UIStackView {
			v.layoutMargins = .zero
		}
		view.stack { [unowned self] in
			$0.axis = .vertical
			$0.isLayoutMarginsRelativeArrangement = true
			$0.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
			self.todaySection = $0.view {
				
				//Top section
				$0.backgroundColor = .white
				$0.stack {
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 24, left: 24, bottom: 24, right: 24)
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 20
					
					self.headerLabel = $0.acLabel {
						Roboto.Style.headingMedium($0, color: .black)
						$0.text = "Today's Sessions".localized(ACTranslationKey.progress_daily_header)
						
					}
					
					$0.acHorizontalBar {
						$0.relativeWidth = 0.15
						$0.color = UIColor(named: "HorizontalSeparator")
						$0.layout {
							$0.height == 2 ~ 999
							
						}
					}
					self.progressViews = $0.circularProgressGroup {
						$0.layout {
							$0.height == 64 ~ 999
						}
						
					}
					$0.stack {
						$0.axis = .vertical
						$0.alignment = .leading
						$0.stack {
							$0.alignment = .center
							$0.view {
								self.todaysSessionCompletionLabel = $0.acLabel {
									Roboto.Style.body($0, color: #colorLiteral(red: 0.04300000146, green: 0.1220000014, blue: 0.3330000043, alpha: 1))
									
									$0.text = "*{#}* Complete|"
									$0.numberOfLines = 1
								}
                                self.todaysSessionCompletionLabel.attachTo(view: $0, margins: UIEdgeInsets(top: 3, left: 8, bottom: 3, right: 8))
								$0.backgroundColor = .clear
                                $0.layer.cornerRadius = 4
                                $0.clipsToBounds = true
							}
							$0.acLabel {
								Roboto.Style.body($0, color: #colorLiteral(red: 0.04300000146, green: 0.1220000014, blue: 0.3330000043, alpha: 1))
								
								$0.text = "|"
                                $0.numberOfLines = 1
							}
                            self.sessionRemainingView = $0.view {
                                self.todaysSessionRemainingLabel = $0.acLabel {
                                    Roboto.Style.body($0, color: #colorLiteral(red: 0.04300000146, green: 0.1220000014, blue: 0.3330000043, alpha: 1))
                                    $0.text = " *{#}* Remaining"
									$0.numberOfLines = 0
                                }
                                self.todaysSessionRemainingLabel.attachTo(view: $0, margins: UIEdgeInsets(top: 3, left: 8, bottom: 3, right: 8))
								$0.backgroundColor = .clear
                                $0.layer.cornerRadius = 4
                                $0.clipsToBounds = true
                                
							}
						}
					}
					
					
				}
			}
			self.weekSection =  $0.view {
				
				//This Week
				$0.backgroundColor = UIColor(named: "Progress Week")
				$0.stack {
					
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 20
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 24, left: 24, bottom: 24, right: 24)
					
					
					self.weekHeaderLabel = $0.acLabel {
						Roboto.Style.headingMedium($0, color: .black)
						$0.text = "This Week".localized(ACTranslationKey.progress_weekly_header)
						
					}
					
					$0.acHorizontalBar {
						$0.relativeWidth = 0.15
						$0.color = UIColor(named: "HorizontalSeparator")
						$0.layout {
							$0.height == 2 ~ 999
							
						}
					}
					
					self.dayOfWeekLabel = $0.acLabel {
						Roboto.Style.subHeading($0)
						
					}
					self.noticeLabel = $0.acLabel {
						Roboto.Style.body($0)
						
					}

					self.weekProgressView = $0.weekStepperProgress {
						$0.set(step: 0, of: ["S".localized(ACTranslationKey.day_abbrev_sun),
											 "M".localized(ACTranslationKey.day_abbrev_mon),
											 "T".localized(ACTranslationKey.day_abbrev_tues),
											 "W".localized(ACTranslationKey.day_abbrev_weds),
											 "T".localized(ACTranslationKey.day_abbrev_thurs),
											 "F".localized(ACTranslationKey.day_abbrev_fri),
											 "S".localized(ACTranslationKey.day_abbrev_sat)])
					}
					
					$0.stack {
						$0.axis = .vertical
						$0.spacing = 8.0
						$0.acLabel {
							Roboto.Style.body($0, color: ACColor.primaryDate)
							$0.text = "".localized(ACTranslationKey.progress_startdate)
							
						}
						self.startDateLabel = $0.acLabel {
                            Roboto.Style.body($0, color: ACColor.secondaryDate)
							$0.text = Date().localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue,
															 options: 0,
															 locale: nil)
						}
					}
					
					$0.stack {
						$0.axis = .vertical
						$0.spacing = 8.0

						
						$0.acLabel {
							Roboto.Style.body($0, color: ACColor.primaryDate)
							$0.text = "".localized(ACTranslationKey.progress_enddate)
						}
						self.endDateLabel = $0.acLabel {
							Roboto.Style.body($0, color: ACColor.secondaryDate)
							$0.text = Date().addingDays(days: 6).localizedFormat(template: ACDateStyle.longWeekdayMonthDay.rawValue,
																				 options: 0,
																				 locale: nil)
						}
					}
					
				}
			}
			self.studySection = $0.view { [unowned self] in
				
				//This Study
				$0.backgroundColor = ACColor.primaryInfo
				$0.stack {
					
					$0.attachTo(view: $0.superview)
					$0.axis = .vertical
					$0.alignment = .fill
					$0.spacing = 20
					$0.isLayoutMarginsRelativeArrangement = true
					$0.layoutMargins = UIEdgeInsets(top: 24, left: 24, bottom: 48, right: 24)
					self.weekHeaderLabel = $0.acLabel {
						Roboto.Style.headingMedium($0, color: .white)
						$0.text = "This Study".localized(ACTranslationKey.progress_study_header)
						
					}
					
					$0.acHorizontalBar {
						$0.relativeWidth = 0.15
						$0.color = UIColor(named: "HorizontalSeparator")
						$0.layout {
							$0.height == 2 ~ 999
							
						}
					}
					$0.stack {
						self.weekOfStudyLabel = $0.acLabel {
							Roboto.Style.subHeading($0, color: .white)
							$0.text = "".localized(ACTranslationKey.progress_studystatus)
						}
					}
					
					
					
					self.blockProgressView = $0.blockProgress {
						$0.layout {
							$0.height == 42
						}
						
					}
					
					$0.stack {
						$0.axis = .vertical
						$0.spacing = 8.0
						$0.acLabel {
							Roboto.Style.body($0, color: ACColor.highlight)
							$0.text = "".localized(ACTranslationKey.progress_joindate)
							
						}
						self.joinDateLabel = $0.acLabel {
							Roboto.Style.body($0, color: .white)
							
						}
					}
					
					$0.stack {
						$0.axis = .vertical
						$0.alignment = .leading
						$0.spacing = 8.0
						
						$0.stack {
							$0.axis = .horizontal
							$0.distribution = .equalSpacing
							$0.acLabel {
								Roboto.Style.body($0, color: ACColor.highlight)
								$0.text = "".localized(ACTranslationKey.progress_finishdate)
								$0.numberOfLines = 1

							}
							$0.acLabel {
								Roboto.Style.body($0, color: ACColor.badgeBackground)
								$0.text = "".localized(ACTranslationKey.footnote_symbol)
								$0.numberOfLines = 1
							}
						}
						self.finishDateLabel = $0.acLabel {
							Roboto.Style.body($0, color:.white)
							
						}
					}
					
					$0.stack {
						$0.axis = .vertical
						$0.spacing = 8.0
						
						
						$0.acLabel {
							Roboto.Style.body($0, color: ACColor.highlight)
							$0.text = "".localized(ACTranslationKey.progress_timebtwtesting)
						}
						self.timeBetweenTestWeeksLabel = $0.acLabel {
							
							
							Roboto.Style.body($0, color:.white)
							
						}
					}
					
					self.nextWeekStack = $0.stack {
						$0.axis = .vertical
						$0.spacing = 8.0
						
						
						$0.acLabel {
							Roboto.Style.body($0, color: ACColor.highlight)
							$0.text = "".localized(ACTranslationKey.progress_nextcycle)
						}
						self.nextTestingCycle = $0.acLabel {
							
							
							Roboto.Style.body($0, color:.white)
							
						}
					}
					
					self.viewFaqButton = $0.acButton {
						$0.primaryColor = ACColor.secondary
						$0.secondaryColor = ACColor.secondaryGradient
						$0.setTitleColor(ACColor.badgeText, for: .normal)
						$0.setTitle("".localized(ACTranslationKey.button_viewfaq), for: .normal)
					}
					$0.stack {
						$0.axis = .horizontal
						$0.alignment = .top
						$0.acLabel {
							$0.layout {
								$0.width == 10
							}
							Roboto.Style.disclaimer($0, color: ACColor.badgeBackground)
							$0.text = "".localized(ACTranslationKey.footnote_symbol)
						}
						$0.acLabel {
							Roboto.Style.disclaimer($0, color: .white)
							$0.text = "".localized(ACTranslationKey.progress_studydisclaimer)
							
						}
						
					}
				}
			}
		}
	}
}
