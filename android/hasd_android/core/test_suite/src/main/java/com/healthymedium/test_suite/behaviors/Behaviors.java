//
// Behaviors.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors;

import com.healthymedium.arc.core.TwoBtnPopupScreen;
import com.healthymedium.arc.paths.availability.AvailabilityBed;
import com.healthymedium.arc.paths.availability.AvailabilityConfirm;
import com.healthymedium.arc.paths.availability.AvailabilityWake;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationOverview;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationPrompt;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationReminder;
import com.healthymedium.arc.paths.home.EarningsScreen;
import com.healthymedium.arc.paths.home.HomeScreen;
import com.healthymedium.arc.paths.home.ProgressScreen;
import com.healthymedium.arc.paths.home.ResourcesScreen;
import com.healthymedium.arc.paths.informative.AboutScreen;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.DayProgressScreen;
import com.healthymedium.arc.paths.informative.EarningsDetailsScreen;
import com.healthymedium.arc.paths.informative.EarningsPostTestLoadingScreen;
import com.healthymedium.arc.paths.informative.EarningsPostTestScreen;
import com.healthymedium.arc.paths.informative.EarningsPostTestUnavailableScreen;
import com.healthymedium.arc.paths.informative.FAQEarningsScreen;
import com.healthymedium.arc.paths.informative.FAQScreen;
import com.healthymedium.arc.paths.informative.FAQTechnologyScreen;
import com.healthymedium.arc.paths.informative.FAQTestingScreen;
import com.healthymedium.arc.paths.informative.FinishedCycleScreen;
import com.healthymedium.arc.paths.informative.FinishedStudyScreen;
import com.healthymedium.arc.paths.informative.FinishedStudyTotalsScreen;
import com.healthymedium.arc.paths.notification.NotificationOverview;
import com.healthymedium.arc.paths.questions.QuestionNonRemoteStudyCommitment;
import com.healthymedium.arc.paths.questions.QuestionPolarAlt;
import com.healthymedium.arc.paths.questions.QuestionRemoteStudyCommitment;
import com.healthymedium.arc.paths.setup.SetupAuthCode;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.paths.templates.TestInfoTemplate;
import com.healthymedium.arc.paths.tests.QuestionInterrupted;
import com.healthymedium.arc.paths.tests.TestBegin;
import com.healthymedium.arc.paths.tests.TestIntro;
import com.healthymedium.arc.paths.tests.TestProgress;
import com.healthymedium.arc.paths.tutorials.GridTutorial;
import com.healthymedium.arc.paths.tutorials.PricesTutorial;
import com.healthymedium.arc.paths.tutorials.SymbolTutorial;
import com.healthymedium.test_suite.behaviors.completed.TestsCompletedEarningsPostBehavior;
import com.healthymedium.test_suite.behaviors.completed.TestsCompletedHomeBehavior;
import com.healthymedium.test_suite.behaviors.nav.EarningsHomeBehavior;
import com.healthymedium.test_suite.behaviors.nav.EarningsScreenBehavior;
import com.healthymedium.test_suite.behaviors.nav.FaqHomeBehavior;
import com.healthymedium.test_suite.behaviors.nav.FaqSubBehavior;
import com.healthymedium.test_suite.behaviors.generic.ClickTheStringBehavior;
import com.healthymedium.test_suite.behaviors.generic.ClickTheThingBehavior;
import com.healthymedium.test_suite.behaviors.generic.IdleBehavior;
import com.healthymedium.test_suite.behaviors.nav.PrivacyPolicyBehavior;
import com.healthymedium.test_suite.behaviors.nav.ProgressHomeBehavior;
import com.healthymedium.test_suite.behaviors.nav.ProgressScreenBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionCheckBoxesBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionIntegerBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionPolarBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionRadioButtonsBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionRatingBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionSignatureBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionTimeBehavior;
import com.healthymedium.test_suite.behaviors.setup.BatteryOptimizationReminderBehavior;
import com.healthymedium.test_suite.behaviors.setup.SetupKeyCodeBehavior;
import com.healthymedium.test_suite.behaviors.tests.GridTestBehavior;
import com.healthymedium.test_suite.behaviors.tests.PriceTestCompareBehavior;
import com.healthymedium.test_suite.behaviors.tests.PriceTestMatchBehavior;
import com.healthymedium.test_suite.behaviors.tests.SymbolTestBehavior;
import com.healthymedium.test_suite.behaviors.tests.TestBeginBehavior;
import com.healthymedium.test_suite.behaviors.tutorials.GridTutorialBehavior;
import com.healthymedium.test_suite.behaviors.tutorials.PricesTutorialBehavior;
import com.healthymedium.test_suite.behaviors.tutorials.SymbolTutorialBehavior;
import com.healthymedium.test_suite.paths.FinishedScreen;
import com.healthymedium.test_suite.behaviors.availability.AvailabilityTimeBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionLanguageBehavior;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.paths.questions.QuestionCheckBoxes;
import com.healthymedium.arc.paths.questions.QuestionDuration;
import com.healthymedium.arc.paths.questions.QuestionInteger;
import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.paths.questions.QuestionRadioButtons;
import com.healthymedium.arc.paths.questions.QuestionRating;
import com.healthymedium.arc.paths.questions.QuestionSignature;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.paths.setup.SetupParticipant;
import com.healthymedium.arc.paths.setup.SetupParticipantConfirm;
import com.healthymedium.arc.paths.setup.SetupWelcome;
import com.healthymedium.arc.paths.templates.InfoTemplate;
import com.healthymedium.arc.paths.tests.GridLetters;
import com.healthymedium.arc.paths.tests.GridStudy;
import com.healthymedium.arc.paths.tests.GridTest;
import com.healthymedium.arc.paths.tests.PriceTestCompareFragment;
import com.healthymedium.arc.paths.tests.PriceTestMatchFragment;
import com.healthymedium.arc.paths.tests.SymbolTest;

