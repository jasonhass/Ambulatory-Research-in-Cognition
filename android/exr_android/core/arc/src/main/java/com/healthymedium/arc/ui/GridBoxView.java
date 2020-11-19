//
// GridBoxView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class GridBoxView extends LinearLayout {

    int defaultWidth = ViewUtil.mmToPx(10);
    int defaultHeight = ViewUtil.mmToPx(16);

    LinearLayout linearLayout;
    ImageView imageView;

    Drawable image;
    ShapeDrawable dot;

    boolean selected = false;
    boolean selectable = true;
    long timestamp = 0;
    int index = -1;

    int colorNormal = ViewUtil.getColor(getContext(),R.color.gridNormal);
    int colorSelected = ViewUtil.getColor(getContext(),R.color.gridSelected);

    Listener listener;

    public GridBoxView(Context context) {
        super(context);
        init(context);
    }

    public GridBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_grid_view,this);
        linearLayout = view.findViewById(R.id.linearLayout);
        imageView = view.findViewById(R.id.imageView);

        dot = new ShapeDrawable(new OvalShape());
        dot.getPaint().setColor(ViewUtil.getColor(getContext(),R.color.white));

        showSelectedState(false);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setImage(@DrawableRes int id){
        image = ViewUtil.getDrawable(id);
        showSelectedState(selected);
    }

    public void removeImage(){
        image = null;
        showSelectedState(selected);
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setSelected(boolean selected) {
        if(this.selected==selected) {
            return;
        }
        this.selected = selected;
        timestamp = selected ? System.currentTimeMillis() : 0;
        showSelectedState(selected);
    }

    private void showSelectedState(boolean selected) {
        linearLayout.setBackgroundColor(selected ? colorSelected : colorNormal);
        if(image==null) {
            imageView.setImageDrawable(selected ? dot : null);
        } else {
            imageView.setImageDrawable(image);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN && selectable) {
            setSelected(!selected);
            if(listener!=null) {
                return listener.onSelected(this, selected);
            }
        }
        return super.onTouchEvent(event);
    }

//    View.OnTouchListener listener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            int action = event.getAction();
//            switch (action){
//                case MotionEvent.ACTION_DOWN:
//                    boolean wasDark = true;
//                    if (view.getTag(R.id.tag_color).equals(R.color.gridSelected)) {
//                        view.setTag(R.id.tag_color,R.color.gridNormal);
//                        if(selections.contains(view)){
//                            view.setTag(R.id.tag_time,0);
//                            selections.remove(view);
//                        }
//                        selectedCount--;
//                    } else if (selectedCount < 3) {
//                        wasDark = false;
//                        selectedCount++;
//                        view.setTag(R.id.tag_time, System.currentTimeMillis());
//                        view.setTag(R.id.tag_color,R.color.gridSelected);
//                        selections.add(view);
//                    }
//
//                    int color = wasDark ? R.color.gridNormal : R.color.gridSelected;
//                    view.setBackgroundColor(ContextCompat.getColor(getContext(), color));
//                    handler.removeCallbacks(runnable);
//                    if(selectedCount >= 3){
//                        handler.postDelayed(runnable,2000);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            return false;
//        }
//    };

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
        int dotSize = widthSize/3;
        dot.setIntrinsicHeight(dotSize);
        dot.setIntrinsicWidth(dotSize);
        setMeasuredDimension(width,height);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++) {
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public interface Listener {
        boolean onSelected(GridBoxView view, boolean selected);
    }

}
