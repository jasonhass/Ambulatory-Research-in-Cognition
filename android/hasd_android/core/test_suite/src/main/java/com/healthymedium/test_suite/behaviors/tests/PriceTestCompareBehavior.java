//
// PriceTestCompareBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.util.Log;
import android.view.View;


import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.TestVariant;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.PriceTestCompareMatcher;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


import org.hamcrest.Matcher;

import java.util.ArrayList;


public class PriceTestCompareBehavior extends Behavior {

    String pricePrefix;
    String priceSuffix;
    public static ArrayList<String> items = null;
    public static ArrayList<String> prices = null;
    private ArrayList<String> both = null;
    String TAG = getClass().getSimpleName();

    @Override
    public void onOpened(BaseFragment fragment) {
        super.onOpened(fragment);

        Resources resources=  InstrumentationRegistry.getTargetContext().getResources();
        pricePrefix = resources.getString(R.string.money_prefix);
        priceSuffix = resources.getString(R.string.money_suffix);

        items = new ArrayList<>();
        prices = new ArrayList<>();
        both = new ArrayList<>();
        String textViewFood;
        String textViewPrice;


        int index = 1;
        while(index <= 10) {
            do{
                onView(withId(R.id.fragmentPriceTestCompare)).check(matches(new PriceTestCompareMatcher()));
                textViewFood = PriceTestCompareMatcher.food;
                textViewPrice = PriceTestCompareMatcher.price;
            }
            while (both.contains(textViewFood+textViewPrice));


            both.add(textViewFood+textViewPrice);


            String currItem;
            String currPrice;
            if (textViewFood.contains(pricePrefix) && textViewFood.contains(priceSuffix)) {
                currItem = textViewPrice;
                currPrice = textViewFood;

            } else {
                currItem = textViewFood;
                currPrice = textViewPrice;
            }
            UI.sleep(500);
            Capture.takeScreenshot(fragment, TAG, "test_" + index);
            items.add(currItem);
            prices.add(currPrice);
            index++;

        }



        for(int a = 0; a < 10; a++){


        }
        UI.sleep(3000);
        return;
    }





}
