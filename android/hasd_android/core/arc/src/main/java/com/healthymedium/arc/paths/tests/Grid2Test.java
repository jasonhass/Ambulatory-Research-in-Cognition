//
// Grid2Test.java
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
import android.support.annotation.DrawableRes;
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
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.ui.Grid2ChoiceDialog;
import com.healthymedium.arc.ui.base.PointerDrawable;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class Grid2Test extends BaseFragment {

    boolean paused;

    boolean phoneSelected = false;
    boolean keySelected = false;
    boolean penSelected = false;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

    Button button;
    Grid2ChoiceDialog dialog;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            openNextFragment();
        }
    };

    public Grid2Test() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_test, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        Grid2BoxView.Listener listener = new Grid2BoxView.Listener() {
            @Override
            public void onSelected(final Grid2BoxView view) {
                handler.removeCallbacks(runnable);

                if(dialog!=null) {
                    if (dialog.isAttachedToWindow()) {
                        dialog.dismiss();
                        if(view.getImage()==0){
                            view.setSelected(false);
                        }
                        enableGrids();
                        return;
                    }
                }

                disableGrids(view);

                int pointerPosition = determinePointerPosition(view);
                view.setSelected(true);

                dialog = new Grid2ChoiceDialog(
                        getMainActivity(),
                        view,
                        pointerPosition);

                dialog.setAnimationDuration(50);

                if(view.getImage()!=0) {
                    dialog.disableChoice(view.getImage());
                }

                dialog.setListener(new Grid2ChoiceDialog.Listener() {
                    @Override
                    public void onSelected(int image) {
                        removeSelection(image);
                        view.setImage(image);
                        updateSelections();
                        enableGrids();
                    }

                    @Override
                    public void onRemove() {
                        view.removeImage();
                        view.setSelected(false);
                        updateSelections();
                        enableGrids();
                    }
                });

                dialog.show();
            }
        };

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView grid2BoxView = (Grid2BoxView) gridLayout.getChildAt(i);
            grid2BoxView.setListener(listener);
        }

        button = view.findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextFragment();
            }
        });

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        adjustGridLayout();

        return view;
    }

    private void openNextFragment() {
        handler.removeCallbacks(runnable);
        handlerInteraction.removeCallbacks(runnable);
        if (isVisible()) {
            updateSection();
            Study.openNextFragment();
        }
    }

    private int determinePointerPosition(Grid2BoxView view) {
        int gridBoxHeight = view.getHeight();
        int[] gridBoxLocation = new int[2];
        view.getLocationOnScreen(gridBoxLocation);

        if(gridBoxLocation[1] < (2*gridBoxHeight)) {
            // if grid box is in the first two rows of the grid, dialog appears below grid box
            return PointerDrawable.POINTER_BELOW;
        } else {
            return PointerDrawable.POINTER_ABOVE;
        }
    }

    private void updateSection(){
        List<GridTestPathData.Tap> choices = new ArrayList<>();

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                // TODO: Make sure we collect the data we need for this test version
                choices.add(new GridTestPathData.Tap(i / 5, i % 5, view.getTimestampImage()));
            }
        }

        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
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


    private void disableGrids(Grid2BoxView exemption){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(false);
        }
        if(exemption!=null) {
            exemption.setSelectable(true);
        }
    }

    private void enableGridsFinal(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                view.setSelectable(true);
            } else {
                view.setSelectable(false);
            }
        }
    }

    private void enableGrids(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
        }
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

    private void removeSelection(@DrawableRes int id){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(!view.isSelected()){
                continue;
            }
            int image = view.getImage();
            if(image == id) {
                view.removeImage();
                view.setSelected(false);
                return;
            }
        }
    }

    private void updateSelections(){
        phoneSelected = false;
        keySelected = false;
        penSelected = false;

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(!view.isSelected()){
                continue;
            }
            int id = view.getImage();
            if(id == R.drawable.phone) {
                phoneSelected = true;
            }
            if(id == R.drawable.key) {
                keySelected = true;
            }
            if(id == R.drawable.pen) {
                penSelected = true;
            }
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility(){
        if(phoneSelected && keySelected && penSelected) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

}
