//
// PriceTestMatchFragment.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.TestEnumerations.PriceTestStyle;
import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.PriceManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressLint("ValidFragment")
public class PriceTestMatchFragment extends BaseFragment {

    List<PriceManager.Item> priceSet;
    PriceTestPathData priceTest;
    PriceTestPathData.Section pricesTestSection;
    int compareIndex = 0;
    String prefix;
    String suffix;
    Random random = new Random(System.currentTimeMillis());
    RadioButton radioButtonTop;
    RadioButton radioButtonBottom;
    TextView textviewFood;
    TextView textViewIsIt;
    int iteration = 0;
    boolean paused;

    public PriceTestMatchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout;
        if (Config.PRICE_TEST_STYLE == PriceTestStyle.REVISED.getStyle()) {
            layout = R.layout.fragment_revised_price_test_match;
        } else {
            layout = R.layout.fragment_price_test_match;
        }
        View view = inflater.inflate(layout, container, false);

        priceTest = (PriceTestPathData) Study.getCurrentSegmentData();
        priceSet = priceTest.getPriceSet();
        Collections.shuffle(priceSet);

        prefix = ViewUtil.getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        radioButtonTop = view.findViewById(R.id.buttonTop);
        radioButtonTop.setCheckable(false);
        radioButtonTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        pricesTestSection.markSelection(0, System.currentTimeMillis());
                        radioButtonTop.setChecked(true);
                        saveIteration();
                        if(iteration==priceSet.size()) {
                            Study.setCurrentSegmentData(priceTest);
                            Study.openNextFragment();
                        } else {
                            setupNextIteration();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        radioButtonTop.setChecked(false);
                        break;
                }
                return true;
            }
        });

        radioButtonBottom = view.findViewById(R.id.buttonBottom);
        radioButtonBottom.setCheckable(false);
        radioButtonBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        pricesTestSection.markSelection(1, System.currentTimeMillis());
                        radioButtonBottom.setChecked(true);
                        saveIteration();
                        if(iteration==priceSet.size()) {
                            Study.setCurrentSegmentData(priceTest);
                            Study.openNextFragment();
                        } else {
                            setupNextIteration();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        radioButtonBottom.setChecked(false);
                        break;
                }
                return true;
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textViewIsIt = view.findViewById(R.id.textView12);
        textViewIsIt.setText(ViewUtil.getHtmlString(R.string.prices_whatwasprice));

        setupNextIteration();

        return view;
    }

    private void setupNextIteration(){
        PriceManager.Item item = priceSet.get(iteration);
        textviewFood.setText(item.item);
        compareIndex = getPriceItemIndex(priceTest,item.item);
        pricesTestSection = priceTest.getSections().get(compareIndex);

        int rand = random.nextInt(2);
        if(rand==0){
            radioButtonTop.setText(prefix+item.price+suffix);
            radioButtonBottom.setText(prefix+item.alt+suffix);
        } else {
            radioButtonTop.setText(prefix+item.alt+suffix);
            radioButtonBottom.setText(prefix+item.price+suffix);
        }
        pricesTestSection.markQuestionDisplayed();
        pricesTestSection.setCorrectIndex(rand);
        iteration++;
    }

    private void saveIteration(){
         priceTest.getSections().set(compareIndex,pricesTestSection);
    }

    public int getPriceItemIndex(PriceTestPathData set, String name){

        List<PriceTestPathData.Section> sections = set.getSections();
        int size = sections.size();
        for(int i=0;i<size;i++){
            if(sections.get(i)==null){
                sections.add(new PriceTestPathData.Section());
            } else if(sections.get(i).getItem().equals(name)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            Study.setCurrentSegmentData(priceTest);
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
