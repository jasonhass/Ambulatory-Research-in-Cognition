package com.dian.arc.libs.rest.models;

public class GridTestTap {

    private Double selectionTime = new Double(0);
    private int x;
    private int y;

    public GridTestTap(int x, int y, double selectionTime){
        this.selectionTime = selectionTime;
        this.x = x;
        this.y = y;
    }
}
