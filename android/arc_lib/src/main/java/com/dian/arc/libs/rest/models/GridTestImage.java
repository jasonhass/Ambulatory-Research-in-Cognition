package com.dian.arc.libs.rest.models;

public class GridTestImage {

    private String image;
    private int x;
    private int y;

    public GridTestImage(int row,int col,String image){
        x = row;
        y = col;
        this.image = image;
    }

}
