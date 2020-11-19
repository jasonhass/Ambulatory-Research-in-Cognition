//
// SymbolTutorial.java
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

import com.healthymedium.arc.ui.SymbolButton;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.SymbolView;
import com.healthymedium.arc.utilities.ViewUtil;

public class SymbolTutorial extends Tutorial {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS = "HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS";

    final Handler handler = new Handler();

    Runnable runnableTileFirst;
    Runnable runnableTileSecond;

    RelativeLayout topSymbols;
    RelativeLayout topSymbolsInnerLayout;
    RelativeLayout bottomSymbolsButtons;

    SymbolView buttonTop1;
    SymbolView buttonTop2;
    SymbolView buttonTop3;
    SymbolButton buttonBottom1;
    SymbolButton buttonBottom2;

    TextView textView20;

    HintHighlighter buttonTop2Highlight;
    HintPointer buttonTop2Hint;

    HintHighlighter topSymbolsHighlight;
    HintPointer topSymbolsHint;

    HintHighlighter bottomSymbolsHighlight;
    HintPointer bottomSymbolsHint;

    HintHighlighter initialTilesOutline;
    HintPointer initialPointer;

    HintHighlighter secondTilesOutline;
    HintHighlighter secondTilesPulsate;
    HintPointer secondPointer;

    HintHighlighter finalTilesOutline;
    HintHighlighter finalTilesPulsate;
    HintPointer finalPointer;

    HintHighlighter greatJobHighlight;
    HintPointer greatJobHint;
    HintPointer niceHint;

