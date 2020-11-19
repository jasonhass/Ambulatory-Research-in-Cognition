//
// LandingScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.paths.informative.EarningsDetailsScreen;
import com.healthymedium.arc.ui.BottomNavigationView;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.SlidingNavigationController;
import com.healthymedium.arc.navigation.NavigationManager;

import static com.healthymedium.arc.ui.BottomNavigationView.TAG_EARNINGS;
import static com.healthymedium.arc.ui.BottomNavigationView.TAG_HOME;
import static com.healthymedium.arc.ui.BottomNavigationView.TAG_PROGRESS;
import static com.healthymedium.arc.ui.BottomNavigationView.TAG_RESOURCES;

@SuppressLint("ValidFragment")
public class LandingScreen extends BaseFragment {

  SlidingNavigationController navigationController;
  BottomNavigationView bottomNavigationView;
  TransitionSet transitionSet;

  HomeScreen home;
  ProgressScreen progress;
  EarningsScreen earnings;
  ResourcesScreen resources;

  public LandingScreen() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_landing, container, false);

    bottomNavigationView = view.findViewById(R.id.navigation);
    transitionSet = new TransitionSet();

    home = new HomeScreen();
    progress = new ProgressScreen();
    earnings = new EarningsScreen();
    resources = new ResourcesScreen();

    home.setBottomNavigationView(bottomNavigationView);

    FragmentManager fragmentManager = getChildFragmentManager();
    navigationController = new SlidingNavigationController(fragmentManager,R.id.landing_frame);
    navigationController.addFragmentToSet(home);
    navigationController.addFragmentToSet(progress);
    navigationController.addFragmentToSet(earnings);
    navigationController.addFragmentToSet(new EarningsDetailsScreen());
    navigationController.addFragmentToSet(resources);
    navigationController.open(home,transitionSet);

    bottomNavigationView.setHomeSelected();
    bottomNavigationView.setListener(new BottomNavigationView.Listener() {
      @Override
      public void onSelected(int tag) {
        switch (tag) {
          case TAG_HOME:
            navigationController.open(home,transitionSet);
            break;
          case TAG_PROGRESS:
            navigationController.open(progress,transitionSet);
            break;
          case TAG_EARNINGS:
            navigationController.open(earnings,transitionSet);
            break;
          case TAG_RESOURCES:
            navigationController.open(resources,transitionSet);
            break;
        }
      }
    });

    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    NavigationManager.getInstance().removeController();
  }

  @Override
  public void onResume() {
    super.onResume();
    NavigationManager.getInstance().setController(navigationController);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setPadding(0,0,0,0);
  }

}
