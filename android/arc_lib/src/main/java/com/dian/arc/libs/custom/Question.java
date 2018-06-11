package com.dian.arc.libs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dian.arc.libs.R;

public class Question extends LinearLayoutCompat{

    OnClickListener listener;
    FancyButton button;
    TextView text;
    TextView value;
    TextView edit;
    TextView separator;

    public Question(Context context) {
        super(context);
        init(context);
    }

    public Question(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setText(context,attrs);
    }

    public Question(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.listitem_question,this);
        separator = (TextView) view.findViewById(R.id.textviewQuestionSeparator);
        text = (TextView)view.findViewById(R.id.textviewQuestionText);
        value = (TextView)view.findViewById(R.id.textviewQuestionValue);
        edit = (TextView)view.findViewById(R.id.textviewQuestionValueEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onEdit();
                }
            }
        });
        button = (FancyButton)view.findViewById(R.id.fancybuttonQuestion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onEdit();
                    button.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Question);
        try {
            text.setText(a.getString(R.styleable.Question_text));
            button.setText(a.getString(R.styleable.Question_button_text));
            if(a.getBoolean(R.styleable.Question_hide_separator,false)){
                separator.setVisibility(INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void setText(String question){
        text.setText(question);
    }

    public String getValue(){
        return value.getText().toString();
    }

    public void setValue(String value){this.value.setText(value);}

    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }

    public interface OnClickListener{
        void onEdit();
    }
}
