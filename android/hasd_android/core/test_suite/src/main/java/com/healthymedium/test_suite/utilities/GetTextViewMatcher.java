//
// GetTextViewMatcher.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.utilities;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import com.healthymedium.arc.ui.RadioButton;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;


public class GetTextViewMatcher extends TypeSafeMatcher<View> {

    public static String value;

    public GetTextViewMatcher() {
        super(View.class);
    }

    @Override public void describeTo(Description description) {
        description.appendText("Extract value from View for public access");
    }

    @Override
    protected boolean matchesSafely(View view) {
        if(view instanceof TextView){
            value = ((TextView) view).getText().toString();
            return true;
        }

        if(view instanceof RadioButton){
            value = ((RadioButton) view).getText();
            return true;
        }


        return false;
    }
}
