//
// GridTutorial.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tutorials;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.GridBoxView;
import com.healthymedium.arc.utilities.ViewUtil;

public class GridTutorial extends Tutorial {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_GRIDS = "HINT_PREVENT_TUTORIAL_CLOSE_GRIDS";

    int selectedCount;
    Boolean image33Selected = false;
    Boolean image24Selected = false;
    Boolean image41Selected = false;
    Boolean neededInitialHint = false;

    RelativeLayout itemsLayout;

    GridLayout gridLayout;
    GridLayout gridLayoutLetters;

    FrameLayout fullScreenGray;

    GridBoxView image24;
    GridBoxView image33;
    GridBoxView image41;
    GridBoxView image43;

    TextView tapThisF;
    TextView textViewGridInstructions;
    TextView textViewLetterInstructions;

    HintPointer itemsHint;
    HintPointer gridsHint;

    HintPointer partTwoHint;
    HintHighlighter partTwoHighlight;

    HintHighlighter pulsateF;
    HintPointer tapThisFHint;
    HintPointer tapAllFsHint;
    HintHighlighter tapAllFsHighlight;

    HintPointer niceWorkHint;

    HintPointer secondItemsHint;

    HintPointer recallHint;
    HintHighlighter pulsateGridItem;
    HintPointer otherTwoHint;
    HintHighlighter gridHighlight;

    HintPointer remindMeHint;
    HintPointer remindMeTapHint;
    HintHighlighter remindMeHighlight;
    HintHighlighter remindMeTapHighlight;

    Handler remindMeHandler = new Handler();
    Handler firstGridReminderHandler = new Handler();
    Handler handler = new Handler();

    // Run after the user has studied the initial layout of the items in the grid
    // Advances the user to setInitialLetterLayout(), the letter tapping test
    Runnable runnableProceedToPartTwo = new Runnable() {
        @Override
        public void run() {
            incrementProgress();
            partTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    partTwoHint.dismiss();
                    partTwoHighlight.dismiss();

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            fadeOutView(fullScreenGray);
                            setInitialLetterLayout();
                        }
                    };
                    handler.postDelayed(runnable,600);
                }
            };

            partTwoHint.addButton(ViewUtil.getString(R.string.button_next), listener);
            partTwoHint.show();
