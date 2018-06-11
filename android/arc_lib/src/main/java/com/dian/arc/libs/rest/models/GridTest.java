package com.dian.arc.libs.rest.models;

import java.util.ArrayList;
import java.util.List;

public class GridTest {

    private Double date= new Double(0);
    private List<GridTestSection> sections =  new ArrayList<>();

    public void startNewSection(){
        sections.add(new GridTestSection());
    }

    public GridTestSection getCurrentSection(){
        return sections.get(sections.size()-1);
    }

    public void updateCurrentSection(GridTestSection section){
        sections.set(sections.size()-1,section);
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public List<GridTestSection> getSections() {
        return sections;
    }

    public void setSections(List<GridTestSection> sections) {
        this.sections = sections;
    }

}
