package com.dian.arc.libs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;

public class GridInstruction2Fragment extends BaseFragment {

    public int order = 3;
    FancyButton button;
    TextView subheader;



    public GridInstruction2Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_instructions2, container, false);
        ((TextView)view.findViewById(R.id.textviewOrder)).setText(getString(R.string.test)+" "+String.valueOf(order)+" / 3");
        subheader = (TextView)view.findViewById(R.id.textviewGridSubheader);
        subheader.setTypeface(FontFactory.georgiaItalic);

        button = (FancyButton)view.findViewById(R.id.fancybuttonGridStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        TimedDialog dialog = new TimedDialog();
                        dialog.bodyText = getString(R.string.grid_transition);
                        dialog.timer = 2000;
                        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
                            @Override
                            public void dismiss() {
                                ArcManager.getInstance().nextFragment();
                            }
                        });
                        dialog.show(getFragmentManager(),TimedDialog.class.getSimpleName());
                    }
                });
            }
        });

        GridLayout layout = (GridLayout)view.findViewById(R.id.gridLayout);
        Typeface font = FontFactory.georgia;
        int size = layout.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)layout.getChildAt(i)).setTypeface(font);
        }

        return view;
    }
}
