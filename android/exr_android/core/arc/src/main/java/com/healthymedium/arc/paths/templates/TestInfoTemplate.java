//
// TestInfoTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.TestEnumerations.PriceTestStyle;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.paths.tutorials.PricesTutorialRevised;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.tutorials.GridTutorial;
import com.healthymedium.arc.paths.tutorials.PricesTutorial;
import com.healthymedium.arc.paths.tutorials.SymbolTutorial;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class TestInfoTemplate extends BaseFragment {

    public static final String HINT_GRID_TUTORIAL = "HINT_GRID_TUTORIAL";
    public static final String HINT_PRICES_TUTORIAL = "HINT_PRICES_TUTORIAL";
    public static final String HINT_SYMBOL_TUTORIAL = "HINT_SYMBOL_TUTORIAL";
    public static final String HINT_REPEAT_TUTORIAL = "HINT_REPEAT_TUTORIAL";

    ImageView backgroundImageView;

    String stringTestNumber;
    String stringHeader;
    String stringBody;
    String stringButton;
    String stringType;

    Drawable buttonImage;

    TextView textViewTestNumber;
    TextView textViewHeader;
    TextView textViewBody;
    TextView textViewTutorial;

    LinearLayout content;

    Button button;

    HintPointer tutorialHint;
    HintHighlighter tutorialHintHighlighter;

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable String buttonText) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        stringButton = buttonText;
        stringType = type;
    }

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable Drawable buttonImage) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        this.buttonImage = buttonImage;
        stringType = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_test_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        backgroundImageView = view.findViewById(R.id.backgroundImageView);

        if (stringType.equals("grids")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_grids_bg));
        }
        else if (stringType.equals("symbols")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_symbols_bg));
        }
        else if (stringType.equals("prices")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_prices_bg));
        }

        textViewTestNumber = view.findViewById(R.id.textViewTestNumber);
        textViewTestNumber.setText(stringTestNumber);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);
        textViewHeader.setText(stringHeader);

        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(Html.fromHtml(stringBody));

        SpannableString styledViewTutorialString =
                new SpannableString(Html.fromHtml(Application.getInstance().getResources().getString(R.string.testing_tutorial_link)));
        styledViewTutorialString.setSpan(new UnderlineSpan(), 0, styledViewTutorialString.length(), 0);
        styledViewTutorialString.setSpan(new StyleSpan(Typeface.BOLD), 0, styledViewTutorialString.length(), 0);

        textViewTutorial = view.findViewById(R.id.textViewTutorial);
        textViewTutorial.setText(styledViewTutorialString);
        textViewTutorial.setVisibility(View.VISIBLE);

        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
        }

        button.setEnabled(false);

        // Show a hint if this test type's tutorial has not yet been completed
        if ((stringType.equals("grids") && !Hints.hasBeenShown(HINT_GRID_TUTORIAL))
                || (stringType.equals("prices") && !Hints.hasBeenShown(HINT_PRICES_TUTORIAL))
                || (stringType.equals("symbols") && !Hints.hasBeenShown(HINT_SYMBOL_TUTORIAL))) {
            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, true);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_view));
            tutorialHint.show();
        }
        else if (!Hints.hasBeenShown(HINT_REPEAT_TUTORIAL)) {
            Hints.markShown(HINT_REPEAT_TUTORIAL);

            tutorialHintHighlighter = new HintHighlighter(getActivity());
            tutorialHintHighlighter.addTarget(textViewTutorial, 5, 0);
            tutorialHintHighlighter.show();

            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, true);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_complete));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tutorialHint.dismiss();
                    tutorialHintHighlighter.dismiss();
                    enableButton();
                }
            };

            tutorialHint.addButton(ViewUtil.getString(R.string.popup_gotit), listener);
            tutorialHint.show();
        }
        // If the tutorial has been completed, enable the test button
        else {
            enableButton();
        }

        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainActivity().getWindow().setBackgroundDrawableResource(R.color.secondary);
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                if (stringType.equals("grids")) {
                    Hints.markShown(HINT_GRID_TUTORIAL);
                    GridTutorial gridTutorial = new GridTutorial();
                    NavigationManager.getInstance().open(gridTutorial);
                }
                else if (stringType.equals("symbols")) {
                    Hints.markShown(HINT_SYMBOL_TUTORIAL);
                    SymbolTutorial symbolTutorial = new SymbolTutorial();
                    NavigationManager.getInstance().open(symbolTutorial);
                }
                else if (stringType.equals("prices")) {
                    Hints.markShown(HINT_PRICES_TUTORIAL);
                    if (Config.PRICE_TEST_STYLE == PriceTestStyle.REVISED.getStyle()) {
                        PricesTutorialRevised pricesTutorialRevised = new PricesTutorialRevised();
                        NavigationManager.getInstance().open(pricesTutorialRevised);
                    } else {
                        PricesTutorial pricesTutorial = new PricesTutorial();
                        NavigationManager.getInstance().open(pricesTutorial);
                    }
                }
            }
        });

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMainActivity().getWindow().setBackgroundDrawableResource(R.drawable.core_background);
            }
            }, 1000);
    }

    private void enableButton() {
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                Study.getInstance().openNextFragment();
            }
        });
    }

}
