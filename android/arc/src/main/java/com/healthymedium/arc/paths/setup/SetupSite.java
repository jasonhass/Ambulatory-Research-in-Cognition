//
// SetupSite.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.setup;

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
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.models.Response;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.custom.DigitView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.StandardTemplate;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.paths.informative.PrivacyScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class SetupSite extends StandardTemplate {

    String defaultError = "Sorry, our app is currently experiencing issues. Please try again later.";
    final int maxDigits = 5;

    CharSequence characterSequence;
    int focusedIndex=0;

    EditText editText;
    List<DigitView> digits;
    LinearLayout inputLayout;
    TextView textViewError;
    TextView textViewProblems;

    TextView textViewPolicyLink;
    TextView textViewPolicy;

    // ---
    LoadingDialog loadingDialog;

    public SetupSite() {
        super(true,"Please enter the<br/><b>Site Code</b> below.","");
        disableScrollBehavior();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater,container,savedInstanceState);

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
        inputLayout.setPadding(0,50,0,ViewUtil.dpToPx(16));
        content.addView(inputLayout);

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        textViewProblems = new TextView(getContext());
        textViewProblems.setTypeface(Fonts.robotoMedium);
        textViewProblems.setPadding(0,ViewUtil.dpToPx(24),0,0);
        textViewProblems.setText("Problems logging in?");
        textViewProblems.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewProblems.setVisibility(View.INVISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpScreen contactScreen = new HelpScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        ViewUtil.underlineTextView(textViewProblems);
        content.addView(textViewProblems);

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
        content.setPadding(ViewUtil.dpToPx(32),50,ViewUtil.dpToPx(32),50);

        digits = new ArrayList<>();
        for(int i=0; i<maxDigits;i++){
            DigitView digitInput = new DigitView(getContext());
            digitInput.setOnClickListener(clickListener);
            digits.add(digitInput);
            inputLayout.addView(digitInput);
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonNext.setEnabled(false);
                hideKeyboard();

                if(Config.REST_BLACKHOLE){
                    String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                    Study.getParticipant().getState().id = id;
                    Study.getInstance().openNextFragment();
                    return;
                }

                if(isErrorShowing()){
                    hideError();
                }

                loadingDialog = new LoadingDialog();
                loadingDialog.show(getFragmentManager(),"LoadingDialog");
                String siteCode = editText.getText().toString();
                String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                Study.getRestClient().registerDevice(id, siteCode, false, registrationListener);
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
        ViewUtil.underlineTextView(textViewPolicyLink);
        textViewPolicyLink.setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
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

        buttonNext.setVisibility(View.GONE);
        textViewPolicy.setVisibility(View.GONE);
        textViewPolicyLink.setVisibility(View.GONE);

        return view;
    }

    void setupEditText(){
        editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setCursorVisible(false);
        editText.setBackground(null);
        editText.setTextColor(ViewUtil.getColor(android.R.color.transparent));

        // hackish but moves the edittext off the screen while still letting us use it
        editText.animate().translationXBy(ViewUtil.dpToPx(1000));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(isErrorShowing()){
                    hideError();
                }
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
                    //done = true;
                    hideKeyboard();
                }

                if(keyEvent!=null){
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        //done = true;
                        hideKeyboard();
                    }
                }

//                if(done && editText.length()==maxDigits){
//                    buttonNext.performClick();
//                }
                return false;
            }
        });
        editText.setSingleLine();

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxDigits);
        editText.setFilters(fArray);

    }

    void updateView(CharSequence s){
        boolean hasError = isErrorShowing();
        int size = s.length();
        for(int i=0;i<size;i++){
            digits.get(i).setDigit(s.charAt(i),hasError);
        }
        int left = digits.size()-size;
        for(int i=size;i<left+size;i++){
            digits.get(i).removeDigit();
        }

    }

    @Override
    protected void onBackRequested() {
        super.onBackRequested();
        hideKeyboard();
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

    private void hideError(){
        textViewProblems.setVisibility(View.INVISIBLE);
        textViewError.setVisibility(View.INVISIBLE);
        textViewError.setText("");
    }

    private boolean isErrorShowing(){
        return textViewError.getVisibility()==View.VISIBLE;
    }

    private void showError(String error){
        buttonNext.setEnabled(false);
        textViewProblems.setVisibility(View.VISIBLE);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(error);
        updateView(editText.getText());
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

    String parseForError(Response response, boolean failed){
        int code = response.code;
        switch (code){
            case 400:
                return defaultError;
            case 401:
                return "Invalid Subject ID or Site Code";
            case 409:
                return "Already enrolled on another device";
        }
        if(response.errors.keySet().size()>0){
            String key = response.errors.keySet().toArray()[0].toString();
            return response.errors.get(key).getAsString();
        }
        if(!response.successful || failed){
            return defaultError;
        }
        return null;
    }

    RestClient.Listener registrationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(Response response) {
            String errorString = parseForError(response,false);
            loadingDialog.dismiss();
            if(errorString==null) {
                String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                Study.getParticipant().getState().id = id;
                Study.openNextFragment();
            } else {
                showError(errorString);
            }
        }

        @Override
        public void onFailure(Response response) {
            String errorString = parseForError(response,true);
            showError(errorString);
            loadingDialog.dismiss();
        }
    };

}
