//
// PointerDialog.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class PointerDialog extends LinearLayout {

    private int dp4 = ViewUtil.dpToPx(4);
    private int dp16 = ViewUtil.dpToPx(16);
    private int dp44 = ViewUtil.dpToPx(44);

    PointerDrawable background;
    private ViewGroup parent;
    private View target;
    private View view;

    private boolean dismissing = false;
    private long animationMillis = 0;

    public PointerDialog(Activity activity, View target, View view, int pointerConfig) {
        super(activity);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        parent.setClipChildren(false);

        this.target = target;
        this.view = view;

        background = new PointerDrawable();
        background.setPointerConfig(pointerConfig);
        setBackground(background);
        init();
    }

    public PointerDialog(Activity activity, View target, View view) {
        super(activity);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        parent.setClipChildren(false);

        this.target = target;
        this.view = view;

        background = new PointerDrawable();
        setBackground(background);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        target.addOnAttachStateChangeListener(attachStateChangeListener);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER);

        int color = ViewUtil.getColor(R.color.white);
        if(view!=null) {
            addView(view);
            Drawable drawable = view.getBackground();
            if (drawable instanceof ColorDrawable) {
                color = ((ColorDrawable) drawable).getColor();
            }
        }
        background.setFillColor(color);
    }

    public void setView(View view) {
        if(view==null) {
            return;
        }
        removeAllViews();
        addView(view);

        int color = ViewUtil.getColor(R.color.white);
        Drawable drawable = view.getBackground();
        if (drawable instanceof ColorDrawable) {
            color = ((ColorDrawable) drawable).getColor();
        }
        background.setFillColor(color);
    }

    public void setRadius(int dp) {
        background.setRadius(ViewUtil.dpToPx(dp));
    }

    public void setStrokeColor(@ColorRes int color) {
        background.setStrokeColor(ViewUtil.getColor(getContext(),color));
    }

    public void setStrokeWidth(int dp) {
        background.setStrokeWidth(ViewUtil.dpToPx(dp));
    }

    public void setAnimationDuration(long millis) {
        animationMillis = millis;
    }

    public void show() {
        if(getParent()!=null) {
           return; // single use only
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAlpha(0.0f);
                parent.addView(PointerDialog.this);
                PointerDialog.this.animate()
                        .alpha(1.0f)
                        .setDuration(animationMillis);
            }
        },100);
    }

    public void dismiss() {
        if (dismissing) {
            return;
        }
        dismissing = true;

        if(target!=null) {
            target.removeOnAttachStateChangeListener(attachStateChangeListener);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parent.removeView(PointerDialog.this);
                dismissing = false;
            }
        },animationMillis+100);

        PointerDialog.this.animate()
                .alpha(0.0f)
                .setDuration(animationMillis);
    }

    public void refresh(){
        background.invalidate();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(background.getOutlineProvider());
    }

    @Override
    public void draw(Canvas canvas) {
        drawingVariables.refresh(canvas.getHeight(), canvas.getWidth());
        background.draw(canvas);
        super.draw(canvas);
    }

    private class DrawingVariables {
        int pointerMin;
        int pointerX;

        int left;
        int top;
        int right;
        int bottom;

        int locations[] = new int[2];
        int x,y;

        void refresh(int height, int width) {

            target.getLocationOnScreen(locations);
            x = locations[0];
            y = locations[1];

            left = dp44;
            top = 0;
            right = dp44;
            bottom = 0;

            pointerMin = background.getRadius()+dp16+dp4;
            pointerX = x+(target.getWidth()/2);

            if(pointerX < (dp44+pointerMin)){
                left = 0;
                right = 2*dp44;
                if(pointerX < pointerMin){
                    pointerX = pointerMin;
                }
            } else if(pointerX > (width+dp44-pointerMin)){
                left = 2*dp44;
                right = 0;
                pointerX -= 2*dp44;
                if(pointerX > (width-pointerMin)){
                    pointerX = (width-pointerMin);
                }
            } else {
                pointerX -= dp44;
            }

            // adjust top margin for canvas size
            if(background.getPointerConfig()== PointerDrawable.POINTER_ABOVE){
                top = y-height;
            } else {
                top = y+target.getHeight();
            }

            background.setPointerX(pointerX);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            params.setMargins(left,top,right,bottom);
            setLayoutParams(params);

            int stroke = (int)background.getStrokeWidth();
            switch (background.getPointerConfig()) {
                case PointerDrawable.POINTER_ABOVE:
                    setPadding(stroke,stroke,stroke,stroke+background.getPointerSize());
                    break;
                case PointerDrawable.POINTER_BELOW:
                    setPadding(stroke,stroke+background.getPointerSize(),stroke,stroke);
                    break;

                default:

            }
        }
    }

    DrawingVariables drawingVariables = new DrawingVariables();

    OnAttachStateChangeListener attachStateChangeListener = new OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if(!dismissing) {
                dismiss();
            }
        }
    };

}
