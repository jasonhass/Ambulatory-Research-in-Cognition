package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SymbolsTestSection {

    private Double appearanceTime = new Double(0);
    private Double selectionTime = new Double(0);
    private int selected;
    private int correct;
    private List<List<String>> options = new ArrayList<>();
    private List<List<String>> choices = new ArrayList<>();

    public void setAppearanceTime(double appearanceTime) {
        this.appearanceTime = appearanceTime;
    }

    public void setSelected(int selected,double date) {
        this.selected = selected;
        this.selectionTime = JodaUtil.toUtcDouble(DateTime.now())-date;
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
