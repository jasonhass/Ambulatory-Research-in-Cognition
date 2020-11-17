//
// QuestionSignature.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.healthymedium.arc.ui.Signature;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionSignature extends QuestionTemplate {
    boolean allowHelp;
    Signature signature;

    public QuestionSignature(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_next));
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        setHelpVisible(allowHelp);

        //Add spacer
        View spacer = new View(getContext());
        LinearLayout.LayoutParams spacerParams =
                new LinearLayout.LayoutParams (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewUtil.dpToPx(100)
                );
        spacer.setLayoutParams(spacerParams);
        spacer.setBackgroundColor(Color.RED);
        spacer.setVisibility(View.INVISIBLE);
        content.addView(spacer);
        
        signature = new Signature(getContext());
        content.addView(signature);

        textViewSubheader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f);
        textViewSubheader.setLineSpacing(26, 1);

        signature.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signature.mSignaturePad.clear();
            }
        });

        signature.mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                buttonNext.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                buttonNext.setEnabled(false);
            }
        });

        return view;
    }

    @Override
    protected void onNextRequested() {
        Bitmap bitmap = getSignature();
        Study.getRestClient().submitSignature(bitmap);
        if(!bitmap.isRecycled()){
            bitmap.recycle();
        }
        Study.getInstance().openNextFragment();
    }

    public Bitmap getSignature(){
        Bitmap bitmap = signature.mSignaturePad.getSignatureBitmap();
        return bitmap;
    }

    @Override
    public Object onValueCollection(){
        return null;
    }
}
