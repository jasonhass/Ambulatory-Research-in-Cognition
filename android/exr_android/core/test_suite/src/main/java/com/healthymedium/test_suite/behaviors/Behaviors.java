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

import com.healthymedium.test_suite.behaviors.informative.SimplePopupBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionCheckBoxesBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionDurationBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionIntegerBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionPolarBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionRadioButtonsBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionRatingBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionSignatureBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionTimeBehavior;
import com.healthymedium.test_suite.behaviors.tests.GridLettersBehavior;
import com.healthymedium.test_suite.behaviors.tests.GridStudyBehavior;
import com.healthymedium.test_suite.behaviors.tests.GridTestBehavior;
import com.healthymedium.test_suite.behaviors.tests.PriceTestCompareBehavior;
import com.healthymedium.test_suite.behaviors.tests.PriceTestMatchBehavior;
import com.healthymedium.test_suite.behaviors.tests.SymbolTestBehavior;
import com.healthymedium.test_suite.paths.FinishedScreen;
import com.healthymedium.test_suite.behaviors.availability.AvailabilityConfirmBehavior;
import com.healthymedium.test_suite.behaviors.availability.AvailabilityTimeBehavior;
import com.healthymedium.test_suite.behaviors.informative.InfoBehavior;
import com.healthymedium.test_suite.behaviors.questions.QuestionLanguageBehavior;
import com.healthymedium.test_suite.behaviors.setup.SetupParticipantBehavior;
import com.healthymedium.test_suite.behaviors.setup.SetupParticipantConfirmBehavior;
import com.healthymedium.test_suite.behaviors.setup.SetupWelcomeBehavior;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.paths.availability.AvailabilityOtherBed;
import com.healthymedium.arc.paths.availability.AvailabilityOtherWake;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayWake;
import com.healthymedium.arc.paths.availability.AvailabilitySundayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySundayWake;
import com.healthymedium.arc.paths.availability.AvailabilityWeekdayConfirm;
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

public class Behaviors {


    public static Map<Class, Behavior> getDefault(){
        Map<Class, Behavior> behaviors = new HashMap<>();

        behaviors.putAll(getInfo());
        behaviors.putAll(getSetup());
        behaviors.putAll(getAvailability());
        behaviors.putAll(getQuestions());
        behaviors.putAll(getTests());

        behaviors.put(SimplePopupScreen.class,new SimplePopupBehavior());
        behaviors.put(SplashScreen.class,new Behavior());
        behaviors.put(FinishedScreen.class,new Behavior());
        return behaviors;
    }


   public static Map<Class, Behavior> getSetup(){
       Map<Class, Behavior> behaviors = new HashMap<>();
       behaviors.put(SetupWelcome.class,new SetupWelcomeBehavior());
       behaviors.put(SetupParticipant.class,new SetupParticipantBehavior());
       behaviors.put(SetupParticipantConfirm.class,new SetupParticipantConfirmBehavior());
       //behaviors.put(SetupAuthCode.class,new SetupSiteBehavior());
       return behaviors;
   }

    public static Map<Class, Behavior> getAvailability(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(AvailabilityOtherWake.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilityOtherBed.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilitySaturdayWake.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilitySaturdayBed.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilitySundayWake.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilitySundayBed.class,new AvailabilityTimeBehavior());
        behaviors.put(AvailabilityWeekdayConfirm.class,new AvailabilityConfirmBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getInfo(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(InfoTemplate.class,new InfoBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getQuestions(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(QuestionLanguagePreference.class,new QuestionLanguageBehavior());
        behaviors.put(QuestionSignature.class,new QuestionSignatureBehavior());
        behaviors.put(QuestionPolar.class,new QuestionPolarBehavior());
        behaviors.put(QuestionRadioButtons.class,new QuestionRadioButtonsBehavior());
        behaviors.put(QuestionTime.class,new QuestionTimeBehavior());
        behaviors.put(QuestionDuration.class,new QuestionDurationBehavior());
        behaviors.put(QuestionInteger.class,new QuestionIntegerBehavior());
        behaviors.put(QuestionRating.class,new QuestionRatingBehavior());
        behaviors.put(QuestionCheckBoxes.class,new QuestionCheckBoxesBehavior());
        return behaviors;
    }

    public static Map<Class, Behavior> getTests(){
        Map<Class, Behavior> behaviors = new HashMap<>();
        behaviors.put(GridStudy.class,new GridStudyBehavior());
        behaviors.put(GridLetters.class,new GridLettersBehavior());
        behaviors.put(GridTest.class,new GridTestBehavior());

        behaviors.put(SymbolTest.class,new SymbolTestBehavior());
        behaviors.put(PriceTestCompareFragment.class,new PriceTestCompareBehavior());
        behaviors.put(PriceTestMatchFragment.class,new PriceTestMatchBehavior());
        return behaviors;
    }



}



