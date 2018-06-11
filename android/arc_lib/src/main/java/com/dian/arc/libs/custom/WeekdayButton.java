package com.dian.arc.libs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.FontFactory;

public class WeekdayButton extends FrameLayout {

    Drawable dotOff;
    Drawable dotOn;
    int blue;
    int white;
    boolean on;
    boolean done;
    TextView textview;
    FrameLayout frame;
    ImageView dot;

    public WeekdayButton(Context context) {
        super(context);
        init(context,"");
    }

    public WeekdayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,getText(context,attrs));
    }

    public WeekdayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,getText(context,attrs));
    }

    private String getText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeekdayButton);
        String text = new String();
        try {
            text = a.getString(R.styleable.WeekdayButton_text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        return text;
    }

    private void init(Context context,String text){
        View view = inflate(context,R.layout.custom_weekday_button,this);
        frame = (FrameLayout)view.findViewById(R.id.frameWeekdayButton);
        textview = (TextView)view.findViewById(R.id.textviewWeekdayButton);
        textview.setText(text);
        if(!isInEditMode()) {
            textview.setTypeface(FontFactory.tahomaBold);
        }

        dot = (ImageView) view.findViewById(R.id.imageWeekdayButton);

        white = ContextCompat.getColor(context,R.color.white);
        blue = ContextCompat.getColor(context,R.color.primary);
        dotOff = ContextCompat.getDrawable(context,R.drawable.weekday_off);
        dotOn = ContextCompat.getDrawable(context,R.drawable.weekday_on);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        toggle(false);
    }

    public void toggle(boolean on){
        if(on) {
            frame.setBackgroundColor(white);
            setBackgroundColor(white);
            textview.setTextColor(blue);
            if(done){
                dot.setImageDrawable(dotOn);
            } else {
                dot.setImageDrawable(dotOff);
            }

        } else {
            frame.setBackgroundColor(blue);
            setBackgroundColor(blue);
            textview.setTextColor(white);
            if(done){
                dot.setImageDrawable(dotOff);
            } else {
                dot.setImageDrawable(dotOn);
            }
        }
    }

    public void toggle(){
        on = !on;
        toggle(on);
    }

    public boolean isOn(){
        return on;
    }

    public void setDone(){
        done = true;
        toggle();
        toggle();
    }

    public boolean isDone(){
        return done;
    }

}
