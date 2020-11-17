//
// StudyProgressView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.healthymedium.arc.ui.base.RoundedDrawable;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;


public class StudyProgressView extends LinearLayout {

    int currentWeek = 4;
    int weekCount = 12;

    int dp4;
    int dp8;
    int dp32;
    int dp42;

    public StudyProgressView(Context context) {
        super(context);
        init(null,0);
    }

    public StudyProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StudyProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        Participant participant = Study.getParticipant();
        weekCount = participant.getState().testCycles.size();

        int currentWeek = weekCount;
        boolean isInCycle = false;

        TestCycle cycle = participant.getCurrentTestCycle();
        if(cycle != null) {
            isInCycle = (cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow());
            currentWeek = cycle.getId()+1;

            if(!isInCycle){
                currentWeek--;
            }
        }

        int dp1 = ViewUtil.dpToPx(1);
        int dp2 = ViewUtil.dpToPx(2);

        dp4 = ViewUtil.dpToPx(4);
        dp8 = ViewUtil.dpToPx(8);
        dp32 = ViewUtil.dpToPx(32);
        dp42 = ViewUtil.dpToPx(42);

        int color = ViewUtil.getColor(getContext(),R.color.secondaryAccent);

        for(int i=0;i<weekCount;i++){
            RoundedDrawable drawable = new RoundedDrawable();
            drawable.setRadius(dp4);
            drawable.setStrokeColor(color);
            drawable.setStrokeWidth(dp1);

            if(i<currentWeek){
                drawable.setFillColor(color);
            }

            View view = new View(context);
            view.setBackground(drawable);

            LayoutParams params;
            if(i==currentWeek-1 && isInCycle){
                params = new LayoutParams(dp8,dp42);
            } else {
                params = new LayoutParams(dp8,dp32);
            }

            if(i==0){
                params.setMargins(0,0,dp2,0);
            } else if(i==weekCount-1){
                params.setMargins(dp2,0,0,0);
            } else {
                params.setMargins(dp2,0,dp2,0);
            }

            view.setLayoutParams(params);
            addView(view);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int blockWidth = ((width-((weekCount-1)*dp8))/weekCount);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = blockWidth;
            view.setLayoutParams(params);
        }

        setMeasuredDimension(width,dp42);

    }

    public void refresh(){
        invalidate();
    }

}
