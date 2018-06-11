package com.dian.arc.libs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.ContextSingleton;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.NotificationManager;
import com.dian.arc.libs.utilities.PreferencesManager;
import com.dian.arc.libs.utilities.PriceManager;
import com.dian.arc.libs.utilities.ScreenUtility;
import com.dian.arc.libs.utilities.SignatureManager;

public class SplashScreen extends BaseFragment {

    boolean paused;

    public SplashScreen() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        AsyncLoader loader = new AsyncLoader();
        loader.execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            paused = false;
            if(ArcManager.getInstance() != null) {
                exit();
            }
        }
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
            Context context = ContextSingleton.getContext();
            PreferencesManager.initialize(context);
            SignatureManager.initialize(context);
            NotificationManager.initialize(context);
            PriceManager.initialize(getContext());
            ArcManager.initialize(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            if(Application.isActivityVisible()){
                exit();
            }
        }
    }

    private void exit(){
        if(getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            if (ScreenUtility.isBiggerThanMinimumInches(1.963, 3.215, getContext())) {
                ArcManager.getInstance().start();
            } else {
                NavigationManager.getInstance().open(new IncompatibleFragment());
            }
        }
    }

}
