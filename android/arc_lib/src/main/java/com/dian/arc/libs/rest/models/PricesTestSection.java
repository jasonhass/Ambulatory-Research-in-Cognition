package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

public class PricesTestSection {

    private int goodPrice;
    private Double stimulusDisplayTime = new Double(0);;
    private Double questionDisplayTime = new Double(0);;
    private String item;
    private String price;
    private String altPrice;
    private int correctIndex;
    private int selectedIndex;
    private Double selectionTime = new Double(0);;

    public PricesTestSection(){
        goodPrice = 99;
        item = "";
    }

    public void selectGoodPrice(int goodPrice) {
        this.goodPrice = goodPrice;
    }

    public void triggerStimulusDisplayTime(double startTime) {
        stimulusDisplayTime = JodaUtil.toUtcDouble(DateTime.now())-startTime;
    }

    public void triggerQuestionDisplayTime(double startTime) {
        questionDisplayTime = JodaUtil.toUtcDouble(DateTime.now())-startTime;
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

    public void triggerSelection(int index, double startTime){
        selectionTime = JodaUtil.toUtcDouble(DateTime.now())-startTime;
        selectedIndex = index;
    }
}
