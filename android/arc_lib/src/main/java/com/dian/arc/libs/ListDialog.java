package com.dian.arc.libs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.ToggledButton;
import com.dian.arc.libs.utilities.ScreenUtility;

import java.util.Arrays;
import java.util.List;

public class ListDialog extends DialogFragment {

    public String options;
    String selected;
    public String title;
    public boolean selectOne;
    public boolean aryaTweak = false;


    FancyButton button;
    LinearLayout layout;
    OnDialogDismiss listener;
    View.OnClickListener clickListener;

    TextView textviewHeader;

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_list, container, false);

        layout = (LinearLayout) v.findViewById(R.id.layoutListDialog);
        layout.setGravity(Gravity.CENTER);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0, (int) ScreenUtility.convertDpToPixel(8,getContext()));

        textviewHeader = (TextView) v.findViewById(R.id.textviewListDialogHeader);
        textviewHeader.setText(title);

        String[] items = options.split(",");
        List<String> selectedItems = Arrays.asList(selected.split(","));

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectOne){
                    if(!((ToggledButton)view).isOn()) {
                        int size = layout.getChildCount();
                        for (int i = 0; i < size; i++) {
                            ((ToggledButton) layout.getChildAt(i)).toggle(false);
                        }
                        ((ToggledButton)view).toggle(true);
                        button.setEnabled(true);
                    }
                } else if(aryaTweak) {
                    String nobody = getString(R.string.context_q1_answers1);
                    boolean nobodySelected = ((ToggledButton)view).getText().toString().equals(nobody);
                    boolean nobodyBefore = getSelected().contains(nobody);

                    if(nobodySelected && nobodyBefore){
                        ((ToggledButton)view).toggle(); //false
                        button.setEnabled(false);
                    } else if(nobodySelected && !nobodyBefore){
                        deselectAll();
                        ((ToggledButton)view).toggle(); // true
                        button.setEnabled(true);
                    } else if(!nobodyBefore && nobodySelected) {

                    } else if(nobodyBefore && !nobodySelected) {
                        deselectAll();
                        ((ToggledButton)view).toggle(); // true
                        button.setEnabled(true);
                    } else {
                        ((ToggledButton)view).toggle();
                        button.setEnabled(!getSelected().isEmpty());
                    }
                } else {
                    ((ToggledButton)view).toggle();
                    button.setEnabled(!getSelected().isEmpty());
                }

            }
        };

        for(int i=0;i<items.length;i++){
            boolean toggle = selectedItems.contains(items[i]);
            layout.addView(getToggledButton(getContext(),items[i], toggle),layoutParams);
        }

        button = (FancyButton) v.findViewById(R.id.buttonListDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getSelected().isEmpty()){
                    if(listener != null) {
                        listener.dismiss(getSelected());
                    }
                    dismiss();
                }
            }
        });
        if(getSelected().isEmpty()){
            button.setEnabled(false);
        }

        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String selected);
    }

    private ToggledButton getToggledButton(Context context,String text,boolean selected){
        ToggledButton toggledButton = new ToggledButton(context);
        toggledButton.setText(text);
        toggledButton.toggle(selected);
        toggledButton.setOnClickListener(clickListener);
        return toggledButton;
    }

    private void deselectAll(){
        int size = layout.getChildCount();
        for(int i=0;i<size;i++){
            ((ToggledButton)layout.getChildAt(i)).toggle(false);
        }
    }

    public String getSelected(){
        String selected = new String();
        int size = layout.getChildCount();
        for (int i = 0; i < size; i++) {
            if(((ToggledButton) layout.getChildAt(i)).isOn()){
                selected +=((ToggledButton) layout.getChildAt(i)).getText().toString()+",";
            }
        }
        if(!selected.isEmpty()){
            selected = selected.substring(0,selected.length()-1);
        }
        return selected;
    }
}
