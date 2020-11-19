//
// LegacyMigrationErrorScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.paths;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.exr.R;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class LegacyMigrationErrorScreen extends BaseFragment {

    TextView textViewBody;
    TextView textViewContact;
    Button buttonNext;

    public LegacyMigrationErrorScreen() {
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legacy_migration_error, container, false);

        textViewBody = view.findViewById(R.id.textViewSubHeader);
        textViewBody.setText(Html.fromHtml(getString(R.string.legacy_migration_error)));

        textViewContact = view.findViewById(R.id.textViewContact);
        textViewContact.setTypeface(Fonts.robotoBold);
        textViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        ViewUtil.underlineTextView(textViewContact);

        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().open(new LegacyMigrationScreen());
            }
        });

        return view;
    }

}
