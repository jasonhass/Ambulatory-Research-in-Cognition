//
// NotificationTurnOn.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.notification;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class NotificationTurnOn extends StateInfoTemplate {

    boolean returning = false;

    public NotificationTurnOn() {
        super(false,
                ViewUtil.getString(R.string.onboarding_notifications_header2),
                null,
                ViewUtil.replaceToken(
                        R.string.onboarding_notifications_body2_android,
                        R.string.token_app_name,
                        R.string.app_name),
                ViewUtil.getString(R.string.button_settings)
                );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationUtil.openNotificationSettings(getMainActivity());
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        returning = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(returning){
            if(NotificationUtil.areNotificationsEnabled(getContext())){
                Study.getParticipant().markShownNotificationOverview();
                Study.openNextFragment();
            }
        }
    }
}
