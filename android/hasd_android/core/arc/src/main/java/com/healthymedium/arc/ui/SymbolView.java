//
// SymbolView.java
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
import com.healthymedium.arc.ui.base.RoundedLinearLayout;

public class SymbolView extends RoundedLinearLayout {

    ImageView topImage;
    ImageView bottomImage;

    public SymbolView(Context context) {
        super(context);
        init(context);
    }

    public SymbolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SymbolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_symbol_view,this);
        topImage = view.findViewById(R.id.symbolTop);
        bottomImage = view.findViewById(R.id.symbolBottom);
    }

    public void setImages(int topId,int bottomId){
        topImage.setImageResource(topId);
        topImage.setTag(topId);
        bottomImage.setImageResource(bottomId);
        bottomImage.setTag(bottomId);
    }

}
