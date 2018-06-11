package com.dian.arc.libs.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.FontFactory;

public class ToggledButton extends AppCompatTextView {

    Drawable backgroundOn;
    Drawable backgroundOff;
    int white;
    int grey;
    boolean on;

    public ToggledButton(Context context) {
        super(context);
        init(context);
    }

    public ToggledButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToggledButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        if(!isInEditMode()) {
            setTypeface(FontFactory.tahoma);
        }
        white = ContextCompat.getColor(context,R.color.white);
        grey = ContextCompat.getColor(context,R.color.text);
        backgroundOn = ContextCompat.getDrawable(context,R.drawable.button_toggle_on);
        backgroundOff = ContextCompat.getDrawable(context,R.drawable.button_toggle_off);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(context,R.color.white));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        toggle(false);
    }

    public void toggle(boolean on){
        this.on = on;
        if(on) {
            setBackground(backgroundOn);
            setTextColor(white);
        } else {
            setBackground(backgroundOff);
            setTextColor(grey);
        }
    }

    public void toggle(){
        toggle(!on);
    }

    public boolean isOn(){
        return on;
    }

}
