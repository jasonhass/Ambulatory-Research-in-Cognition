package com.dian.arc.libs.rest.models;

import java.util.ArrayList;
import java.util.List;

public class PricesTest {

    private Double date = new Double(0);
    private List<PricesTestSection> sections = new ArrayList<>();

    public List<PricesTestSection> getSections() {
        return sections;
    }

    public void setSections(List<PricesTestSection> sections) {
        this.sections = sections;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

}
