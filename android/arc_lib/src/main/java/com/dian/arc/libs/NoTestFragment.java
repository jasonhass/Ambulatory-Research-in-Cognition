package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;

import org.joda.time.DateTime;

public class NoTestFragment extends BaseFragment {

    public NoTestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_none, container, false);

        DateTime dateTime = ArcManager.getInstance().getCurrentVisit().getUserStartDate();
        if(dateTime.isBeforeNow()){
            ArcManager.getInstance().setPathBetweenTests();
            ArcManager.getInstance().nextFragment(true);
        }

        if(ArcManager.getInstance().getLifecycleStatus()==ArcManager.LIFECYCLE_IDLE){

        } else if(ArcManager.getInstance().getCurrentTestSession().getSessionDate().isBeforeNow()){
            if(ArcManager.getInstance().getCurrentTestSession().getExpirationDate().isBeforeNow()){
                ArcManager.getInstance().markTestMissed();
            }
            ArcManager.getInstance().decidePath();
            ArcManager.getInstance().setupPath();
            ArcManager.getInstance().nextFragment(true);
        }

        final TextView textView = (TextView) view.findViewById(R.id.textviewTestNextDayHeader);
        textView.setTypeface(FontFactory.georgiaItalic);
        textView.setText(getString(R.string.confirm_body1).replace("{DATE}",dateTime.toString(getString(R.string.format_date))));

        setupDebug(view,R.id.textviewTestNextDayHeader);

        final TextView change = (TextView)view.findViewById(R.id.textView34);
        if(DateTime.now().plusDays(1).isAfter(dateTime)){
            change.setVisibility(View.GONE);
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDialogDate dialog = new QuestionDialogDate();
                dialog.setOnDialogDismissListener(new QuestionDialogDate.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        textView.setText(getString(R.string.confirm_body1).replace("{DATE}",time));
                        ArcManager.getInstance().saveState();
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogDate.class.getSimpleName());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ArcManager.getInstance().getLifecycleStatus()==ArcManager.LIFECYCLE_IDLE){

        } else if(ArcManager.getInstance().getCurrentTestSession().getSessionDate().isBeforeNow()){
            if(ArcManager.getInstance().getCurrentTestSession().getExpirationDate().isBeforeNow()){
                ArcManager.getInstance().markTestMissed();
            }
            ArcManager.getInstance().decidePath();
            ArcManager.getInstance().setupPath();
            ArcManager.getInstance().nextFragment(true);
        }
    }
}
