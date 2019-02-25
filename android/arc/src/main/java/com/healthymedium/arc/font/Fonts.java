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
    public static Typeface georgiaBold;
    public static Typeface georgiaBoldItalic;
    public static Typeface georgiaItalic;

    public static Typeface roboto;
    public static Typeface robotoBold;
    public static Typeface robotoBoldItalic;
    public static Typeface robotoItalic;

    public static Typeface robotoLight;
    public static Typeface robotoLightItalic;

    public static Typeface robotoMedium;
    public static Typeface robotoMediumItalic;

    public static Typeface robotoThin;
    public static Typeface robotoThinItalic;

    public static Typeface tahoma;
    public static Typeface tahomaBold;

    public static void load(){
        if(loaded){
            return;
        }
        FontFactory fontFactory = FontFactory.getInstance();
        if(fontFactory != null) {
            georgia = fontFactory.getFont("fonts/Georgia.ttf");
            georgiaBold = fontFactory.getFont("fonts/Georgia-Bold.ttf");
            georgiaBoldItalic = fontFactory.getFont("fonts/Georgia-BoldItalic.ttf");
            georgiaItalic = fontFactory.getFont("fonts/Georgia-Italic.ttf");

            roboto = fontFactory.getFont("fonts/Roboto-Regular.ttf");
            robotoBold = fontFactory.getFont("fonts/Roboto-Bold.ttf");
            robotoBoldItalic = fontFactory.getFont("fonts/Roboto-BoldItalic.ttf");
            robotoItalic = fontFactory.getFont("fonts/Roboto-Italic.ttf");

            robotoLight = fontFactory.getFont("fonts/Roboto-Light.ttf");
            robotoLightItalic = fontFactory.getFont("fonts/Roboto-LightItalic.ttf");

            robotoMedium = fontFactory.getFont("fonts/Roboto-Medium.ttf");
            robotoMediumItalic = fontFactory.getFont("fonts/Roboto-MediumItalic.ttf");

            robotoThin = fontFactory.getFont("fonts/Roboto-Thin.ttf");
            robotoThinItalic = fontFactory.getFont("fonts/Roboto-ThinItalic.ttf");

            tahoma = fontFactory.getFont("fonts/Tahoma.ttf");
            tahomaBold = fontFactory.getFont("fonts/Tahoma-Bold.ttf");
            loaded = true;
        }
    }

    public static boolean areLoaded() {
        return loaded;
    }
}
