//
// EarningsTwentyOneSessionsView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.earnings;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class EarningsTwentyOneSessionsView extends View {

    private float progress = 0;
    private String text = "?";

    private Paint basePaint;
    private Paint progressPaint;
    private Paint textPaint;

    private int dp18;
    private int dp6;
    private float textOffset;

    private int width;
    private int centerHeight;

    public EarningsTwentyOneSessionsView(Context context) {
        super(context);
        init(null,0);
    }

    public EarningsTwentyOneSessionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EarningsTwentyOneSessionsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        int baseColor = ViewUtil.getColor(getContext(),R.color.secondaryAccent);
        int progressColor = ViewUtil.getColor(getContext(),R.color.secondary);
        int white = ViewUtil.getColor(getContext(),R.color.white);

        dp18 = ViewUtil.dpToPx(18);
        dp6 = ViewUtil.dpToPx(6);

        // initialize member variables
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setStrokeWidth(ViewUtil.dpToPx(12));
        basePaint.setColor(baseColor);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(ViewUtil.dpToPx(12));
        progressPaint.setColor(progressColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTypeface(Fonts.robotoBold);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp18);
        textPaint.setColor(white);
        textOffset = ((textPaint.descent() + textPaint.ascent()) / 2);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        centerHeight = dp18;
        setMeasuredDimension(width,dp18*2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // base
        basePaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(dp6,centerHeight,width-dp18,centerHeight,basePaint);
        basePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width-dp18,centerHeight,dp18,basePaint);

        // progress
        float x = ((width-(2*dp18))*(progress))+dp18;
        progressPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(dp6,centerHeight,x,centerHeight,progressPaint);
        progressPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x,centerHeight,dp18,progressPaint);

        float y = ((canvas.getHeight() / 2) - textOffset);
        canvas.drawText(text,x,y,textPaint);
    }

    public void setProgress(int value) {
        if(value<0){
            value = 0;
        }
        if (value>100){
            value = 100;
        }
        progress = value/100f;
        int testCount = (int)(progress*21);
        text = String.valueOf(testCount);
        invalidate();
    }

}
