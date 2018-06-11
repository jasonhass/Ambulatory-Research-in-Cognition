package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.Question;
import com.dian.arc.libs.custom.Rating;
import com.dian.arc.libs.rest.models.WakeSurvey;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.StatUtil;

public class Wakeup1Fragment extends BaseFragment {

    FancyButton button;
    Question question1;
    Question question2;
    Rating question3;
    boolean answered1;
    boolean answered2;

    public Wakeup1Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wakeup_1, container, false);

        setupDebug(view);

        button = (FancyButton)view.findViewById(R.id.fancybuttonWakeup1Next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WakeSurvey survey = ArcManager.getInstance().getCurrentTestSession().getWakeSurvey();
                survey.setCpuLoad(StatUtil.getUserLoad());
                survey.setMemory(StatUtil.getDeviceMemory(getContext()));
                survey.setWakeTime(question1.getValue());
                survey.setGetUpTime(question2.getValue());
                survey.setSleepQuality(question3.getValue());
                ArcManager.getInstance().getCurrentTestSession().setWakeSurvey(survey);
                ArcManager.getInstance().nextFragment();
            }
        });
        button.setEnabled(false);

        question1 = (Question) view.findViewById(R.id.questionWakeup1Q1);
        question1.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = question1.getValue();
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        question1.setValue(time);
                        answered1 = true;
                        if(answered1 && answered2){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        question2 = (Question) view.findViewById(R.id.questionWakeup1Q2);
        question2.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = question2.getValue();
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        question2.setValue(time);
                        answered2 = true;
                        if(answered1 && answered2){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        question3 = (Rating) view.findViewById(R.id.ratingWakeup1Q3);

        return view;
    }

}
