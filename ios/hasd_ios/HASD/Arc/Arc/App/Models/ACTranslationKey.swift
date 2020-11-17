//
// ACTranslationKey.swift
//


import Foundation
public enum ACTranslationKey : String, TranslationKey {

	case app_name

	case country_key

	case language_key

	case notification_monthbefore

	case notification_weekbefore

	case notification_daybefore

	case notification1_firstday

	case notification1_default

	case notification1_halfway

	case notification1_lastday

	case notifications2_default

	case notification3_default

	case notification3_variation

	case notification4_default

	case notification4_lastday

	case notification_missedtests

	case notification_testproctor_header

	case notification_testproctor_body

	case dateshift_picker

	case dateshift_confirmation

	case language_prefer

	case gen_welcome_key

	case bysigning_key

	case privacy_linked

	case about_linked

	case login_enter_raterid

	case login_enter_arcid

	case login_confirm_arcid

	case login_problems_linked

	case login_enter_2FA

	case login_2FA_body

	case login_problems_2FA

	case login_resend_header

	case login_resend_subheader

	case login_2FA_text

	case login_2FA_morehelp_linked

	case onboarding_header

	case onboarding_body

	case radio_understand

	case radio_commit

	case radio_nocommit

	case onboarding_nocommit_header

	case onboarding_nocommit_body

	case onboarding_nocommit_landing_header

	case onboarding_nocommit_landing_body

	case onboarding_commit_header

	case onboarding_commit_body

	case onboarding_notifications_header1

	case onboarding_notifications_body1

	case onboarding_notifications_popup

	case onboarding_notifications_header2

	case onboarding_notifications_body2_ios

	case onboarding_notifications_body2_android

	case availability_header

	case availability_body

	case availability_start

	case availability_stop

	case availability_minimum_error

	case availability_maximum_error

	case availability_confirm

	case availability_change_linked

	case availability_change_confirm

	case availability_change_week

	case availability_change_week_confirm

	case idverify_header

	case idverify_body

	case idverify_undo

	case home_header1

	case home_header2

	case home_body2

	case home_header3

	case home_body3

	case home_header4

	case home_body4

	case home_header5

	case home_body5

	case home_header6

	case home_body6

	case home_header7

	case home_body7

	case resources_header

	case resources_availability

	case resources_contact

	case resources_about_link

	case resources_faq_link

	case resources_privacy_link

	case resources_nav_home

	case resources_nav_progress

	case resources_nav_earnings

	case resources_nav_resources

	case availability_changetime

	case availability_changedates

	case contact_call1

	case contact_call2

	case contact_email1

	case contact_email2

	case about_header

	case about_body

	case faq_header

	case faq_testing

	case faq_earnings

	case faq_technology

	case faq_subpage_subheader

	case faq_testing_header

	case faq_testing_q1

	case faq_testing_q2

	case faq_testing_q3

	case faq_testing_q4

	case faq_testing_q5

	case faq_testing_q6

	case faq_testing_q7

	case faq_testing_q8

	case faq_testing_q9

	case faq_testing_q10

	case faq_testing_a1

	case faq_testing_a2

	case faq_testing_a3

	case faq_testing_a4

	case faq_testing_a5

	case faq_testing_a6

	case faq_testing_a7

	case faq_testing_a8

	case faq_testing_a9

	case faq_testing_a10

	case faq_tech_header

	case faq_tech_q1

	case faq_tech_q2

	case faq_tech_q3

	case faq_tech_q4

	case faq_tech_q5

	case faq_tech_q6

	case faq_tech_q7

	case faq_tech_a1

	case faq_tech_a2

	case faq_tech_a3

	case faq_tech_a4

	case faq_tech_a5

	case faq_tech_a6

	case faq_tech_a7

	case faq_earnings_header

	case faq_earnings_q1

	case faq_earnings_q2

	case faq_earnings_q3

	case faq_earnings_q4

	case faq_earnings_q5

	case faq_earnings_a1

	case faq_earnings_a2

	case faq_earnings_a3

