//
// LegacyMigrationState.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dian.arc.exr.Application;
import com.dian.arc.exr.MainActivity;
import com.dian.arc.exr.api.RestClient;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.notifications.types.NotificationType;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.FileUtil;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.healthymedium.arc.heartbeat.HeartbeatManager.TAG_LAST_HEARTBEAT;
import static com.healthymedium.arc.notifications.types.TestMissed.TAG_TEST_MISSED_COUNT;

public class LegacyMigrationState {

    boolean initialized = false;
    boolean hasRemovedOldNotificationChannels = false;
    boolean hasUpdatedDeviceId = false;
    boolean hasContactInfo = false;
    boolean hasSessionInfo = false;
    boolean hasCopiedZipsToCache = false;
    int numberOfZipsUploaded = 0;
    int numberOfZipsCopied = 0;
    boolean hasUploadedAllZips = false;
    boolean hasCopiedPreferences = false;
    boolean hasBeenCompleted = false;

}
