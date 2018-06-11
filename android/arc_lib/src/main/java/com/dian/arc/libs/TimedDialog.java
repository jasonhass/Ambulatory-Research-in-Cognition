package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimedDialog extends DialogFragment {

    public String bodyText;

    TextView textView;
    OnDialogDismiss listener;
    long timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_timed, container, false);
        textView = (TextView) v.findViewById(R.id.textviewTimedDialog);
        textView.setText(bodyText);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(listener != null) {
                    listener.dismiss();
                }
                dismiss();
            }
        }, timer);
        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
