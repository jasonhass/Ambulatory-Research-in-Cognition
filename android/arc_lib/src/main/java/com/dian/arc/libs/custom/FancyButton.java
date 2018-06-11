package com.dian.arc.libs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.ScreenUtility;

public class FancyButton extends AppCompatTextView{

    public FancyButton(Context context) {
        super(context);
        init(context,false);
    }

    public FancyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,isRounded(context,attrs));
    }

    public FancyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,isRounded(context,attrs));
    }

    private boolean isRounded(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FancyButton);
        boolean rounded = false;
        try {
            rounded = a.getBoolean(R.styleable.FancyButton_rounded,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        return rounded;
    }

    private void init(Context context,boolean rounded){
        if(!isInEditMode()) {
            setTypeface(FontFactory.georgiaBoldItalic);
            setTextColor(ContextCompat.getColor(context,R.color.white));
        }
        int background = (rounded) ? R.drawable.button_fancy_rounded : R.drawable.button_fancy_straight;
        setBackground(ContextCompat.getDrawable(context, background));
        int padding = (int) ScreenUtility.convertDpToPixel(10,context);
        setPadding(padding,padding,padding,padding);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
    }

    public void setDrawable(int resourceId) {
        ImageSpan imageSpan = new ImageSpan(getContext(),resourceId);
        SpannableString spannableString = new SpannableString("d");
        int start = 0;
        int end = 1;
        int flag = 0;
        spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
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
}
