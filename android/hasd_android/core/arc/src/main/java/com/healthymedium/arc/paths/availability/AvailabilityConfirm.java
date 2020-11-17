//
// AvailabilityConfirm.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
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
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import static com.healthymedium.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class AvailabilityConfirm extends BaseFragment {

    CircadianClock clock;

    TranslateAnimation showAnimation;
    TranslateAnimation hideAnimation;
    boolean buttonShowing;
    boolean autoscroll = false;

    String stringButton;
    String stringHeader;
    String stringSubHeader;
    String stringChangeTimes;

    protected TextView textViewHeader;
    TextView textViewSubheader;

    protected LinearLayout content;

    TextView textViewBack;
    protected TextView textViewHelp;

    ScrollView scrollView;

    TextView textViewChangeTimes;

    protected Button buttonNext;
    Button textViewScroll;
    Button textViewScrollTop;

    boolean allowBack;
    boolean disableScrollBehavior;

    int minWakeTime = 4;
    int maxWakeTime = 24;
    boolean reschedule = false;

    public AvailabilityConfirm(int minWakeTime, int maxWakeTime, boolean reschedule, boolean allowBack) {
        this.allowBack = allowBack;
        stringButton = ViewUtil.getString(R.string.button_next);

        this.minWakeTime = minWakeTime;
        this.maxWakeTime = maxWakeTime;
        this.reschedule = reschedule;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public AvailabilityConfirm(boolean allowBack, String button) {
        this.allowBack = allowBack;
        stringButton = button;


        if(allowBack){
            allowBackPress(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability_confirm, container, false);

        content = view.findViewById(R.id.linearLayoutContent);

        clock = Study.getParticipant().getCircadianClock();
        String wakeTime = clock.getRhythm("Monday").getWakeTime().toString("h:mm a");
        String bedTime = clock.getRhythm("Monday").getBedTime().toString("h:mm a");

        //Place user inputted times into confirmation page header string time slots
        Phrase confirmPhrase;
        if(reschedule)
            confirmPhrase = Phrase.getTimeReplacedPhrase (
                    R.string.availability_change_confirm,
                    wakeTime,
                    bedTime
            );
        else
            confirmPhrase = Phrase.getTimeReplacedPhrase (
                    R.string.availability_confirm,
                    wakeTime,
                    bedTime
            );

        stringHeader = confirmPhrase.toString();

        //Header textview init
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHelp = view.findViewById(R.id.textViewHelp);
        textViewHelp.setText(ViewUtil.getString(R.string.button_help));
        textViewHelp.setTypeface(Fonts.robotoMedium);
        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment helpScreen;
                if (USE_HELP_SCREEN)
                    helpScreen = new HelpScreen();
                else
                    helpScreen = new ContactScreen();

                NavigationManager.getInstance().open(helpScreen);
            }
        });

        textViewChangeTimes = view.findViewById(R.id.textViewChangeTimes);
        textViewChangeTimes.setText(Html.fromHtml(ViewUtil.getString(R.string.availability_change_linked)));
        textViewChangeTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.updateAvailabilityOnboarding(8, 18);
            }
        });
        ViewUtil.underlineTextView(textViewChangeTimes);

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

        if(allowBack)
            textViewBack.setVisibility(View.VISIBLE);

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(disableScrollBehavior) {
            buttonNext.setVisibility(View.VISIBLE);
            buttonShowing = true;
        }
        else {
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
    }

    protected void setHelpVisible(boolean visible){
        textViewHelp.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    boolean scrollViewIsAtBottom(){
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        return diff < 50;
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
                if(disableScrollBehavior)
                    return;

                boolean needsToShow = scrollViewIsAtBottom();
                if(needsToShow == buttonShowing) {
                    return;
                }
                else if(needsToShow) {
                    buttonShowing = true;
                    buttonNext.startAnimation(showAnimation);
                }
                else {
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
        buttonNext.setEnabled(false);
        AsyncLoader loader = new AsyncLoader();
        loader.execute();
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
        public void onAnimationEnd(Animation animation) {}
        @Override
        public void onAnimationRepeat(Animation animation) {}

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

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = new LoadingDialog();
            loadingDialog.show(getFragmentManager(),"LoadingDialog");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Study.getParticipant().setCircadianClock(clock);
            Study.getParticipant().save();
            Study.getRestClient().submitWakeSleepSchedule();

            DateTime start = Study.getParticipant().getStartDate();
            if(start==null){
                start = DateTime.now();
                Study.getParticipant().getState().studyStartDate = start;
            }
            else {
                start = start.withTime(LocalTime.now());
            }

            Study.getScheduler().scheduleTests(start,Study.getInstance().getParticipant());
            Study.getRestClient().submitTestSchedule();
            Study.getScheduler().scheduleNotifications(Study.getCurrentTestCycle(), reschedule);

            if(Study.getParticipant().shouldCurrentlyBeInTestCycle()) {
                if (reschedule) {
                    Proctor.refreshData(getContext());
                }
                else {
                    Proctor.startService(getContext());
                }
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            Study.openNextFragment();
        }
    }
}
