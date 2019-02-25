//
// SetupParticipant.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.setup;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.healthymedium.arc.custom.DigitView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.StandardTemplate;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.paths.informative.PrivacyScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class SetupParticipant extends StandardTemplate {

    int maxDigits = 8;
    int firstDigits = 5;
    int secondDigits = 3;

    CharSequence characterSequence;
    int focusedIndex=0;

    EditText editText;
    List<DigitView> digits;
    LinearLayout inputLayout;

    TextView textViewPolicyLink;
    TextView textViewPolicy;

    public SetupParticipant() {
        super(true,"Please enter the<br/><b>Subject ID</b> below.","");
        disableScrollBehavior();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        // This isn't quite as safe as I would like
        // It still assumes all of the keys exist in the bundle
        if (getArguments() != null) {
            if (getArguments().containsKey("firstDigits")) {
                firstDigits = getArguments().getInt("firstDigits");
            }

            if (getArguments().containsKey("secondDigits")) {
                secondDigits = getArguments().getInt("secondDigits");
            }
        }

        maxDigits = firstDigits + secondDigits;

        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpScreen helpScreen = new HelpScreen();
                NavigationManager.getInstance().open(helpScreen);
            }
        });

        inputLayout = new LinearLayout(getContext());
        inputLayout.setGravity(Gravity.CENTER);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(0,50,0,50);
        content.addView(inputLayout);

        setupEditText();
        content.addView(editText);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.requestFocus();
                showKeyboard(editText);
            }
        };
        content.setOnClickListener(clickListener);
        content.setPadding(0,50,0,50);

        digits = new ArrayList<>();
        for(int i=0; i<firstDigits;i++){
            DigitView digitInput = new DigitView(getContext());
            digitInput.setOnClickListener(clickListener);
            digits.add(digitInput);
            inputLayout.addView(digitInput);
        }

        if (secondDigits > 0) {
            addSpacer(16);

            for(int i=0;i<secondDigits;i++){
                DigitView digitInput = new DigitView(getContext());
                digitInput.setOnClickListener(clickListener);
                digits.add(digitInput);
                inputLayout.addView(digitInput);
            }
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SetupPathData)Study.getCurrentSegmentData()).id = characterSequence.toString();
                Study.openNextFragment();
            }
        });

        RelativeLayout relativeLayout = (RelativeLayout) view;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        textViewPolicy = new TextView(getContext());
        textViewPolicy.setText("By signing in you agree to our");
        textViewPolicy.setTextSize(15);
        linearLayout.addView(textViewPolicy);

        textViewPolicyLink = new TextView(getContext());
        textViewPolicyLink.setTypeface(Fonts.robotoMedium);
        textViewPolicyLink.setPaintFlags(textViewPolicyLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewPolicyLink.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewPolicyLink.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewPolicyLink.setText("Privacy Policy");
        textViewPolicyLink.setTextSize(15);


        textViewPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyScreen privacyScreen = new PrivacyScreen();
                NavigationManager.getInstance().open(privacyScreen);
            }
        });
        linearLayout.addView(textViewPolicyLink);

        RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params0.bottomMargin = ViewUtil.dpToPx(24);
        params0.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params0.addRule(RelativeLayout.ABOVE,buttonNext.getId());
        relativeLayout.addView(linearLayout,params0);

        return view;
    }

    void addSpacer(int widthDp){
        int width = ViewUtil.dpToPx(widthDp);
        Space space = new Space(getContext());
        space.setLayoutParams(new ViewGroup.LayoutParams(width,width));
        inputLayout.addView(space);
    }

    void setupEditText(){
        editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setCursorVisible(false);
        editText.setBackground(null);
        editText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.transparent));

        // hackish but moves the edittext off the screen while still letting us use it
        editText.animate().translationXBy(ViewUtil.dpToPx(1000));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Log.i("textChanged","start="+start+" before="+before+" count="+count);
                characterSequence = charSequence;
                if(before>count){
                    if(start >= 0){
                        if(start < maxDigits-1){
                            digits.get(start+1).setFocused(false);
                        }

                        digits.get(start).setFocused(true);
                        focusedIndex = start;
                    }
                } else {
                    if(start < maxDigits-1){
                        digits.get(start).setFocused(false);
                        digits.get(start+1).setFocused(true);
                        focusedIndex = start+1;
                    }
                }

                updateView(charSequence);
                if(charSequence.length()==maxDigits){
                    buttonNext.setEnabled(true);
                } else if(buttonNext.isEnabled()){
                    buttonNext.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

                if(done && editText.length()==maxDigits){
                    buttonNext.performClick();
                }
                return false;
            }
        });
        editText.setSingleLine();

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxDigits);
        editText.setFilters(fArray);

    }

    void updateView(CharSequence s){
        int size = s.length();
        for(int i=0;i<size;i++){
            digits.get(i).setDigit(s.charAt(i),false);
        }
        int left = digits.size()-size;
        for(int i=size;i<left+size;i++){
            digits.get(i).removeDigit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        focusedIndex = editText.getSelectionStart();
        getMainActivity().removeKeyboardListener();
        hideKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();

        getMainActivity().setKeyboardListener(keyboardToggleListener);

        if(characterSequence!=null) {
            int lastIndex = focusedIndex;
            editText.setText(characterSequence);
            editText.setSelection(lastIndex);

            digits.get(focusedIndex).setFocused(false);
            int digitFocus = characterSequence.length();
            if(digitFocus==maxDigits){
                digitFocus--;
            }
            digits.get(digitFocus).setFocused(true);
            focusedIndex = lastIndex;

            updateView(characterSequence);
        } else {
            digits.get(0).setFocused(true);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped){
        super.onEnterTransitionEnd(popped);
        editText.requestFocus();
        showKeyboard(editText);
    }

    @Override
    protected void onExitTransitionStart(boolean popped){
        super.onExitTransitionStart(popped);
        hideKeyboard();
    }

    @Override
    protected void onBackRequested() {
        hideKeyboard();
        super.onBackRequested();
    }

    KeyboardWatcher.OnKeyboardToggleListener keyboardToggleListener = new KeyboardWatcher.OnKeyboardToggleListener() {
        @Override
        public void onKeyboardShown(int keyboardSize) {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.GONE);
                textViewPolicy.setVisibility(View.GONE);
                textViewPolicyLink.setVisibility(View.GONE);
            }
        }

        @Override
        public void onKeyboardClosed() {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.VISIBLE);
                textViewPolicy.setVisibility(View.VISIBLE);
                textViewPolicyLink.setVisibility(View.VISIBLE);
            }
        }
    };
}
