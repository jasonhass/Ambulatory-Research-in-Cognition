//
// SimpleGradient.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;

import android.graphics.LinearGradient;
import android.graphics.Shader;

public class SimpleGradient {

    public static final int LINEAR_HORIZONTAL = 0;
    public static final int LINEAR_VERTICAL = 1;

    int id;
    int color0;
    int color1;
    Shader.TileMode tileMode;

    public SimpleGradient(int enumeratedValue){
        id = enumeratedValue;
    }

    public int getId() {
        return id;
    }

    Shader getShader(int width, int height){
        switch (id){
            case LINEAR_VERTICAL:
                return new LinearGradient(0, 0, 0, height, color0, color1, tileMode);
            case LINEAR_HORIZONTAL:
                return new LinearGradient(0,0,width,0,color0,color1,tileMode);
        }
        return null;
    }

    void setColor0(int color){
        this.color0 = color;
    }

    void setColor1(int color){
        this.color1 = color;
    }

    void setTileMode(Shader.TileMode tileMode){
        this.tileMode = tileMode;
    }

    public static SimpleGradient getGradient(int gradientId, int colorFirst, int colorSecond) {
        switch (gradientId){
            case SimpleGradient.LINEAR_HORIZONTAL:
                return getHorizontalGradient(colorFirst,colorSecond);
            case SimpleGradient.LINEAR_VERTICAL:
                return getVerticalGradient(colorFirst,colorSecond);
            default:
                return null;
        }
    }

    public static SimpleGradient getHorizontalGradient(int colorLeft, int colorRight) {
        SimpleGradient gradient = new SimpleGradient(SimpleGradient.LINEAR_HORIZONTAL);
        gradient.setColor0(colorLeft);
        gradient.setColor1(colorRight);
        gradient.setTileMode(Shader.TileMode.CLAMP);
        return gradient;
    }

    public static SimpleGradient getVerticalGradient(int colorTop, int colorBottom) {
        SimpleGradient gradient = new SimpleGradient(SimpleGradient.LINEAR_VERTICAL);
        gradient.setColor0(colorTop);
        gradient.setColor1(colorBottom);
        gradient.setTileMode(Shader.TileMode.CLAMP);
        return gradient;
    }

}
