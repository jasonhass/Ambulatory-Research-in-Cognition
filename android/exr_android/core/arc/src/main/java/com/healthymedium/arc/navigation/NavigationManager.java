//
// NavigationManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.navigation;

import android.support.v4.app.FragmentManager;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;

public class NavigationManager {

    private static NavigationManager instance;
    private NavigationController defaultController;
    private NavigationController registeredController;

    private NavigationManager() {
        // Make empty constructor private
    }

    public static synchronized void initialize(final FragmentManager fragmentManager) {
        instance = new NavigationManager();
        instance.defaultController = new NavigationController(fragmentManager,R.id.content_frame);
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NavigationManager.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public FragmentManager getFragmentManager() {
        return getController().getFragmentManager();
    }

    public NavigationController.Listener getListener() {
        return getController().getListener();
    }

    public void setListener(NavigationController.Listener listener) {
        getController().setListener(listener);
    }

    public void open(BaseFragment fragment) {
        getController().open(fragment);
    }

    public void open(BaseFragment fragment, TransitionSet transition) {
        getController().open(fragment,transition);
    }

    public void popBackStack() {
        getController().popBackStack();
    }

    public int getBackStackEntryCount() {
        return getController().getBackStackEntryCount();
    }

    public void clearBackStack() {
        getController().clearBackStack();
    }

    public BaseFragment getCurrentFragment(){
        return getController().getCurrentFragment();
    }

    public void setController(NavigationController controller) {
        registeredController = controller;
    }

    public NavigationController getController() {
        if(registeredController!=null) {
            return registeredController;
        }
        return defaultController;
    }

    public void removeController() {
        registeredController = null;
    }

}
