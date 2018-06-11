package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.Question;
import com.dian.arc.libs.rest.models.WakeSurvey;
import com.dian.arc.libs.utilities.ArcManager;

public class Wakeup0Fragment extends BaseFragment {

    FancyButton button;
    Question question1;
    Question question2;
    Question question3;
    boolean answered1;
    boolean answered2;
    boolean answered3;

    public Wakeup0Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wakeup_0, container, false);

        setupDebug(view);

        button = (FancyButton)view.findViewById(R.id.fancybuttonWakeup0Next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WakeSurvey survey = new WakeSurvey();
                survey.setBedTime(question1.getValue());
                survey.setSleepTime(question2.getValue());
                survey.setNumWakes(Integer.valueOf(question3.getValue().split(" ")[0]));
                ArcManager.getInstance().getCurrentTestSession().setWakeSurvey(survey);
                ArcManager.getInstance().nextFragment();
            }
        });
        button.setEnabled(false);

        question1 = (Question) view.findViewById(R.id.questionWakeup0Q1);
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
                        if(answered1 && answered2 && answered3){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        question2 = (Question) view.findViewById(R.id.questionWakeup0Q2);
        question2.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogDuration dialog = new QuestionDialogDuration();
                dialog.duration = question2.getValue();
                dialog.setOnDialogDismissListener(new QuestionDialogDuration.OnDialogDismiss() {
                    @Override
                    public void dismiss(String duration) {
                        question2.setValue(duration);
                        answered2 = true;
                        if(answered1 && answered2 && answered3){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogDuration.class.getSimpleName());
            }
        });

        question3 = (Question) view.findViewById(R.id.questionWakeup0Q3);
        question3.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                QuestionDialogNumber dialog = new QuestionDialogNumber();
                String value = question3.getValue().split(" ")[0];
                if(!value.isEmpty()){
                    dialog.value = Integer.valueOf(value);
                }
                dialog.max = 20;
                dialog.min = 0;
                dialog.setOnDialogDismissListener(new QuestionDialogNumber.OnDialogDismiss() {
                    @Override
                    public void dismiss(String times) {
                        if(Integer.valueOf(times)!=1) {
                            question3.setValue(times + " ");
                        } else {
                            question3.setValue(times + " ");
                        }
                        answered3 = true;
                        if(answered1 && answered2 && answered3){
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
