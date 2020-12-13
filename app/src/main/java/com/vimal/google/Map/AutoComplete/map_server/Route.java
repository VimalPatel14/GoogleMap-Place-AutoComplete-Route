package com.vimal.google.Map.AutoComplete.map_server;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String name;
    private final List<LatLng> points;
    private List<Segment> segments;
    private String copyright;
    private String warning;
    private String country;
    private int length;
    private String polyline;
    private String durationText;
    private String distanceText;
    private String endAddressText;

    public String getEndAddressText() {
        return endAddressText;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Route() {
        points = new ArrayList<LatLng>();
        segments = new ArrayList<Segment>();
    }

    public void addPoint(final LatLng p) {
        points.add(p);
    }

    public void addPoints(final List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void addSegment(final Segment s) {
        segments.add(s);
    }

    public List<Segment> getSegments() {
        return segments;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyright() {
        return copyright;
    }


    public void setWarning(String warning) {
        this.warning = warning;
    }


    public String getWarning() {
        return warning;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public String getCountry() {
        return country;
    }


    public void setLength(int length) {
        this.length = length;
    }


    public int getLength() {
        return length;
    }


    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }


    public String getPolyline() {
        return polyline;
    }

}

