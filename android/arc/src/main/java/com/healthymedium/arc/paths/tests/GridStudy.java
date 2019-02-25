//
// GridStudy.java
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
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.TimedDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridStudy extends BaseFragment {

    boolean paused;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

    TimedDialog dialog;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isVisible()){
                Study.openNextFragment();
            }
        }
    };

    public GridStudy() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_study, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        dialog = new TimedDialog(getString(R.string.grid_transition),2000);
        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                setupTest();
                handler = new Handler();
                handler.postDelayed(runnable,3000);
            }
        });
        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();


        return view;
    }

    private ImageView getImageView(int row, int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!gridTest.hasStarted()){
            gridTest.markStarted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!paused) {
            gridTest.startNewSection();
            section = gridTest.getCurrentSection();
        } else {
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog.isVisible()){
            dialog.setOnDialogDismissListener(null);
            dialog.dismiss();
        }
        paused = true;
    }

    private void setupTest(){
        List<GridTestPathData.Image> images = new ArrayList<>();

        Random random = new Random(SystemClock.currentThreadTimeMillis());
        int row1 = random.nextInt(5);
        int row2 = random.nextInt(5);
        int row3 = random.nextInt(5);
        while(row1 == row2){
            row2 = random.nextInt(5);
        }
        while(row3 == row1 || row3 == row2){
            row3 = random.nextInt(5);
        }

        int col1 = random.nextInt(5);
        int col2 = random.nextInt(5);
        int col3 = random.nextInt(5);
        while(col1 == col2){
            col2 = random.nextInt(5);
        }
        while(col3 == col1 || col3 == col2){
            col3 = random.nextInt(5);
        }

        getImageView(row1,col1).setImageResource(R.drawable.phone);
        getImageView(row2,col2).setImageResource(R.drawable.pen);
        getImageView(row3,col3).setImageResource(R.drawable.key);

        section.markSymbolsDisplayed();

        images.add(new GridTestPathData.Image(row1,col1,"phone"));
        images.add(new GridTestPathData.Image(row2,col2,"pen"));
        images.add(new GridTestPathData.Image(row3,col3,"key"));
        section.setImages(images);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

}
