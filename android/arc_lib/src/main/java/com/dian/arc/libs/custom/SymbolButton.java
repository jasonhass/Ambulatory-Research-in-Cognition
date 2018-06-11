package com.dian.arc.libs.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dian.arc.libs.R;

public class SymbolButton extends LinearLayout{

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
        topImage = (ImageView)view.findViewById(R.id.symbolTop);
        bottomImage = (ImageView)view.findViewById(R.id.symbolBottom);
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


    public void setImages(int topId,int bottomId){
        topImage.setImageResource(topId);
        bottomImage.setImageResource(bottomId);
    }

}
