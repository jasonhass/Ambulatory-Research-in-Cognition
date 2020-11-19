//
// Fonts.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.font;

import android.graphics.Typeface;

public class Fonts {

    private static boolean loaded = false;
    public static Typeface georgia;
    public static Typeface georgiaItalic;

    public static Typeface roboto;
    public static Typeface robotoMedium;
    public static Typeface robotoBold;
    public static Typeface robotoBlack;

    public static void load(){
        if(loaded){
            return;
        }
        FontFactory fontFactory = FontFactory.getInstance();
        if(fontFactory != null) {
            georgia = fontFactory.getFont("fonts/Georgia.ttf");
            georgiaItalic = fontFactory.getFont("fonts/Georgia-Italic.ttf");

            roboto = fontFactory.getFont("fonts/Roboto-Regular.ttf");
            robotoMedium = fontFactory.getFont("fonts/Roboto-Medium.ttf");
            robotoBold = fontFactory.getFont("fonts/Roboto-Bold.ttf");
            robotoBlack = fontFactory.getFont("fonts/Roboto-Black.ttf");

            loaded = true;
        }
    }

    public static boolean areLoaded() {
        return loaded;
    }
}
