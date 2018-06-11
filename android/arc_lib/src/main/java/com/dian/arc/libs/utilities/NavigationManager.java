package com.dian.arc.libs.utilities;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dian.arc.libs.BaseFragment;
import com.dian.arc.libs.R;

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

    private void popAllFragments() {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            int id = fragmentManager.getBackStackEntryAt(i).getId();
            String tag = fragmentManager.getBackStackEntryAt(i).getName();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).disableFragmentAnimation = true;
            }
            fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void open(Fragment fragment) {
        if (fragmentManager != null) {
            String tag = fragment.getClass().getName() + "." + SystemClock.uptimeMillis();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
            currentFragmentId = fragment.getId();
        }
    }

    public void add(Fragment fragment) {
        if (fragmentManager != null) {
            String tag = fragment.getClass().getName() + "." + SystemClock.uptimeMillis();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                    .add(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
            currentFragmentId = fragment.getId();
        }
    }

    public void openAsRoot(Fragment fragment) {
        popAllFragments();
        open(fragment);
    }

}
