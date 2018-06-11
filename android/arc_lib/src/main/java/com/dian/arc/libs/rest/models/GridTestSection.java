package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GridTestSection {

    private Double displaySymbols = new Double(0);
    private Double displayDistraction = new Double(0);
    private Double displayTestGrid = new Double(0);
    private int eCount;
    private int fCount;
    private List<GridTestImage> images = new ArrayList<>();
    private List<GridTestTap> choices = new ArrayList<>();

    public void triggerDisplaySymbols(double startTime) {
        displaySymbols = JodaUtil.toUtcDouble(DateTime.now())-startTime;
    }

    public void triggerDisplayDistraction(double startTime) {
        displayDistraction = JodaUtil.toUtcDouble(DateTime.now())-startTime;
    }

    public void triggerDisplayTestGrid(double startTime) {
        displayTestGrid = JodaUtil.toUtcDouble(DateTime.now())-startTime;
    }

    public void setECount(int eCount) {
        this.eCount = eCount;
    }

    public void setFCount(int fCount) {
        this.fCount = fCount;
    }

    public List<GridTestImage> getImages() {
        return images;
    }

    public void setImages(List<GridTestImage> images) {
        this.images = images;
    }

    public List<GridTestTap> getChoices() {
        return choices;
    }

    public void setChoices(List<GridTestTap> choices) {
        this.choices = choices;
    }

}
