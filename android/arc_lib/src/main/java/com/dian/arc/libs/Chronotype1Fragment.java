package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.ToggledButton;
import com.dian.arc.libs.utilities.ArcManager;

public class Chronotype1Fragment extends BaseFragment {

    TextView editText;
    FancyButton button;
    ToggledButton toggleYes;
    ToggledButton toggleNo;

    public Chronotype1Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chronotype_1, container, false);

        setupDebug(view);

        button = (FancyButton)view.findViewById(R.id.fancybuttonChronotype1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().getCurrentVisit().getChronotype().wasShiftWorker = toggleYes.isOn() ?1:0;
                int value = Integer.valueOf(editText.getText().toString());
                ArcManager.getInstance().getCurrentVisit().getChronotype().numWorkDays = value;
                ArcManager.getInstance().nextFragment();
            }
        });
        button.setEnabled(false);

        editText = (TextView)view.findViewById(R.id.edittextChronotype1);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDialogNumber dialog = new QuestionDialogNumber();
                String value = editText.getText().toString();
                if(!value.isEmpty()){
                    dialog.value = Integer.valueOf(value);
                }
                dialog.max = 7;
                dialog.min = 0;
                dialog.setOnDialogDismissListener(new QuestionDialogNumber.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        editText.setText(time);
                        if(toggleYes.isOn() || toggleNo.isOn()){
                            button.setEnabled(true);
                        }
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        toggleYes = (ToggledButton) view.findViewById(R.id.togglebuttonChronotype1Yes);
        toggleYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYes.toggle(true);
                toggleNo.toggle(false);
                if(!editText.getText().toString().isEmpty()){
                    button.setEnabled(true);
                }
            }
        });

        toggleNo = (ToggledButton) view.findViewById(R.id.togglebuttonChronotype1No);
        toggleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYes.toggle(false);
                toggleNo.toggle(true);
                if(!editText.getText().toString().isEmpty()){
                    button.setEnabled(true);
                }
            }
        });

        return view;
    }

}
