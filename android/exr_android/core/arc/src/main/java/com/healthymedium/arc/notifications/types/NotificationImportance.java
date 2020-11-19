//
// NotificationImportance.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications.types;

import android.app.NotificationManager;

public class NotificationImportance {
    public static final int MIN = NotificationManager.IMPORTANCE_MIN;
    public static final int DEFAULT = NotificationManager.IMPORTANCE_DEFAULT;
    public static final int HIGH = NotificationManager.IMPORTANCE_HIGH;
    public static final int LOW = NotificationManager.IMPORTANCE_LOW;
    public static final int MAX = NotificationManager.IMPORTANCE_MAX;
    public static final int NONE = NotificationManager.IMPORTANCE_NONE;
    public static final int UNSPECIFIED = NotificationManager.IMPORTANCE_UNSPECIFIED;
}
