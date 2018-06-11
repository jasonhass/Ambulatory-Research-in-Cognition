package com.dian.arc.libs;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.LocaleEntry;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.PreferencesManager;

import java.util.Locale;

public class SetupLocaleFragment extends BaseFragment {

    FancyButton button;
    LinearLayout linearLayout;
    LocaleEntry lastSelected = null;

    public SetupLocaleFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_locale, container, false);

        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutLocale);
        button = (FancyButton)view.findViewById(R.id.fancybuttonSetupLocaleConfirm);
        button.setEnabled(false);
        button.setDrawable(R.drawable.arrow_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = lastSelected.getLanguage();
                String country = lastSelected.getCountry();
                PreferencesManager.getInstance().putString("language",language);
                PreferencesManager.getInstance().putString("country",country);

                Resources res = getContext().getResources();
                Configuration conf = res.getConfiguration();
                conf.locale = new Locale(language,country);
                res.updateConfiguration(conf, res.getDisplayMetrics());

                NavigationManager.getInstance().open(new SplashScreen());
            }
        });

        addListItem("Australia - English","en","AU",false);
        addListItem("Canada - English","en","CA",false);
        //addListItem("Canada - Francais","fr","CA",false);
        addListItem("España - Español","es","ES",false);
        //addListItem("France - Francais","fr","FR",false);
        addListItem("United Kingdom - English","en","GB",false);
        addListItem("United States - English","en","US",false);
        addListItem("United States - Español","es","US",false);

        return view;
    }

    private void addListItem(String text, String language, String country, boolean last){
        LocaleEntry entry = new LocaleEntry(getContext());
        entry.setText(text);
        entry.setLanguage(language);
        entry.setCountry(country);
        if(last){
            entry.setSeparatorVisibility(false);
        }
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastSelected != null){
                    lastSelected.toggle(false);
                } else {
                    button.setEnabled(true);
                }
                lastSelected = (LocaleEntry) v;
                lastSelected.toggle(true);
            }
        });
        linearLayout.addView(entry);
    }

}
