//
// NavigationController.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.navigation;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;


import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.misc.TransitionSet;


public class NavigationController {

    protected static String tag = "NavigationController";

    protected FragmentManager fragmentManager;
    protected Listener listener;

    protected int currentFragmentId = -1;
    protected int containerViewId = -1;

    public NavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void removeListener() {
        listener = null;
    }

    public void open(BaseFragment fragment) {
        if (fragmentManager != null) {
            TransitionSet transitions = fragment.getTransitionSet();
            String tag = fragment.getSimpleTag();

            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void open(BaseFragment fragment, TransitionSet transitions) {
        if (fragmentManager != null) {
            String tag = fragment.getSimpleTag();

            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void popBackStack() {
        if (fragmentManager != null) {

            fragmentManager.popBackStack();
            if(listener!=null){
                listener.onPopBack();
            }
        }
    }

    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    public void clearBackStack() {
        try {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < count; i++) {
                int id = fragmentManager.getBackStackEntryAt(i).getId();
                fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (IllegalStateException e) {
            // Log exception
        }
    }

    public BaseFragment getCurrentFragment(){
        return (BaseFragment) fragmentManager.findFragmentById(currentFragmentId);
    }

    public interface Listener {
        void onOpen();
        void onPopBack();
    }

}
