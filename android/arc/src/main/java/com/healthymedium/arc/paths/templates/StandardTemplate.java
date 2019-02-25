//
// StandardTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class StandardTemplate extends BaseFragment {

    TranslateAnimation showAnimation;
    TranslateAnimation hideAnimation;
    boolean buttonShowing;

    String stringButton;
    String stringHeader;
    String stringSubHeader;

    protected TextView textViewHeader;
    TextView textViewSubheader;

    protected LinearLayout content;

    TextView textViewBack;
    protected TextView textViewHelp;

    ScrollView scrollView;

    protected Button buttonNext;
    TextView textViewScroll;

    boolean allowBack;
    boolean disableScrollBehavior;

    public StandardTemplate(boolean allowBack, String header, String subheader) {
        this.allowBack = allowBack;
        stringButton = "NEXT";
        stringHeader = header;
        stringSubHeader = subheader;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public StandardTemplate(boolean allowBack, String header, String subheader, String button) {
        this.allowBack = allowBack;
        stringButton = button;
        stringHeader = header;
        stringSubHeader = subheader;

        if(allowBack){
            allowBackPress(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_standard, container, false);
        content = view.findViewById(R.id.linearLayoutContent);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        if(stringSubHeader!=null){
            textViewSubheader = view.findViewById(R.id.textViewSubHeader);
            textViewSubheader.setText(Html.fromHtml(stringSubHeader));
            textViewSubheader.setVisibility(View.VISIBLE);
        }

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHelp = view.findViewById(R.id.textViewHelp);
        textViewHelp.setTypeface(Fonts.robotoMedium);
        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });

        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextRequested();
            }
        });
        if(stringButton!=null){
            buttonNext.setText(stringButton);
        }

        textViewScroll = view.findViewById(R.id.textViewScroll);

        scrollView = view.findViewById(R.id.scrollView);

        showAnimation = new TranslateAnimation(0,0,ViewUtil.dpToPx(100),0);
        showAnimation.setDuration(250);
        showAnimation.setAnimationListener(showAnimationListener);

        hideAnimation = new TranslateAnimation(0,0,0,ViewUtil.dpToPx(100));
        hideAnimation.setDuration(500);
        hideAnimation.setAnimationListener(hideAnimationListener);

        if(allowBack){
            textViewBack.setVisibility(View.VISIBLE);
        }

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(disableScrollBehavior) {
            buttonNext.setVisibility(View.VISIBLE);
            buttonShowing = true;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scrollViewIsAtBottom()) {
                        buttonNext.setVisibility(View.VISIBLE);
                        buttonShowing = true;
                    } else {
                        textViewScroll.setVisibility(View.VISIBLE);
                        buttonNext.startAnimation(hideAnimation);
                    }
                }
            }, 50);
        }
    }

    protected void setHelpVisible(boolean visible){
        textViewHelp.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    boolean scrollViewIsAtBottom(){
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        //Log.i("scroll", "view.getBottom()="+view.getBottom()+" scrollView.getHeight()="+scrollView.getHeight()+" scrollView.getScrollY()=" + scrollView.getScrollY());
        return (diff < 50);
    }

    protected void disableScrollBehavior(){
        disableScrollBehavior = true;
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(disableScrollBehavior){
                    return;
                }
                boolean needsToShow = scrollViewIsAtBottom();
                if(needsToShow==buttonShowing){
                    return;
                } else if(needsToShow){
                    buttonShowing = true;
                    buttonNext.startAnimation(showAnimation);
                } else {
                    buttonShowing = false;
                    buttonNext.startAnimation(hideAnimation);
                }
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return disableScrollBehavior;
            }
        });
    }

    protected void onNextButtonEnabled(boolean enabled){

    }

    protected void onNextRequested() {
        Study.getInstance().openNextFragment();
    }

    protected void onBackRequested() {
        Log.i("StandardTemplate","onBackRequested");
        Study.getInstance().openPreviousFragment();
    }


    private Animation.AnimationListener showAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            buttonNext.setVisibility(View.VISIBLE);
            textViewScroll.animate().alpha(0.0f).setDuration(250);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //textViewScroll.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation.AnimationListener hideAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            textViewScroll.animate().alpha(1.0f).setDuration(500);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            buttonNext.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
