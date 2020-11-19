//
// EarningsBonusView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class EarningsBonusView extends RoundedLinearLayout {

    TextView textViewLeft;
    TextView textViewRight;
    TextView textViewCenter;

    boolean unearned;

    int defaultWidth = ViewUtil.dpToPx(280);
    int defaultHeight = ViewUtil.dpToPx(48);

    public EarningsBonusView(Context context) {
        super(context);
        init(context, null);
    }

    public EarningsBonusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EarningsBonusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        textViewLeft = new TextView(context);
        textViewRight = new TextView(context);
        textViewCenter  = new TextView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        textViewLeft.setLayoutParams(params);
        textViewRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewCenter.setLayoutParams(params);

        textViewLeft.setTypeface(Fonts.roboto);
        textViewLeft.setTextSize(16);
        textViewRight.setTypeface(Fonts.robotoBold);
        textViewRight.setTextSize(16);
        textViewCenter.setTypeface(Fonts.robotoBold);
        textViewCenter.setTextSize(16);

        textViewLeft.setTextColor(ViewUtil.getColor(context, R.color.secondaryDark));
        textViewRight.setTextColor(ViewUtil.getColor(context, R.color.secondaryDark));
        textViewCenter.setTextColor(ViewUtil.getColor(context, R.color.secondaryDark));

        textViewCenter.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textViewLeft.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        textViewRight.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);

        addView(textViewLeft);
        addView(textViewCenter);
        addView(textViewRight);

        // attributes
        TypedArray options = context.obtainStyledAttributes(attrs, R.styleable.EarningsBonusView, 0, 0);

        unearned = options.getBoolean(R.styleable.EarningsBonusView_unearned, false);

        if(unearned) {
            applyUnearnedStyle();
        } else {
            applyDefaultStyle();
        }

        String textCenter = options.getString(R.styleable.EarningsBonusView_textCenter);
        String textLeft = options.getString(R.styleable.EarningsBonusView_textLeft);
        String textRight = options.getString(R.styleable.EarningsBonusView_textRight);
        if(textCenter != null) {
            applyCenterTextFontStyle();
            textViewCenter.setText(textCenter);
        } else {
            applyDefaultFontStyle();
            textViewLeft.setText(textLeft);
            textViewRight.setText(textRight);
        }

        options.recycle();
    }

    private void applyDefaultStyle() {
        setHorizontalGradient(R.color.earnedGradientLight, R.color.hintDark);
        setStrokeColor(R.color.hintDark);
        setStrokeWidth(2);
        setRadius(3);
        setPadding(ViewUtil.dpToPx(11),ViewUtil.dpToPx(11),ViewUtil.dpToPx(11),ViewUtil.dpToPx(11));
    }

    private void applyUnearnedStyle() {
        setStrokeDash(6, 6);
        setStrokeColor(R.color.unearnedGray);
        setStrokeWidth(2);
        setRadius(6);
        textViewCenter.setTextColor(ViewUtil.getColor(R.color.unearnedGray));
        setPadding(ViewUtil.dpToPx(13),ViewUtil.dpToPx(13),ViewUtil.dpToPx(13),ViewUtil.dpToPx(13));
    }

    private void applyDefaultFontStyle() {
        textViewCenter.setVisibility(GONE);
    }

    private void applyCenterTextFontStyle() {
        textViewLeft.setVisibility(GONE);
        textViewRight.setVisibility(GONE);
    }

    public TextView getTextViewLeft() {
        return textViewLeft;
    }

    public void setTextViewLeft(TextView textViewLeft) {
        this.textViewLeft = textViewLeft;
        applyDefaultFontStyle();
    }

    public TextView getTextViewRight() {
        return textViewRight;
    }

    public void setTextViewRight(TextView textViewRight) {
        this.textViewRight = textViewRight;
    }

    public TextView getTextViewCenter() {
        return textViewCenter;
    }

    public void setTextViewCenter(TextView textViewCenter) {
        this.textViewCenter = textViewCenter;
        applyCenterTextFontStyle();
    }

    public void setTextCenter(String text) {
        textViewCenter.setText(text);
        applyCenterTextFontStyle();
    }


    public boolean isUnearned() {
        return unearned;
    }

    public void setUnearned(boolean unearned) {
        this.unearned = unearned;
        if(unearned) {
            applyUnearnedStyle();
        }
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
    }
}
