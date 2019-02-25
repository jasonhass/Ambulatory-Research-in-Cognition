//
// LandingTemplate.java
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
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class LandingTemplate extends BaseFragment {

    String stringHeader;

    protected TextView textViewHeader;
    protected LinearLayout content;
    protected FrameLayout frameLayoutContact;
    protected TextView textViewContact;

    public LandingTemplate(String header) {
        stringHeader = header;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_landing, container, false);
        content = view.findViewById(R.id.linearLayoutContent);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewContact = view.findViewById(R.id.textViewContact);
        ViewUtil.underlineTextView(textViewContact);

        frameLayoutContact = view.findViewById(R.id.frameLayoutContact);
        frameLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

}
