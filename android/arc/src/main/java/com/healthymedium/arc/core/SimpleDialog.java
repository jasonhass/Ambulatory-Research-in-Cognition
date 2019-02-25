//
// SimpleDialog.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.library.R;

@SuppressLint("ValidFragment")
public class SimpleDialog extends DialogFragment {

    public String bodyText;
    public String buttonText;
    public long delayTime;
    public long maxTime;

    Handler handlerTimeout;
    Runnable runnableTimeout;

    Handler handlerEnable;
    Runnable runnableEnable;

    TextView textView;
    Button button;
    OnDialogDismiss listener;

    public SimpleDialog(String bodyText, String buttonText, long delayTime, long maxTime){
        this.bodyText = bodyText;
        this.buttonText = buttonText;
        this.delayTime = delayTime;
        this.maxTime = maxTime;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        //setStyle(STYLE_NO_TITLE,R.style.AppTheme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_simple, container, false);
        button = view.findViewById(R.id.buttonSimpleDialog);
        if(buttonText.isEmpty()){
            button.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dismiss();
                }
            });
        } else {
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
        }


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

        textView = view.findViewById(R.id.textviewSimpleDialog);
        textView.setText(bodyText);


        return view;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
