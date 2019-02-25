//
// SplashScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.Locale;

public class SplashScreen extends BaseFragment {

    boolean paused = false;
    boolean ready = false;

    public SplashScreen() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.core_fragment_splash, container, false);
        AsyncLoader loader = new AsyncLoader();
        loader.execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && ready) {
            if(Study.isValid()){
                exit();
            }
        }
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }


    private class AsyncLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = getApplication();
            getMainActivity().setupHomeWatcher();
            getMainActivity().setupKeyboardWatcher();

            String language = PreferencesManager.getInstance().getString("language","en");
            String country = PreferencesManager.getInstance().getString("country","US");
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language,country));
            res.updateConfiguration(conf, res.getDisplayMetrics());

            FontFactory.initialize(context);
            Study.initialize(context);
            NotificationManager.initialize(context);

            getApplication().registerStudyComponents();

            if(!Fonts.areLoaded()){
                Fonts.load();
                FontFactory.getInstance().setDefaultFont(Fonts.roboto);
                FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
            }
            Study.getInstance().load();
            Study.getInstance().run();

            //MigrationUtil.checkForUpdate(context);
            //SignatureManager.initialize(context);
            //PriceManager.initialize(getContext());
            ready = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            if(getMainActivity()==null) {
                return;
            }

            getMainActivity().getWindow().setBackgroundDrawableResource(R.drawable.core_background);

            if(!paused){
                exit();
            }
        }
    }

    private void exit(){
        if(getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            Study.getInstance().openNextFragment();
        }
    }

}
