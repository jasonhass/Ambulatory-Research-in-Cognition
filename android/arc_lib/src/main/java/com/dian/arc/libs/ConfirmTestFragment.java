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

public class ConfirmTestFragment extends BaseFragment {

    public ConfirmTestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        final DateTime dateTime = ArcManager.getInstance().getCurrentVisit().getUserStartDate();
        if(dateTime.minusDays(1).isBeforeNow()){
            ArcManager.getInstance().setPathNoTests();
            ArcManager.getInstance().nextFragment(true);
        }

        final TextView textView = (TextView) view.findViewById(R.id.textView40);
        textView.setTypeface(FontFactory.georgiaItalic);
        textView.setText(getString(R.string.confirm_body1).replace("{DATE}",dateTime.toString(getString(R.string.format_date)))+getString(R.string.confirm_body2));

        setupDebug(view);

        TextView textviewDate = (TextView)view.findViewById(R.id.textView34);

        textviewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDialogDate dialog = new QuestionDialogDate();
                dialog.setOnDialogDismissListener(new QuestionDialogDate.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        textView.setText(getString(R.string.confirm_body1)+time+getString(R.string.confirm_body2));
                        ArcManager.getInstance().saveState();
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogDate.class.getSimpleName());
            }
        });

        view.findViewById(R.id.fancybuttonConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().getCurrentVisit().confirmDate();
                ArcManager.getInstance().setPathNoTests();
                ArcManager.getInstance().nextFragment(true);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DateTime dateTime = ArcManager.getInstance().getCurrentVisit().getUserStartDate();
        if(dateTime.minusDays(1).isBeforeNow()){
            ArcManager.getInstance().setPathNoTests();
            ArcManager.getInstance().nextFragment(true);
        }
    }
}
