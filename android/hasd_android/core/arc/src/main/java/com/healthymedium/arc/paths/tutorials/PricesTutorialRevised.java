//
// PricesTutorialRevised.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tutorials;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.ui.base.RoundedRelativeLayout;

import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class PricesTutorialRevised extends Tutorial {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_PRICES = "HINT_PREVENT_TUTORIAL_CLOSE_PRICES";
    private static final long WHAT_DO_YOU_THINK_HINT_DELAY = 10000;
    private static final long TAP_THIS_PRICE_HINT_DELAY = 10000;

    Runnable runnableWhatDoYouThink;
    Handler handlerWhatDoYouThink = new Handler();
    Runnable runnableCorrectAnswer;
    Handler handlerCorrectAnswer = new Handler();

    Runnable progressRunnable;
    Handler handler;

    List<View> childSet;

    RelativeLayout mainContainer;
    RoundedRelativeLayout priceContainer;

    RadioButton buttonYes;
    RadioButton buttonNo;

    TextView textviewFood;
    TextView textviewPrice;
    TextView textView12;

    View hintHandle;

    HintPointer initialViewHint;

    HintHighlighter firstPriceContainerHighlight;

    HintPointer greatChoiceHint;

    HintHighlighter firstMatchContainerHighlight;
    HintPointer firstMatchHint;
    HintPointer firstMatchGreatChoiceHint;

    HintHighlighter secondMatchContainerHighlight;
    HintPointer secondMatchHint;


    HintHighlighter firstMatchAnswerHilighter;
    HintPointer firstMatchAnswerPointer;
    HintHighlighter secondMatchAnswerHilighter;
    HintPointer secondMatchAnswerPointer;

    HintHighlighter highlighter;
    HintPointer pointer;

    public PricesTutorialRevised() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
        childSet = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prices_tutorial, container, false);

        handler = new Handler();

        mainContainer = view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Color.parseColor("#EDEDEF"));

        priceContainer = view.findViewById(R.id.priceContainer);
        priceContainer.setVisibility(View.INVISIBLE);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText(ViewUtil.getString(R.string.radio_yes));
        buttonYes.setCheckable(false);
        buttonYes.setVisibility(View.INVISIBLE);

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.radio_no));
        buttonNo.setCheckable(false);
        buttonNo.setVisibility(View.INVISIBLE);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);
        progressIncrement = 25;

        closeButton = view.findViewById(R.id.closeButton);

        checkmark = view.findViewById(R.id.checkmark);

        hintHandle = view.findViewById(R.id.hintHandle);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        firstPriceContainerHighlight = new HintHighlighter(getActivity());

        firstMatchContainerHighlight = new HintHighlighter(getActivity());
        firstMatchHint = new HintPointer(getActivity(), priceContainer, true, false);

        secondMatchContainerHighlight = new HintHighlighter(getActivity());
        secondMatchHint = new HintPointer(getActivity(), priceContainer, true, false);

        firstMatchAnswerHilighter = new HintHighlighter(getActivity());
        firstMatchAnswerPointer = new HintPointer(getActivity(), buttonNo, true, false);

        secondMatchAnswerHilighter = new HintHighlighter(getActivity());
        secondMatchAnswerPointer = new HintPointer(getActivity(), buttonYes, true, false);


        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        firstMatchGreatChoiceHint = new HintPointer(getActivity(), hintHandle, false, false);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        initialViewHint = new HintPointer(getActivity(), hintHandle, false, false);
        greatChoiceHint = new HintPointer(getActivity(), hintHandle, false, false);

        textView12=view.findViewById(R.id.textView12);
        textView12.setVisibility(View.INVISIBLE);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        endButton.setText(ViewUtil.getHtmlString(R.string.button_close));

        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        highlighter = new HintHighlighter(getActivity());

        //Store children of priceContainer to be reinjected
        saveChildren(priceContainer);

        injectMemorizationViews();

        priceContainer.setRadius(16);
        priceContainer.setFillColor(R.color.white);
        priceContainer.setStrokeColor("#6C7373");
        priceContainer.setStrokeWidth(1);


        return view;
    }

    private void injectMemorizationViews() {
        priceContainer.removeAllViews();
        priceContainer.setGravity(Gravity.CENTER);

        //Resize container
        priceContainer.getLayoutParams().height = ViewUtil.dpToPx(210);
        priceContainer.getLayoutParams().width = ViewUtil.dpToPx(350);

        TextView viewUpdater;
        RelativeLayout.LayoutParams paramManip;

        //Update textViewFood attributes for revised prices test
        viewUpdater = textviewFood;
        viewUpdater.setVisibility(View.VISIBLE);
        viewUpdater.setGravity(Gravity.CENTER);
        viewUpdater.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
        viewUpdater.setPadding(0, 0, 0, 0);
        viewUpdater.setLineSpacing(0, 0);

        resetLayoutParams(viewUpdater);
        paramManip = (RelativeLayout.LayoutParams) viewUpdater.getLayoutParams();
        paramManip.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewUpdater.setLayoutParams(paramManip);

        priceContainer.addView(viewUpdater);

        //Update textviewPrices attributes for revised prices test
        viewUpdater = textviewPrice;
        viewUpdater.setVisibility(View.VISIBLE);
        viewUpdater.setGravity(Gravity.CENTER);
        viewUpdater.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
        viewUpdater.setPadding(0,0,0,0);
        viewUpdater.setLineSpacing(0, 0);

        resetLayoutParams(viewUpdater);
        paramManip = (RelativeLayout.LayoutParams) viewUpdater.getLayoutParams();
        paramManip.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        paramManip.addRule(RelativeLayout.BELOW, textviewFood.getId());
        viewUpdater.setLayoutParams(paramManip);

        priceContainer.addView(viewUpdater);
}

    private void saveChildren(ViewGroup container) {
        childSet.clear();
        for(int i = 0; i < container.getChildCount(); ++i)
            childSet.add(i, container.getChildAt(i));
    }

    private void injectSavedChildren() {
        //Ensure that inject children will not execute without a preceding saveChildren()
        if(childSet.isEmpty())
            return;

        priceContainer.removeAllViews();

        for(View view : childSet) {
            if(view.equals(textviewFood))
                resetLayoutParams((TextView)view);

            if(view.equals(textView12))
                resetLayoutParams((TextView)view);

            priceContainer.addView(view, childSet.indexOf(view));
        }

        childSet.clear();
    }

    private void resetLayoutParams(TextView tv) {
        RelativeLayout.LayoutParams defaultParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(tv.equals(textView12)) {
            defaultParams.addRule(RelativeLayout.BELOW, textviewFood.getId());
        }

        tv.setLayoutParams(defaultParams);
    }

    @Override
    protected void onEnterTransitionStart(boolean popped) {
        super.onEnterTransitionStart(popped);
        if(!Hints.hasBeenShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES)) {
            closeButton.setVisibility(View.GONE);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFirstPricesCompare();
            }
        }, 1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeButton.setEnabled(false);

                        handler.removeCallbacksAndMessages(null);
                        handlerWhatDoYouThink.removeCallbacksAndMessages(null);
                        handlerCorrectAnswer.removeCallbacksAndMessages(null);

                        handler = null;
                        handlerWhatDoYouThink = null;
                        handlerCorrectAnswer = null;

                        welcomeHighlight.dismiss();
                        welcomeHint.dismiss();

                        quitHighlight.dismiss();
                        quitHint.dismiss();

                        initialViewHint.dismiss();

                        firstPriceContainerHighlight.dismiss();

                        greatChoiceHint.dismiss();

                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        firstMatchGreatChoiceHint.dismiss();
                        firstMatchAnswerHilighter.dismiss();
                        firstMatchAnswerPointer.dismiss();

                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();
                        secondMatchAnswerHilighter.dismiss();
                        secondMatchAnswerPointer.dismiss();

                        highlighter.dismiss();

                        if(pointer != null)
                            pointer.dismiss();

                        exit();
                    }
                });
            }
        },1200);
    }

    private void setFirstPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

        initialViewHint.setText(ViewUtil.getString(R.string.popup_tutorial_price_intro));
        highlighter.addTarget(progressBar);
        highlighter.show();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialViewHint.dismiss();
                highlighter.dismiss();
                highlighter.clearTargets();
                priceContainer.setVisibility(View.VISIBLE);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        setSecondPricesCompare();
                    }
                };
                handler.postDelayed(runnable,3000);
            }
        };

        initialViewHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);

        initialViewHint.show();
    }

    private void setSecondPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item2));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price2));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setThirdPricesCompare();
            }
        };
        handler.postDelayed(runnable,3000);


    }

    private void setThirdPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item3));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price3));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                incrementProgress();
                greatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        greatChoiceHint.dismiss();
                        highlighter.dismiss();
                        highlighter.clearTargets();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                //add back original children
                                injectSavedChildren();

                                priceContainer.setBackground(null);
                                priceContainer.removeView(textviewPrice);
                                priceContainer.setVisibility(View.VISIBLE);
                                priceContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                priceContainer.setPadding(0, 20, 0, 20);

                                mainContainer.setBackgroundColor(Color.WHITE);

                                setFirstPriceMatch();
                            }
                        };
                        handler.postDelayed(runnable,600);
                    }
                };

                greatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);
                greatChoiceHint.show();

                highlighter.addTarget(progressBar);
                highlighter.show();

                priceContainer.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }

    private void setFirstPriceMatch() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));
        textviewFood.setGravity(Gravity.CENTER_HORIZONTAL);
        textviewPrice.setVisibility(View.GONE);

        textView12.setVisibility(View.VISIBLE);
        buttonYes.setVisibility(View.VISIBLE);
        buttonNo.setVisibility(View.VISIBLE);

        textView12.setText(ViewUtil.getString(R.string.prices_whatwasprice));
        textView12.setGravity(Gravity.CENTER_HORIZONTAL);

        buttonYes.setText(ViewUtil.getString(R.string.prices_tutorial_price1_match));
        buttonNo.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

        buttonYes.showButton(false);
        buttonYes.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);
        buttonNo.showButton(false);
        buttonNo.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                firstMatchContainerHighlight.addTarget(priceContainer, 10);
                firstMatchContainerHighlight.addTarget(progressBar);
                firstMatchContainerHighlight.show();

                firstMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_recall));
                firstMatchHint.show();
            }
        };

        handlerWhatDoYouThink.post(runnableWhatDoYouThink);

        runnableCorrectAnswer = new Runnable() {
            @Override
            public void run() {
                firstMatchContainerHighlight.dismiss();
                firstMatchHint.dismiss();
                firstMatchAnswerHilighter.addPulsingTarget(buttonNo.findViewById(R.id.frameLayoutRadioButton),42);
                firstMatchAnswerHilighter.addTarget(progressBar);
                firstMatchAnswerHilighter.show();

                firstMatchAnswerPointer.setText(ViewUtil.getString(R.string.popup_tutorial_pricetap));
                firstMatchAnswerPointer.show();
            }
        };

        handlerCorrectAnswer.postDelayed(runnableCorrectAnswer, TAP_THIS_PRICE_HINT_DELAY);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        handlerCorrectAnswer.post(runnableCorrectAnswer);

                        buttonYes.setChecked(false);

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        firstMatchAnswerHilighter.dismiss();
                        firstMatchAnswerPointer.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        incrementProgress();
                        updateButtons(false);

                        //priceContainer.setVisibility(View.INVISIBLE);

                        highlighter.addTarget(progressBar);
                        highlighter.show();
                        firstMatchGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatjob));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstMatchGreatChoiceHint.dismiss();
                                highlighter.dismiss();
                                highlighter.clearTargets();

                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        buttonYes.setChecked(false);
                                        setSecondPriceMatch();
                                        priceContainer.setVisibility(View.VISIBLE);
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstMatchGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                        firstMatchGreatChoiceHint.show();

                        break;
                }
                return true;
            }
        });

    }

    private void setSecondPriceMatch() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item2));
        textviewPrice.setVisibility(View.GONE);

        buttonYes.setText(ViewUtil.getString(R.string.prices_tutorial_price2));
        buttonNo.setText(ViewUtil.getString(R.string.prices_tutorial_price2_match));

        buttonYes.showButton(false);
        buttonYes.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);
        buttonNo.showButton(false);
        buttonNo.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                secondMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_recall));
                secondMatchHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink, WHAT_DO_YOU_THINK_HINT_DELAY);

        runnableCorrectAnswer = new Runnable() {
            @Override
            public void run() {
                secondMatchContainerHighlight.dismiss();
                secondMatchHint.dismiss();
                secondMatchAnswerHilighter.addPulsingTarget(buttonYes.findViewById(R.id.frameLayoutRadioButton),42);
                secondMatchAnswerHilighter.addTarget(progressBar);
                secondMatchAnswerHilighter.show();

                secondMatchAnswerPointer.setText(ViewUtil.getString(R.string.popup_tutorial_pricetap));
                secondMatchAnswerPointer.show();
            }
        };

        handlerCorrectAnswer.postDelayed(runnableCorrectAnswer, WHAT_DO_YOU_THINK_HINT_DELAY + TAP_THIS_PRICE_HINT_DELAY);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:

                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();
                        secondMatchAnswerHilighter.dismiss();
                        secondMatchAnswerPointer.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        incrementProgress();
                        updateButtons(true);

                        progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                pointer = new HintPointer(getActivity(), hintHandle, false, false);
                                pointer.setText(ViewUtil.getString(R.string.popup_tutorial_nice));
                                pointer.addButton(ViewUtil.getString(R.string.button_next), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pointer.dismiss();
                                        highlighter.dismiss();
                                        setThirdPriceMatch();
                                    }
                                });
                                highlighter = new HintHighlighter(getActivity());
                                highlighter.addTarget(progressBar);
                                highlighter.show();
                                pointer.show();
                            }
                        };

                        handler.post(progressRunnable);

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        handlerCorrectAnswer.post(runnableCorrectAnswer);

                        break;
                }
                return true;
            }
        });
    }

    private void setThirdPriceMatch() {
        priceContainer.setVisibility(View.VISIBLE);

        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item3));

        buttonYes.setText(ViewUtil.getString(R.string.prices_tutorial_price3));
        buttonNo.setText(ViewUtil.getString(R.string.prices_tutorial_price3_match));

        buttonYes.setChecked(false);
        buttonNo.setChecked(false);

        buttonYes.showButton(false);
        buttonYes.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);
        buttonNo.showButton(false);
        buttonNo.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                secondMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_recall));
                secondMatchHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink, WHAT_DO_YOU_THINK_HINT_DELAY);

        runnableCorrectAnswer = new Runnable() {
            @Override
            public void run() {
                secondMatchHint.dismiss();

                highlighter = new HintHighlighter(getActivity());
                highlighter.addPulsingTarget(buttonYes.findViewById(R.id.frameLayoutRadioButton), 42);
                highlighter.addTarget(progressBar);

                pointer = new HintPointer(getActivity(), buttonYes.findViewById(R.id.frameLayoutRadioButton), true, false);
                pointer.setText(ViewUtil.getString(R.string.popup_tutorial_pricetap));

                highlighter.show();
                pointer.show();
            }
        };

        handlerCorrectAnswer.postDelayed(runnableCorrectAnswer, WHAT_DO_YOU_THINK_HINT_DELAY + TAP_THIS_PRICE_HINT_DELAY);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        if(pointer != null && highlighter != null) {
                            highlighter.dismiss();
                            pointer.dismiss();
                            highlighter.clearTargets();
                        }

                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);

                        incrementProgress();
                        updateButtons(true);

                        fadeOutView(textView12);
                        fadeOutView(buttonNo);
                        fadeOutView(buttonYes);
                        fadeOutView(textviewFood);
                        fadeOutView(textviewPrice);

                        showComplete();

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        handlerCorrectAnswer.post(runnableCorrectAnswer);

                        break;
                }
                return true;
            }
        });
    }

    private void updateButtons(Boolean isYesChecked) {
        if (isYesChecked) {
            buttonYes.setChecked(true);
            buttonNo.setChecked(false);
        } else {
            buttonYes.setChecked(false);
            buttonNo.setChecked(true);
        }

        buttonYes.setOnTouchListener(null);
        buttonNo.setOnTouchListener(null);
    }

    /* ----- Utility ----- */

    private int getIndexOfChildInParent(ViewGroup parent, View child) {
        for(int i = 0; i < parent.getChildCount(); ++i) {
            if(parent.getChildAt(i).equals(child))
                return i;
        }

        return -1;
    }
}
