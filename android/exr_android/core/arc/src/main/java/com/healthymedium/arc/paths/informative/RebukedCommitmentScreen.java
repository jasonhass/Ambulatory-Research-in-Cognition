//
// RebukedCommitmentScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class RebukedCommitmentScreen extends StateInfoTemplate {

    public RebukedCommitmentScreen(String header, String body, String buttonText) {
        super(false,header,null,body,buttonText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) super.onCreateView(inflater,container,savedInstanceState);

        TextView textView = new TextView(getContext());

        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,ViewUtil.dpToPx(24));
        params.addRule(RelativeLayout.ABOVE, R.id.button);
        textView.setLayoutParams(params);

        textView.setTextColor(ViewUtil.getColor(R.color.white));
        textView.setText(R.string.resources_contact);
        textView.setTypeface(Fonts.robotoBold);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        ViewUtil.underlineTextView(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });


        view.addView(textView);

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

}
