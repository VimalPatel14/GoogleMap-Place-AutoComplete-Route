package com.vimal.google.Map.AutoComplete.model;

import com.google.android.gms.maps.model.LatLng;


public class CurrentRide {

    String userId;
    String rideId;
    String date;
    String time;
    String pickupAddress;
    String dropOffAddress;
    boolean fillAddressDropoff;
    String currentCity;
    String currentCountry;
    String dropoffCity;
    String dropoffCountry;
    LatLng currentLatlng;
    boolean isShownLanguageOption;

    public boolean isShownLanguageOption() {
        return isShownLanguageOption;
    }

    public void setShownLanguageOption(boolean shownLanguageOption) {
        isShownLanguageOption = shownLanguageOption;
    }

    public LatLng getCurrentLatlng() {
        return currentLatlng;
    }

    public void setCurrentLatlng(LatLng currentLatlng) {
        this.currentLatlng = currentLatlng;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(String currentCountry) {
        this.currentCountry = currentCountry;
    }

    public String getDropoffCity() {
        return dropoffCity;
    }

    public void setDropoffCity(String dropoffCity) {
        this.dropoffCity = dropoffCity;
    }

    public String getDropoffCountry() {
        return dropoffCountry;
    }

    public void setDropoffCountry(String dropoffCountry) {
        this.dropoffCountry = dropoffCountry;
    }

    public boolean isFillAddressPickup() {
        return fillAddressPickup;
    }

    public void setFillAddressPickup(boolean fillAddressPickup) {
        this.fillAddressPickup = fillAddressPickup;
    }

    boolean fillAddressPickup;

    public boolean isFillAddressDropoff() {
        return fillAddressDropoff;
    }

    public void setFillAddressDropoff(boolean fillAddressDropoff) {
        this.fillAddressDropoff = fillAddressDropoff;
    }


    public String getCurrentLOngitude() {
        return currentLOngitude;
    }

    public void setCurrentLOngitude(String currentLOngitude) {
        this.currentLOngitude = currentLOngitude;
    }

    public String getCurrentLatitude() {

        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    String promocode;
    String tripType;
    String carTypeId;
    String carType;
    String currentLatitude;
    String currentLOngitude;
    String mapType;

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(String carTypeId) {
        this.carTypeId = carTypeId;
    }

    double pickLatitude;
    double pickLongitude;

    double dropOffLatitude;
    double dropOffLongitude;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public double getPickLatitude() {
        return pickLatitude;
    }

    public void setPickLatitude(double pickLatitude) {
        this.pickLatitude = pickLatitude;
    }

    public double getPickLongitude() {
        return pickLongitude;
    }

    public void setPickLongitude(double pickLongitude) {
        this.pickLongitude = pickLongitude;
    }

    public double getDropOffLatitude() {
        return dropOffLatitude;
    }

    public void setDropOffLatitude(double dropOffLatitude) {
        this.dropOffLatitude = dropOffLatitude;
    }

    public double getDropOffLongitude() {
        return dropOffLongitude;
    }

    public void setDropOffLongitude(double dropOffLongitude) {
        this.dropOffLongitude = dropOffLongitude;
    }
}
