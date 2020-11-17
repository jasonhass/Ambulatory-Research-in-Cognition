//
// DigitView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

public class DigitView extends FrameLayout {

    TextView textViewDigit;
    TextView textViewUnselected;
    TextView textViewSelected;
    OnFocusChangeListener listener;

    public DigitView(Context context) {
        super(context);
        init(context);
    }

    public DigitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DigitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_input_digit,this);
        textViewDigit = view.findViewById(R.id.textViewDigit);
        textViewUnselected = view.findViewById(R.id.textViewUnselected);
        textViewSelected = view.findViewById(R.id.textViewSelected);
    }

    public void setFocused(boolean focused){
        if(focused){
            textViewUnselected.setVisibility(INVISIBLE);
            textViewSelected.setVisibility(VISIBLE);
        } else {
            textViewUnselected.setVisibility(VISIBLE);
            textViewSelected.setVisibility(INVISIBLE);
        }
        if(listener!=null){
            listener.onFocusChange(this,focused);
        }
    }

    public char getDigit(){
        return textViewDigit.getText().charAt(0);
    }

    public void setDigit(Character digit, boolean error){
        textViewDigit.setText(Character.toString(digit));
        int color = error ? R.color.red : R.color.primary;
        textViewUnselected.setBackgroundResource(color);
        textViewSelected.setBackgroundResource(color);
    }

    public void removeDigit(){
        textViewDigit.setText("");
        textViewUnselected.setBackgroundResource(R.color.grey);
        textViewSelected.setBackgroundResource(R.color.primary);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        super.setOnClickListener(l);
        textViewDigit.setOnClickListener(l);
        textViewSelected.setOnClickListener(l);
        textViewUnselected.setOnClickListener(l);
    }
}
