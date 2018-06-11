package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.NavigationManager;

public class TwoFactorHelpConfirmFragment extends BaseFragment {

    FancyButton fancyButton;

    public TwoFactorHelpConfirmFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twofactor_help_confirm, container, false);

        TextView help = (TextView)view.findViewById(R.id.textviewHeader);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().getFragmentManager().popBackStack();
            }
        });

        fancyButton = (FancyButton)view.findViewById(R.id.fancybutton);
        fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().getFragmentManager().popBackStack();
            }
        });

        TextView subheader = (TextView)view.findViewById(R.id.textviewSubheader);
        subheader.setTypeface(FontFactory.georgiaItalic);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}
