package com.dian.arc.libs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.HomeWatcher;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.PreferencesManager;

import org.joda.time.DateTime;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS = 101;

    boolean checkAbandonment = false;

    DateTime lastTime;
    HomeWatcher homeWatcher;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermissions(this)){
            setup();
        }
    }

    public void setup(){
        homeWatcher = new HomeWatcher(this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                Application.activityPaused();
                checkAbandonment = false;
                if(ArcManager.getInstance() != null){
                    if(ArcManager.getInstance().isCurrentlyInTestSession()){
                        checkAbandonment = true;
                        lastTime = DateTime.now();
                    }
                }
            }
            @Override
            public void onHomeLongPressed() {

            }
        });
        homeWatcher.startWatch();


        NavigationManager.initializeInstance(getSupportFragmentManager());
        PreferencesManager.initialize(this);
        if(PreferencesManager.getInstance().contains("language") || !Config.CHOOSE_LOCALE){
            String language = PreferencesManager.getInstance().getString("language","en");
            String country = PreferencesManager.getInstance().getString("country","US");
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language,country));
            res.updateConfiguration(conf, res.getDisplayMetrics());

            NavigationManager.getInstance().open(new SplashScreen());
        } else {
            NavigationManager.getInstance().open(new SetupLocaleFragment());
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if(homeWatcher != null){
                homeWatcher.stopWatch();
                homeWatcher = null;
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if(homeWatcher != null){
            homeWatcher.stopWatch();
            homeWatcher = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.activityResumed();
        if(checkAbandonment){
            if(lastTime.plusMinutes(5).isBeforeNow()){
                ArcManager.getInstance().markTestAbandoned();
                ArcManager.getInstance().nextFragment(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.activityPaused();
        checkAbandonment = false;
        if(ArcManager.getInstance() != null){
            if(ArcManager.getInstance().isCurrentlyInTestSession()){
                checkAbandonment = true;
                lastTime = DateTime.now();
            }
        }
    }

    static public boolean checkPermissions(final AppCompatActivity activity) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean read = (activity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
            boolean write = (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
            if (!read || !write) {
                activity.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS);
            }
            result = read && write;
        }
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
