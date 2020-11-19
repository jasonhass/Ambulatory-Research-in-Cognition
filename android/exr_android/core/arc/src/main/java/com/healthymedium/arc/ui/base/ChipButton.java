//
// ChipButton.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import com.healthymedium.arc.utilities.ViewUtil;

public class ChipButton extends LinearLayout {

    protected LayerDrawable background;
    protected ChipDrawable bottomLayer;
    protected ChipDrawable gradientLayer;
    protected FadingDrawable topLayer;

    float elevation;

    int defaultWidth = ViewUtil.dpToPx(216);
    int defaultHeight = ViewUtil.dpToPx(48);

    public ChipButton(Context context) {
        super(context);
        init(null,0);
    }

    public ChipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChipButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        setGravity(Gravity.CENTER);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        bottomLayer = new ChipDrawable();
        gradientLayer = new ChipDrawable();
        topLayer = new FadingDrawable();

        background = new LayerDrawable(new Drawable[]{bottomLayer, gradientLayer, topLayer});
        setBackground(background);

        int white = Color.argb(64,255,255,255);
        int black = Color.argb(64,0,0,0);
        gradientLayer.setFillGradient(SimpleGradient.LINEAR_VERTICAL,white,black);

        topLayer.setStrokeWidth(ViewUtil.dpToPx(16));

        elevation = getElevation();
        setOnTouchListener(touchListener);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(bottomLayer.getOutlineProvider());
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return bottomLayer.getOutlineProvider();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            gradientLayer.setAlpha(255);
            topLayer.setAlpha(255);
            super.setElevation(elevation);
            setAlpha(1.0f);
        } else {
            gradientLayer.setAlpha(0);
            topLayer.setAlpha(0);
            super.setElevation(0);
            setAlpha(0.5f);
        }
    }

    @Override
    public void setElevation(float elevation) {
        super.setElevation(elevation);
        this.elevation = elevation;
    }

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    setElevation(elevation);
                    gradientLayer.setAlpha(255);
                    topLayer.setAlpha(255);
                    invalidate();

                    float x = event.getX();
                    float y = event.getY();
                    int w = v.getWidth();
                    int h = v.getHeight();

                    if(x>0 && x<w && y>0 && y<h){
                        callOnClick();
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    setElevation(0);
                    gradientLayer.setAlpha(0);
                    topLayer.setAlpha(0);
                    invalidate();
                    break;
            }
            return true;
        }
    };

    public class FadingDrawable extends ChipDrawable {

        float numberOfPasses = 20;

        @Override
        public void draw(Canvas canvas) {
            if(alphaBlock){
                return;
            }
            if(path==null){
                return;
            }
            if(drawFill){
                canvas.drawPath(path,fillPaint);
            }
            if(drawStroke) {
                float maxWidth = strokeWidth;

                for (float i = 0; i <= numberOfPasses; i++){
                    int alpha = (int) (i / numberOfPasses * 255f);
                    float width = maxWidth * (1 - i / numberOfPasses);
                    strokePaint.setAlpha(alpha);
                    strokePaint.setStrokeWidth(width);
                    canvas.drawPath(path, strokePaint);
                }
            }
        }
    }

}
