package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;

import com.dian.arc.libs.utilities.Config;

public class BaseFragment extends Fragment {

    public boolean disableFragmentAnimation = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (disableFragmentAnimation && !enter) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public void setupDebug(View view){
        setupDebug(view,R.id.textView);
    }

    public void setupDebug(View view,int id){
        if(Config.DEBUG_DIALOGS) {
            final int[] count = {0};
            view.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count[0] <= 0) {
                        count[0]++;
                    } else {
                        DebugDialog.launch();
                    }
                }
            });
        }
    }


    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

}
