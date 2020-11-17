//
// PriceTestMatchBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;


import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.GetTextViewMatcher;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.RadioButton;

import org.junit.Assert;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.first;
import static org.hamcrest.Matchers.endsWith;

public class PriceTestMatchBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        Assert.assertNotNull("PriceTestCompareBehavior's items list must not be null.",
                PriceTestCompareBehavior.items);

        for(int i=0;i<10;i++) {

            onView(withId(R.id.textviewFood)).check(matches(new GetTextViewMatcher()));
            String textViewFood = GetTextViewMatcher.value;

            onView(withId(R.id.buttonTop)).check(matches(new GetTextViewMatcher()));
            String buttonTopPrice = GetTextViewMatcher.value;

            onView(withId(R.id.buttonBottom)).check(matches(new GetTextViewMatcher()));
            String buttonBottomPrice = GetTextViewMatcher.value;

            Assert.assertTrue("PriceTestCompare must contain the same items as PriceTestMatch.",
                    PriceTestCompareBehavior.items.contains(textViewFood));


            int currIndex = PriceTestCompareBehavior.items.indexOf(textViewFood);


            //Logical Exclusive OR
            boolean condition =
                    (PriceTestCompareBehavior.prices.get(currIndex).equals(buttonBottomPrice)) !=
                    (PriceTestCompareBehavior.prices.get(currIndex).equals(buttonTopPrice)) ;

            Assert.assertTrue("PriceTestCompare item's price can only appear once in PriceTestMatch.",
                    condition);


            PriceTestCompareBehavior.prices.remove(currIndex);
            PriceTestCompareBehavior.items.remove(currIndex);
            UI.sleep(300);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "test_"+i);
            onView(first(withClassName(endsWith(RadioButton.class.getName())))).perform(click());
            UI.sleep(300);
        }

        PriceTestCompareBehavior.items = null;
        PriceTestCompareBehavior.prices = null;
    }

}
