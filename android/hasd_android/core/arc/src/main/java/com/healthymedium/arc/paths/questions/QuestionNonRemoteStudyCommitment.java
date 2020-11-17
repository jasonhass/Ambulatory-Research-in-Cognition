//
// QuestionNonRemoteStudyCommitment.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.templates.AltStandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionNonRemoteStudyCommitment extends QuestionCheckBoxesAlt {

    public QuestionNonRemoteStudyCommitment(boolean allowBack, String header, String subheader, List<String> options, String exclusive) {
        super(allowBack,header,subheader, options, exclusive);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubHeaderTextSize(17);
        setSubHeaderLineSpacing(ViewUtil.dpToPx(9), 1);
    }

    @Override
    protected void onNextRequested() {
        Study.getParticipant().markCommittedToStudy();
        Study.getInstance().openNextFragment();
    }
}
