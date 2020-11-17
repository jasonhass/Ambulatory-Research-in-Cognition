//
// SymbolsTestPathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.SymbolTest;
import com.healthymedium.arc.api.tests.data.SymbolTestSection;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.TimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SymbolsTestPathData extends PathSegmentData {

    private DateTime start;
    private List<Section> sections = new ArrayList<>();

    public SymbolsTestPathData(){
        super();

        for(int i=0;i<12;i++){
            sections.add(new Section());
        }
    }

    public void markStarted(){
        start = DateTime.now();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    protected BaseData onProcess() {
        SymbolTest test = new SymbolTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = TimeUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Section section : sections){
            SymbolTestSection testSection = new SymbolTestSection();
            testSection.selected = section.selected;
            testSection.correct = section.correct;
            testSection.choices = section.choices;
            testSection.options = section.options;

            if(start != null)
            {
                testSection.appearanceTime = Double.valueOf((section.appearanceTime - startTime) / (double)1000);
                testSection.selectionTime = Double.valueOf((section.selectionTime - startTime) / (double)1000);
            }

            test.sections.add(testSection);
        }

        return test;
    }

    public class Section {

        private long appearanceTime;
        private long selectionTime;
        private int selected;
        private int correct;
        private List<List<String>> options = new ArrayList<>();
        private List<List<String>> choices = new ArrayList<>();

        public void markAppearanceTime() {
            this.appearanceTime = System.currentTimeMillis();
        }

        public void setSelected(int selected, long selectionTime) {
            this.selected = selected;
            this.selectionTime = selectionTime;
        }

        public void setCorrect(Integer correct) {
            this.correct = correct;
        }

        public void setOptions(List<List<String>> options) {
            this.options = options;
        }

        public void setChoices(List<List<String>> choices) {
            this.choices = choices;
        }

    }
}
