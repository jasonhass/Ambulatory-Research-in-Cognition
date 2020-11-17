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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.ViewUtil;

public class BaseFragment extends Fragment {

    TransitionSet transitions = new TransitionSet();
    String tag = getClass().getSimpleName();

    boolean backAllowed = false;
    boolean backInStudy = false;

    // methods related to enabling back press from a base fragment ---------------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0, ViewUtil.getStatusBarHeight(),0,ViewUtil.getNavBarHeight());
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

    // convenience getters -------------------------------------------------------------------------

    public String getSimpleTag(){
        return tag;
    }

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

    public Application getApplication(){
        return (Application)getMainActivity().getApplication();
    }

    // convenience methods for manipulating the keyboard -------------------------------------------

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

    // part of the magic sewage system -------------------------------------------------------------

    public Object onDataCollection(){
        return null;
    }

    // methods relating to transitions -------------------------------------------------------------

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, final int nextAnim) {
        if(nextAnim==0){
            return null;
        }

        Animation anim = AnimationUtils.loadAnimation(getMainActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(nextAnim==transitions.enter){
                    onEnterTransitionStart(false);
                } else if(nextAnim==transitions.exit){
                    onExitTransitionStart(false);
                } else if(nextAnim==transitions.popEnter){
                    onEnterTransitionStart(true);
                } else if(nextAnim==transitions.popExit){
                    onExitTransitionStart(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(nextAnim==transitions.enter){
                    onEnterTransitionEnd(false);
                } else if(nextAnim==transitions.exit){
                    onExitTransitionEnd(false);
                } else if(nextAnim==transitions.popEnter){
                    onEnterTransitionEnd(true);
                } else if(nextAnim==transitions.popExit){
                    onExitTransitionEnd(true);
                }
            }
        });

        return anim;
    }


    protected void onEnterTransitionStart(boolean popped) {

    }

    protected void onEnterTransitionEnd(boolean popped) {

    }

    protected void onExitTransitionStart(boolean popped) {

    }

    protected void onExitTransitionEnd(boolean popped) {

    }

    public TransitionSet getTransitionSet() {
        return transitions;
    }

    public void setTransitionSet(TransitionSet transitions) {
        if(transitions!=null){
            this.transitions = transitions;
        }
    }

    // debug ---------------------------------------------------------------------------------------

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
}
