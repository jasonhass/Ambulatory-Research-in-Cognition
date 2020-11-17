//
// SetupParticipant.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupParticipant extends SetupTemplate {

    public SetupParticipant(int firstDigitCount, int secondDigitCount) {
        super(false, false, firstDigitCount, secondDigitCount, ViewUtil.getString(R.string.login_enter_ARCID));
    }

    @Override
    protected void onNextRequested() {
        SetupPathData setupPathData = (SetupPathData) Study.getCurrentSegmentData();
        String newId = characterSequence.toString();
        if(!newId.equals(setupPathData.id)){
            setupPathData.requested2FA = false;
        }
        setupPathData.id = newId;
        super.onNextRequested();
    }

}
