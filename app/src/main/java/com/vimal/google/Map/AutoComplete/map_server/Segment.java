package com.vimal.google.Map.AutoComplete.map_server;

import com.google.android.gms.maps.model.LatLng;

public class Segment {

    private LatLng start;

    private String instruction;

    private int length;

    private double distance;


    public Segment() {
    }


    public void setInstruction(final String turn) {
        this.instruction = turn;
    }


    public String getInstruction() {
        return instruction;
    }


    public void setPoint(final LatLng point) {
        start = point;
    }


    public LatLng startPoint() {
        return start;
    }


    public Segment copy() {
        final Segment copy = new Segment();
        copy.start = start;
        copy.instruction = instruction;
        copy.length = length;
        copy.distance = distance;
        return copy;
    }


    public void setLength(final int length) {
        this.length = length;
    }


    public int getLength() {
        return length;
    }


    public void setDistance(double distance) {
        this.distance = distance;
    }


    public double getDistance() {
        return distance;
    }

}