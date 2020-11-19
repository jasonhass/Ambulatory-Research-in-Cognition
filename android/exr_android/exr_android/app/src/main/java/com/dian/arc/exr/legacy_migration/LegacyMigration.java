//
// LegacyMigration.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dian.arc.exr.Application;
import com.dian.arc.exr.MainActivity;
import com.dian.arc.exr.api.RestClient;
import com.dian.arc.exr.legacy_migration.models.Visit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthymedium.arc.api.CallbackChain;
import com.healthymedium.arc.api.DoubleTypeAdapter;
import com.healthymedium.arc.api.ItemTypeAdapterFactory;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.notifications.types.NotificationType;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.FileUtil;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.healthymedium.arc.heartbeat.HeartbeatManager.TAG_LAST_HEARTBEAT;
import static com.healthymedium.arc.notifications.types.TestMissed.TAG_TEST_MISSED_COUNT;
import static com.healthymedium.arc.paths.tutorials.GridTutorial.HINT_PREVENT_TUTORIAL_CLOSE_GRIDS;
import static com.healthymedium.arc.paths.tutorials.PricesTutorial.HINT_PREVENT_TUTORIAL_CLOSE_PRICES;
import static com.healthymedium.arc.paths.tutorials.SymbolTutorial.HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS;

public class LegacyMigration extends Service {

    private static final String tag = "LegacyMigration";

    public static final String TAG_LEGACY_MIGRATION_STATE = "legacyMigrationState";
    public static final String TAG_LEGACY_MIGRATION_SHOWN = "legacyMigrationShown";

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";

    public static final String ACTION_LEGACY_MIGRATION = "ACTION_LEGACY_MIGRATION";
    public static final String EXTRA_PROGRESS = "EXTRA_PROGRESS";
    public static final String EXTRA_FAILED = "EXTRA_FAILED";

    NotificationCompat.Builder builder;

    LegacyMigrationState migrationState;
    PreferencesManager preferences;
    OldPreferences oldPreferences;
    RestClient restClient;
    Gson gson;

    int progressIncrement = 0;
    int notificationId;
    int progress = 0;

    private boolean running = false;
    private boolean intentionalDestruction = false;

    public LegacyMigration() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {

        running = false;
        if(!intentionalDestruction){

            start(this);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }

        if(running) {
            broadcastProgress(progress,false);
            return START_STICKY;
        }

        String action = intent.getAction();


        switch (action) {
            case ACTION_START_SERVICE:
                running = true;
                startForegroundService();
                startMigration();
                break;
        }

        return START_STICKY;
    }

    private void startForegroundService() {


        NotificationType type = new LegacyMigrationNotification();
        NotificationUtil.createChannel(this,type);
        notificationId = type.getId();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(this, type.getChannelId())
                .setContentTitle("Installing Update")
                .setContentText("Saving your progress and adding new features.")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(notificationId, notification);
    }

    private void stopForegroundService() {

        running = false;
        stopForeground(true);
        NotificationType type = new LegacyMigrationNotification();
        NotificationUtil.removeChannel(this,type);
        intentionalDestruction = true;
        stopSelf();
    }

    private void startMigration() {

        migrationState = getState();
        preferences = PreferencesManager.getInstance();
        oldPreferences = new OldPreferences(this);
        restClient = (RestClient) Study.getRestClient();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        new Handler().post(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            progressIncrement = (int) Math.ceil(90f/getNumberOfProgressSegments());
            progress = 10;

            broadcastProgress(progress,false);

            CallbackChain chain = new CallbackChain(gson);

            if(!migrationState.hasRemovedOldNotificationChannels) {
                chain.addLink(removedOldNotificationChannels);
            }

            if(!migrationState.hasCopiedPreferences) {
                chain.addLink(copyPreferences);
            }

            if(!migrationState.hasUpdatedDeviceId) {
                OldDevice oldDevice = oldPreferences.getObject("device",OldDevice.class);
                restClient.addUpdateDeviceIdLink(chain,oldDevice.id);
                chain.addLink(deviceIdConfirmation);
            }

            if(!migrationState.hasUploadedAllZips) {

                List<File> zips = copyZipsToCache();
                if(zips==null) {
                    broadcastFailure();
                    return;
                }

                int startingIndex = migrationState.numberOfZipsUploaded;
                for(int i=startingIndex;i<zips.size();i++) {
                    restClient.addZipUploadLink(chain,zips.get(i));
                    chain.addLink(new CallbackChain.ShallowListener() {
                        @Override
                        public void onExecute(CallbackChain chain) {
                            migrationState.numberOfZipsUploaded++;
                            saveMigrationState(migrationState);
                            broadcastIncrement();
                        }
                    });
                }

                chain.addLink(new CallbackChain.ShallowListener() {
                    @Override
                    public void onExecute(CallbackChain chain) {
                        migrationState.hasUploadedAllZips = true;
                        saveMigrationState(migrationState);
                    }
                });
            }

            if(!migrationState.hasContactInfo) {
                restClient.addContactLink(chain);
                chain.addLink(contactConfirmation);
            }

            if(!migrationState.hasSessionInfo) {
                chain.addLink(sessionInfo);
                chain.addLink(sessionConfirmation);
            }

            chain.execute(new com.healthymedium.arc.api.RestClient.Listener() {
                @Override
                public void onSuccess(RestResponse response) {
                    migrationState.hasBeenCompleted = true;
                    saveMigrationState(migrationState);
                    broadcastSuccess();
                }

                @Override
                public void onFailure(RestResponse response) {
                    broadcastFailure();
                }
            });
        }
    };

