//
// Grid2Study.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.TimedDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid2Study extends BaseFragment {

    boolean paused;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

    int rowCount;
    int columnCount;

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

    public Grid2Study() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_study, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);
        columnCount = gridLayout.getColumnCount();
        rowCount = gridLayout.getRowCount();

        for(int i=0; i<rowCount; i++) {
            for(int j=0; j<columnCount; j++) {
                getView(i,j).setSelectable(false);
            }
        }

        dialog = new TimedDialog(ViewUtil.getHtmlString(R.string.grids_overlay1),2000);
        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                Random random = new Random(SystemClock.currentThreadTimeMillis());
                List<GridTestPathData.Image> images = setupTest(random,rowCount,columnCount);
                displayImages(images);

                handler = new Handler();
                handler.postDelayed(runnable,3000);
            }
        });
        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();

        adjustGridLayout();

        return view;
    }

    private Grid2BoxView getView(int row, int col) {
        return (Grid2BoxView)gridLayout.getChildAt((columnCount*row)+col);
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
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        paused = true;
    }

    private void adjustGridLayout(){
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        int auxHeight = ViewUtil.dpToPx(124);
        int viewHeight = displayHeight-ViewUtil.getStatusBarHeight()-ViewUtil.getNavBarHeight()-auxHeight;

        float aspectRatio = ((float)displayWidth)/((float)viewHeight);
        if(aspectRatio < 0.75f) {
            return;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) (0.75f * viewHeight),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(26);
        layoutParams.bottomMargin = ViewUtil.dpToPx(2);
        layoutParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(layoutParams);
    }

    public static List<GridTestPathData.Image> setupTest(Random random, int rowCount, int columnCount){
        List<GridTestPathData.Image> images = new ArrayList<>();

        int row1 = random.nextInt(rowCount);
        int row2 = random.nextInt(rowCount);
        int row3 = random.nextInt(rowCount);
        while(row1 == row2){
            row2 = random.nextInt(rowCount);
        }
        while(row3 == row1 || row3 == row2){
            row3 = random.nextInt(rowCount);
        }

        int col1 = random.nextInt(columnCount);
        int col2 = random.nextInt(columnCount);
        int col3 = random.nextInt(columnCount);
        while(col1 == col2){
            col2 = random.nextInt(columnCount);
        }
        while(col3 == col1 || col3 == col2){
            col3 = random.nextInt(columnCount);
        }

        images.add(new GridTestPathData.Image(row1,col1,GridTestPathData.Image.PHONE));
        images.add(new GridTestPathData.Image(row2,col2,GridTestPathData.Image.PEN));
        images.add(new GridTestPathData.Image(row3,col3,GridTestPathData.Image.KEY));

        return images;
    }

    private void displayImages(List<GridTestPathData.Image> images) {

        for(GridTestPathData.Image image : images) {
            getView(image.row(),image.column()).setImage(image.id());
        }

        section.markSymbolsDisplayed();
        section.setImages(images);
        gridTest.updateCurrentSection(section);

        Study.setCurrentSegmentData(gridTest);
    }
}
