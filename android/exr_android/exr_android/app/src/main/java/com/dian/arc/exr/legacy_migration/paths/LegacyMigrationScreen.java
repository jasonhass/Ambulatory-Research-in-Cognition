//
// LegacyMigrationScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.paths;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dian.arc.exr.Application;
import com.dian.arc.exr.R;
import com.dian.arc.exr.legacy_migration.LegacyMigration;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.navigation.NavigationManager;

@SuppressLint("ValidFragment")
public class LegacyMigrationScreen extends BaseFragment {

    View rootView;
    TextView textViewHeader;
    TextView textViewSubheader;
    ScrollView scrollView;
    Button buttonNext;

    LinearLayout linearLayoutProgress;
    TextView textViewProgressBar;
    ProgressBar progressBar;

    public LegacyMigrationScreen() {
        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(Application.getInstance());
        }
        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_legacy_migration, container, false);
        textViewHeader = rootView.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(getString(R.string.legacy_migration_header)));
        textViewHeader.setTypeface(Fonts.robotoMedium);

        textViewSubheader = rootView.findViewById(R.id.textViewSubHeader);
        textViewSubheader.setText(Html.fromHtml(getString(R.string.legacy_migration_body)));

        scrollView = rootView.findViewById(R.id.scrollView);

        buttonNext = rootView.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNext.setEnabled(false);
                buttonNext.animate().alpha(0);
                scrollView.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        LegacyMigration.markScreenShown();
                        NavigationManager.getInstance().open(new SplashScreen(false));
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });

        textViewProgressBar = rootView.findViewById(R.id.textViewProgressBar);
        textViewProgressBar.setTypeface(Fonts.georgiaItalic);

        progressBar = rootView.findViewById(R.id.progressBar);
        linearLayoutProgress = rootView.findViewById(R.id.linearLayoutProgress);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        getMainActivity().getWindow().setBackgroundDrawableResource(com.healthymedium.arc.library.R.drawable.core_background);
    }

    @Override
    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(LegacyMigration.ACTION_LEGACY_MIGRATION));
        if(!LegacyMigration.hasBeenCompleted()){
            LegacyMigration.start(getContext());
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int progress = intent.getIntExtra(LegacyMigration.EXTRA_PROGRESS,0);
            final boolean failed = intent.getBooleanExtra(LegacyMigration.EXTRA_FAILED,true);


            progressBar.setProgress(progress);

            if(progress!=100) {
                return;
            }

            linearLayoutProgress.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    linearLayoutProgress.setVisibility(View.GONE);
                    if(!failed) {
                        buttonNext.setVisibility(View.VISIBLE);
                    } else {
                        NavigationManager.getInstance().open(new LegacyMigrationErrorScreen());
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    };



}
