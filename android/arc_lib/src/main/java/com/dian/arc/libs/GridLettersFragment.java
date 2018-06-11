package com.dian.arc.libs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.models.GridTest;
import com.dian.arc.libs.rest.models.GridTestSection;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GridLettersFragment extends BaseFragment {

    boolean paused;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(Application.isActivityVisible()){
                TimedDialog dialog = new TimedDialog();
                dialog.bodyText = getString(R.string.ready);
                dialog.timer = 1000;
                dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss() {
                        section.setECount(eCount);
                        section.setFCount(fCount);
                        gridTest.updateCurrentSection(section);
                        ArcManager.getInstance().getCurrentTestSession().setGridTest(gridTest);
                        ArcManager.getInstance().nextFragment();
                    }
                });
                dialog.show(getFragmentManager(),TimedDialog.class.getSimpleName());
            }
        }
    };

    FancyButton button;
    GridLayout gridLayout;
    GridTest gridTest;
    GridTestSection section;
    int eCount = 0;
    int fCount = 0;

    public GridLettersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_letters, container, false);
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isE = ((TextView)view).getText().toString().equals("E");
                if(view.getTag() == null){
                    view.setTag(true);
                    if(isE){
                        eCount++;
                    } else {
                        fCount++;
                    }
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightBlue));
                    return;
                }
                if(view.getTag().equals(false)) {
                    view.setTag(true);
                    if(isE){
                        eCount++;
                    } else {
                        fCount++;
                    }
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightBlue));
                } else {
                    view.setTag(false);
                    if(isE){
                        eCount--;
                    } else {
                        fCount--;
                    }
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                }
            }
        };
        Typeface font = FontFactory.georgia;
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setOnClickListener(listener);
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
        gridTest = ArcManager.getInstance().getCurrentTestSession().getGridTest();
        section = gridTest.getCurrentSection();
        section.triggerDisplayDistraction(gridTest.getDate());

        return view;
    }

    private TextView getTextView(int row,int col){
        return (TextView)gridLayout.getChildAt((6*row)+col);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            handler.post(runnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        paused = true;
    }
}
