//
// QuestionCheckBoxes.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.CheckBox;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionCheckBoxes extends QuestionTemplate {

    List<CheckBox> checkBoxes = new ArrayList<>();
    List<String> options = new ArrayList<>();
    String exclusive;
    int exclusiveIndex = -1;
    boolean exclusiveChecked = false;
    List<String> selections = new ArrayList<>();
    boolean settingUp = false;

    public QuestionCheckBoxes(boolean allowBack, String header, String subheader, List<String> options, String exclusive) {
        super(allowBack,header,subheader);
        type = "checkbox";
        this.options = options;
        this.exclusive = exclusive;
        int size = options.size();
        for(int i=0;i<size;i++){
            if(options.get(i).equals(exclusive)){
                exclusiveIndex = i;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHelpVisible(false);

        settingUp = true;
        checkBoxes.clear();

        int index=0;
        for(final String option : options){
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(option);

            final int checkBoxIndex = index;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    response_time = System.currentTimeMillis();
                    if(checked && (checkBoxIndex==exclusiveIndex)){
                        int size = checkBoxes.size();
                        for(int i=0;i<size;i++){
                            if(i!=exclusiveIndex){
                                checkBoxes.get(i).setChecked(false);
                            }
                        }
                        exclusiveChecked = true;
                    } else if(checked && exclusiveChecked){
                        checkBoxes.get(exclusiveIndex).setChecked(false);
                        exclusiveChecked = false;
                    }


                    if(checked && !settingUp){
                        String option = options.get(checkBoxIndex);
                        if(!selections.contains(option)){
                            selections.add(option);
                        }
                        if(!buttonNext.isEnabled()){
                            buttonNext.setEnabled(true);
                        }
                    } else if(!settingUp) {
                        selections.remove(options.get(checkBoxIndex));
                        if(selections.size()==0 && buttonNext.isEnabled()){
                            buttonNext.setEnabled(false);
                        }
                    }


                }
            });

            checkBoxes.add(checkBox);
            content.addView(checkBox);
            index++;
        }

        return view;
    }

    @Override
    public void onPause() {
        selections.clear();
        int size = checkBoxes.size();
        for(int i=0;i<size;i++){
            if(checkBoxes.get(i).isChecked()){
                selections.add(options.get(i));
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(checkBoxes.size()>0) {
            int optionCount = options.size();
            for(int i=0;i<optionCount;i++){
                checkBoxes.get(i).setChecked(selections.contains(options.get(i)));
            }
        }

        buttonNext.setEnabled(selections.size()>0);
        settingUp = false;
    }

    @Override
    public Object onDataCollection(){
        selections.clear();
        int size = checkBoxes.size();
        for(int i=0;i<size;i++){
            if(checkBoxes.get(i).isChecked()){
                selections.add(options.get(i));
            }
        }
        return super.onDataCollection();
    }

    @Override
    public Object onValueCollection(){
        int size = checkBoxes.size();
        List<Integer> selectedIndexes = new ArrayList<>();
        for (int i=0;i<size;i++) {
            if (checkBoxes.get(i).isChecked()) {
                selectedIndexes.add(i);
            }
        }
        return selectedIndexes;
    }

    @Override
    public String onTextValueCollection(){
        String selectionString = "";
        for (String string : options) {
            if (selections.contains(string)) {
                selectionString += (string + ",");
            }
        }
        if (selectionString.length() > 2) {
            selectionString = selectionString.substring(0, selectionString.length() - 1);
        }
        return selectionString;
    }

}