import java.util.HashMap;
import java.util.Map;

import com.healthymedium.arc.library.R;

import org.joda.time.DateTime;

public class Behaviors {


    public static Map<Class, Behavior> getDefault(){
        Map<Class, Behavior> behaviors = new HashMap<>();

        behaviors.putAll(getInfo());
        behaviors.putAll(getParticipantSetup());
        behaviors.putAll(getAvailability());
        behaviors.putAll(getQuestions());
        behaviors.putAll(getTests());


        behaviors.put(SimplePopupScreen.class,new ClickTheThingBehavior(R.id.buttonSimpleDialog, 5000, "opened"));
        behaviors.put(HomeScreen.class,new IdleBehavior(500));
        behaviors.put(FinishedScreen.class,new IdleBehavior(500));
        return behaviors;
    }


   public static Map<Class, Behavior> getParticipantSetup(){
       Map<Class, Behavior> behaviors = new HashMap<>();
       behaviors.put(SetupWelcome.class,new ClickTheThingBehavior(R.id.button, 2000, "opened"));
       behaviors.put(SetupParticipant.class,new SetupKeyCodeBehavior("id"));
       behaviors.put(SetupParticipantConfirm.class,new SetupKeyCodeBehavior("confirm"));
       behaviors.put(SetupAuthCode.class,new SetupKeyCodeBehavior("auth"));

       behaviors.put(QuestionRemoteStudyCommitment.class, new QuestionRadioButtonsBehavior());
       behaviors.put(QuestionNonRemoteStudyCommitment.class, new QuestionCheckBoxesBehavior());

       behaviors.put(NotificationOverview.class,new ClickTheThingBehavior(R.id.button,500, "opened"));
       behaviors.put(BatteryOptimizationOverview.class,new ClickTheThingBehavior(R.id.button, 500, "opened"));
       behaviors.put(BatteryOptimizationPrompt.class,new BatteryOptimizationReminderBehavior());

       return behaviors;
   }

    public static Map<Class, Behavior> getAvailability(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(AvailabilityWake.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilityBed.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilityConfirm.class,new ClickTheThingBehavior(R.id.buttonNext, 500, "opened"));
        return behaviors;
    }

    public static Map<Class, Behavior> getInfo(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(InfoTemplate.class,new ClickTheThingBehavior(R.id.button,500, "opened"));
        behaviors.put(StateInfoTemplate.class,new ClickTheThingBehavior(R.id.button, 500,"opened"));
        behaviors.put(DayProgressScreen.class,new  ClickTheThingBehavior(R.id.button, 2000, "opened"));
        behaviors.put(EarningsPostTestLoadingScreen.class, new IdleBehavior(500, "opened"));
        behaviors.put(EarningsPostTestScreen.class, new ClickTheThingBehavior(R.id.button, 2000, "opened"));
        return behaviors;
    }

