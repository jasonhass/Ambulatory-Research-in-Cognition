//
// TotalEarningsView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import java.text.DecimalFormat;

/*
    Displays an initial value of text on screen, then text slides up to reveal new text.

    Usage:
        Define in XML:
            <com.healthymedium.arc.ui.TotalEarningsView
                android:id="@+id/totalEarningsView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

        Set first and second values in Java:
            TotalEarningsView totalEarningsView = view.findViewById(R.id.totalEarningsView);
            totalEarningsView.setText($0.00);
            totalEarningsView.setText($7.50,true);
*/

public class TotalEarningsView extends LinearLayout {

    private int white;
    private Handler handler;
    TextSwitcher textSwitcher;

    public TotalEarningsView(Context context) {
        super(context);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        white = ViewUtil.getColor(getContext(), R.color.white);
        handler = new Handler();

        textSwitcher = new TextSwitcher(getContext());
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTypeface(Fonts.robotoMedium);
                textView.setTextSize(32);
                textView.setTextColor(white);
                return textView;
            }
        });
        textSwitcher.setInAnimation(getContext(), R.anim.slide_in_up);
        textSwitcher.setOutAnimation(getContext(), R.anim.slide_out_up);
        addView(textSwitcher);
    }

    public void setText(String value) {
        setText(value,false,null);
    }

    public void setText(String value, boolean animate) {
        setText(value,animate,null);
    }

    public void setText(String value, boolean animate, final Listener listener) {
        if(animate){
            textSwitcher.setText(value);
            if(listener!=null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinished();
                    }
                },500);
            }
        } else {
            textSwitcher.setCurrentText(value);
        }
    }

    public interface Listener {
        void onFinished();
    }

}
