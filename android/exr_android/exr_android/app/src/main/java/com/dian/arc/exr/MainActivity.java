//
// MainActivity.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.dian.arc.exr.legacy_migration.LegacyMigration;
import com.dian.arc.exr.legacy_migration.paths.LegacyMigrationScreen;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends com.healthymedium.arc.core.MainActivity {

    public static final int PERMISSIONS = 101;

    @Override
    public void setup(){
        if(!checkPermissions(this)) {
            return;
        }

        NavigationManager.initialize(getSupportFragmentManager());

        if(PreferencesManager.getInstance().getBoolean(Application.TAG_RESTART,false)){
            PreferencesManager.getInstance().putBoolean(Application.TAG_RESTART,false);

            Phrase phrase = new Phrase(R.string.low_memory_restart_dialogue);
            phrase.replace(R.string.token_app_name, R.string.app_name);

            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(getString(R.string.low_memory_restart_dialogue_header))
                    .setMessage(phrase.toString())
                    .setPositiveButton(getString(R.string.button_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        boolean migrationCompleted = LegacyMigration.hasBeenCompleted();
        boolean migrationScreenShown = LegacyMigration.hasScreenBeenShown();
        if(!migrationCompleted || !migrationScreenShown) {
            NavigationManager.getInstance().open(new LegacyMigrationScreen());
            return;
        }

        if(PreferencesManager.getInstance().contains(Locale.TAG_LANGUAGE) || !Config.CHOOSE_LOCALE) {
            NavigationManager.getInstance().open(new SplashScreen());
            return;
        }

        List<Locale> locales = Application.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        for(Locale locale : locales) {
            options.add(locale.getLabel());
        }

        QuestionLanguagePreference fragment = new QuestionLanguagePreference();
        NavigationManager.getInstance().open(fragment);
    }

    static public boolean checkPermissions(final AppCompatActivity activity) {
        boolean read = (activity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
        boolean write = (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
        if (!read || !write) {
            activity.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS);
        }
        boolean result = read && write;
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS) {
            if (checkPermissions(this)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setup();
                    }
                },100);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
