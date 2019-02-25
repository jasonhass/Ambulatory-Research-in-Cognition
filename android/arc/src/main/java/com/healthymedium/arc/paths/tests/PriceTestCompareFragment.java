//
// PriceTestCompareFragment.java
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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.PriceManager;

import java.util.Random;

@SuppressLint("ValidFragment")
public class PriceTestCompareFragment extends BaseFragment {

    int index;

    PriceManager.Item item;

    PriceTestPathData priceTest;
    PriceTestPathData.Section section;
    String prefix;
    String suffix;

    RadioButton buttonYes;
    RadioButton buttonNo;
    TextView textviewFood;
    TextView textviewPrice;

    boolean paused;
    Random random;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            saveIteration();
            if(!paused) {
                Study.setCurrentSegmentData(priceTest);
                Study.openNextFragment();
            }
        }
    };

    public PriceTestCompareFragment(int index) {
        this.index = index;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_test_compare, container, false);
        random = new Random(System.currentTimeMillis());
        prefix = getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        priceTest = (PriceTestPathData) Study.getCurrentSegmentData();
        if(!priceTest.hasStarted()){
            priceTest.markStarted();
        }
        section = priceTest.getSections().get(index);
        item = priceTest.getPriceSet().get(index);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText("Yes");
        buttonYes.setCheckable(false);
        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);
                        break;
                }
                return true;
            }
        });

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText("No");
        buttonNo.setCheckable(false);
        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);
                        break;
                }
                return true;
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        handler = new Handler();
        setupIteration();

        return view;
    }

    private void setupIteration(){
        String price = prefix+item.price+suffix;
        section.setItem(item.item);
        section.setPrice(price);
        section.setAltPrice( prefix+item.alt+suffix);

        if(random.nextBoolean()){
            textviewFood.setText(item.item);
            textviewPrice.setText(price+" ");
        } else {
            textviewFood.setText(price+" ");
            textviewPrice.setText(item.item);
        }

        section.markStimulusDisplayed();
        handler.postDelayed(runnable,3000);
    }

    private void saveIteration(){
        if(buttonYes.isChecked()){
            section.selectGoodPrice(1);
        } else if (buttonNo.isChecked()){
            section.selectGoodPrice(0);
        } else {
            section.selectGoodPrice(99);
        }
        priceTest.getSections().set(index,section);
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
        handler.removeCallbacks(runnable);
        paused = true;
    }



}