    public static Map<Class, Behavior> getQuestions(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(QuestionLanguagePreference.class,new QuestionLanguageBehavior());
        behaviors.put(QuestionSignature.class,new QuestionSignatureBehavior());
        behaviors.put(QuestionPolar.class,new QuestionPolarBehavior());
        behaviors.put(QuestionPolarAlt.class, new QuestionPolarBehavior());
        behaviors.put(QuestionRadioButtons.class,new QuestionRadioButtonsBehavior());
        behaviors.put(QuestionTime.class,new QuestionTimeBehavior());
        behaviors.put(QuestionDuration.class,new ClickTheThingBehavior(R.id.buttonNext,500, "opened"));
        behaviors.put(QuestionInteger.class,new QuestionIntegerBehavior());
        behaviors.put(QuestionRating.class,new QuestionRatingBehavior());
        behaviors.put(QuestionCheckBoxes.class,new QuestionCheckBoxesBehavior());
        behaviors.put(QuestionInterrupted.class,new QuestionPolarBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getTests(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(TestIntro.class,new ClickTheThingBehavior(R.id.nextButton, 2000, "opened"));
        behaviors.put(TestInfoTemplate.class, new ClickTheThingBehavior(R.id.button, 2000, "opened"));
        behaviors.put(TestBegin.class, new TestBeginBehavior());
        behaviors.put(TestProgress.class, new IdleBehavior(
                new long[]{2500, 2500},
                new String[]{"opened", null}));

        behaviors.put(GridStudy.class, new IdleBehavior(
                new long[]{750, 2250,2000},
                new String[]{"dialog_before", "opened", null}));
        behaviors.put(GridLetters.class,new IdleBehavior(
                new long[]{5000,3000,6000},
                new String[]{"opened", "dialog_after", null}));
        behaviors.put(GridTest.class,new GridTestBehavior());

        behaviors.put(SymbolTest.class,new SymbolTestBehavior());


        behaviors.put(PriceTestCompareFragment.class,new PriceTestCompareBehavior());
        behaviors.put(PriceTestMatchFragment.class,new PriceTestMatchBehavior());

        behaviors.put(PricesTutorial.class,new PricesTutorialBehavior());
        behaviors.put(GridTutorial.class,new GridTutorialBehavior());
        behaviors.put(SymbolTutorial.class,new SymbolTutorialBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getAboutApp() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new ClickTheStringBehavior(R.string.resources_nav_resources, 5000, "opened"));
        behaviors.put(ResourcesScreen.class, new ClickTheThingBehavior(R.id.textViewAbout, 500, "opened"));
        behaviors.put(AboutScreen.class, new IdleBehavior(500, "opened"));
        return behaviors;
    }

    public static Map<Class, Behavior> getBatteryOptimizationReminder() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new IdleBehavior(500));
        behaviors.put(BatteryOptimizationReminder.class, new BatteryOptimizationReminderBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getContact() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new ClickTheStringBehavior(R.string.resources_nav_resources, 5000, "opened"));
        behaviors.put(ResourcesScreen.class, new ClickTheThingBehavior(R.id.textViewContact, 500, "opened"));
        behaviors.put(ContactScreen.class, new IdleBehavior(2000, "opened"));
        return behaviors;
    }

    public static Map<Class, Behavior> getEarnings() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new EarningsHomeBehavior());
        behaviors.put(EarningsScreen.class, new EarningsScreenBehavior());
        behaviors.put(EarningsDetailsScreen.class, new ClickTheStringBehavior(R.string.resources_nav_home,500, "opened"));
        return behaviors;
    }

    public static Map<Class, Behavior> getFaq() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new ClickTheStringBehavior(R.string.resources_nav_resources, 5000));
        behaviors.put(ResourcesScreen.class, new ClickTheThingBehavior(R.id.textViewFAQ, 500));
        behaviors.put(FAQScreen.class, new FaqHomeBehavior());
        behaviors.put(FAQTestingScreen.class, new FaqSubBehavior(10));
        behaviors.put(FAQEarningsScreen.class, new FaqSubBehavior(5));
        behaviors.put(FAQTechnologyScreen.class, new FaqSubBehavior(7));
        return behaviors;
    }

    public static Map<Class, Behavior> getPrivacyPolicy() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new ClickTheStringBehavior(R.string.resources_nav_resources, 5000));
        behaviors.put(ResourcesScreen.class, new PrivacyPolicyBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getProgress(DateTime date) {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new ProgressHomeBehavior(date));
        behaviors.put(ProgressScreen.class, new ProgressScreenBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getTestsCompleted() {
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(HomeScreen.class, new TestsCompletedHomeBehavior());

        //finishedStudy, when testCycle == 10
        behaviors.put(FinishedStudyScreen.class, new ClickTheThingBehavior(R.id.button,3000, "opened"));
        behaviors.put(FinishedStudyTotalsScreen.class, new ClickTheThingBehavior(R.id.button, 3000, "opened"));

        //currentTestDay == 0 and currentTestSession = 0
        behaviors.put(DayProgressScreen.class, new ClickTheThingBehavior(R.id.button, 500, "opened"));
        behaviors.put(EarningsPostTestLoadingScreen.class, new IdleBehavior(
                new long[]{1000, 1000},
                new String[]{"opened", null}));
        EarningsPostTestLoadingScreen.DELAY_MSEC = 2000;
        behaviors.put(EarningsPostTestScreen.class, new TestsCompletedEarningsPostBehavior());
        behaviors.put(EarningsPostTestUnavailableScreen.class, new ClickTheThingBehavior(R.id.buttonNext, 1000, "opened"));

        behaviors.put(FinishedCycleScreen.class, new ClickTheThingBehavior(R.id.button,500, "opened"));
        behaviors.put(TwoBtnPopupScreen.class, new IdleBehavior(500, "opened"));
        return behaviors;
    }



}



