//
// QuestionRemoteStudyCommitment.java
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
import com.healthymedium.arc.paths.informative.RebukedCommitmentThankYouScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionRemoteStudyCommitment extends QuestionPolarAlt {

    public QuestionRemoteStudyCommitment(boolean allowBack, String header, String subheader, String yesAnswer, String noAnswer) {
        super(allowBack,header,subheader, yesAnswer, noAnswer);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubHeaderTextSize(17);
        setSubHeaderLineSpacing(ViewUtil.dpToPx(9), 1);
    }

    @Override
    protected void onNextRequested() {
        if (answered) {
            if (answerIsYes) {
                // go to next fragment
                Study.getParticipant().markCommittedToStudy();
                Study.getInstance().openNextFragment();
            } else {
                // go to thank you screen
                Study.getParticipant().rebukeCommitmentToStudy();
                Study.getParticipant().save();
                BaseFragment fragment = new RebukedCommitmentThankYouScreen(ViewUtil.getString(R.string.onboarding_nocommit_header), ViewUtil.getString(R.string.onboarding_nocommit_body), false);
                fragment.setTransitionSet(TransitionSet.getSlidingDefault());
                NavigationManager.getInstance().open(fragment);
            }
        }
    }

}
