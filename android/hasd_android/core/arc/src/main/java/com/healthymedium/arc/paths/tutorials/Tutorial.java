//
// Tutorial.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.TutorialProgressView;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class Tutorial extends BaseFragment {

    protected int shortAnimationDuration;

    protected HintHighlighter welcomeHighlight;
    protected HintPointer welcomeHint;
    protected HintHighlighter quitHighlight;
    protected HintPointer quitHint;

    protected TutorialProgressView progressView;
    protected ImageView closeButton;
    protected View loadingView;

    protected LinearLayout progressBar;
    protected int progressIncrement;
    protected int progress = 0;

    protected ImageView checkmark;
    protected TextView textViewComplete;
    protected Button endButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        loadingView.animate()
                .setStartDelay(400)
                .setDuration(400)
                .translationYBy(-loadingView.getHeight());
    }

    public Tutorial() {
    }

    // Display the hints for the progress bar and quit button
    protected void showProgressTutorial(final String tag, final Runnable nextSection) {
        welcomeHighlight.addTarget(progressView, 10, 2);
        welcomeHint.setText(ViewUtil.getString(R.string.popup_tutorial_welcome));

        View.OnClickListener welcomeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeHint.dismiss();
                welcomeHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        welcomeHint.addButton(ViewUtil.getString(R.string.popup_gotit), welcomeListener);
        welcomeHighlight.show();
        welcomeHint.show();
    }

    protected void showCloseTutorial(final String tag, final Runnable nextSection) {
        quitHighlight.addTarget(closeButton, 50, 10);
        quitHint.setText(ViewUtil.getString(R.string.popup_tutorial_quit));

        View.OnClickListener quitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitHint.dismiss();
                quitHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        quitHint.addButton(ViewUtil.getString(R.string.popup_gotit), quitListener);
        quitHighlight.show();
        quitHint.show();
    }


    protected void fadeInView(View view, Float opacity) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(opacity)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    protected void fadeOutView(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    // Displays the tutorial complete screen
    protected void showComplete() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeInView(checkmark, 1f);
                fadeInView(textViewComplete, 1f);
                fadeInView(endButton, 1f);

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endButton.setEnabled(false);
                        exit();
                    }
                });
            }
        }, 1000);
    }

    protected void exit(){
        loadingView.animate()
                .setDuration(400)
                .translationY(0);
        progressBar.animate()
                .setDuration(400)
                .alpha(0.0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationManager.getInstance().popBackStack();
            }
        },1200);
    }

    protected void incrementProgress(){
        progress += progressIncrement;
        progressView.setProgress(progress,true);
    }
}
