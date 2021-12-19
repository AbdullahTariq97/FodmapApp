package com.sky.fodmap.service.enums;

public enum Colours {

    R("red"),
    A("amber"),
    G("green");

    private String colourName;

    Colours(String colourName){
        this.colourName = colourName;
    }

    public String getColourName() {
        return colourName;
    }
}
