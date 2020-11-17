//
// RebukedCommitmentThankYouScreen.java
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.templates.AltStandardTemplate;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class RebukedCommitmentThankYouScreen extends AltStandardTemplate {

    public RebukedCommitmentThankYouScreen(String header, String subheader, Boolean showButton) {
        super(true, header, subheader, showButton);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        textViewBack = v.findViewById(R.id.textViewBack);

        textViewHelp.setText(ViewUtil.getString(R.string.button_help));
        textViewBack.setText(ViewUtil.getString(R.string.button_back));

        return v;
    }

    @Override
    protected void onBackRequested() {
        NavigationManager.getInstance().popBackStack();
    }

}
