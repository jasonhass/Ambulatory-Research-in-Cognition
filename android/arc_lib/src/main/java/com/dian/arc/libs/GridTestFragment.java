package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.models.GridTest;
import com.dian.arc.libs.rest.models.GridTestSection;
import com.dian.arc.libs.rest.models.GridTestTap;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GridTestFragment extends BaseFragment {

    boolean paused;
    int selectedCount = 0;
    public boolean second = false;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            handlerInteraction.removeCallbacks(runnable);
            if(Application.isActivityVisible()){
                if(!second) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            TimedDialog dialog = new TimedDialog();
                            dialog.bodyText = getString(R.string.grid_transition);
                            dialog.timer = 2000;
                            dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
                                @Override
                                public void dismiss() {
                                    updateSection();
                                    ArcManager.getInstance().nextFragment();
                                }
                            });
                            dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());
                        }
                    });
                } else {
                    updateSection();
                    ArcManager.getInstance().nextFragment();
                }
            }
        }
    };

    FancyButton button;
    GridLayout gridLayout;
    GridTest gridTest;
    GridTestSection section;
    List<Selection> selections;

    public GridTestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_test, container, false);
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wasDark = true;
                if (view.getTag().equals(R.color.darkBlue)) {
                    view.setTag(R.color.lightBlue);
                    int size = selections.size();
                    for(int i=0;i<size;i++){
                        if(selections.get(i).view.equals(view)) {
                            selections.remove(i);
                            size--;
                        }
                    }
                    selectedCount--;
                } else if (selectedCount < 3) {
                    wasDark = false;
                    selectedCount++;
                    selections.add(new Selection(view));
                    view.setTag(R.color.darkBlue);
                }

                int color = wasDark ? R.color.lightBlue : R.color.darkBlue;
                view.setBackgroundColor(ContextCompat.getColor(getContext(), color));
                handler.removeCallbacks(runnable);
                if(selectedCount >= 3){
                    handler.postDelayed(runnable,2000);
                }
            }
        };
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setTag(R.color.lightBlue);
            gridLayout.getChildAt(i).setOnClickListener(listener);
        }

        selections = new ArrayList<>();
        gridTest = ArcManager.getInstance().getCurrentTestSession().getGridTest();
        section = gridTest.getCurrentSection();
        section.triggerDisplayTestGrid(gridTest.getDate());

        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    private ImageView getView(int row,int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            if(!second) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        TimedDialog dialog = new TimedDialog();
                        dialog.bodyText = getString(R.string.grid_transition);
                        dialog.timer = 2000;
                        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
                            @Override
                            public void dismiss() {
                                updateSection();
                                ArcManager.getInstance().nextFragment();
                            }
                        });
                        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());
                    }
                });
            } else {
                updateSection();
                ArcManager.getInstance().nextFragment();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerInteraction.removeCallbacks(runnable);
        handler.removeCallbacks(runnable);
        paused = true;
    }

    private void updateSection(){
        int size = gridLayout.getChildCount();
        List<GridTestTap> choices = new ArrayList<>();
        for(int i=0;i<size;i++){
            for(Selection selection: selections){
                if(selection.view.equals(gridLayout.getChildAt(i))){
                    choices.add(new GridTestTap(i / 5,i % 5,selection.time-gridTest.getDate()));
                }
            }
        }
        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        ArcManager.getInstance().getCurrentTestSession().setGridTest(gridTest);
    }

    private class Selection{
        View view;
        double time;

        public Selection(View view){
            this.view = view;
            time = JodaUtil.toUtcDouble(DateTime.now());
        }
    }

}
