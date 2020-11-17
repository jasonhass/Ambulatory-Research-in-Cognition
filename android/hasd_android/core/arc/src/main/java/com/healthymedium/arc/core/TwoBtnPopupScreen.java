//
// TwoBtnPopupScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class TwoBtnPopupScreen extends BaseFragment {

    boolean paused;
    boolean shouldSkipSegmentIfPaused;
    TwoBtnDialog dialog;

    public TwoBtnPopupScreen(String headerText, String bodyText, String buttonText, String buttonText2) {
        this.shouldSkipSegmentIfPaused = false;
        dialog = new TwoBtnDialog(headerText, bodyText,buttonText,buttonText2);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        dialog.setOnDialogDismissListener(new SimpleDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                if(!paused) {
                    Study.openNextFragment();
                }
            }
        });

        //dialog.show(getFragmentManager(),TimedDialog.class.getSimpleName());
        dialog.show(getFragmentManager(), TwoBtnDialog.class.getSimpleName());
        return view;
    }

    public void skipSegmentIfPaused(){
        shouldSkipSegmentIfPaused = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            if(shouldSkipSegmentIfPaused){
                Study.skipToNextSegment();
            } else {
                Study.openNextFragment();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog.isVisible()){
            dialog.setOnDialogDismissListener(null);
            dialog.dismiss();
        }
        paused = true;
    }
}
