//
// TestTake.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications.types;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TestTake extends NotificationType {

    private DateTime expirationTime;
    private String body;

    public TestTake(){
        super();
        id = 1;
        channelId = "TEST_TAKE";
        channelName = "Test Reminder";
        channelDesc = "Notifies user when it is time to take a test";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {

        Participant participant = Study.getParticipant();
        TestSession session = participant.getSessionById(node.id);
        TestCycle cycle = participant.getCycleBySessionId(node.id);

        if(session==null) {
            return "";
        }

        expirationTime = session.getExpirationTime();

        // if first test of the cycle
        if (session.getIndex() == 0 && session.getDayIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_firstday);
        }

        // if first of day 4
        else if (session.getIndex() == 0 && session.getDayIndex() == 3) {
            body = ViewUtil.getString(R.string.notification1_halfway);
        }

        // if first session of last day of cycle
        else if (session.getIndex() == 0 && session.getDayIndex() == cycle.getNumberOfTestDays()-1) {
            body = ViewUtil.getString(R.string.notification1_lastday);
        }

        // if first of day
        else if (session.getIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_default);
        }

        // if second of day
        else if (session.getIndex() == 1) {
            body = ViewUtil.getString(R.string.notifications2_default);
        }

        // if third of day
        else if (session.getIndex() == 2) {
            body = ViewUtil.getString(R.string.notification3_default);
        }

        // if last of cycle
        else if (session.getIndex() == 3 && session.getDayIndex() == cycle.getNumberOfTestDays()-1) {
            body = ViewUtil.getString(R.string.notification4_lastday);
        }

        // if last of day
        else if (session.getIndex() == 3) {
            body = ViewUtil.getString(R.string.notification4_default);
        }

        String pattern = DateTimeFormat.patternForStyle("-S", Application.getInstance().getLocale());
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        String time = fmt.print(expirationTime);

        return body.replace("{TIME}", time);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }

    @Override
    public void onNotify(NotificationNode node) {

    }

}
