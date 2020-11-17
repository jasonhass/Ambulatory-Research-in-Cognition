//
// Grid2ChoiceView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2ChoiceView extends LinearLayout {

    int defaultWidth = ViewUtil.mmToPx(10);
    int defaultHeight = ViewUtil.mmToPx(16);
    int drawableImageId;

    RoundedLinearLayout layout;
    ImageView imageView;

    Drawable image;

    public Grid2ChoiceView(Context context) {
        super(context);
        init(context);
    }

    public Grid2ChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Grid2ChoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_grid2_choice_option,this);
        layout = view.findViewById(R.id.linearLayout);
        imageView = view.findViewById(R.id.imageView);
        setElevation(ViewUtil.dpToPx(8));
    }

    public void setImage(@DrawableRes int id){
        image = ViewUtil.getDrawable(id);
        imageView.setImageDrawable(image);
        drawableImageId = id;
    }

    public int getDrawableImageId(){
        return drawableImageId;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width,height);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++) {
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
    }

}
