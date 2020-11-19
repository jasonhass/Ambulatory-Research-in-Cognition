//
// PricesTutorial.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tutorials;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.ViewUtil;

public class PricesTutorial extends Tutorial {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_PRICES = "HINT_PREVENT_TUTORIAL_CLOSE_PRICES";

    Runnable runnableWhatDoYouThink;
    Handler handlerWhatDoYouThink = new Handler();

    RelativeLayout priceContainer;

    RadioButton buttonYes;
    RadioButton buttonNo;

    TextView textviewFood;
    TextView textviewPrice;
    TextView textView12;

    HintPointer initialViewHint;

    HintHighlighter firstPriceContainerHighlight;
    HintPointer firstPriceHint;
    HintPointer firstGreatChoiceHint;

    HintHighlighter secondPriceContainerHighlight;
    HintPointer secondPriceHint;
    HintPointer secondGreatChoiceHint;

    HintHighlighter firstMatchContainerHighlight;
    HintPointer firstMatchHint;
    HintPointer firstMatchGreatChoiceHint;

    HintHighlighter secondMatchContainerHighlight;
    HintPointer secondMatchHint;


    public PricesTutorial() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prices_tutorial, container, false);

        priceContainer = view.findViewById(R.id.priceContainer);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText(ViewUtil.getString(R.string.radio_yes));
        buttonYes.setCheckable(false);

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.radio_no));
        buttonNo.setCheckable(false);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);
        progressIncrement = 25;

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        initialViewHint = new HintPointer(getActivity(), priceContainer, true, false);

        firstPriceContainerHighlight = new HintHighlighter(getActivity());
        firstPriceHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        secondPriceContainerHighlight = new HintHighlighter(getActivity());
        secondPriceHint = new HintPointer(getActivity(), priceContainer, true, false);
        secondGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        firstMatchContainerHighlight = new HintHighlighter(getActivity());
        firstMatchHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstMatchGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        secondMatchContainerHighlight = new HintHighlighter(getActivity());
        secondMatchHint = new HintPointer(getActivity(), priceContainer, true, false);

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

        textView12 = view.findViewById(R.id.textView12);
        textView12.setText(ViewUtil.getHtmlString(R.string.prices_isthisgood));

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        endButton.setText(ViewUtil.getHtmlString(R.string.button_close));

        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);

        return view;
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
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);

                        welcomeHighlight.dismiss();
                        welcomeHint.dismiss();

                        quitHighlight.dismiss();
                        quitHint.dismiss();

                        initialViewHint.dismiss();

                        firstPriceContainerHighlight.dismiss();
                        firstPriceHint.dismiss();
                        firstGreatChoiceHint.dismiss();

                        secondPriceContainerHighlight.dismiss();
                        secondPriceHint.dismiss();
                        secondGreatChoiceHint.dismiss();

                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        firstMatchGreatChoiceHint.dismiss();

                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();

                        exit();
                    }
                });
            }
        },1200);
    }

    private void setFirstPricesCompare() {
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

        firstPriceContainerHighlight.addTarget(priceContainer, 10);
        firstPriceContainerHighlight.addTarget(progressBar);
        firstPriceContainerHighlight.show();

        initialViewHint.setText(ViewUtil.getString(R.string.popup_tutorial_price_intro));
        initialViewHint.show();

        firstPriceHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose1));

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                initialViewHint.dismiss();
                firstPriceHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink,10000);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstPriceContainerHighlight.dismiss();
                        firstPriceHint.dismiss();
                        initialViewHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(true);

                        firstGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setSecondPricesCompare();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                        firstGreatChoiceHint.show();

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
                        firstPriceContainerHighlight.dismiss();
                        firstPriceHint.dismiss();
                        initialViewHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(false);

                        firstGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setSecondPricesCompare();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                        firstGreatChoiceHint.show();

                        break;
                }
                return true;
            }
        });
    }

    private void setSecondPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item2));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price2));

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                secondPriceContainerHighlight.addTarget(priceContainer, 10);
                secondPriceContainerHighlight.addTarget(progressBar);
                secondPriceContainerHighlight.show();

                secondPriceHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose1));
                secondPriceHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink,10000);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        secondPriceContainerHighlight.dismiss();
                        secondPriceHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(true);

                        secondGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice2));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secondGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setFirstPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        secondGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                        secondGreatChoiceHint.show();

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
                        secondPriceContainerHighlight.dismiss();
                        secondPriceHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(false);

                        secondGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice2));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secondGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setFirstPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        secondGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                        secondGreatChoiceHint.show();

                        break;
                }
                return true;
            }
        });
    }

    private void setFirstPriceMatch() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));
        textviewPrice.setVisibility(View.GONE);

        textView12.setText(ViewUtil.getString(R.string.prices_whatwasprice));

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

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(true);

                        firstMatchGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstMatchGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setSecondPriceMatch();
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

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(false);

                        firstMatchGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstMatchGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setSecondPriceMatch();
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
                secondMatchContainerHighlight.addTarget(priceContainer, 10);
                secondMatchContainerHighlight.addTarget(progressBar);
                secondMatchContainerHighlight.show();

                secondMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose2));
                secondMatchHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink,10000);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
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
                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        incrementProgress();
                        updateButtons(false);

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
}
