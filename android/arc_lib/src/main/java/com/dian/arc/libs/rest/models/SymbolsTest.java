package com.dian.arc.libs.rest.models;

import java.util.ArrayList;
import java.util.List;

public class SymbolsTest {

    private Double date = new Double(0);;
    private List<SymbolsTestSection> sections = new ArrayList<>();

    public List<SymbolsTestSection> getSections() {
        return sections;
    }

    public void setSections(List<SymbolsTestSection> sections) {
        this.sections = sections;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }
}
