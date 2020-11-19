//
// HintPointer.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.hints;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class HintPointer extends LinearLayout {

    int dp4 = ViewUtil.dpToPx(4);
    int dp8 = ViewUtil.dpToPx(8);
    int dp12 = ViewUtil.dpToPx(12);
    int dp16 = ViewUtil.dpToPx(16);
    int dp44 = ViewUtil.dpToPx(44);

    private int x,y;

    private int left;
    private int top;
    private int right;
    private int bottom;

    private int pointerX;

    private int height;
    private int width;

    private boolean showArrow;
    private boolean showAbove;

    private View border;
    private View spacer;
    private TextView textView;
    private TextView textViewButton;

    private ViewGroup parent;
    private View target;

    private Paint fillPaint;
    private Paint strokePaint;
    private int radius;

    private boolean dismissing = false;

    public HintPointer(Activity activity, View view) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();

        this.showArrow = false;
        this.target = view;
        init();
    }

    public HintPointer(Activity activity, View view, boolean showArrow, boolean showAbove) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();

        this.showArrow = showArrow;
        this.showAbove = showAbove;
        this.target = view;
        init();
    }

    private void init() {
        setWillNotDraw(false);

        target.addOnAttachStateChangeListener(attachStateChangeListener);

        radius = ViewUtil.dpToPx(16); // default to 16dp radius

        setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp44,0,dp44,0);
        setLayoutParams(layoutParams);

        textView = new TextView(getContext());
        textView.setPadding(dp16,dp16,dp16,dp16);
        textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(Fonts.roboto);
        textView.setTextColor(getResources().getColor(R.color.black));
        addView(textView);

        border = new View(getContext());
        border.setBackgroundColor(ViewUtil.getColor(R.color.hintDark));
        LayoutParams borderParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewUtil.dpToPx(2));
        borderParams.setMargins(dp4,0,dp4,0);
        border.setLayoutParams(borderParams);
        border.setVisibility(GONE);
        addView(border);

        textViewButton = new TextView(getContext());
        textViewButton.setPadding(dp16,dp8,dp16,dp16);

        textViewButton.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textViewButton.setTypeface(Fonts.robotoBold);
        ViewUtil.underlineTextView(textViewButton);
        textViewButton.setVisibility(GONE);
        textViewButton.setTextColor(getResources().getColor(R.color.black));
        addView(textViewButton);

        spacer = new View(getContext());
        spacer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dp16));
        // don't add yet

        if(showArrow){
            if(showAbove){
                addView(spacer);
            } else {
                addView(spacer,0);
            }
        }

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(ViewUtil.getColor(R.color.hintLight));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(dp4);
        strokePaint.setColor(ViewUtil.getColor(R.color.hintDark));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        height = canvas.getHeight();
        width = canvas.getWidth();

        int locations[] = new int[2];
        target.getLocationOnScreen(locations);

        x = locations[0];
        y = locations[1];

        left = dp44;
        top = 0;
        right = dp44;
        bottom = 0;

        int pointerMin = radius+dp16+dp4;

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
        if(showAbove){
            top = y-height;
        } else {
            top = y+target.getHeight();
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.setMargins(left,top,right,bottom);
        setLayoutParams(params);

        // create a rect that's small enough that the stroke isn't cut off
        Rect rect = new Rect(dp4,dp4,width-dp4,height-dp4);

        Path path = getPath(pointerX,rect,radius,showArrow,showAbove);

        canvas.drawPath(path,fillPaint);
        canvas.drawPath(path,strokePaint);
    }

    private Path getPath(int pointerX, Rect rect, int radius, boolean showArrow, boolean showAbove) {
        int pointerSize = ViewUtil.dpToPx(16);

        int top = rect.top;
        int bottom = rect.bottom;

        if(showArrow && !showAbove){
            top = rect.top+pointerSize;
        }
        if(showArrow && showAbove){
            bottom = rect.bottom-pointerSize;
        }

        Path path = new Path();

        path.moveTo(rect.left + radius, top);

        // top pointer
        if(showArrow && !showAbove) {
            path.lineTo(pointerX - pointerSize, top);
            path.lineTo(pointerX, rect.top);
            path.lineTo(pointerX + pointerSize, top);
        }

        // top line
        path.lineTo(rect.right - radius, top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radius,top,rect.right,top+radius);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, bottom - radius);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radius,bottom - radius,rect.right,bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom pointer
        if(showArrow && showAbove) {
            path.lineTo(pointerX + pointerSize, bottom);
            path.lineTo(pointerX, rect.bottom);
            path.lineTo(pointerX - pointerSize, bottom);
        }

        // bottom line
        path.lineTo(rect.left + radius, bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,bottom - radius,rect.left+radius,bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, top + radius);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,top,rect.left+radius,top+radius);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


    public void addButton(String text, final OnClickListener listener) {
        textViewButton.setText(Html.fromHtml(text));
        textViewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(listener!=null) {
                    listener.onClick(v);
                }
            }
        });
        border.setVisibility(VISIBLE);
        textViewButton.setVisibility(VISIBLE);
    }

    public void setText(String text) {
        textView.setText(Html.fromHtml(text));
    }

    public void hideText() {
        border.setVisibility(GONE);
        textView.setVisibility(GONE);
        textViewButton.setPadding(dp16, ViewUtil.dpToPx(18), dp16, ViewUtil.dpToPx(22));
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public void show() {
        if(getParent()!=null) {
           return; // single use only
        }
        textViewButton.setEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAlpha(0.0f);
                parent.addView(HintPointer.this);
                HintPointer.this.animate()
                        .alpha(1.0f)
                        .setDuration(400);
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
                parent.removeView(HintPointer.this);
                dismissing = false;
            }
        },500);

        HintPointer.this.animate()
                .alpha(0.0f)
                .setDuration(400);
    }

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
