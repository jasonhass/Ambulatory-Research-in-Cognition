//
// HelpScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class HelpScreen extends BaseFragment {

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;

    TextView textViewAbout;
    TextView textViewPrivacyPolicy;

    public HelpScreen() {
        allowBackPress(false);
        setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
        setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setLineSpacing(3,1.0f);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        textViewAbout = view.findViewById(R.id.textViewAbout);
        ViewUtil.underlineTextView(textViewAbout);
        textViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutScreen aboutScreen = new AboutScreen();
                NavigationManager.getInstance().open(aboutScreen);
            }
        });

        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        ViewUtil.underlineTextView(textViewPrivacyPolicy);
        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyScreen privacyScreen = new PrivacyScreen();
                NavigationManager.getInstance().open(privacyScreen);
            }
        });

        return view;
    }

}
