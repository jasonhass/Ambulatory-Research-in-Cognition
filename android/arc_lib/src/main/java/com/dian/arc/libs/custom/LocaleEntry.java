package com.dian.arc.libs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dian.arc.libs.R;

public class LocaleEntry extends RelativeLayout{

    TextView text;
    TextView separator;
    String language;
    String country;

    int color_primary;
    int color_white;
    int color_text;

    public LocaleEntry(Context context) {
        super(context);
        init(context);
    }

    public LocaleEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setText(context,attrs);
    }

    public LocaleEntry(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.listitem_locale,this);
        separator = (TextView) view.findViewById(R.id.textviewLocaleSeparator);
        text = (TextView)view.findViewById(R.id.textviewLocaleText);

        color_primary = ContextCompat.getColor(context,R.color.primary);
        color_white = ContextCompat.getColor(context,R.color.white);
        color_text = ContextCompat.getColor(context,R.color.text);
    }

    private void setText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Question);
        try {
            /*
            text.setText(a.getString(R.styleable.Question_text));
            if(a.getBoolean(R.styleable.Question_hide_separator,false)){
                separator.setVisibility(INVISIBLE);
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void setSeparatorVisibility(boolean visible){
        if(visible){
            separator.setVisibility(VISIBLE);
        } else {
            separator.setVisibility(INVISIBLE);
        }
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public String getLanguage(){
        return language;
    }

    public String getCountry(){
        return country;
    }

    public void setText(String question){
        text.setText(question);
    }


    public void toggle(boolean selected){
        if(selected){
            text.setBackgroundColor(color_primary);
            text.setTextColor(color_white);
        } else {
            text.setBackgroundResource(0);
            text.setTextColor(color_text);
        }
    }
}
