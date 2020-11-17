//
// TwoBtnDialog.java
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class TwoBtnDialog extends android.support.v4.app.DialogFragment {
    public String headerText;
    public String bodyText;
    public String buttonText;
    public String buttonTextBottom;

    Handler handlerTimeout;
    Runnable runnableTimeout;

    Handler handlerEnable;
    Runnable runnableEnable;

    TextView textViewHeader;
    TextView textViewBody;
    Button button;
    Button buttonBottom;
    SimpleDialog.OnDialogDismiss listener;

    public TwoBtnDialog(String headerText, String bodyText, String buttonText, String buttonTextBottom){
        this.headerText = headerText;
        this.bodyText = bodyText;
        this.buttonText = buttonText;
        this.buttonTextBottom = buttonTextBottom;

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
        View view = inflater.inflate(R.layout.dialog_two_btn, container, false);
        button = view.findViewById(R.id.buttonTop);
        buttonBottom = view.findViewById(R.id.buttonBottom);

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

        // TODO
        // Generalize the onClickListener
        // Hardcoded in the interest of time
        if(buttonTextBottom.isEmpty()){
            buttonBottom.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dismiss();
                }
            });
        } else {
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(handlerTimeout != null){
                        handlerTimeout.removeCallbacks(runnableTimeout);
                    }
                    if(listener != null) {
                        listener.dismiss();
                    }
                    dismiss();
                    Study.getInstance().adjustSchedule();
                }
            });
            buttonBottom.setText(buttonTextBottom);
        }

        textViewHeader = view.findViewById(R.id.textviewHeaderText);
        textViewHeader.setText(Html.fromHtml(headerText));

        textViewBody = view.findViewById(R.id.textviewBodyText);
        textViewBody.setText(Html.fromHtml(bodyText));

        return view;
    }

    public void setOnDialogDismissListener(SimpleDialog.OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
