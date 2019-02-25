//
// QuestionInteger.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.custom.IntegerInput;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionInteger extends QuestionTemplate {

    String value;
    IntegerInput input;
    int maxLength;

    public QuestionInteger(boolean allowBack, String header, String subheader, int maxLength) {
        super(allowBack,header,subheader);
        this.maxLength = maxLength;
        type = "multilineText";
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        input = new IntegerInput(getContext());
        input.setMaxLength(maxLength);
        input.setListener(new IntegerInput.Listener() {
            @Override
            public void onValueChanged() {
                response_time = System.currentTimeMillis();
                if(input.getString().isEmpty()){
                    buttonNext.setEnabled(false);
                } else if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                }
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean done = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    done = true;
                }

                if(keyEvent!=null){
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        done = true;
                    }
                }

                if(done && input.length()>0){
                    buttonNext.performClick();
                }
                return false;
            }
        });
        input.setSingleLine();


        content.addView(input);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ViewUtil.dpToPx(32);
        params.topMargin = ViewUtil.dpToPx(19);
        content.setLayoutParams(params);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(input !=null) {
            value = input.getString();
        }
        hideKeyboard();
        getMainActivity().removeKeyboardListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().setKeyboardListener(keyboardToggleListener);

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        if(input != null) {
            input.setText(value);
            input.requestFocus();
            showKeyboard(input);
        }
    }

    @Override
    public Object onValueCollection(){
        if(input !=null) {
            value = input.getString();
        }
        if(!isInteger(value,10)){
            return null;
        }
        return value;
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();
    }

    KeyboardWatcher.OnKeyboardToggleListener keyboardToggleListener = new KeyboardWatcher.OnKeyboardToggleListener() {
        @Override
        public void onKeyboardShown(int keyboardSize) {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.GONE);
            }
        }

        @Override
        public void onKeyboardClosed() {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.VISIBLE);
            }
        }
    };

    public static boolean isInteger(String s, int radix) {
        if(s==null) return false;
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

}
