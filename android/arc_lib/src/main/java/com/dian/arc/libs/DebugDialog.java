package com.dian.arc.libs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.models.TestSession;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.NavigationManager;

public class DebugDialog extends DialogFragment {

    FancyButton button;
    OnDialogDismiss listener;

    static public void launch(){
        DebugDialog dialog = new DebugDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),DebugDialog.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_debug, container, false);
        button = (FancyButton) v.findViewById(R.id.buttonDebugDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        /*
        if(PreferencesManager.getInstance()==null){
          PreferencesManager.initialize(getContext());
        }
        ArcManager.State state = PreferencesManager.getInstance().getObject("state",ArcManager.State.class);
        */

        ArcManager.State state = ArcManager.getInstance().getState();

        String status = "lifecycle: "+getLifecycle(state.lifecycle)+"\n";
        status += "arc: "+state.currentVisit +"\n";
        status += "test: "+state.currentTestSession+"\n";
        status += "path: "+getPath(state.currentPath)+"\n";
        status += "steps: "+state.currentPathStepsTaken+"\n\n";
        status += "scheduled tests:\n\n";
        if(state.visits.size()>state.currentVisit) {
            Log.e("Test Count",String.valueOf(state.visits.get(state.currentVisit).getTestSessions().size()));
            for (TestSession session : state.visits.get(state.currentVisit).getTestSessions()) {
                status += session.getSessionDate().toString("MM/dd/yyyy hh:mm:ss a\n");
            }
        }
        TextView textView = (TextView)v.findViewById(R.id.textviewState);
        textView.setText(status);

        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }

    private String getLifecycle(int state){
        String result = new String();
        switch (state){
            case ArcManager.LIFECYCLE_INIT:
            result = "init";
                break;
            case ArcManager.LIFECYCLE_TRIAL:
                result = "trial";
                break;
            case ArcManager.LIFECYCLE_IDLE:
                result = "idle";
                break;
            case ArcManager.LIFECYCLE_ARC:
                result = "arc";
                break;
            case ArcManager.LIFECYCLE_OVER:
                result = "over";
                break;
            case ArcManager.LIFECYCLE_BASELINE:
                result = "baseline";
                break;
        }
        return result;
    }

    private String getPath(int state){
        String result = new String();
        switch (state){
            case ArcManager.SETUP_INIT:
                result = "setup init";
                break;
            case ArcManager.SETUP_NEW:
                result = "setup new";
                break;
            case ArcManager.SETUP_EXISTING:
                result = "setup existing";
                break;
            case ArcManager.PATH_FIRST_DAY:
                result = "first of day";
                break;
            case ArcManager.PATH_FIRST_BURST:
                result = "first of burst";
                break;
            case ArcManager.PATH_LAST_BURST:
                result = "last of burst";
                break;
            case ArcManager.PATH_OTHER:
                result = "other";
                break;
            case ArcManager.PATH_BETWEEN:
                result = "between";
                break;
            case ArcManager.PATH_NONE:
                result = "none";
                break;
            case ArcManager.PATH_BASELINE:
                result = "baseline";
                break;
            case ArcManager.PATH_AVAILABILITY:
                result = "availability";
                break;
            case ArcManager.PATH_CONFIRM:
                result = "one month confirm";
                break;
        }
        return result;
    }
}
