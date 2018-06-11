package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.utilities.ArcManager;

public class TestPopup1Fragment extends BaseFragment {

    boolean paused;

    public TestPopup1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_popup, container, false);
        SimpleDialog dialog = new SimpleDialog();
        dialog.bodyText = getString(R.string.popup_first);
        dialog.buttonText = getString(R.string.next);
        dialog.setOnDialogDismissListener(new SimpleDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                ArcManager.getInstance().nextFragment();
            }
        });
        dialog.show(getFragmentManager(),SimpleDialog.class.getSimpleName());
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            ArcManager.getInstance().nextFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
