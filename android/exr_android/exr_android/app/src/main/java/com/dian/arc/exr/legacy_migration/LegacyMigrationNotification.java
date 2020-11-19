//
// LegacyMigrationNotification.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.types.NotificationImportance;
import com.healthymedium.arc.notifications.types.NotificationType;

public class LegacyMigrationNotification extends NotificationType {

    public LegacyMigrationNotification() {
        super();
        id = -99;
        channelId = "LEGACY_MIGRATION_SERVICE";
        channelName = "Legacy Migration Service";
        channelDesc = "Handles legacy migration";
        importance = NotificationImportance.LOW;
        extra = "";
        proctored = false;
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
