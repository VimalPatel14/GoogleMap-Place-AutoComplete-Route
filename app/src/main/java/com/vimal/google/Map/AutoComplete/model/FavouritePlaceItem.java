package com.vimal.google.Map.AutoComplete.model;

public class FavouritePlaceItem {

    String id, placeDisplayName, mainAddress, placeId, placeLatitude, placeLongitude, sublocality, locality, adminArea, postalcode, countryName;

    public FavouritePlaceItem() {
    }

    public FavouritePlaceItem(String id, String placeDisplayName, String mainAddress, String placeId, String placeLatitude, String placeLongitude, String sublocality, String locality, String adminArea, String postalcode, String countryName) {
        this.id = id;
        this.placeDisplayName = placeDisplayName;
        this.mainAddress = mainAddress;
        this.placeId = placeId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.sublocality = sublocality;
        this.locality = locality;
        this.adminArea = adminArea;
        this.postalcode = postalcode;
        this.countryName = countryName;
    }

    public FavouritePlaceItem(String placeDisplayName, String mainAddress, String placeId, String placeLatitude, String placeLongitude, String sublocality, String locality, String adminArea, String postalcode, String countryName) {
        this.placeDisplayName = placeDisplayName;
        this.mainAddress = mainAddress;
        this.placeId = placeId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.sublocality = sublocality;
        this.locality = locality;
        this.adminArea = adminArea;
        this.postalcode = postalcode;
        this.countryName = countryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceDisplayName() {
        return placeDisplayName;
    }

    public void setPlaceDisplayName(String placeDisplayName) {
        this.placeDisplayName = placeDisplayName;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        this.mainAddress = mainAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public String getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