    private int getNumberOfProgressSegments() {

        int segmentCount = 0;

        if(!migrationState.hasRemovedOldNotificationChannels) {
            segmentCount++;
        }
        if(!migrationState.hasCopiedPreferences) {
            segmentCount++;
        }
        if(!migrationState.hasUpdatedDeviceId) {
            segmentCount++;
        }
        if(!migrationState.hasUploadedAllZips) {
            List<String> uploadQueue = new ArrayList<>();
            if (oldPreferences.contains("uploadQueue")) {
                String[] nodeArrays = oldPreferences.getObject("uploadQueue", String[].class);
                uploadQueue = new ArrayList<>(Arrays.asList(nodeArrays));
            }
            segmentCount += (uploadQueue.size() - migrationState.numberOfZipsCopied);
            segmentCount += (uploadQueue.size() - migrationState.numberOfZipsUploaded);
        }
        if(!migrationState.hasContactInfo) {
            segmentCount++;
        }
        if(!migrationState.hasSessionInfo) {
            segmentCount++;
        }

        return segmentCount;
    }

    private List<File> copyZipsToCache() {
        List<String> uploadQueue = new ArrayList<>();
        if (oldPreferences.contains("uploadQueue")) {
            String[] nodeArrays = oldPreferences.getObject("uploadQueue", String[].class);
            uploadQueue = new ArrayList<>(Arrays.asList(nodeArrays));
        }

        File cacheDir = CacheManager.getInstance().getCacheDir();
        List<File> zips = new ArrayList<>();

        for (String filename : uploadQueue) {
            File oldFile = new File(filename);
            File newFile = new File(cacheDir, oldFile.getName());
            zips.add(newFile);
            if(!newFile.exists()) {
                boolean copied = FileUtil.copy(oldFile, newFile);
                if(!copied) {
                    if(newFile.exists()){
                        newFile.delete();
                    }
                    return null;
                }
                migrationState.numberOfZipsCopied++;
                broadcastIncrement();
            }
        }

        CacheManager.getInstance().resetCache();

        migrationState.hasCopiedZipsToCache = true;
        saveMigrationState(migrationState);
        return zips;
    }

    // callbacks to update migration state appropriately -------------------------------------------

