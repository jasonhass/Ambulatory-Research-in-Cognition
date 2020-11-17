//
// FinishedStudyScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

public class FinishedStudyScreen extends BaseFragment {

    ImageView confetti;

    public FinishedStudyScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getFadingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_study, container, false);

        // get inflated views ----------------------------------------------------------------------

        Button button = view.findViewById(R.id.button);
        confetti = view.findViewById(R.id.imageViewConfetti);

        TextView header = view.findViewById(R.id.textViewHeader);
        header.setTypeface(Fonts.robotoMedium);
        ViewUtil.setLineHeight(header,32);

        TextView textView = view.findViewById(R.id.textView);
        ViewUtil.setLineHeight(textView,26);

        // display progress views ------------------------------------------------------------------

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });
        confetti.animate().translationYBy(-200);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        confetti.animate().translationYBy(200).setDuration(1000);
        confetti.animate().alpha(1.0f).setDuration(1000);
    }

    @Override
    protected void onExitTransitionStart(boolean popped) {
        super.onExitTransitionStart(popped);
        confetti.animate().alpha(0f).setDuration(100);
    }
}
