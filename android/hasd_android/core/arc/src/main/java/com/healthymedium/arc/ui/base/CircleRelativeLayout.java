//
// CircleRelativeLayout.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class CircleRelativeLayout extends RelativeLayout {

    CircleDrawable background;

    public CircleRelativeLayout(Context context) {
        super(context);
        init(null,0);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        background = new CircleDrawable();
        setBackground(background);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleRelativeLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.CircleRelativeLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.CircleRelativeLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.CircleRelativeLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.CircleRelativeLayout_dashSpacing,0);
        int strokeWidth = (int) typedArray.getDimension(R.styleable.CircleRelativeLayout_strokeWidth,0);

        int gradientEnum = (int) typedArray.getInt(R.styleable.CircleRelativeLayout_gradient,-1);
        int gradientColor0 = (int) typedArray.getColor(R.styleable.CircleRelativeLayout_gradientColor0,0);
        int gradientColor1 = (int) typedArray.getColor(R.styleable.CircleRelativeLayout_gradientColor1,0);

        typedArray.recycle();


        if(fillColor!=0){
            background.setFillColor(fillColor);
        }
        if(strokeColor!=0) {
            background.setStrokeColor(strokeColor);
        }
        background.setStrokeWidth(strokeWidth);

        if(dashLength!=0 && dashSpacing!=0){
            background.setStrokeDash(dashLength,dashSpacing);
        }

        if(gradientEnum!=-1 && gradientColor0!=0 && gradientColor1!=0){
            background.setGradient(gradientEnum,gradientColor0,gradientColor1);
        }
    }

    public void setFillColor(@ColorRes int color) {
        background.setFillColor(ViewUtil.getColor(getContext(),color));
    }

    public void setFillColor(String colorString) {
        background.setFillColor(Color.parseColor(colorString));
    }

    public void setStrokeColor(@ColorRes int color) {
        background.setStrokeColor(ViewUtil.getColor(getContext(),color));
    }

    public void setStrokeColor(String colorString) {
        background.setStrokeColor(Color.parseColor(colorString));
    }

    public void setStrokeWidth(int dp) {
        background.setStrokeWidth(ViewUtil.dpToPx(dp));
    }

    public void setStrokeDash(int dpLength, int dpSpacing) {
        int length = ViewUtil.dpToPx(dpLength);
        int spacing = ViewUtil.dpToPx(dpSpacing);
        background.setStrokeDash(length,spacing);
    }

    public void setHorizontalGradient(@ColorRes int colorLeft, @ColorRes int colorRight) {
        int left = ViewUtil.getColor(getContext(),colorLeft);
        int right = ViewUtil.getColor(getContext(),colorRight);
        background.setGradient(SimpleGradient.LINEAR_HORIZONTAL,left,right);
    }

    public void setVerticalGradient(@ColorRes int colorTop, @ColorRes int colorBottom) {
        int top = ViewUtil.getColor(getContext(),colorTop);
        int bottom = ViewUtil.getColor(getContext(),colorBottom);
        background.setGradient(SimpleGradient.LINEAR_VERTICAL,top,bottom);
    }

    public void removeStrokeDash() {
        background.removeStrokeDash();
    }

    public void refresh(){
        background.invalidate();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(background.getOutlineProvider());
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return background.getOutlineProvider();
    }

}
