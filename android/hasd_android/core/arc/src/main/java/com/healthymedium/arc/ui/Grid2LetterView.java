//
// Grid2LetterView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.CircleFrameLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2LetterView extends CircleFrameLayout {

    boolean selected = false;

    String text = "E";
    float textHeight;
    float textSize;

    int defaultSize = ViewUtil.dpToPx(48);
    int viewSize;

    Paint paint;

    public Grid2LetterView(Context context) {
        super(context);
        init(null,0);
    }

    public Grid2LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Grid2LetterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setFillColor(R.color.white);

        paint = new Paint();
        paint.setColor(ViewUtil.getColor(getContext(), R.color.primaryButtonDark));
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Fonts.georgia);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
    }

    public void setF(){
        text = "F";
        invalidate();
    }

    public boolean isF(){
        return text.equals("F");
    }

    public boolean isSelected(){
        return selected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            viewSize = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            viewSize = Math.max(defaultSize, widthSize);
        } else {
            viewSize = defaultSize;
        }

        setMeasuredDimension(viewSize,viewSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        textHeight = fm.bottom - fm.top + fm.leading;

        textSize = 0.75f*viewSize;
        paint.setTextSize(textSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            selected = !selected;
            setFillColor(R.color.primaryButtonDark);
            paint.setColor(ViewUtil.getColor(getContext(), R.color.white));
            invalidate();
        }
        if(action == MotionEvent.ACTION_UP) {
            int color = selected ? R.color.accent : R.color.white;
            setFillColor(color);
            paint.setColor(ViewUtil.getColor(getContext(), R.color.primaryButtonDark));
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = canvas.getClipBounds();
        canvas.drawText(text,rect.centerX(),rect.centerY()*1.5f,paint);
    }

}
