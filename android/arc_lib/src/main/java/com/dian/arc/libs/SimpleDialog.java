package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;

public class SimpleDialog extends DialogFragment {

    public String bodyText;
    public String buttonText;
    public int delayTime = 0;
    public int maxTime = 0;
    public boolean hideButton;

    Handler handlerTimeout;
    Runnable runnableTimeout;

    Handler handlerEnable;
    Runnable runnableEnable;

    TextView textView;
    FancyButton button;
    OnDialogDismiss listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_simple, container, false);
        button = (FancyButton) v.findViewById(R.id.buttonSimpleDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(handlerTimeout != null){
                    handlerTimeout.removeCallbacks(runnableTimeout);
                }
                if(listener != null) {
                    listener.dismiss();
                }
                dismiss();
            }
        });
        button.setText(buttonText);

        if(delayTime > 0){
            button.setEnabled(false);
            runnableEnable = new Runnable() {
                @Override
                public void run() {
                    if(button != null) {
                        button.setEnabled(true);
                    }
                }
            };
            handlerEnable = new Handler();
            handlerEnable.postDelayed(runnableEnable,delayTime);
        }

        if(maxTime > 0){
            runnableTimeout = new Runnable() {
                @Override
                public void run() {
                    if(listener != null) {
                        listener.dismiss();
                    }
                    dismiss();
                }
            };
            handlerTimeout = new Handler();
            handlerTimeout.postDelayed(runnableTimeout,maxTime);
        }

        textView = (TextView) v.findViewById(R.id.textviewSimpleDialog);
        textView.setText(bodyText);

        if(hideButton){
            button.setVisibility(View.GONE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dismiss();
                }
            });
        }
        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
