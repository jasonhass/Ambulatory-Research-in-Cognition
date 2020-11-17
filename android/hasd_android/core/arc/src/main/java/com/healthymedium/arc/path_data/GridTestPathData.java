//
// GridTestPathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.GridTest;
import com.healthymedium.arc.api.tests.data.GridTestImage;
import com.healthymedium.arc.api.tests.data.GridTestSection;
import com.healthymedium.arc.api.tests.data.GridTestTap;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.TimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GridTestPathData extends PathSegmentData {

    DateTime start;
    List<Section> sections = new ArrayList<>();

    public GridTestPathData(){
        super();
    }

    public void markStarted() {
        start = DateTime.now();
    }

    public boolean hasStarted(){
        return start!=null;
    }

    public void startNewSection(){
        sections.add(new Section());
    }

    public Section getCurrentSection(){
        return sections.get(sections.size()-1);
    }

    public void updateCurrentSection(Section section){
        sections.set(sections.size()-1,section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    protected BaseData onProcess() {
        GridTest test = new GridTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = TimeUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Section section : sections){
            GridTestSection testSection = new GridTestSection();
            testSection.eCount = section.eCount;
            testSection.fCount = section.fCount;

            testSection.images = new ArrayList<>();
            for(Image image : section.images){
                GridTestImage testImage = new GridTestImage();
                testImage.x = image.x;
                testImage.y = image.y;
                testImage.image = image.image;
                testSection.images.add(testImage);
            }

            testSection.choices = new ArrayList<>();
            for(Tap tap : section.choices){
                GridTestTap testTap = new GridTestTap();
                testTap.x = tap.x;
                testTap.y = tap.y;
                if(start != null)
                {
                    testTap.selectionTime = Double.valueOf((tap.selectionTime - startTime) / (double) 1000);
                }
                testSection.choices.add(testTap);
            }

            if(start != null)
            {
                testSection.displayDistraction = Double.valueOf((section.displayTimeDistraction - startTime) / (double)1000);
                testSection.displayTestGrid = Double.valueOf((section.displayTimeTestGrid - startTime) / (double)1000);
                testSection.displaySymbols = Double.valueOf((section.displayTimeSymbols - startTime) / (double)1000);
            }
            test.sections.add(testSection);
        }

        return test;

    }

    public class Section {

        private long displayTimeSymbols;
        private long displayTimeDistraction;
        private long displayTimeTestGrid;
        private int eCount;
        private int fCount;
        private List<Image> images = new ArrayList<>();
        private List<Tap> choices = new ArrayList<>();

        public Section(){

        }

        public void markSymbolsDisplayed() {
            displayTimeSymbols = System.currentTimeMillis();
        }

        public void markDistractionDisplayed() {
            displayTimeDistraction = System.currentTimeMillis();
        }

        public void markTestGridDisplayed() {
            displayTimeTestGrid = System.currentTimeMillis();
        }

        public void setECount(int eCount) {
            this.eCount = eCount;
        }

        public void setFCount(int fCount) {
            this.fCount = fCount;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public List<Tap> getChoices() {
            return choices;
        }

        public void setChoices(List<Tap> choices) {
            this.choices = choices;
        }





    }

    public static class Tap {

        private long selectionTime;
        private int x;
        private int y;

        public Tap(int x, int y, long selectionTime){
            this.selectionTime = selectionTime;
            this.x = x;
            this.y = y;
        }

        public Tap(){

        }

    }

    public static class Image {

        public static transient final String PHONE = "phone";
        public static transient final String PEN = "pen";
        public static transient final String KEY = "key";

        private String image;
        private int x;
        private int y;

        public Image(int row,int col,String name) {
            image = name;
            x = row;
            y = col;
        }

        public String name() {
            return image;
        }

        public int row() {
            return x;
        }

        public int column() {
            return y;
        }

        public int id() {
            if(image.equals(PHONE)) {
                return R.drawable.phone;
            }
            if(image.equals(PEN)) {
                return R.drawable.pen;
            }
            if(image.equals(KEY)) {
                return R.drawable.key;
            }
            return 0;
        }

    }

}
