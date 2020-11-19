//
// GridTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;

import java.util.ArrayList;
import java.util.List;

public class GridTest extends BaseFragment {

    boolean paused;
    int selectedCount = 0;
    public boolean second = false;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;
    List<View> selections;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            handlerInteraction.removeCallbacks(runnable);
            if(isVisible()){
                updateSection();
                Study.openNextFragment();
            }
        }
    };

    public GridTest() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_test, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                boolean preventTouch = false;
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        if (view.getTag(R.id.tag_color).equals(R.color.gridSelected)) {
                            view.setTag(R.id.tag_color,R.color.gridNormal);
                            if(selections.contains(view)){
                                view.setTag(R.id.tag_time,0);
                                selections.remove(view);
                            }
                            selectedCount--;
                        } else if (selectedCount < 3) {
                            selectedCount++;
                            view.setTag(R.id.tag_time, System.currentTimeMillis());
                            view.setTag(R.id.tag_color,R.color.gridSelected);
                            selections.add(view);
                        } else {
                            preventTouch = true;
                        }

                        handler.removeCallbacks(runnable);
                        if(selectedCount >= 3){
                            handler.postDelayed(runnable,2000);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return preventTouch;
            }
        };

        // Init each grid-cell as unselected + add onTouchlistener
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setTag(R.id.tag_color,R.color.gridNormal);
            gridLayout.getChildAt(i).setOnTouchListener(listener);
        }

        selections = new ArrayList<>();
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();


        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markTestGridDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            updateSection();
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerInteraction.removeCallbacks(runnable);
        handler.removeCallbacks(runnable);
        paused = true;
    }

    private void updateSection(){
        int size = gridLayout.getChildCount();
        List<GridTestPathData.Tap> choices = new ArrayList<>();
        for(int i=0;i<size;i++){
            if(selections.contains(gridLayout.getChildAt(i))){
                View view = gridLayout.getChildAt(i);
                choices.add(new GridTestPathData.Tap(i / 5,i % 5,(long)view.getTag(R.id.tag_time)));
            }
        }
        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

}
