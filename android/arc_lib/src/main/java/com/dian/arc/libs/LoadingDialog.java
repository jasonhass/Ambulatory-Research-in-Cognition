package com.dian.arc.libs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_loading, container, false);
        v.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        //progressBar.setIndeterminate(true);
        //progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(),R.drawable.spinner));
        return v;
    }


}
