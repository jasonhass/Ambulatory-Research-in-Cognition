//
// HintHighlightTarget.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.hints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.healthymedium.arc.utilities.ViewUtil;

public class HintHighlightTarget extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewTreeObserver viewTreeObserver;
    private Listener listener;

    private long pulseDelay = 0;
    private HintPulse pulse;

    private View view;
    private Path path;


    private int radius = 0;
    private int padding = 0;
    private int height;
    private int width;
    private int x,y;

    public HintHighlightTarget(Context context, View view, Listener listener) {
        super(context);
        this.view = view;
        this.listener = listener;

        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener);
    }

    void process(){

        width = view.getWidth()+(2*padding);
        height = view.getHeight()+(2*padding);

        int locations[] = new int[2];
        view.getLocationOnScreen(locations);

        x = locations[0]-padding;
        y = locations[1]-padding;

        RectF rect = new RectF(x, y, x+width, y+height);

        path = new Path();

        boolean radiusMatchesWidth = isApproximatelyEqual(radius,width/2,20);
        boolean radiusMatchesHeight = isApproximatelyEqual(radius,height/2,20);

        if(radiusMatchesWidth || radiusMatchesHeight){
            path.addCircle(x+width/2,y+height/2, radius, Path.Direction.CW);
        } else {
            path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        }

        if(pulse!=null){
            pulse.setRadius(ViewUtil.pxToDp(radius));
            pulse.process();
        }
    }

    private boolean isApproximatelyEqual(int val0, int val1, int variance){
        return (Math.abs(val0-val1) < variance);
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public void setPulsing(Context context, long delay) {
        pulse = new HintPulse(context,view);
    }

    public void setPadding(int dp) {
        padding = ViewUtil.dpToPx(dp);
    }

    public View getView(){
        return view;
    }

    public HintPulse getPulse(){
        return pulse;
    }

    protected void onDraw(Canvas canvas) {
        if(path==null){
            return;
        }
        canvas.drawPath(path, paint);
        if(pulse!=null) {
            pulse.draw(canvas);
        }
    }

    public void cleanup(){
        if(viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener);
        }
        if(pulse!=null){
            pulse.cleanup();
        }
    }

    public boolean wasTouched(MotionEvent event){
        int touch_x = (int) event.getX();
        int touch_y = (int) event.getY();
        if(touch_x<x || touch_x>(x+width)){
            return false;
        }
        if(touch_y<y || touch_y>(y+height)){
            return false;
        }
        return true;
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout () {
            if(!view.isLaidOut()){
                return;
            }
            if(viewTreeObserver.isAlive()){
                viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener);
            }
            process();
            if(pulse!=null){
                pulse.start(pulseDelay);
            }
            if(listener!=null){
                listener.onLayout(HintHighlightTarget.this);
            }
        }
    };

    public interface Listener {
        void onLayout(HintHighlightTarget target);
    }

}
