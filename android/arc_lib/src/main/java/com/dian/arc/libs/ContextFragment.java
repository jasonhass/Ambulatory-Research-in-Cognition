package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.Question;
import com.dian.arc.libs.custom.Rating;
import com.dian.arc.libs.rest.models.ContextSurvey;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.StatUtil;

public class ContextFragment extends BaseFragment {

    FancyButton button;
    Question question1;
    Question question2;
    Rating question3;
    Rating question4;
    Question question5;
    boolean answered1;
    boolean answered2;
    boolean answered5;

    public ContextFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_context, container, false);

        setupDebug(view);

        button = (FancyButton)view.findViewById(R.id.fancybuttonContextDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                ContextSurvey survey = new ContextSurvey();
                survey.setCpuLoad(StatUtil.getUserLoad());
                survey.setMemory(StatUtil.getDeviceMemory(getContext()));
                survey.setWhoIsWith(question1.getValue());
                survey.setLocation(question2.getValue());
                survey.setMood(question3.getValue());
                survey.setAlertness(question4.getValue());
                survey.setRecentActivity(question5.getValue());
                ArcManager.getInstance().getCurrentTestSession().setContextSurvey(survey);
                ArcManager.getInstance().nextFragment();
            }
        });
        button.setEnabled(false);


        question1 = (Question) view.findViewById(R.id.questionContextQ1);
        question1.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                ListDialog dialog = new ListDialog();
                dialog.title = getString(R.string.list_selectall);
                dialog.selected = question1.getValue();
                dialog.aryaTweak = true;
                String options = getString(R.string.context_q1_answers1);
                options += ","+getString(R.string.context_q1_answers2);
                options += ","+getString(R.string.context_q1_answers3);
                options += ","+getString(R.string.context_q1_answers4);
                options += ","+getString(R.string.context_q1_answers5);
                options += ","+getString(R.string.context_q1_answers6);
                options += ","+getString(R.string.context_q1_answers7);
                dialog.options = options;
                dialog.setOnDialogDismissListener(new ListDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss(String selected) {
                        question1.setValue(selected);
                        answered1 = true;
                        if(answered1 && answered2 && answered5){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),ListDialog.class.getSimpleName());

            }
        });

        question2 = (Question) view.findViewById(R.id.questionContextQ2);
        question2.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                ListDialog dialog = new ListDialog();
                dialog.title = getString(R.string.list_selectone);
                dialog.selectOne = true;
                dialog.selected = question2.getValue();
                String options = getString(R.string.context_q2_answers1);
                options += ","+getString(R.string.context_q2_answers2);
                options += ","+getString(R.string.context_q2_answers3);
                options += ","+getString(R.string.context_q2_answers4);
                options += ","+getString(R.string.context_q2_answers5);
                options += ","+getString(R.string.context_q2_answers6);
                options += ","+getString(R.string.context_q2_answers7);
                dialog.options = options;
                dialog.setOnDialogDismissListener(new ListDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss(String selected) {
                        question2.setValue(selected);
                        answered2 = true;
                        if(answered1 && answered2 && answered5){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),ListDialog.class.getSimpleName());
            }
        });

        question3 = (Rating) view.findViewById(R.id.ratingContextQ3);
        question4 = (Rating) view.findViewById(R.id.ratingContextQ4);

        question5 = (Question) view.findViewById(R.id.questionContextQ5);
        question5.setOnClickListener(new Question.OnClickListener() {
            @Override
            public void onEdit() {
                ListDialog dialog = new ListDialog();
                dialog.title = getString(R.string.list_selectone);
                dialog.selectOne = true;
                dialog.selected = question5.getValue();
                String options = getString(R.string.context_q5_answers1);
                options += ","+getString(R.string.context_q5_answers2);
                options += ","+getString(R.string.context_q5_answers3);
                options += ","+getString(R.string.context_q5_answers4);
                options += ","+getString(R.string.context_q5_answers5);
                options += ","+getString(R.string.context_q5_answers6);
                options += ","+getString(R.string.context_q5_answers7);
                options += ","+getString(R.string.context_q5_answers8);
                options += ","+getString(R.string.context_q5_answers9);
                options += ","+getString(R.string.context_q5_answers10);
                dialog.options = options;
                dialog.setOnDialogDismissListener(new ListDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss(String selected) {
                        question5.setValue(selected);
                        answered5 = true;
                        if(answered1 && answered2 && answered5){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),ListDialog.class.getSimpleName());
            }
        });

        return view;
    }

}