    CallbackChain.ShallowListener removedOldNotificationChannels = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {
            Application app = (Application) Application.getInstance();
            NotificationUtil.removeUnusedChannels(app, app.getNotificationTypes());
            migrationState.hasRemovedOldNotificationChannels = true;
            saveMigrationState(migrationState);
            broadcastIncrement();
        }
    };

    CallbackChain.ShallowListener deviceIdConfirmation = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {
            migrationState.hasUpdatedDeviceId = true;
            saveMigrationState(migrationState);
            broadcastIncrement();
        }
    };

    CallbackChain.ShallowListener contactConfirmation = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {
            migrationState.hasContactInfo = true;
            saveMigrationState(migrationState);
            broadcastIncrement();
        }
    };

    CallbackChain.ShallowListener sessionConfirmation = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {
            migrationState.hasSessionInfo = true;
            saveMigrationState(migrationState);
            broadcastIncrement();
        }
    };

    CallbackChain.ShallowListener copyPreferences = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {
            int missedTests = oldPreferences.getInt("test_missed_count", 0);
            preferences.putInt(TAG_TEST_MISSED_COUNT, missedTests);

            double lastHearbeat = oldPreferences.getDouble("LAST_HEARTBEAT", 0);
            preferences.putLong(TAG_LAST_HEARTBEAT, (long) (lastHearbeat));

            Hints.load();
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_GRIDS);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS);

            OldState oldState = oldPreferences.getObject("state", OldState.class);

            Participant participant = Study.getParticipant();
            participant.load();

            ParticipantState participantState = participant.getState();
            participantState.id = oldState.arcId;
            participantState.isStudyRunning = true;
            participantState.hasBeenShownNotificationOverview = true;
            participantState.hasCommittedToStudy = 1;

            Study.getParticipant().save();

            migrationState.hasCopiedPreferences = true;
            saveMigrationState(migrationState);
        }
    };

    CallbackChain.ShallowListener sessionInfo = new CallbackChain.ShallowListener() {
        @Override
        public void onExecute(CallbackChain chain) {

            Participant participant = Study.getParticipant();
            ParticipantState state = participant.getState();

            CircadianClock clock = new CircadianClock();
            clock.setRhythms("00:00:00","08:00:00");
            state.circadianClock = clock;

            OldState oldState = oldPreferences.getObject("state", OldState.class);

            Visit visit = oldState.getCurrentVisit();
            int weeksFromStart = (visit.getVisitId()-1)*26;
            DateTime startDate = visit.getVisitStartDate().minusWeeks(weeksFromStart);
            Study.getScheduler().scheduleTests(startDate,participant);
            state.hasValidSchedule = false;
            state.studyStartDate = startDate;

            state.currentTestCycle = visit.getVisitId()-1;
            state.currentTestDay = (int) Math.floor(oldState.currentTestSession/4);
            state.currentTestSession = oldState.currentTestSession - (state.currentTestDay*4);

            int dayOffset = Days.daysBetween(visit.getVisitStartDate(),visit.getUserStartDate()).getDays();

            TestCycle currentCycle = participant.getCurrentTestCycle();
            if(currentCycle==null) {
                chain.stop();
                return;
            }
            currentCycle.shiftSchedule(dayOffset);

            TestDay currentDay = participant.getCurrentTestDay();
            if(currentDay==null) {
                chain.stop();
                return;
            }

            // if day has started, mark all tests left as missed
            if(currentDay.hasStarted()) {
                for(com.healthymedium.arc.study.TestSession session : currentDay.getTestSessions()) {
                    session.markMissed();
                }
                state.currentTestSession = 0;
                state.currentTestDay++;
                if(currentCycle.getNumberOfTestDays()<=state.currentTestDay){
                    state.currentTestDay = 0;
                    state.currentTestCycle++;
                }
            }

            participant.save();
        }

    };

    // broadcast state changes ---------------------------------------------------------------------

    private void broadcastSuccess() {
       broadcastProgress(100,false);
        stopForegroundService();
    }

    private void broadcastFailure() {
        broadcastProgress(100,true);
        stopForegroundService();
    }

    private void broadcastIncrement() {
        progress += progressIncrement;
        if(progress > 99) { // didn't seem to need this, more for insurance if someone messes with this
            progress = 99;
        }
        broadcastProgress(progress,false);
    }

    private void broadcastProgress(int progress, boolean failed) {
        builder.setProgress(100,progress,false);
        Notification notification = builder.build();
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId,notification);

        Intent resultIntent = new Intent(ACTION_LEGACY_MIGRATION);
        resultIntent.putExtra(EXTRA_FAILED, failed);
        resultIntent.putExtra(EXTRA_PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    // static methods ------------------------------------------------------------------------------

    public static void start(Context context) {

        Intent intent = new Intent(context, LegacyMigration.class);
        intent.setAction(LegacyMigration.ACTION_START_SERVICE);
        ContextCompat.startForegroundService(context, intent);
    }

    public static boolean hasBeenCompleted() {
        LegacyMigrationState state = getState();
        return state.hasBeenCompleted;
    }


    public static boolean hasScreenBeenShown() {
        return PreferencesManager.getInstance().getBoolean(TAG_LEGACY_MIGRATION_SHOWN,false);
    }

    public static void markScreenShown() {
        PreferencesManager.getInstance().putBoolean(TAG_LEGACY_MIGRATION_SHOWN,true);
    }

    public static LegacyMigrationState getState() {
        LegacyMigrationState state = PreferencesManager.getInstance().getObject(TAG_LEGACY_MIGRATION_STATE,LegacyMigrationState.class);
        if(!state.initialized) {
            OldPreferences oldPreferences = new OldPreferences(Application.getInstance());
            // if no key found (ie no old preferences), migration will be marked as complete
            state.hasBeenCompleted = !oldPreferences.getBoolean("initialized",false);
            if(state.hasBeenCompleted){
                markScreenShown();
            }
            state.initialized = true;
            saveMigrationState(state);
        }
        return state;
    }

    private static void saveMigrationState(LegacyMigrationState state) {
        PreferencesManager.getInstance().putObject(TAG_LEGACY_MIGRATION_STATE,state);
    }

}
