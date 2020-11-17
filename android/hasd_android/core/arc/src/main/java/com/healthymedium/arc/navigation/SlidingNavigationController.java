//
// SlidingNavigationController.java
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
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;

import java.util.ArrayList;
import java.util.List;

public class SlidingNavigationController extends NavigationController {

    List<BaseFragment> fragments = new ArrayList<>();
    int currentIndex;

    public SlidingNavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        super(fragmentManager,containerViewId);
    }

    public void setFragmentSet(List<BaseFragment> fragments) {
        this.fragments = fragments;
    }

    public void addFragmentToSet(BaseFragment fragment) {
        this.fragments.add(fragment);
    }

    @Override
    public void open(BaseFragment fragment) {
        if(!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
        int index = fragments.indexOf(fragment);
        if(index<currentIndex) {
            openLeft(fragment);
        } else {
            openRight(fragment);
        }
        currentIndex = index;
    }

    private void openLeft(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_left;
        set.popEnter =  R.anim.slide_in_right;
        set.exit = R.anim.slide_out_right;
        set.popExit =  R.anim.slide_out_left;
        open(fragment,set);
    }

    private void openRight(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_right;
        set.popEnter =  R.anim.slide_in_left;
        set.exit = R.anim.slide_out_left;
        set.popExit =  R.anim.slide_out_right;
        open(fragment,set);
    }



}
