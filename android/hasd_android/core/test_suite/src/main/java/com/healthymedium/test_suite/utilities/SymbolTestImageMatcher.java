//
// SymbolTestImageMatcher.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.utilities;

import com.healthymedium.arc.library.R;


import android.view.View;
import android.widget.ImageView;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class SymbolTestImageMatcher extends TypeSafeMatcher<View> {

    public static int top;
    public static int bottom;


    public SymbolTestImageMatcher() {
        super(View.class);
    }

    @Override public void describeTo(Description description) {
        description.appendText("Extract Tag from image for public access");
    }

    @Override
    protected boolean matchesSafely(View view) {
        if(view instanceof RoundedLinearLayout) {
            RoundedLinearLayout group = (RoundedLinearLayout) view;
            ImageView topImage = group.findViewById(R.id.symbolTop);
            ImageView bottomImage = group.findViewById(R.id.symbolBottom);

            top = (Integer) topImage.getTag();
            bottom = (Integer) bottomImage.getTag();
            return true;
        }

        return false;
    }
}
