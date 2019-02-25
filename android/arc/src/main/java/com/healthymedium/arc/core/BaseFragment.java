//
// BaseFragment.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {

    int enterTransition = 0;
    int exitTransition = 0;
    int popEnterTransition = 0;
    int popExitTransition = 0;

    boolean backAllowed = false;
    boolean backInStudy = false;

    String tag = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tag = getClass().getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().enableBackPress(backAllowed,backInStudy);
    }

    public void allowBackPress(boolean inStudy){
        backAllowed = true;
        backInStudy = inStudy;
    }

    public boolean isBackAllowed(){
        return backAllowed;
    }

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

    public Application getApplication(){
        return (Application)getMainActivity().getApplication();
    }

    public void hideKeyboard() {
        Activity activity = getMainActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public Object onDataCollection(){
        return null;
    }

    public void setupDebug(View view, int id){
        if(Config.DEBUG_DIALOGS) {
            final int[] count = {0};
            view.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count[0] <= 0) {
                        count[0]++;
                    } else {
                        count[0] = 0;
                        DebugDialog.launch();
                    }
                }
            });
        }
    }

    public void setEnterTransitionRes(@AnimRes int id, @AnimRes int popId){
        enterTransition = id;
        popEnterTransition = popId;
    }

    public int getEnterTransitionRes(){
        return enterTransition;
    }

    public int getPopEnterTransitionRes() {
        return popEnterTransition;
    }

    public void setExitTransitionRes(@AnimRes int id, @AnimRes int popId){
        exitTransition = id;
        popExitTransition = popId;
    }

    public int getExitTransitionRes(){
        return exitTransition;
    }

    public int getPopExitTransitionRes() {
        return popExitTransition;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, final int nextAnim) {
        if(nextAnim==0){
            return null;
        }
        Animation anim = AnimationUtils.loadAnimation(getMainActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(nextAnim==enterTransition){
                    onEnterTransitionStart(false);
                } else if(nextAnim==exitTransition){
                    onExitTransitionStart(false);
                } else if(nextAnim==popEnterTransition){
                    onEnterTransitionStart(true);
                } else if(nextAnim==popExitTransition){
                    onExitTransitionStart(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(nextAnim==enterTransition){
                    onEnterTransitionEnd(false);
                } else if(nextAnim==exitTransition){
                    onExitTransitionEnd(false);
                } else if(nextAnim==popEnterTransition){
                    onEnterTransitionEnd(true);
                } else if(nextAnim==popExitTransition){
                    onExitTransitionEnd(true);
                }
            }
        });

        return anim;
    }


    protected void onEnterTransitionStart(boolean popped) {
        Log.v(tag,"onEnterTransitionStart");
    }

    protected void onEnterTransitionEnd(boolean popped) {
        Log.v(tag,"onEnterTransitionEnd");
    }

    protected void onExitTransitionStart(boolean popped) {
        Log.v(tag,"onExitTransitionStart");
    }

    protected void onExitTransitionEnd(boolean popped) {
        Log.v(tag,"onExitTransitionEnd");
    }

}
