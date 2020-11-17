//
// SymbolButton.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedFrameLayout;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class SymbolButton extends RoundedLinearLayout {

    ImageView topImage;
    ImageView bottomImage;

    float elevation = ViewUtil.dpToPx(2);

    RoundedLinearLayout layout;

    public SymbolButton(Context context) {
        super(context);
        init(context);
    }

    public SymbolButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SymbolButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.custom_symbol_button, this);
        layout = view.findViewById(R.id.linearLayout);
        topImage = view.findViewById(R.id.symbolTop);
        bottomImage = view.findViewById(R.id.symbolBottom);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.4f);
        }
        super.setEnabled(enabled);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    public void setImages(int topId, int bottomId) {
        topImage.setImageResource(topId);
        topImage.setTag(topId);
        bottomImage.setImageResource(bottomId);
        bottomImage.setTag(bottomId);
    }
}
