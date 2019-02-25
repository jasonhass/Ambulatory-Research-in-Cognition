//
// InfoTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class InfoTemplate extends BaseFragment {

    String stringHeader;
    String stringSubHeader;
    String stringBody;
    String stringButton;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubheader;
    TextView textViewBody;

    LinearLayout content;

    Button button;
    boolean allowBack;

    public InfoTemplate(boolean allowBack, String header, String subheader, String body, @Nullable String buttonText) {
        this.allowBack = allowBack;
        stringHeader = header;
        stringSubHeader = subheader;
        stringBody = body;
        stringButton = buttonText;

        if(allowBack){
            allowBackPress(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.georgiaItalic);
        textViewHeader.setText(stringHeader);

        textViewSubheader = view.findViewById(R.id.textViewSubHeader);
        textViewSubheader.setText(stringSubHeader);

        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(stringBody);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openPreviousFragment();
            }
        });


        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        if(allowBack){
            textViewBack.setVisibility(View.VISIBLE);
        }

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

}
