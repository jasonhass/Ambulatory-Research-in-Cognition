package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;

public class GridInstruction1Fragment extends BaseFragment {

    public int order = 3;
    FancyButton button;
    TextView subheader;

    public GridInstruction1Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_instructions1, container, false);
        ((TextView)view.findViewById(R.id.textviewOrder)).setText(getString(R.string.test)+" "+String.valueOf(order)+" / 3");
        subheader = (TextView)view.findViewById(R.id.textviewGridSubheader);
        subheader.setTypeface(FontFactory.georgiaItalic);

        button = (FancyButton)view.findViewById(R.id.fancybuttonGridNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().nextFragment();
            }
        });

        return view;
    }

}
