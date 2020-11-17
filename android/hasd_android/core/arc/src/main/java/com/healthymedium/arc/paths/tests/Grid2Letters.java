//
// Grid2Letters.java
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
import com.healthymedium.arc.core.TimedDialogMultipart;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Grid2LetterView;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Grid2Letters extends BaseFragment {

    GridTestPathData gridTest;
    GridTestPathData.Section section;

    GridLayout gridLayout;
    int columnCount;
    int rowCount;

    TimedDialogMultipart dialog;
    Handler handler;
    boolean paused;

    int eCount = 0;
    int fCount = 0;

    public Grid2Letters() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_letters, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);
        columnCount = gridLayout.getColumnCount();
        rowCount = gridLayout.getRowCount();

        List<Integer> indices = setupTest(
                new Random(SystemClock.elapsedRealtime()),
                rowCount,
                columnCount);

        for(Integer index: indices){
            getView(index / rowCount,index % rowCount).setF();
        }

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handler.postDelayed(runnable,8000);

        adjustGridLayout();

        return view;
    }

    private Grid2LetterView getView(int row, int col) {
        return (Grid2LetterView)gridLayout.getChildAt((columnCount*row)+col);
    }

    public static List<Integer> setupTest(Random random, int rowCount, int columnCount) {

        // init variables
        List<Integer> items = new ArrayList<>();
        int gap = rowCount*columnCount;
        int lower = 0;
        int picked = 0;

        // add bounds
        items.add(-1);
        items.add(gap);

        // loop
        while(picked < 8){
            int var = random.nextInt(gap-1)+1;
            items.add(var+lower);
            Collections.sort(items);
            gap = 0;
            lower = 0;
            for(int i=0;i<items.size()-1;i++){
                int tempGap = items.get(i+1)-items.get(i);
                if(tempGap > gap){
                    gap = tempGap;
                    lower = items.get(i);
                }
            }
            picked++;
        }
        Collections.sort(items);

        // remove bounds
        items.remove(0);
        items.remove(items.size()-1);

        // exit, stage left
        return items;
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
            viewHeight = (int) (displayWidth / 0.75f);
        }

        viewHeight -= ViewUtil.dpToPx(16);

        float letterRatio = ((float)columnCount)/((float)rowCount);
        int width = (int) (letterRatio*(viewHeight));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(28);
        layoutParams.bottomMargin = ViewUtil.dpToPx(4);
        layoutParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(layoutParams);
    }

    private void calculateCounts() {
        if(gridLayout==null){
            return;
        }

        for(int i=0; i<rowCount; i++) {
            for(int j=0; j<columnCount; j++) {
                Grid2LetterView view = getView(i,j);
                if(!view.isSelected()){
                    continue;
                }
                if(view.isF()){
                    fCount++;
                } else {
                    eCount++;
                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markDistractionDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if(dialog!=null){
            if(dialog.isVisible()){
                dialog.setOnDialogDismissListener(null);
                dialog.dismiss();
            }
        }
        paused = true;
    }

    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {

            calculateCounts();

            if(isVisible()){
                dialog = new TimedDialogMultipart(
                        ViewUtil.getHtmlString(R.string.grids_overlay3),
                        ViewUtil.getHtmlString(R.string.grids_overlay3_pt2),
                        3000,
                        6000
                );
                dialog.setOnDialogDismissListener(new TimedDialogMultipart.OnDialogDismiss() {
                    @Override
                    public void dismiss() {
                        section.setECount(eCount);
                        section.setFCount(fCount);
                        gridTest.updateCurrentSection(section);
                        Study.setCurrentSegmentData(gridTest);
                        Study.openNextFragment();
                    }
                });
                dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());
            }
        }
    };

}
