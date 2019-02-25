//
// NavigationManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

public class NavigationManager {

    public interface NavigationListener {
        void onBackStackChanged();
    }

    private static NavigationManager instance;
    private FragmentManager fragmentManager;
    private NavigationListener navigationListener;

    private int currentFragmentId = -1;

    private NavigationManager() {
        // Make empty constructor private
    }

    public static synchronized void initializeInstance(final FragmentManager fragmentManager) {
        instance = new NavigationManager();
        instance.fragmentManager = fragmentManager;
        instance.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = instance.fragmentManager.findFragmentById(instance.currentFragmentId);
                if (currentFragment instanceof BaseFragment) {
                }
                if (instance.navigationListener != null) {
                    instance.navigationListener.onBackStackChanged();
                }
            }
        });

    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NavigationManager.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public NavigationListener getNavigationListener() {
        return navigationListener;
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    public void open(BaseFragment fragment) {
        if (fragmentManager != null) {
            int enterTransition = fragment.getEnterTransitionRes();
            int exitTransition = fragment.getExitTransitionRes();
            int popEnterTransition = fragment.getPopEnterTransitionRes();
            int popExitTransition = fragment.getPopExitTransitionRes();
            String tag = fragment.getClass().getName() + "." + SystemClock.uptimeMillis();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(enterTransition,exitTransition,popEnterTransition,popExitTransition)
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
        }
    }

    public void popBackStack() {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
        }
    }

    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    public void clearBackStack() {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            int id = fragmentManager.getBackStackEntryAt(i).getId();
            //String tag = fragmentManager.getBackStackEntryAt(i).getName();
            /*Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).disableFragmentAnimation = true;
            }*/
            fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
