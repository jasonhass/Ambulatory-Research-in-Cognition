package com.dian.arc.libs.custom;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.Calendar;

public class CalendarView extends RelativeLayout{

    DateTime calendar;
    TextView title;
    String[] monthArray;
    ImageView navPrev;
    ImageView navNext;
    GridLayout gridLayout;
    int lastSelectedIndex = -1;
    int numDays;
    int startIndex;
    OnDateSelectedListener listener;
    int colorText;
    int colorGrey;
    DateTime today;

    public CalendarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.custom_calendar,this);
        today = DateTime.now();
        calendar = JodaUtil.setMidnight(DateTime.now().withDayOfMonth(1));
        colorText = ContextCompat.getColor(context,R.color.text);
        colorGrey = ContextCompat.getColor(context,R.color.grey);

        title = (TextView)view.findViewById(R.id.textviewCalendarTitle);
        if(!isInEditMode()) {
            title.setTypeface(FontFactory.georgiaBoldItalic);
        }
        Context c = getContext();
        final String string = c.getString(R.string.month_january)+","+c.getString(R.string.month_febuary)+","+c.getString(R.string.month_march)+","+c.getString(R.string.month_april)+","+c.getString(R.string.month_may)+","+c.getString(R.string.month_june)+","+c.getString(R.string.month_july)+","+c.getString(R.string.month_august)+","+c.getString(R.string.month_september)+","+c.getString(R.string.month_october)+","+c.getString(R.string.month_november)+","+c.getString(R.string.month_december);
        monthArray = string.split(",");

        navPrev = (ImageView)view.findViewById(R.id.imageCalendarNavLeft);
        navPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(prevMonth());
            }
        });

        navNext = (ImageView)view.findViewById(R.id.imageCalendarNavRight);
        navNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(nextMonth());
            }
        });

        gridLayout = (GridLayout)view.findViewById(R.id.gridCalendar);
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            final int finalI = i;
            gridLayout.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI < startIndex || (finalI >= startIndex+numDays)){
                        return;
                    }
                    if(((TextView)view).getCurrentTextColor()==colorGrey){
                        return;
                    }
                    if(lastSelectedIndex != -1){
                        gridLayout.getChildAt(lastSelectedIndex).setBackgroundResource(R.drawable.background_app);
                        ((TextView)gridLayout.getChildAt(lastSelectedIndex)).setTextColor(ContextCompat.getColor(getContext(),R.color.text));
                    }
                    gridLayout.getChildAt(finalI).setBackgroundResource(R.color.primary);
                    ((TextView)gridLayout.getChildAt(finalI)).setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                    lastSelectedIndex = finalI;
                    if(listener != null){
                        listener.dateSelected(getDate());
                    }
                }
            });
        }
        title.setText(monthArray[calendar.getMonthOfYear()-1]+" "+calendar.getYear());
        populateDays();
    }

    private String prevMonth(){
        if(listener != null){
            listener.invalidated();
        }
        calendar = calendar.minusMonths(1).withDayOfMonth(1);
        populateDays();
        return monthArray[calendar.getMonthOfYear()-1]+" "+calendar.getYear();
    }

    private String nextMonth(){
        if(listener != null){
            listener.invalidated();
        }
        calendar = calendar.plusMonths(1).withDayOfMonth(1);
        populateDays();
        return monthArray[calendar.getMonthOfYear()-1]+" "+calendar.getYear();
    }

    private void populateDays(){
        numDays = calendar.dayOfMonth().getMaximumValue();
        startIndex = calendar.getDayOfWeek();

        int year = calendar.getYear();
        int month = calendar.getMonthOfYear();

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(year,month-1,1);
        if((int)tempCalendar.get(Calendar.WEEK_OF_MONTH)>1){
            startIndex+=7;
        }

        if(lastSelectedIndex != -1){
            gridLayout.getChildAt(lastSelectedIndex).setBackgroundResource(R.drawable.background_app);
            ((TextView)gridLayout.getChildAt(lastSelectedIndex)).setTextColor(ContextCompat.getColor(getContext(),R.color.text));
            lastSelectedIndex = -1;
        }

        int validStart = 0;
        if(((month)< today.getMonthOfYear() && year == today.getYear()) || year < today.getYear()){
            validStart = 42;
        } else if((month)> today.getMonthOfYear() || year > today.getYear()){
            validStart = 1;
        } else {
            validStart = today.getDayOfMonth();
        }

        // if selected before, highlight

        for(int i=0;i<startIndex;i++) {
            ((TextView)gridLayout.getChildAt(i)).setText("");
        }
        for(int i=0;i<numDays;i++) {
            ((TextView)gridLayout.getChildAt(startIndex+i)).setText(String.valueOf(i+1));

            int color = ((i+1)>=validStart) ? colorText:colorGrey;
            ((TextView)gridLayout.getChildAt(startIndex+i)).setTextColor(color);
        }
        int max = 42;
        for(int i=startIndex+numDays;i<max;i++) {
            ((TextView)gridLayout.getChildAt(i)).setText("");
        }
    }

    public boolean hasValidDate(){
        return lastSelectedIndex != -1;
    }

    public DateTime getDate(){
        int day = (lastSelectedIndex-startIndex)+1;
        return calendar.withDayOfMonth(day);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener){
        this.listener = listener;
    }

    public interface OnDateSelectedListener{
        void dateSelected(DateTime date);
        void invalidated();
    }
}