	case faq_earnings_a4

	case faq_earnings_a5

	case progress_daily_header

	case progress_dailystatus_complete

	case progress_dailystatus_remaining

	case progress_weekly_header

	case progess_weeklystatus

	case progress_startdate

	case progress_startdate_date

	case progress_enddate

	case progress_enddate_date

	case progress_study_header

	case progress_studystatus

	case progress_joindate

	case progress_joindate_date

	case progress_finishdate

	case progress_finishdate_date

	case progress_timebtwtesting

	case progress_timebtwtesting_unit

	case progress_studydisclaimer

	case progress_weekscompleted

	case progress_weeksremaining

	case progress_nextcycle

	case progress_cycledates

	case earnings_body0

	case earnings_body1

	case earnings_weektotal

	case earnings_studytotal

	case earnings_bonus_header

	case earnings_bonus_body

	case earnings_4of4_header

	case earnings_4of4_body

	case earnings_2aday_header

	case earnings_2aday_body

	case earnings_21tests_header

	case earnings_21tests_body

	case earnings_bonus_incomplete

	case earnings_bonus_complete

	case earnings_details_header

	case earnings_sync

	case earnings_sync_justnow

	case earnings_sync_datetime

	case earnings_details_subheader1

	case earnings_details_dates

	case earnings_details_complete_test_session

	case earnings_details_number_sessions

	case earnings_details_4of4

	case earnings_details_2aday

	case earnings_details_21sessions

	case earnings_details_currenttotal

	case earnings_details_subheader2

	case earnings_details_cycletotal

	case chronotype_header

	case chronotype_subheader

	case chronotype_body1

	case chronotype_q1

	case chronotype_q2

	case chronotype_body2

	case chronotype_workdays_sleep

	case chronotype_workdays_wake

	case chronotype_workfree_sleep

	case chronotype_workfree_wake

	case chronotype_disclaim1

	case chronotype_disclaim2

	case wakesurvey_header

	case wakesurvey_subheader

	case wakesurvey_body

	case wake_q1

	case wake_q2

	case wake_q3a

	case wake_q3b

	case wake_q4

	case wake_q5

	case wake_q6

	case wake_poor

	case wake_excellent

	case context_header

	case context_subheader

	case context_body

	case context_q1

	case context_q1_a1

	case context_q1_a2

	case context_q1_a3

	case context_q1_a4

	case context_q1_a5

	case context_q1_a6

	case context_q1_a7

	case context_q2

	case context_q2_a1

	case context_q2_a2

	case context_q2_a3

	case context_q2_a4

	case context_q2_a5

	case context_q2_a6

	case context_q2_a7

	case context_q3

	case context_bad

	case context_good

	case context_q4

	case context_tired

	case context_active

	case context_q5

	case context_q5_a1

	case context_q5_a2

	case context_q5_a3

	case context_q5_a4

	case context_q5_a5

	case context_q5_a6

	case context_q5_a7

	case context_q5_a8

	case context_q5_a9

	case context_q5_a10

	case testing_intro_header

	case testing_intro_body

	case testing_header_1

	case testing_header_2

	case testing_header_3

	case testing_tutorial_link

	case testing_tutorial_complete

	case testing_begin

	case testing_loading

	case testing_done

	case testing_interrupted_header

	case testing_interrupted_body

	case testing_id_header

	case testing_id_body

	case prices_body2

	case prices_header

	case prices_body

	case prices_isthisgood

	case prices_overlay

	case prices_whatwasprice

	case prices_complete

	case symbols_header

	case symbols_body

	case symbols_match

	case symbols_or

	case symbols_complete

	case grids_header

	case grids_body

	case grids_overlay1

	case grids_overlay2

	case grids_subheader_fs

	case grids_overlay3

	case grids_overlay3_pt2

	case grids_subheader_boxes

	case grids_complete

	case progress_schedule_header

	case progress_baseline_notice

	case progress_practice_body1

	case progress_practice_body2

	case progress_schedule_body1

	case progress_schedule_body2

	case progress_schedule_status1

