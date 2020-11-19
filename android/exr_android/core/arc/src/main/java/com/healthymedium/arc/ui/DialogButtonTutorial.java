//
// DialogButtonTutorial.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

public class DialogButtonTutorial extends LinearLayout {

    public TextView header;
    public TextView body;
    public TextView button;

    private FrameLayout lineFrameLayout;

    public DialogButtonTutorial(Context context) {
        super(context);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.dialog_btn_tutorial,this);
        header = view.findViewById(R.id.dialogBtnTutorialHeader);
        body = view.findViewById(R.id.dialogBtnTutorialBody);
        button = view.findViewById(R.id.button);

        lineFrameLayout = view.findViewById(R.id.lineFrameLayout);

        SpannableString content = new SpannableString("View a Tutorial");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        button.setText(content);
    }

    public void enableButton() {
        lineFrameLayout.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public void disableButton() {
        lineFrameLayout.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
    }

}
