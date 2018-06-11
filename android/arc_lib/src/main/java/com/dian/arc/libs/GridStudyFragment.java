package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.models.GridTest;
import com.dian.arc.libs.rest.models.GridTestImage;
import com.dian.arc.libs.rest.models.GridTestSection;
import com.dian.arc.libs.utilities.ArcManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridStudyFragment extends BaseFragment {

    boolean paused;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(Application.isActivityVisible()){
                ArcManager.getInstance().nextFragment();
            }
        }
    };

    FancyButton button;
    GridLayout gridLayout;
    GridTest gridTest;
    GridTestSection section;

    public GridStudyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_study, container, false);
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);

        ArcManager.getInstance().getCurrentTestSession().getGridTest().startNewSection();
        gridTest = ArcManager.getInstance().getCurrentTestSession().getGridTest();
        section = gridTest.getCurrentSection();
        setupTest();

        handler = new Handler();
        handler.postDelayed(runnable,3000);

        return view;
    }

    private ImageView getImageView(int row,int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            ArcManager.getInstance().nextFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        paused = true;
    }

    private void setupTest(){
        List<GridTestImage> images = new ArrayList<>();

        Random random = new Random(SystemClock.currentThreadTimeMillis());
        int row1 = random.nextInt(5);
        int row2 = random.nextInt(5);
        int row3 = random.nextInt(5);
        while(row1 == row2){
            row2 = random.nextInt(5);
        }
        while(row3 == row1 || row3 == row2){
            row3 = random.nextInt(5);
        }

        int col1 = random.nextInt(5);
        int col2 = random.nextInt(5);
        int col3 = random.nextInt(5);
        while(col1 == col2){
            col2 = random.nextInt(5);
        }
        while(col3 == col1 || col3 == col2){
            col3 = random.nextInt(5);
        }

        getImageView(row1,col1).setImageResource(R.drawable.phone);
        getImageView(row2,col2).setImageResource(R.drawable.pen);
        getImageView(row3,col3).setImageResource(R.drawable.key);

        section.triggerDisplaySymbols(gridTest.getDate());

        images.add(new GridTestImage(row1,col1,"phone"));
        images.add(new GridTestImage(row2,col2,"pen"));
        images.add(new GridTestImage(row3,col3,"key"));
        section.setImages(images);
        gridTest.updateCurrentSection(section);
        ArcManager.getInstance().getCurrentTestSession().setGridTest(gridTest);
    }

}