//            partTwoHighlight.addTarget(partTwoHint);
            partTwoHighlight.addTarget(progressBar);
            partTwoHighlight.show();
        }
    };

    Runnable remindMeRunnable = new Runnable() {
        @Override
        public void run() {
            remindMeHighlight.addTarget(progressBar);
            remindMeHighlight.show();
            remindMeHint.show();
        }
    };

    // Run when the user has exceeded the given time to tap Fs
    // Displays a popup and advances to setSecondItemLayout()
    Runnable runnableTapTheFs = new Runnable() {
        @Override
        public void run() {
            incrementProgress();
            fadeInView(fullScreenGray, 0.9f);

            niceWorkHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf3));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    niceWorkHint.dismiss();

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            fadeOutView(fullScreenGray);
                            fadeOutView(gridLayoutLetters);
                            fadeOutView(textViewLetterInstructions);
                            setSecondItemLayout();
                        }
                    };
                    handler.postDelayed(runnable,600);
                }
            };

            niceWorkHint.addButton(ViewUtil.getString(R.string.button_next), listener);
            niceWorkHint.show();
        }
    };

    Runnable firstGridReminderRunnable = new Runnable() {
        @Override
        public void run() {

            image33.setImage(R.drawable.phone);
            recallHint.setText(ViewUtil.getString(R.string.popup_tutorial_boxhint));
            recallHint.show();

            pulsateGridItem.addPulsingTarget(image33);
            pulsateGridItem.addTarget(progressBar);
            pulsateGridItem.show();
        }
    };

    Runnable otherTwoRunnable = new Runnable() {
        @Override
        public void run() {
            otherTwoHint.dismiss();
            gridHighlight.dismiss();

            // If no response for 5 seconds after the hint has disappeared then show another hint
            remindMeHandler.postDelayed(remindMeRunnable, 5000);
        }
    };

    public GridTutorial() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_tutorial, container, false);

        itemsLayout = view.findViewById(R.id.itemsLayout);

        gridLayout = view.findViewById(R.id.gridLayout);
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            GridBoxView gridBoxView = ((GridBoxView)gridLayout.getChildAt(i));
            gridBoxView.setListener(new GridBoxView.Listener() {
                @Override
                public boolean onSelected(GridBoxView view, boolean selected) {
                    view.setSelectable(false);

                    Integer tag = (Integer) view.getTag();
                    if(tag!=null) {
                        switch (tag) {
                            case 24:
                                image24Selected = true;
                                break;
                            case 33:
                                image33Selected = true;
                                break;
                            case 41:
                                image41Selected = true;
                                break;
                        }
                    }

                    selectedCount++;

                    firstGridReminderHandler.removeCallbacks(firstGridReminderRunnable);
                    remindMeHandler.removeCallbacks(remindMeRunnable);
                    handler.removeCallbacksAndMessages(null);

                    pulsateGridItem.dismiss();
                    recallHint.dismiss();
                    remindMeHint.dismiss();
                    remindMeTapHint.dismiss();
                    remindMeTapHighlight.dismiss();
                    remindMeHighlight.dismiss();
                    remindMeHighlight.clearTargets();

                    hideGridImages();
                    if (selectedCount == 1) {
                        gridHighlight.addTarget(gridLayout, 10, 10);
                        gridHighlight.addTarget(progressBar);
                        otherTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapbox));
                        otherTwoHint.show();
                        gridHighlight.show();

                        handler.postDelayed(otherTwoRunnable, 3000);
                        return true;
                    }

                    if (selectedCount == 2) {
                        otherTwoHint.dismiss();
                        gridHighlight.dismiss();
                        // If no response for 5 seconds after selection then show a hint
                        remindMeHandler.postDelayed(remindMeRunnable, 5000);
                        return true;
                    }

                    if (selectedCount == 3) {
                        remindMeHandler.removeCallbacksAndMessages(null);
                        handler.removeCallbacksAndMessages(null);

                        incrementProgress();
                        setGridsSelectable(false);
                        fadeOutView(gridLayout);
                        fadeOutView(textViewGridInstructions);
                        showComplete();
                        return true;
                    }

                    return true;
                }
            });
        }
        gridLayoutLetters = view.findViewById(R.id.gridLettersLayout);

        fullScreenGray = view.findViewById(R.id.fullScreenGray);
        fullScreenGray.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);
        progressIncrement = 25;

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);
        image24 = view.findViewById(R.id.image24);
        image33 = view.findViewById(R.id.image33);
        image41 = view.findViewById(R.id.image41);
        image43 = view.findViewById(R.id.image43);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        textViewGridInstructions = view.findViewById(R.id.gridInstructions);
        textViewLetterInstructions = view.findViewById(R.id.gridLettersInstructions);

        tapThisF = view.findViewById(R.id.tapThisF);

        endButton = view.findViewById(R.id.endButton);
        endButton.setText(ViewUtil.getHtmlString(R.string.button_close));

        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        itemsHint = new HintPointer(getActivity(), itemsLayout, true, false);
        gridsHint = new HintPointer(getActivity(), image43, false, true);

        partTwoHint = new HintPointer(getActivity(), image43, false, true);
        partTwoHighlight = new HintHighlighter(getActivity());

        pulsateF = new HintHighlighter(getActivity());
        tapThisFHint = new HintPointer(getActivity(), tapThisF, true, false);
        tapAllFsHint = new HintPointer(getActivity(), image43, false, true);
        tapAllFsHighlight = new HintHighlighter(getActivity());

        niceWorkHint = new HintPointer(getActivity(), image43, false, true);

        secondItemsHint = new HintPointer(getActivity(), itemsLayout, true, false);

        recallHint = new HintPointer(getActivity(), image33, true, false);
        pulsateGridItem = new HintHighlighter(getActivity());
        otherTwoHint = new HintPointer(getActivity(), gridLayout, true, true);
        gridHighlight = new HintHighlighter(getActivity());

        remindMeHint = new HintPointer(getActivity(), image43, false, true);
        remindMeTapHint = new HintPointer(getActivity(), gridLayout, true, true);
        remindMeTapHighlight = new HintHighlighter(getActivity());
        remindMeHighlight = new HintHighlighter(getActivity());

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onEnterTransitionStart(boolean popped) {
        super.onEnterTransitionStart(popped);
        if(!Hints.hasBeenShown(HINT_PREVENT_TUTORIAL_CLOSE_GRIDS)) {
            closeButton.setVisibility(View.GONE);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_GRIDS);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setInitialItemLayout();
            }
        },1200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeButton.setEnabled(false);

                        handler.removeCallbacksAndMessages(null);
                        remindMeHandler.removeCallbacksAndMessages(null);
                        firstGridReminderHandler.removeCallbacksAndMessages(null);

                        welcomeHighlight.dismiss();
                        welcomeHint.dismiss();

                        quitHighlight.dismiss();
                        quitHint.dismiss();

                        itemsHint.dismiss();
                        gridsHint.dismiss();

                        partTwoHint.dismiss();
                        partTwoHighlight.dismiss();

                        pulsateF.dismiss();
                        tapThisFHint.dismiss();
                        tapAllFsHint.dismiss();
                        tapAllFsHighlight.dismiss();

                        niceWorkHint.dismiss();

                        secondItemsHint.dismiss();

                        recallHint.dismiss();
                        pulsateGridItem.dismiss();
                        otherTwoHint.dismiss();
                        gridHighlight.dismiss();

                        remindMeHint.dismiss();
                        remindMeTapHint.dismiss();
                        remindMeTapHighlight.dismiss();
                        remindMeHighlight.dismiss();
                        remindMeHighlight.clearTargets();

                        exit();
                    }
                });
            }
        },1200);
    }

    // Displays the items that will appear in the grid and the relevant hints
    private void setInitialItemLayout() {
        itemsHint.setText(ViewUtil.getString(R.string.popup_tutorial_grid_recall));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsHint.dismiss();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        fadeOutView(itemsLayout);
                        setInitialGridLayout();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        itemsHint.addButton(ViewUtil.getString(R.string.popup_gotit), listener);
        itemsHint.show();

    }

    // Displays the initial layout of the items in the grid
    // Displays hints
    private void setInitialGridLayout() {
        setGridsSelectable(false);
        fadeInView(gridLayout, 1f);
        fadeInView(fullScreenGray, 0.9f);

        getImageView(3,0).setImage(R.drawable.pen);
        getImageView(2,2).setImage(R.drawable.phone);
        getImageView(1,3).setImage(R.drawable.key);

        gridsHint.setText(ViewUtil.getString(R.string.popup_tutorial_rememberbox));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridsHint.dismiss();
                fadeOutView(fullScreenGray);

                handler.postDelayed(runnableProceedToPartTwo,3000);
            }
        };

        gridsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
        gridsHint.show();
    }

    // Displays the letters layout and prompts the user to tap a specific letter F
    private void setInitialLetterLayout() {
        fadeOutView(gridLayout);
        fadeInView(gridLayoutLetters, 1f);
        fadeInView(textViewLetterInstructions, 1f);

        fadeOutView(fullScreenGray);

        Typeface font = Fonts.georgia;
        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)gridLayoutLetters.getChildAt(i)).setTypeface(font);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pulsateF.addPulsingTarget(tapThisF,22);
                pulsateF.addTarget(progressBar);
                pulsateF.show();

                tapThisFHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf1));
                tapThisFHint.show();

                View.OnTouchListener listener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        switch (action){
                            case MotionEvent.ACTION_DOWN:
                                if((view.getTag() == null) || (view.getTag().equals(false))){
                                    view.setTag(true);
                                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));

                                    pulsateF.dismiss();
                                    tapThisFHint.dismiss();
                                    incrementProgress();

                                    tapAllFsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf2));

                                    View.OnClickListener listener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            tapAllFsHint.dismiss();
                                            tapAllFsHighlight.dismiss();
                                            tapLetters();
                                        }
                                    };

                                    tapAllFsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
                                    tapAllFsHighlight.addTarget(progressBar);
                                    tapAllFsHighlight.show();
                                    tapAllFsHint.show();

                                    return false;
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                        }
                        return false;
                    }
                };

                tapThisF.setOnTouchListener(listener);

            }
        },500);


    }

    // Responds to letter that are tapped, changes their color
    private void tapLetters() {
        handler.postDelayed(runnableTapTheFs,8000);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        if(view.getTag() == null){
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                            return false;
                        }
                        if(view.getTag().equals(false)) {
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                        } else {
                            view.setTag(false);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };

        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            gridLayoutLetters.getChildAt(i).setOnTouchListener(listener);
        }
    }

    // Displays the same items as setInitialItemLayout()
    // Displays a new hint
    private void setSecondItemLayout() {
        fadeInView(itemsLayout, 1f);

        secondItemsHint.setText(ViewUtil.getString(R.string.popup_tutorial_selectbox));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondItemsHint.dismiss();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        fadeOutView(itemsLayout);
                        setGridRecall();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        secondItemsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
        secondItemsHint.show();
    }

    // Displays the grid recall test and associated hints/prompts
    // TODO
    // This is all awful and super gross, make it cleaner at some point
    private void setGridRecall() {
        setGridsSelectable(true);
        fadeInView(gridLayout, 1f);
        fadeInView(textViewGridInstructions, 1f);

        fadeOutView(fullScreenGray);
        gridLayout.setVisibility(View.VISIBLE);

        getImageView(3,0).removeImage();
        getImageView(2,2).removeImage();
        getImageView(1,3).removeImage();

        selectedCount = 0;

        firstGridReminderHandler.postDelayed(firstGridReminderRunnable,5000);

        View.OnClickListener remindMeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeHint.dismiss();
                remindMeHighlight.dismiss();
                remindMeHighlight.clearTargets();

                final int maxTargets;
                if (selectedCount == 2) {
                    maxTargets = 1;
                    remindMeTapHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapbox3));
                } else {
                    maxTargets = 2;
                    remindMeTapHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapbox2));
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        remindMeTapHint.show();
                        remindMeHighlights(maxTargets);
                    }
                }, 600);
            }
        };

        remindMeHint.setText(ViewUtil.getString(R.string.popup_tutorial_needhelp));
        remindMeHint.addButton(ViewUtil.getString(R.string.popup_tutorial_remindme), remindMeListener);

        gridLayout.getChildAt(8).setTag(24);
        gridLayout.getChildAt(12).setTag(33);
        gridLayout.getChildAt(15).setTag(41);
    }

    // Determines which items to highlight for the remind me hints in the grid recall
    private void remindMeHighlights(int maxTargets) {
        int targetCount = 0;
        remindMeTapHighlight = new HintHighlighter(getActivity());
        remindMeTapHighlight.addTarget(progressBar);

        if (!image33Selected) {
            targetCount++;
            getImageView(2,2).setImage(R.drawable.phone);
            remindMeTapHighlight.addPulsingTarget(image33, 0);
        }
        if(targetCount>=maxTargets){
            remindMeTapHighlight.show();
            return;
        }
        if (!image41Selected) {
            targetCount++;
            getImageView(3,0).setImage(R.drawable.pen);
            remindMeTapHighlight.addPulsingTarget(image41, 0);
        }
        if(targetCount>=maxTargets){
            remindMeTapHighlight.show();
            return;
        }
        if (!image24Selected) {
            targetCount++;
            getImageView(1,3).setImage(R.drawable.key);
            remindMeTapHighlight.addPulsingTarget(image24, 0);
        }
        remindMeTapHighlight.show();
    }

    private void hideGridImages() {
        getImageView(1,3).removeImage();
        getImageView(2,2).removeImage();
        getImageView(3,0).removeImage();
    }

    private GridBoxView getImageView(int row, int col){
        return (GridBoxView)gridLayout.getChildAt((5*row)+col);
    }

    private void setGridsSelectable(boolean selectable) {
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            GridBoxView view = ((GridBoxView)gridLayout.getChildAt(i));
            view.setSelectable(selectable);
        }
    }

}
