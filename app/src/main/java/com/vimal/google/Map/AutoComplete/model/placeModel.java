package com.vimal.google.Map.AutoComplete.model;

public class placeModel {
    String mainTitle;
    String SecondaryTitle;
    String placeID;

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getSecondaryTitle() {
        return SecondaryTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        SecondaryTitle = secondaryTitle;
    }
}