package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.ToggledButton;
import com.dian.arc.libs.utilities.ArcManager;

public class UpgradeFragment extends BaseFragment {

    ToggledButton buttonYes;
    ToggledButton buttonNo;
    FancyButton fancyButton;

    public UpgradeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade, container, false);

        setupDebug(view);


        fancyButton = (FancyButton)view.findViewById(R.id.fancyButtonUpgrade);
        fancyButton.setEnabled(false);
        fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcManager.getInstance().getCurrentTestSession().setWillUpgradePhone(buttonYes.isOn());
                ArcManager.getInstance().nextFragment();
            }
        });
        buttonYes = (ToggledButton)view.findViewById(R.id.toggledbuttonUpgradeYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYes.toggle(true);
                buttonNo.toggle(false);
                fancyButton.setEnabled(true);
            }
        });

        buttonNo = (ToggledButton)view.findViewById(R.id.toggledbuttonUpgradeNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYes.toggle(false);
                buttonNo.toggle(true);
                fancyButton.setEnabled(true);
            }
        });

        return view;
    }

}
