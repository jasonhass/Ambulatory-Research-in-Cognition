//
// ContactScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Intent.ACTION_DIAL;
import static android.content.Intent.ACTION_SENDTO;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.study.Study.TAG_CONTACT_EMAIL;
import static com.healthymedium.arc.study.Study.TAG_CONTACT_INFO;

public class ContactScreen extends BaseFragment {

    String stringPhoneNumber;
    String stringEmail;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewPhoneNumber;

    TextView textViewEmailHeader;
    TextView textViewEmailAddress;

    Button callButton;
    Button emailButton;

    public ContactScreen() {
        stringPhoneNumber = PreferencesManager.getInstance().getString(TAG_CONTACT_INFO ,ViewUtil.getString(R.string.contact_call2));
        stringEmail = PreferencesManager.getInstance().getString(TAG_CONTACT_EMAIL, ViewUtil.getString(R.string.contact_email2));
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(ViewUtil.getHtmlString(R.string.contact_call1));

        textViewPhoneNumber = view.findViewById(R.id.textViewSubHeader);
        textViewPhoneNumber.setText(stringPhoneNumber);

        textViewEmailHeader = view.findViewById(R.id.textViewEmailHeader);
        textViewEmailHeader.setText(ViewUtil.getHtmlString(R.string.contact_email1));

        textViewEmailAddress = view.findViewById(R.id.textViewEmailSubHeader);
        textViewEmailAddress.setText(stringEmail);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });


        callButton = view.findViewById(R.id.button);
        callButton.setText(ViewUtil.getString(R.string.button_call));
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = stringPhoneNumber.replace("-","");
                number = number.replace("+", "");
                Intent intent = new Intent(ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });

        emailButton = view.findViewById(R.id.emailButton);
        emailButton.setText(ViewUtil.getString(R.string.button_email));
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ACTION_SENDTO, Uri.fromParts(
                        "mailto",stringEmail, null));
                startActivity(Intent.createChooser(intent,""));
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
