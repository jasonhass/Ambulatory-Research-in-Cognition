package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.ToggledButton;
import com.dian.arc.libs.rest.models.PricesTest;
import com.dian.arc.libs.rest.models.PricesTestSection;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.JodaUtil;
import com.dian.arc.libs.utilities.PriceManager;

import java.util.Random;

import static com.dian.arc.libs.Application.isActivityVisible;

public class PriceTestCompareFragment extends BaseFragment {

    PriceManager.PriceSet priceSet;
    PricesTest priceTest;
    PricesTestSection section;
    String prefix;
    String suffix;

    ToggledButton buttonYes;
    ToggledButton buttonNo;
    TextView textviewFood;
    TextView textviewPrice;
    FrameLayout frameHide;

    int iteration = 0;
    boolean paused;
    Random random;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            saveIteration();
            if(iteration==10) {
                if(frameHide != null) {
                    frameHide.setVisibility(View.VISIBLE);
                }
                SimpleDialog dialog = new SimpleDialog();
                dialog.bodyText = getString(R.string.price_popup);
                dialog.buttonText = getString(R.string.okay);
                dialog.delayTime = 3000;
                dialog.maxTime = 15000;
                dialog.setOnDialogDismissListener(new SimpleDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss() {
                        if(isActivityVisible()) {
                            ArcManager.getInstance().getCurrentTestSession().setPricesTest(priceTest);
                            ArcManager.getInstance().nextFragment();
                        }
                    }
                });
                dialog.show(getFragmentManager(),SimpleDialog.class.getSimpleName());
            } else {
                setupNextIteration();
            }
        }
    };

    public PriceTestCompareFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_test_compare, container, false);
        frameHide = (FrameLayout)view.findViewById(R.id.frameHide);
        int set = ArcManager.getInstance().getState().currentVisit;
        int test = ArcManager.getInstance().getState().currentTestSession;
        priceSet = PriceManager.getInstance().getPriceSet(set,test);
        //Collections.shuffle(priceSet.items);
        random = new Random(System.currentTimeMillis());
        prefix = getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        priceTest = ArcManager.getInstance().getCurrentTestSession().getPricesTest();

        buttonYes = (ToggledButton)view.findViewById(R.id.togglebuttonPriceYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonYes.isOn()) {
                    buttonYes.toggle(true);
                    buttonNo.toggle(false);
                }
            }
        });

        buttonNo = (ToggledButton)view.findViewById(R.id.togglebuttonPriceNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonNo.isOn()) {
                    buttonNo.toggle(true);
                    buttonYes.toggle(false);
                }
            }
        });

        textviewFood = (TextView)view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(FontFactory.georgiaItalic);

        textviewPrice = (TextView)view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(FontFactory.georgiaItalic);

        handler = new Handler();
        setupNextIteration();

        return view;
    }

    private void setupNextIteration(){

        buttonNo.toggle(false);
        buttonYes.toggle(false);

        PriceManager.Item item = priceSet.items.get(iteration);
        String price = prefix+item.price+suffix;
        section = priceTest.getSections().get(iteration);
        section.setItem(item.item);
        section.setPrice(price);
        section.setAltPrice( prefix+item.alt+suffix);

        if(random.nextBoolean()){
            textviewFood.setText(item.item);
            textviewPrice.setText(" "+price+" ");
        } else {
            textviewFood.setText(" "+price+" ");
            textviewPrice.setText(item.item);
        }

        section.triggerStimulusDisplayTime(JodaUtil.toUtcDouble(ArcManager.getInstance().getCurrentTestSession().getStartTime()));
        iteration++;
        handler.postDelayed(runnable,3000);
    }

    private void saveIteration(){
        if(buttonYes.isOn()){
            section.selectGoodPrice(1);
        } else if (buttonNo.isOn()){
            section.selectGoodPrice(0);
        } else {
            section.selectGoodPrice(99);
        }
        priceTest.getSections().set(iteration-1,section);
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
        handler.removeCallbacks(runnable);
        paused = true;
    }

}
