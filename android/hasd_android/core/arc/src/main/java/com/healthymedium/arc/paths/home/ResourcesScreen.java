//
// ResourcesScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.AboutScreen;
import com.healthymedium.arc.paths.informative.ChangeAvailabilityScreen;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.FAQScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class ResourcesScreen extends BaseFragment {

    protected TextView textViewHeader;

    protected RelativeLayout frameLayoutAvailability;
    protected TextView textViewAvailability;

    protected FrameLayout frameLayoutContact;
    protected TextView textViewContact;

    protected FrameLayout frameLayoutAbout;
    protected TextView textViewAbout;

    protected FrameLayout frameLayoutFAQ;
    protected TextView textViewFAQ;

    protected FrameLayout frameLayoutPrivacyPolicy;
    protected TextView textViewPrivacyPolicy;

    public ResourcesScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_header)));
        textViewHeader.setTypeface(Fonts.robotoMedium);

        textViewAvailability = view.findViewById(R.id.textViewAvailability);
        textViewAvailability.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_availability)));
        ViewUtil.underlineTextView(textViewAvailability);

        frameLayoutAvailability = view.findViewById(R.id.frameLayoutAvailability);

        boolean isStudyOver = (Study.getCurrentTestCycle()==null);
        boolean isTestReady = false;

        if(!isStudyOver) {
            isTestReady = Study.getCurrentTestSession().getScheduledTime().minusMinutes(5).isBeforeNow();
        }

        if (isStudyOver || isTestReady) {
            textViewAvailability.setAlpha(0.5f);
            if(isTestReady) {
                TextView textViewChangeDenied = view.findViewById(R.id.textViewChangeDenied);
                textViewChangeDenied.setVisibility(View.VISIBLE);
            }
        } else {
            frameLayoutAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeAvailabilityScreen changeAvailabilityScreen = new ChangeAvailabilityScreen();
                    NavigationManager.getInstance().open(changeAvailabilityScreen);
                }
            });
        }

        textViewContact = view.findViewById(R.id.textViewContact);
        textViewContact.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_contact)));
        ViewUtil.underlineTextView(textViewContact);

        frameLayoutContact = view.findViewById(R.id.frameLayoutContact);
        frameLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });


        textViewAbout = view.findViewById(R.id.textViewAbout);
        textViewAbout.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_about_link)));
        ViewUtil.underlineTextView(textViewAbout);

        frameLayoutAbout = view.findViewById(R.id.frameLayoutAbout);
        frameLayoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutScreen aboutScreen = new AboutScreen();
                NavigationManager.getInstance().open(aboutScreen);
            }
        });


        textViewFAQ = view.findViewById(R.id.textViewFAQ);
        textViewFAQ.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_faq_link)));
        ViewUtil.underlineTextView(textViewFAQ);

        frameLayoutFAQ = view.findViewById(R.id.frameLayoutFAQ);
        frameLayoutFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });


        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        textViewPrivacyPolicy.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_privacy_link)));
        ViewUtil.underlineTextView(textViewPrivacyPolicy);

        frameLayoutPrivacyPolicy = view.findViewById(R.id.frameLayoutPrivacyPolicy);
        frameLayoutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getPrivacyPolicy().show(getContext());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }
}
