//
// SetupResendCode.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.informative.FAQAnswerScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupResendCode extends BaseFragment {

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;
    TextView textViewError;
    TextView textViewProblems;

    Button newCodeButton;

    LinearLayout content;

    public SetupResendCode() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resend_code, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(ViewUtil.getString(R.string.login_resend_header));

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setText(ViewUtil.getString(R.string.login_resend_subheader));
        ViewUtil.setLineHeight(textViewSubHeader,26);

        newCodeButton = view.findViewById(R.id.newCodeButton);
        newCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                Study.getRestClient().requestVerificationCode(id,verificationCodeListener);
            }
        });

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        textViewProblems = new TextView(getContext());
        textViewProblems.setTypeface(Fonts.robotoMedium);
        textViewProblems.setPadding(0, ViewUtil.dpToPx(24), 0, 0);
        textViewProblems.setText(ViewUtil.getString(R.string.login_2FA_morehelp_linked));
        textViewProblems.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewProblems.setVisibility(View.VISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQAnswerScreen helpScreen = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_tech_q5), ViewUtil.getString(R.string.faq_tech_a5));
                NavigationManager.getInstance().open(helpScreen);
            }
        });
        ViewUtil.underlineTextView(textViewProblems);

        // add below textViewError
        int index = content.indexOfChild(textViewError) + 1;
        content.addView(textViewProblems, index);

        return view;
    }

    protected void onBackRequested() {

        NavigationManager.getInstance().popBackStack();
    }

    String parseForError(RestResponse response, boolean failed){
        int code = response.code;
        switch (code){
            case 400:
                return getResources().getString(R.string.login_error3);
            case 401:
                return getResources().getString(R.string.login_error1);
            case 409:
                return getResources().getString(R.string.login_error2);
        }
        if(response.errors.keySet().size()>0){
            String key = response.errors.keySet().toArray()[0].toString();
            return response.errors.get(key).getAsString();
        }
        if(!response.successful || failed){
            return getResources().getString(R.string.login_error3);
        }
        return null;
    }

    RestClient.Listener verificationCodeListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            String errorString = parseForError(response,false);
            if(errorString!=null) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText(errorString);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            String errorString = parseForError(response,true);
            textViewError.setVisibility(View.VISIBLE);
            textViewError.setText(errorString);
        }
    };
}
