//
// VisitNextDay.java
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
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.utilities.ViewUtil;

public class VisitNextDay extends NotificationType {

    public VisitNextDay(){
        super();
        id = 7;
        channelId = "VISIT_NEXT_DAY";
        channelName = "Next Testing Cycle, Day Prior";
        channelDesc = "Notifies the user one day before their next testing cycle";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION;
        proctored = false;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        return ViewUtil.getString(R.string.notification_daybefore);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }

    @Override
    public void onNotify(NotificationNode node) {

    }

}
