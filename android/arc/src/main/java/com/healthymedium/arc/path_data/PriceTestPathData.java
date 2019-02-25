//
// PriceTestPathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.PriceTest;
import com.healthymedium.arc.api.tests.data.PriceTestSection;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.utilities.PriceManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PriceTestPathData extends PathSegmentData {

    List<Section> sections = new ArrayList<>();
    List<PriceManager.Item> priceSet;
    DateTime start;

    public PriceTestPathData(){
        super();

        priceSet = PriceManager.getInstance().getPriceSet();
        int size = priceSet.size();
        for(int i=0;i<size;i++){
            sections.add(new Section());
        }
    }

    public void markStarted(){
        start = DateTime.now();
    }

    public boolean hasStarted(){
        return start!=null;
    }

    public List<PriceManager.Item> getPriceSet(){
        return priceSet;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    protected BaseData onProcess() {

        PriceTest test = new PriceTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = JodaUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Section section : sections){
            PriceTestSection testSection = new PriceTestSection();
            testSection.selectedIndex = section.selectedIndex;
            testSection.correctIndex = section.correctIndex;
            testSection.goodPrice = section.goodPrice;
            testSection.altPrice = section.altPrice;
            testSection.price = section.price;
            testSection.item = section.item;

            if(start != null)
            {
                testSection.stimulusDisplayTime = Double.valueOf((section.stimulusDisplayTime - startTime) / (double)1000);
                testSection.questionDisplayTime = Double.valueOf((section.questionDisplayTime - startTime) / (double)1000);
                testSection.selectionTime = Double.valueOf((section.selectionTime - startTime) / (double)1000);
            }

            test.sections.add(testSection);
        }

        return test;
    }

    public static class Section {

        private int goodPrice;
        private long stimulusDisplayTime;
        private long questionDisplayTime;
        private String item;
        private String price;
        private String altPrice;
        private int correctIndex;
        private int selectedIndex;
        private long selectionTime;

        public Section(){
            goodPrice = 99;
            item = "";
        }

        public void selectGoodPrice(int goodPrice) {
            this.goodPrice = goodPrice;
        }

        public void markStimulusDisplayed() {
            stimulusDisplayTime = System.currentTimeMillis();
        }

        public void markQuestionDisplayed() {
            questionDisplayTime = System.currentTimeMillis();
        }

        public void setItem(String item) {
            this.item = item;
        }
        public String getItem() {
            return this.item;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setAltPrice(String altPrice) {
            this.altPrice = altPrice;
        }

        public void setCorrectIndex(Integer correctIndex) {
            this.correctIndex = correctIndex;
        }

        public void markSelection(int index, long selectionTime){
            this.selectionTime =  selectionTime;
            selectedIndex = index;
        }

    }
}
