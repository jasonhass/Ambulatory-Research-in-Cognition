//
// ViewUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;

public class ViewUtil {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int getColor(@ColorRes int id){
        return ContextCompat.getColor(Application.getInstance(),id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(Application.getInstance(),id);
    }

    public static String getString(@StringRes int id){
        return Application.getInstance().getString(id);
    }

    public static void underlineTextView(TextView textView){
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

}
