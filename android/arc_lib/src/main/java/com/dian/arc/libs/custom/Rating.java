package com.dian.arc.libs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.FontFactory;

public class Rating extends LinearLayoutCompat{

    TextView text;
    TextView textLow;
    TextView textHigh;
    SeekBar value;
    TextView separator;

    public Rating(Context context) {
        super(context);
        init(context);
    }

    public Rating(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setText(context,attrs);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.listitem_rating,this);
        separator = (TextView) view.findViewById(R.id.textviewRatingSeparator);
        text = (TextView) view.findViewById(R.id.textviewRatingText);
        textLow = (TextView)view.findViewById(R.id.textviewRatingLow);
        textHigh = (TextView)view.findViewById(R.id.textviewRatingHigh);
        value = (SeekBar) view.findViewById(R.id.seekbarRating);


    }

    private void setText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Rating);
        try {
            text.setText(a.getString(R.styleable.Rating_text));
            textLow.setText(a.getString(R.styleable.Rating_text_low));
            textLow.setTypeface(FontFactory.tahomaBold);
            textHigh.setText(a.getString(R.styleable.Rating_text_high));
            textHigh.setTypeface(FontFactory.tahomaBold);

            if(a.getBoolean(R.styleable.Rating_hide_separator,false)){
                separator.setVisibility(INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public float getValue(){
        return (float)value.getProgress()/100;
    }


}