    public SymbolTutorial() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_symbols_tutorial, container, false);

        topSymbols = view.findViewById(R.id.topSymbols);
        topSymbolsInnerLayout = view.findViewById(R.id.topSymbolsInnerLayout);
        bottomSymbolsButtons = view.findViewById(R.id.bottomSymbolsButtons);

        buttonTop1 = view.findViewById(R.id.symbolbutton_top1);
        buttonTop2 = view.findViewById(R.id.symbolbutton_top2);
        buttonTop3 = view.findViewById(R.id.symbolbutton_top3);

        buttonTop1.setVisibility(View.GONE);
        buttonTop3.setVisibility(View.GONE);

        buttonBottom1 = view.findViewById(R.id.symbolbutton_bottom1);
        buttonBottom2 = view.findViewById(R.id.symbolbutton_bottom2);

        buttonBottom1.setVisibility(View.GONE);
        buttonBottom2.setVisibility(View.GONE);

        buttonBottom1.setOnClickListener(null);
        buttonBottom2.setOnClickListener(null);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);
        progressIncrement = 34;

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        textView20 = view.findViewById(R.id.textView20);
        textView20.setText(Html.fromHtml(ViewUtil.getString(R.string.popup_tutorial_middle_instructions)));
        textView20.setVisibility(View.INVISIBLE);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        endButton.setText(ViewUtil.getHtmlString(R.string.button_close));

        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_8_tutorial, R.drawable.ic_symbol_1_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_7_tutorial, R.drawable.ic_symbol_3_tutorial);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        buttonTop2Highlight = new HintHighlighter(getActivity());
        buttonTop2Hint = new HintPointer(getActivity(), buttonTop2, true, false);

        topSymbolsHighlight = new HintHighlighter(getActivity());
        topSymbolsHint = new HintPointer(getActivity(), topSymbols, true, false);

        bottomSymbolsHighlight = new HintHighlighter(getActivity());
        bottomSymbolsHint = new HintPointer(getActivity(), bottomSymbolsButtons, true, true);

        initialTilesOutline = new HintHighlighter(getActivity());
        initialPointer = new HintPointer(getActivity(), buttonBottom1, true, false);

        secondTilesOutline = new HintHighlighter(getActivity());
        secondTilesPulsate = new HintHighlighter(getActivity());
        secondPointer = new HintPointer(getActivity(), buttonBottom2, true, false);

        finalTilesOutline = new HintHighlighter(getActivity());
        finalTilesPulsate = new HintHighlighter(getActivity());
        finalPointer = new HintPointer(getActivity(), buttonBottom1, true, false);

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);

        return view;
    }

    @Override
    protected void onEnterTransitionStart(boolean popped) {
        super.onEnterTransitionStart(popped);
        if(!Hints.hasBeenShown(HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS)) {
            closeButton.setVisibility(View.GONE);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stepMiddleTopTile();
                }
            },1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeButton.setEnabled(false);
                        handler.removeCallbacks(runnableTileFirst);
                        handler.removeCallbacks(runnableTileSecond);

                        welcomeHighlight.dismiss();
                        welcomeHint.dismiss();

                        quitHighlight.dismiss();
                        quitHint.dismiss();

                        buttonTop2Highlight.dismiss();
                        buttonTop2Hint.dismiss();

                        topSymbolsHighlight.dismiss();
                        topSymbolsHint.dismiss();

                        bottomSymbolsHighlight.dismiss();
                        bottomSymbolsHint.dismiss();

                        initialTilesOutline.dismiss();
                        initialPointer.dismiss();

                        secondTilesOutline.dismiss();
                        secondTilesPulsate.dismiss();
                        secondPointer.dismiss();

                        finalTilesOutline.dismiss();
                        finalTilesPulsate.dismiss();
                        finalPointer.dismiss();

                        if(greatJobHint!=null){
                            greatJobHint.dismiss();
                        }

                        if(niceHint!=null){
                            niceHint.dismiss();
                        }

                        exit();
                    }
                });
            }
        },1200);
    }

    // Displays hints for the middle tile of the top set
    // Explains what a tile is
    private void stepMiddleTopTile() {
        buttonTop2Highlight.addTarget(buttonTop2, 10, 10);
        buttonTop2Highlight.addTarget(progressBar);
        buttonTop2Highlight.show();

        buttonTop2Hint.setText(ViewUtil.getString(R.string.popup_tutorial_tile));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonTop2Highlight.dismiss();
                buttonTop2Hint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        stepAllTopTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        buttonTop2Hint.addButton(ViewUtil.getString(R.string.button_next), listener);

        buttonTop2Hint.show();
    }

    // Displays hints for the entire top set of tiles
    private void stepAllTopTiles() {
        buttonTop1.setVisibility(View.VISIBLE);
        buttonTop3.setVisibility(View.VISIBLE);

        topSymbolsHighlight.addTarget(topSymbolsInnerLayout, 10, 0);
        topSymbolsHighlight.addTarget(progressBar);
        topSymbolsHighlight.show();

        topSymbolsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tilestop));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSymbolsHighlight.dismiss();
                topSymbolsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        stepBottomTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        topSymbolsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        topSymbolsHint.show();
    }

    // Displays hints for the entire bottom set of tiles
    private void stepBottomTiles() {
        buttonBottom1.setVisibility(View.VISIBLE);
        buttonBottom2.setVisibility(View.VISIBLE);
        textView20.setVisibility(View.VISIBLE);

        bottomSymbolsHighlight.addTarget(bottomSymbolsButtons, 10, 0);
        bottomSymbolsHighlight.addTarget(progressBar);
        bottomSymbolsHighlight.show();

        bottomSymbolsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tilesbottom));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSymbolsHighlight.dismiss();
                bottomSymbolsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        initialTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        bottomSymbolsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        bottomSymbolsHint.show();
    }

    // The first set of tiles to match
    private void initialTiles() {

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        view.setSelected(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setSelected(false);
                        break;
                }
                return false;
            }
        };

        buttonBottom1.setOnTouchListener(touchListener);
        buttonBottom2.setOnTouchListener(touchListener);

        initialTilesOutline.addPulsingTarget(buttonBottom1,4);
        initialTilesOutline.addTarget(buttonTop3,6);
        initialTilesOutline.addTarget(progressBar);

        runnableTileFirst = new Runnable() {
            @Override
            public void run() {
                initialTilesOutline.show();
                initialPointer.setText(ViewUtil.getString(R.string.popup_tutorial_tiletap));
                initialPointer.show();
            }
        };

        handler.postDelayed(runnableTileFirst,10000);
        handler.postDelayed(runnableTileSecond, 10000);

        View.OnTouchListener onPressListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.removeCallbacks(runnableTileFirst);
                    handler.removeCallbacks(runnableTileSecond);

                    initialTilesOutline.dismiss();
                    initialPointer.dismiss();

                    buttonBottom1.setOnClickListener(null);
                    buttonBottom2.setOnClickListener(null);
                    buttonBottom1.setOnTouchListener(null);
                    buttonBottom2.setOnTouchListener(null);
                    incrementProgress();

                    greatJobHint = new HintPointer(getActivity(), bottomSymbolsButtons, false, true);
                    greatJobHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatjob));
                    greatJobHighlight = new HintHighlighter(getActivity());
                    greatJobHighlight.addTarget(progressBar);
                    greatJobHighlight.show();

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            greatJobHint.dismiss();
                            greatJobHighlight.dismiss();

                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    secondTiles();
                                }
                            };
                            handler.postDelayed(runnable,600);
                        }
                    };

                    greatJobHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                    greatJobHint.show();
                }

                return v.performClick();
            }

        };

         buttonBottom1.setOnTouchListener(onPressListener);
         buttonBottom2.setOnTouchListener(onPressListener);
    }

    // The second set of tiles to match
    private void secondTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        secondTilesOutline.addPulsingTarget(buttonBottom2,4);
        secondTilesOutline.addTarget(buttonTop2,6);
        secondTilesOutline.addTarget(progressBar);
        secondTilesOutline.setShadowEnabled(false,false);

        runnableTileFirst = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.show();
            }
        };

        runnableTileSecond = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.setShadowEnabled(true,true);
                secondPointer.setText(ViewUtil.getString(R.string.popup_tutorial_tiletap));
                secondPointer.show();
            }
        };

        handler.postDelayed(runnableTileFirst,10000);
        handler.postDelayed(runnableTileSecond,15000);


        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.removeCallbacks(runnableTileFirst);
                    handler.removeCallbacks(runnableTileSecond);

                    secondTilesOutline.dismiss();
                    secondTilesPulsate.dismiss();
                    secondPointer.dismiss();

                    buttonBottom1.setOnClickListener(null);
                    buttonBottom2.setOnClickListener(null);
                    incrementProgress();

                    niceHint = new HintPointer(getActivity(), bottomSymbolsButtons, false, true);
                    niceHint.setText(ViewUtil.getString(R.string.popup_tutorial_nice));
                    greatJobHighlight = new HintHighlighter(getActivity());
                    greatJobHighlight.addTarget(progressBar);
                    greatJobHighlight.show();

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            niceHint.dismiss();
                            greatJobHighlight.dismiss();

                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    lastTiles();
                                }
                            };
                            handler.postDelayed(runnable,600);
                        }
                    };

                    niceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                    niceHint.show();
                }
                return v.performClick();
            }
        };

        buttonBottom1.setOnTouchListener(onTouchListener);
        buttonBottom2.setOnTouchListener(onTouchListener);
    }

    // The final set of tiles to match
    private void lastTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        finalTilesOutline.addPulsingTarget(buttonBottom1, 4);
        finalTilesOutline.addTarget(buttonTop1, 6);
        finalTilesOutline.addTarget(progressBar);
        finalTilesOutline.setShadowEnabled(false, false);

        runnableTileFirst = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.show();
            }
        };

        runnableTileSecond = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.setShadowEnabled(true, true);
                finalPointer.setText(ViewUtil.getString(R.string.popup_tutorial_tiletap));
                finalPointer.show();
            }
        };

        handler.postDelayed(runnableTileFirst, 10000);
        handler.postDelayed(runnableTileSecond, 15000);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.removeCallbacks(runnableTileFirst);
                    handler.removeCallbacks(runnableTileSecond);

                    finalTilesOutline.dismiss();
                    finalTilesPulsate.dismiss();
                    finalPointer.dismiss();

                    fadeOutView(topSymbols);
                    fadeOutView(textView20);
                    fadeOutView(bottomSymbolsButtons);

                    buttonBottom1.setOnClickListener(null);
                    buttonBottom2.setOnClickListener(null);
                    incrementProgress();

                    showComplete();
                }

                return v.performClick();
            }
        };

        buttonBottom1.setOnTouchListener(onTouchListener);
        buttonBottom2.setOnTouchListener(onTouchListener);
    }
}
