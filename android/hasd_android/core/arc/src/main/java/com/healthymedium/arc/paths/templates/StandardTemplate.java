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

import com.healthymedium.arc.paths.informative.ContactScreen;

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
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class StandardTemplate extends BaseFragment {

    TranslateAnimation showAnimation;
    TranslateAnimation hideAnimation;
    boolean buttonShowing;
    boolean autoscroll = false;

    String stringButton;
    String stringHeader;
    String stringSubHeader;

    protected TextView textViewHeader;
    protected TextView textViewSubheader;

    protected LinearLayout content;

    TextView textViewBack;
    protected TextView textViewHelp;

    ScrollView scrollView;

    protected Button buttonNext;
    Button textViewScroll;
    Button textViewScrollTop;

    boolean allowBack;
    boolean disableScrollBehavior;

    public StandardTemplate(boolean allowBack, String header, String subheader) {
        this.allowBack = allowBack;
        stringButton = ViewUtil.getString(R.string.button_next);
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
        textViewBack.setText(Html.fromHtml(ViewUtil.getString(R.string.button_back)));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHelp = view.findViewById(R.id.textViewHelp);
        textViewHelp.setText(Html.fromHtml(ViewUtil.getString(R.string.button_help)));
        textViewHelp.setTypeface(Fonts.robotoMedium);
        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BaseFragment helpScreen;
                if (USE_HELP_SCREEN) {
                    helpScreen = new HelpScreen();
                } else {
                    helpScreen = new ContactScreen();
                }
                NavigationManager.getInstance().open(helpScreen);
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
        textViewScrollTop = view.findViewById(R.id.textViewScrollTop);

        scrollView = view.findViewById(R.id.scrollView);

        textViewScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, scrollView.getHeight());
                autoscroll = true;
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoscroll = false;
                }
            }, 300);
            }
        });

        textViewScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, 0);
                autoscroll = true;
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoscroll = false;
                }
            }, 300);
            }
        });

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
        buttonNext.setVisibility(View.GONE);
        buttonShowing = false;
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
                        textViewScrollTop.setAlpha(0);
                        textViewScrollTop.setVisibility(View.VISIBLE);
                        buttonNext.startAnimation(hideAnimation);
                    }
                }
            }, 50);
        }

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
                    buttonNext.setVisibility(View.VISIBLE);
                    buttonShowing = true;
                    buttonNext.startAnimation(showAnimation);
                } else {
                    buttonNext.setVisibility(View.GONE);
                    buttonShowing = false;
                    buttonNext.startAnimation(hideAnimation);
                }
                if (!autoscroll) {
                    textViewScroll.animate().alpha(0.0f).setDuration(300);
                    textViewScrollTop.animate().alpha(0.0f).setDuration(300);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewScroll.setVisibility(View.GONE);
                            textViewScrollTop.setVisibility(View.GONE);
                        }
                    }, 300);
                }
            }
        });
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

        Study.getInstance().openPreviousFragment();
    }


    private Animation.AnimationListener showAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            buttonNext.setVisibility(View.VISIBLE);
            textViewScroll.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewScrollTop.animate().alpha(1.0f).setDuration(300);
                }
            }, 300);
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
            textViewScrollTop.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewScroll.animate().alpha(1.0f).setDuration(300);
                }
            }, 300);
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
