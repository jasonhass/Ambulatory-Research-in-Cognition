//
// FontFactory.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.font;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class FontFactory {

    static FontFactory instance;
    Context context;

    FontFactory(Context context){
        instance = this;
        instance.context = context;
    }

    public static synchronized void initialize(Context context) {
        instance = new FontFactory(context);
    }

    public static FontFactory getInstance(){
        return instance;
    }


    public void setDefaultFont(Typeface typeface) {
        replaceFont("DEFAULT", typeface);
    }

    public void setDefaultBoldFont(Typeface typeface) {
        replaceFont("DEFAULT_BOLD", typeface);
    }

    public Typeface getDefaultFont() {
        return Typeface.DEFAULT;
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Typeface getFont(String path){
        return Typeface.createFromAsset(context.getResources().getAssets(), path);
    }
}
