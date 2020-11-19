//
// GridLetters.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.TimedDialog;
import com.healthymedium.arc.core.TimedDialogMultipart;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GridLetters extends BaseFragment {

    boolean paused;
    GridLayout gridLayout;
    protected  GridTestPathData gridTest;
    protected GridTestPathData.Section section;
    protected int eCount = 0;
    protected int fCount = 0;

    private TextView textViewTapFsLabel;

    protected TimedDialogMultipart dialog;
    Handler handler;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isVisible()){

                dialog = new TimedDialogMultipart (
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
                dialog.show(getFragmentManager(),TimedDialog.class.getSimpleName());
            }
        }
    };

    public GridLetters() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_letters, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        textViewTapFsLabel = view.findViewById(R.id.textView32);
        textViewTapFsLabel.setText(ViewUtil.getHtmlString(R.string.grids_subheader_fs));

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        boolean isE = ((TextView)view).getText().toString().equals("E");
                        if(view.getTag() == null){
                            view.setTag(true);
                            if(isE){
                                eCount++;
                            } else {
                                fCount++;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                            return false;
                        }
                        if(view.getTag().equals(false)) {
                            view.setTag(true);
                            if(isE){
                                eCount++;
                            } else {
                                fCount++;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                        } else {
                            view.setTag(false);
                            if(isE){
                                eCount--;
                            } else {
                                fCount--;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };


        Typeface font = Fonts.georgia;
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setOnTouchListener(listener);
            ((TextView)gridLayout.getChildAt(i)).setTypeface(font);
        }
        Random random = new Random(SystemClock.elapsedRealtime());
        List<Integer> items = new ArrayList<>();
        items.add(-1);
        items.add(60);
        int gap = 60;
        int lower = 0;
        int picked = 0;
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
        items.remove(0);
        items.remove(items.size()-1);

        for(Integer var: items){
            getTextView(var / 6,var % 6).setText("F");
        }

        handler = new Handler();
        handler.postDelayed(runnable,8000);
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();
        return view;
    }

    private TextView getTextView(int row, int col){
        return (TextView)gridLayout.getChildAt((6*row)+col);
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
}
