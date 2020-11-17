//
// TestMissed.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestMissed extends NotificationType {

    public static final String TAG_TEST_MISSED_COUNT = "TestMissedCount";

    public TestMissed(){
        super();
        id = 2;
        channelId = "TEST_MISSED";
        channelName = "Test Missed";
        channelDesc = "Notifies user when a test was missed";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        return ViewUtil.getString(R.string.notification_missedtests);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        PreferencesManager preferences = PreferencesManager.getInstance();
        int count = preferences.getInt(TAG_TEST_MISSED_COUNT, 0);
        count++;

        boolean showUser = (count == 4);
        return showUser;
    }

    @Override
    public void onNotify(NotificationNode node) {
        int notifyId = NotificationNode.getNotifyId(node.id, NotificationTypes.TestTake.id);
        NotificationManager.getInstance().removeUserNotification(notifyId);

        PreferencesManager preferences = PreferencesManager.getInstance();
        preferences.putInt(TAG_TEST_MISSED_COUNT, 0);
    }

}
