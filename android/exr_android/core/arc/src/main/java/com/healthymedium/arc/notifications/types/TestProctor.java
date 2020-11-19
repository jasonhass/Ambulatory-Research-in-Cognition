//
// TestProctor.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.notifications.NotificationNode;

public class TestProctor extends NotificationType {

    public TestProctor(){
        super();
        id = -1;
        channelId = "TEST_PROCTOR_SERVICE";
        channelName = "Test Proctor Service";
        channelDesc = "Handles time critical notifications for testing";
        importance = NotificationImportance.LOW;
        extra = "";
        proctored = false;
        showBadge = false;
    }

    @Override
    public String getContent(NotificationNode node) {
        return "";
    }

    // not used but still here because it extends an abstract class
    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
