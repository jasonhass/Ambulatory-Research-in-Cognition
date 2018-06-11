package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.Question;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;

public class Chronotype3Fragment extends BaseFragment {

    FancyButton button;
    TextView subheader;
    Question questionSleep;
    Question questionWake;
    boolean answeredSleep;
    boolean answeredWake;

    public Chronotype3Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chronotype_3, container, false);

        setupDebug(view);

        subheader = (TextView)view.findViewById(R.id.textviewChronotype3Subheader);
        subheader.setTypeface(FontFactory.getInstance().georgiaItalic);

        button = (FancyButton)view.findViewById(R.id.fancybuttonChronotype3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().getCurrentVisit().getChronotype().workSleepTime = questionSleep.getValue();
                ArcManager.getInstance().getCurrentVisit().getChronotype().workWakeTime = questionWake.getValue();
                ArcManager.getInstance().nextFragment();
            }
        });
        button.setEnabled(false);

        questionSleep = (Question) view.findViewById(R.id.questionChronotype3Sleep);
        questionSleep.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = questionSleep.getValue();
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        questionSleep.setValue(time);
                        answeredSleep = true;
                        if(answeredSleep && answeredWake){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        questionWake = (Question) view.findViewById(R.id.questionChronotype3Wake);
        questionWake.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = questionWake.getValue();
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        questionWake.setValue(time);
                        answeredWake = true;
                        if(answeredSleep && answeredWake){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        return view;
    }

}
