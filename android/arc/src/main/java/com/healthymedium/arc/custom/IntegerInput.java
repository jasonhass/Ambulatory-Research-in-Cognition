//
// IntegerInput.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class IntegerInput extends android.support.v7.widget.AppCompatEditText {

    Listener listener;

    public IntegerInput(Context context) {
        super(context);
        init(context);
    }

    public IntegerInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IntegerInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int dp10 = ViewUtil.dpToPx(10);
        int dp16 = ViewUtil.dpToPx(16);
        setPadding(dp16,dp10,dp16,dp10);
        setBackgroundResource(R.drawable.edit_text);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT,WRAP_CONTENT));
        setMinimumWidth(ViewUtil.dpToPx(144));
        setTextColor(ContextCompat.getColor(context,R.color.primary));
        setGravity(Gravity.CENTER);
    }

    public void setMaxLength(int maxLength){
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        setFilters(fArray);
    }

    public int getInteger() {
            String value = getText().toString();
            if(!value.isEmpty()){
                Integer.valueOf(getText().toString());
            }
        return 0;
    }

    public String getString() {
        return getText().toString();
    }

    public void setInteger(int value) {
        setText(String.valueOf(value));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onValueChanged();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && isEnabled() && isFocusable()) {
            setTypeface(Fonts.robotoBold);

            post(new Runnable() {
                @Override
                public void run() {
                    final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getRootView(),InputMethodManager.SHOW_IMPLICIT);
                }
            });
        } else if(!gainFocus){
            setTypeface(Fonts.roboto);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(listener!=null){
            listener.onValueChanged();
        }
    }
}
