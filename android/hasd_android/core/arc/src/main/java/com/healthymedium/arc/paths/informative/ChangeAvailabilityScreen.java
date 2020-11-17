//
// ChangeAvailabilityScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class ChangeAvailabilityScreen extends BaseFragment {

    String stringHeader;
    String changeDateHeader;

    boolean showChangeDate;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewChangeDate;

    Button button;
    Button changeDateButton;

    FrameLayout lineFrameLayout;

    public ChangeAvailabilityScreen() {
        stringHeader = Application.getInstance().getResources().getString(R.string.availability_changetime);

        Participant participant = Study.getParticipant();
        TestCycle cycle = participant.getCurrentTestCycle();
        DateTime startDate = cycle.getScheduledStartDate();
        DateTime endDate = cycle.getScheduledEndDate();
        DateTime now = DateTime.now();

        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            // We're not in a visit, so show the 1 week adjustment option
            showChangeDate = true;
            changeDateHeader = Application.getInstance().getResources().getString(R.string.availability_changedates);
        } else {
            // We are in a visit
            showChangeDate = false;
        }

        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_availability, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewChangeDate = view.findViewById(R.id.textViewChangeDate);
        changeDateButton = view.findViewById(R.id.changeDateButton);

        lineFrameLayout = view.findViewById(R.id.lineFrameLayout);

        if (showChangeDate) {
            textViewChangeDate.setText(Html.fromHtml(changeDateHeader));
            changeDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Study.adjustSchedule();
                }
            });
        } else {
            textViewChangeDate.setVisibility(View.GONE);
            changeDateButton.setVisibility(View.GONE);
            lineFrameLayout.setVisibility(View.GONE);
        }

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });


        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.updateAvailability(8, 18);
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
