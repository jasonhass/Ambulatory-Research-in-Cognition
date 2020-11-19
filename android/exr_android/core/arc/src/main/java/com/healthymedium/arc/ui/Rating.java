//
// Rating.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class Rating extends LinearLayoutCompat{

    TextView textLow;
    TextView textHigh;

    SeekBar seekBar;
    SeekBar.OnSeekBarChangeListener listener;

    Drawable thumbNormal;
    Drawable thumbPressed;

    int padding;

    public Rating(Context context) {
        super(context);
        init(context);
    }

    public Rating(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setText(context,attrs);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_rating,this);
        textLow = view.findViewById(R.id.textviewRatingLow);
        textHigh = view.findViewById(R.id.textviewRatingHigh);
        seekBar = view.findViewById(R.id.seekbarRating);
        seekBar.setThumbOffset(0);

        padding = ViewUtil.dpToPx(49);

        thumbPressed =  ViewUtil.getDrawable(R.drawable.rating_thumb_pressed);
        thumbNormal =  ViewUtil.getDrawable(R.drawable.rating_thumb_normal);


        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        seekBar.setThumb(thumbPressed);
                        seekBar.setPadding(padding,0,padding,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        seekBar.setThumb(thumbNormal);
                        break;
                }
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(listener!=null){
                    listener.onProgressChanged(seekBar,i,b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(listener!=null){
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setThumb(thumbNormal);
                if(listener!=null){
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    private void setText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Rating);
        try {
            textLow.setText(a.getString(R.styleable.Rating_text_low));
            textHigh.setText(a.getString(R.styleable.Rating_text_high));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void setLowText(String low){
        textLow.setText(low);
    }


    public void setHighText(String high){
        textHigh.setText(high);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        this.listener = listener;
    }

    public float getValue(){
        return (float) seekBar.getProgress()/100;
    }

    public void setValue(float value){
        if(seekBar!=null){
            seekBar.setProgress((int) (value*100));
        }
    }

    public SeekBar getSeekBar(){
        return seekBar;
    }



}
