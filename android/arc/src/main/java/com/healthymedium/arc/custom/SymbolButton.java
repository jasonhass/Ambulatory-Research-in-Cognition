//
// SymbolButton.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;

public class SymbolButton extends LinearLayout {

    ImageView topImage;
    ImageView bottomImage;

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

    private void init(Context context){
        View view = inflate(context,R.layout.custom_symbol_button,this);
        topImage = view.findViewById(R.id.symbolTop);
        bottomImage = view.findViewById(R.id.symbolBottom);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if(enabled){
            setAlpha(1.0f);
        } else {
            setAlpha(0.4f);
        }
        super.setEnabled(enabled);
    }

    public void setSelected(boolean selected){
        if(selected){
            setBackgroundResource(R.drawable.background_symbol_selected);
        } else {
            setBackgroundResource(R.drawable.background_symbol);

        }
    }


    public void setImages(int topId,int bottomId){
        topImage.setImageResource(topId);
        bottomImage.setImageResource(bottomId);
    }

}
