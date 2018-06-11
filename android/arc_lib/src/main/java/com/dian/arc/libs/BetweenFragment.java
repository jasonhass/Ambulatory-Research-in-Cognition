package com.dian.arc.libs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.utilities.ArcManager;

public class BetweenFragment extends BaseFragment {

    public BetweenFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_between, container, false);
        TextView editAvailability = (TextView)view.findViewById(R.id.textviewTestBetweenEditAvailability);
        editAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().setPathEditAvailability();
                ArcManager.getInstance().nextFragment(true);
            }
        });

        if(ArcManager.getInstance().getCurrentTestSession().getSessionDate().isBeforeNow()){
            if(ArcManager.getInstance().getCurrentTestSession().getExpirationDate().isBeforeNow()){
                ArcManager.getInstance().markTestMissed();
            }
            ArcManager.getInstance().decidePath();
            ArcManager.getInstance().setupPath();
            ArcManager.getInstance().nextFragment(true);
        }

        setupDebug(view,R.id.textView44);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ArcManager.getInstance().getCurrentTestSession().getExpirationDate().isBeforeNow()){
            AsyncLoader loader = new AsyncLoader();
            loader.execute();
        } else if (ArcManager.getInstance().getCurrentTestSession().getSessionDate().minusMinutes(5).isBeforeNow()){
            ArcManager.getInstance().decidePath();
            ArcManager.getInstance().setupPath();
            ArcManager.getInstance().nextFragment(true);
        }
    }
    private class AsyncLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArcManager.getInstance().markTestMissed();
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            ArcManager.getInstance().decidePath();
            ArcManager.getInstance().setupPath();
            ArcManager.getInstance().nextFragment(true);
        }
    }

}
