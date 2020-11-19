//
// Signature.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.github.gcacace.signaturepad.views.SignaturePad;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class Signature extends FrameLayout {

    public SignaturePad mSignaturePad;
    public TextView clear;

    public Signature(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.custom_signature,this);

        mSignaturePad = findViewById(R.id.signature_pad);
        clear = findViewById(R.id.clear_signature);
        clear.setText(ViewUtil.getHtmlString(R.string.idverify_undo));
        ViewUtil.underlineTextView(clear);
    }
}