	case progress_schedule_status2

	case progress_earnings_header

	case progress_earnings_body1

	case progress_earnings_body2

	case progress_earnings_status1

	case progress_earnings_status2

	case progress_earnings_status3

	case progress_earnings_status4

	case progress_goal_header

	case progress_cyclecomplete_header

	case progress_cycleorstudycomplete_body

	case overlay_nextcycle

	case progress_studycomplete_header

	case progress_studytotals_header

	case progress_studytotals_body

	case progress_studytotals_earned

	case progress_studytotals_tests

	case progress_studytotals_days

	case progress_studytotals_goals

	case progress_endoftest_syncing

	case progress_endoftest_nosync

	case login_error1

	case login_error2

	case login_error3

	case login_error4
	
	case button_help

	case button_begin

	case button_back

	case button_sendnewcode

	case button_submittime

	case button_next

	case button_call

	case button_email

	case button_done

	case button_change
		
	case button_proceed_without_notifications
	
	case button_submit

	case button_submitdates

	case button_signin

	case button_beginsurvey

	case button_begintest

	case button_okay

	case button_viewfaq

	case button_viewdetails

	case button_chooseanswer

	case button_settings

	case button_close

	case button_confirm

	case button_adjustschedule

	case button_returntohome

	case popup_scroll

	case popup_showmore

	case popup_begin

	case popup_drag

	case popup_gotit

	case popup_next

	case popup_done

	case popup_tutorial_view

	case popup_tutorial_welcome

	case popup_tutorial_quit

	case popup_tutorial_price_intro

	case popup_tutorial_price_memorize

	case prices_tutorial_item1

	case prices_tutorial_price1

	case prices_tutorial_item2

	case prices_tutorial_price2

	case prices_tutorial_price1_match

	case prices_tutorial_price2_match

	case prices_tutorial_item3

	case prices_tutorial_price3

	case prices_tutorial_price3_match

	case popup_tutorial_choose1

	case popup_tutorial_greatchoice1

	case popup_tutorial_greatchoice2

	case popup_tutorial_recall

	case popup_tutorial_choose2

	case popup_tutorial_complete

	case popup_tutorial_tile

	case popup_tutorial_tilestop

	case popup_tutorial_tilesbottom

	case popup_tutorial_middle_instructions

	case popup_tutorial_tiletap

	case popup_tutorial_pricetap

	case popup_tutorial_greatjob

	case popup_tutorial_nice

	case popup_tutorial_grid_recall

	case popup_tutorial_rememberbox

	case popup_tutorial_ready

	case popup_tutorial_part2

	case popup_tutorial_tapf1

	case popup_tutorial_tapf2

	case popup_tutorial_tapf3

	case popup_tutorial_selectbox

	case popup_tutorial_boxhint

	case popup_tutorial_tapbox

	case popup_tutorial_tapbox2

	case popup_tutorial_tapbox3

	case popup_tutorial_needhelp

	case popup_tutorial_remindme

	case popup_tour

	case popup_tab_home

	case popup_tab_progress

	case popup_tab_earnings

	case popup_tab_resources

	case popup_nicejob

	case low_memory_toast

	case low_memory_restart_dialogue

	case day_abbrev_sun

	case day_abbrev_mon

	case day_abbrev_tues

	case day_abbrev_weds

	case day_abbrev_thurs

	case day_abbrev_fri

	case day_abbrev_sat

	case status_ongoing

	case status_inprogress

	case status_done

	case status_done_withdate

	case status_nonedone

	case radio_yes

	case radio_no

	case radio_0

	case radio_1

	case radio_2

	case radio_3

	case radio_4

	case radio_5

	case radio_6

	case radio_7

	case list_selectone

	case list_selectall

	case month_january

	case month_febuary

	case month_march

	case month_april

	case month_may

	case month_june

	case month_july

	case month_august

	case month_september

	case month_october

	case month_november

	case month_december

	case footnote_symbol

	case earnings_symbol

	case time_format

	case format_date

	case money_prefix

	case money_suffix



}
