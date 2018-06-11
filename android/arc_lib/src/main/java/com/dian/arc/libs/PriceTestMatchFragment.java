package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.models.PricesTest;
import com.dian.arc.libs.rest.models.PricesTestSection;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.JodaUtil;
import com.dian.arc.libs.utilities.PriceManager;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PriceTestMatchFragment extends BaseFragment {

    PriceManager.PriceSet priceSet;
    PricesTest priceTest;
    PricesTestSection pricesTestSection;
    int compareIndex = 0;
    String prefix;
    String suffix;
    Random random = new Random(System.currentTimeMillis());
    FancyButton buttonTop;
    FancyButton buttonBottom;
    TextView textviewFood;
    FrameLayout frameHide;
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
        View view = inflater.inflate(R.layout.fragment_price_test_match, container, false);
        frameHide = (FrameLayout)view.findViewById(R.id.frameHide);
        int set = ArcManager.getInstance().getState().currentVisit;
        int test = ArcManager.getInstance().getState().currentTestSession;
        priceSet = PriceManager.getInstance().getPriceSet(set,test);
        Collections.shuffle(priceSet.items);

        priceTest = ArcManager.getInstance().getCurrentTestSession().getPricesTest();

        prefix = getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        buttonTop = (FancyButton)view.findViewById(R.id.fancybuttonPriceTop);
        buttonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricesTestSection.triggerSelection(0,JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
                //priceTest.getSections().get(compareIndex).triggerSelection(0,JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
                saveIteration();
                if(iteration==10) {
                    ArcManager.getInstance().getCurrentTestSession().setPricesTest(priceTest);
                    ArcManager.getInstance().nextFragment();
                } else {
                    setupNextIteration();
                }
            }
        });

        buttonBottom = (FancyButton)view.findViewById(R.id.fancybuttonPriceBottom);
        buttonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricesTestSection.triggerSelection(1,JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
                //priceTest.getSections().get(compareIndex).triggerSelection(1,JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
                saveIteration();
                if(iteration==10) {
                    ArcManager.getInstance().nextFragment();
                } else {
                    setupNextIteration();
                }
            }
        });

        textviewFood = (TextView)view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(FontFactory.georgiaItalic);

        setupNextIteration();

        return view;
    }

    private void setupNextIteration(){
        PriceManager.Item item = priceSet.items.get(iteration);
        textviewFood.setText(item.item);
        compareIndex = getPriceItemIndex(priceTest,item.item);
        pricesTestSection = priceTest.getSections().get(compareIndex);

        int rand = random.nextInt(2);
        if(rand==0){
            buttonTop.setText(prefix+item.price+suffix);
            buttonBottom.setText(prefix+item.alt+suffix);
        } else {
            buttonTop.setText(prefix+item.alt+suffix);
            buttonBottom.setText(prefix+item.price+suffix);
        }
        pricesTestSection.triggerQuestionDisplayTime(JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
        pricesTestSection.setCorrectIndex(rand);
        iteration++;
    }

    private void saveIteration(){
         priceTest.getSections().set(compareIndex,pricesTestSection);
    }

    public int getPriceItemIndex(PricesTest set, String name){

        List<PricesTestSection> sections = set.getSections();
        int size = sections.size();
        for(int i=0;i<size;i++){
            if(sections.get(i)==null){
                sections.add(new PricesTestSection());
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
            ArcManager.getInstance().getCurrentTestSession().setPricesTest(priceTest);
            ArcManager.getInstance().nextFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
