package com.dian.arc.libs;

import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.dian.arc.libs.utilities.ContextSingleton;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.PreferencesManager;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Locale;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextSingleton.initialize(this);
        JodaTimeAndroid.init(this);
        FontFactory.initialize(this);
        FontFactory.getInstance().setDefaultFont(FontFactory.tahoma);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(PreferencesManager.getInstance() != null){
            if(PreferencesManager.getInstance().contains("language")){
                String language = PreferencesManager.getInstance().getString("language","en");
                String country = PreferencesManager.getInstance().getString("country","US");
                newConfig.setLocale(new Locale(language,country));
                getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
            }
        }
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible = false;

}
