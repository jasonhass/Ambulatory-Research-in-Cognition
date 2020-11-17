//
// StateInfoTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class StateInfoTemplate extends BaseFragment {

    String stringHeader;
    String stringSubHeader;
    String stringBody;
    String stringButton;

    Drawable buttonImage;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubheader;
    TextView textViewBody;

    LinearLayout content;

    protected Button button;
    boolean allowBack;

    public StateInfoTemplate(boolean allowBack, String header, String subheader, String body, @Nullable String buttonText) {
        this.allowBack = allowBack;
        stringHeader = header;
        stringSubHeader = subheader;
        stringBody = body;
        stringButton = buttonText;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public StateInfoTemplate(boolean allowBack, String header, String subheader, String body, @Nullable Drawable buttonImage) {
        this.allowBack = allowBack;
        stringHeader = header;
        stringSubHeader = subheader;
        stringBody = body;
        this.buttonImage = buttonImage;

        if(allowBack){
            allowBackPress(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_state_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewSubheader = view.findViewById(R.id.textViewSubHeader);
        textViewSubheader.setText(stringSubHeader);

        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(Html.fromHtml(stringBody));

        if (stringSubHeader == "") {
            textViewSubheader.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            float dpRatio = getResources().getDisplayMetrics().density;
            int side = (int)(32 * dpRatio);
            int top = (int)(15 * dpRatio);

            params.setMargins(side,top,side,0);
            textViewBody.setLayoutParams(params);
        }



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
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
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
