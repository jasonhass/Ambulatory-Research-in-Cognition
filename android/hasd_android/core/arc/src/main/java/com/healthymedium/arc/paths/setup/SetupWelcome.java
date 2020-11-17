//
// SetupWelcome.java
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.AboutScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.VersionUtil;
import com.healthymedium.arc.utilities.ViewUtil;

public class SetupWelcome extends BaseFragment {

    Button button;
    TextView textViewHeader;
    TextView textViewAboutApp;
    TextView textViewPrivacyPolicy;
    // TextView textViewAppName;
    TextView textViewVersion;

    public SetupWelcome() {
        //Locale.update(Locale.getDefault(), getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_welcome, container, false);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.gen_welcome_key)));

        // textViewAppName = view.findViewById(R.id.textViewAppName);
        // textViewAppName.setText(ViewUtil.getString(R.string.app_name)+" app");

        textViewAboutApp = view.findViewById(R.id.textViewAboutApp);
        textViewAboutApp.setTypeface(Fonts.robotoBold);
        textViewAboutApp.setPaintFlags(textViewAboutApp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutScreen aboutScreen = new AboutScreen();
                NavigationManager.getInstance().open(aboutScreen);
            }
        });

        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        textViewPrivacyPolicy.setTypeface(Fonts.robotoBold);
        textViewPrivacyPolicy.setPaintFlags(textViewPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getPrivacyPolicy().show(getContext());
            }
        });

        textViewVersion = view.findViewById(R.id.textViewVersion);
        String ver = "v"+VersionUtil.getAppVersionName();
        textViewVersion.setText(ver);

        button = view.findViewById(R.id.button);
        button.setText(ViewUtil.getHtmlString(R.string.button_signin));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
