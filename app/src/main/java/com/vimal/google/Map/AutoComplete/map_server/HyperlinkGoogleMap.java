package com.vimal.google.Map.AutoComplete.map_server;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import com.vimal.google.Map.AutoComplete.extra.BaseApp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HyperlinkGoogleMap implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public static HyperlinkGoogleMap getCurrentLocation = new HyperlinkGoogleMap();
    ProvideLocation provideLocation;
    RoutingListener routingListner;
    Context context;
    private final String TAG = "";
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private int GooglePlayServicestatus;
    private float MIN_DISTANCE_FOR_UPDATE = 0.0f;
    private long MIN_TIME_FOR_UPDATE = 500 * 1 * 1;
    private Routing routing;

    public HyperlinkGoogleMap() {
    }

    public static HyperlinkGoogleMap getInstance() {
        return getCurrentLocation;
    }


    // This constructor is for a default location request interval

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This constructor i for a customize a location request based on time and distance for update location

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    // Connect a google api client for get A location

    public void startLocationUpdate(Context context, ProvideLocation provideLocation) {
        this.context = context;
        this.provideLocation = provideLocation;
        if (isLatestPlayserviceinstalled()) {

                try {
                    if(googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }


    // When google Api client is connected at that time fist get  location and initialize a location request time interval for update a location

    public void startLocationUpdate(Context context, ProvideLocation provideLocation, long MIN_TIME_FOR_UPDATE, float MIN_DISTANCE_FOR_UPDATE) {
        this.context = context;
        this.provideLocation = provideLocation;
        this.MIN_DISTANCE_FOR_UPDATE = MIN_DISTANCE_FOR_UPDATE;
        this.MIN_TIME_FOR_UPDATE = MIN_TIME_FOR_UPDATE;
        if (isLatestPlayserviceinstalled()) {

                try {
                    buildGoogleApiClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

    }

    protected synchronized void buildGoogleApiClient() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (doesUserHavePermission()) {
            locationRequest = LocationRequest.create();
            locationRequest.setSmallestDisplacement(MIN_DISTANCE_FOR_UPDATE);
            locationRequest.setInterval(MIN_TIME_FOR_UPDATE); // milliseconds
            locationRequest.setFastestInterval(MIN_TIME_FOR_UPDATE); // the fastest rate in milliseconds at which your app can handle location updates
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            isNewLocationAvailable(lastLocation);
        } else {
            if (provideLocation != null)
                provideLocation.onError("Please provide a ACCESS_FINE_LOCATION permission in manifests");
        }


    }


    // When location change at that time onLocationChange method is called

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "connection failed*********************");
    }

    // Set a call back and send location to activity or fragment

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Callback for using this we can get a location in fragment or activity

    @Override
    public void onLocationChanged(Location location) {
        isNewLocationAvailable(location);

    }


    // Use for a check a permission is provided in manifests

    private void isNewLocationAvailable(Location location) {
        if (location != null && provideLocation != null) {
            BaseApp.currentRide.setCurrentLatitude(String.valueOf(location.getLatitude()));
            BaseApp.currentRide.setCurrentLOngitude(String.valueOf(location.getLongitude()));
            BaseApp.currentRide.setCurrentLatlng(new LatLng(location.getLatitude(),location.getLongitude()));
            provideLocation.currentLocation(location);
            DataToPref.setSharedPreferanceData(context.getApplicationContext(), Constant.USER_DEFAULT_LONGITUDE, Constant.USER_DEFAULT_LONGITUDE_DATA, String.valueOf(location.getLatitude()));
            DataToPref.setSharedPreferanceData(context.getApplicationContext(), Constant.USER_DEFAULT_LATITUDE, Constant.USER_DEFAULT_LATITUDE_DATA, String.valueOf(location.getLongitude()));

        }

    }

    // Check a latest GooglePlayService is available or not in device

    private boolean doesUserHavePermission() {
        int result = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Checking for a location is enable or disable

    private boolean isLatestPlayserviceinstalled() {
        GooglePlayServicestatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context.getApplicationContext());
        if (GooglePlayServicestatus == ConnectionResult.SUCCESS) {
            return true;
        } else {
            try {
                int requestCode = 10;
                GooglePlayServicesUtil.getErrorDialog(GooglePlayServicestatus, (Activity) context, requestCode).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    // Get current location from network provider

    /*private boolean isLocationEnabled() {
        int m_locationMode = 0;
        String m_locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                m_locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException m_e) {
                m_e.printStackTrace();
            }
            return m_locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            m_locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(m_locationProviders);
        }
    }
*/

    // Stop a location update

    private void getLocationFromNetworkProvider() {
        LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Log.e("location", "location from a network provider=============================" + location);
        } else {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }
    }

    // Get a Full address from a latlng

    public void stopLocationUpdate() {
        try {
            if(googleApiClient!=null)
            {
                if (googleApiClient.isConnected()) {
                    googleApiClient.disconnect();
                    googleApiClient=null;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get a Only city from a latlng

    public String getAddressFromLatlong(Context context, LatLng latLng) {
        this.context = context;
        Geocoder geocoder;
        String area = null;
        if (latLng != null) {
            try {
                List<Address> addresses = new ArrayList<Address>();
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (!addresses.isEmpty()) {

                    if (addresses.get(0).getAddressLine(0) != null) {
                        area = addresses.get(0).getAddressLine(0);
                        if (addresses.get(0).getAddressLine(1) != null) {
                            area = area + " " + addresses.get(0).getAddressLine(1);

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return area;
    }

    // Get a Only country from a latlng

    public String getCityFromLatlong(Context context, LatLng latLng) {
        this.context = context;
        Geocoder geocoder;
        String city = null;
        if (latLng != null) {
            try {
                List<Address> addresses = new ArrayList<Address>();
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (!addresses.isEmpty() && addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return city;
    }

    // Get a Location from a address

    public String getCountryFromLatlong(Context context, LatLng latLng) {
        this.context = context;
        Geocoder geocoder;
        String country = null;
        if (latLng != null) {
            try {
                List<Address> addresses = new ArrayList<Address>();
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (!addresses.isEmpty() && addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return country;
    }

    // Use for a draw a path on google map between two point

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        LatLng latLng = new LatLng(0, 0);
        this.context = context;

        if (strAddress != null && !strAddress.trim().equalsIgnoreCase("")) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            try {
                address = coder.getFromLocationName(strAddress, 5);
                if (address != null && address.size() != 0) {
                    Address location = address.get(0);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return latLng;
    }

    //Comparison for two latlong in kilometer

    public void drawPath(Context context, LatLng pickUp, LatLng dropOff, final RoutingListener routingListner) {
        try {
            this.routingListner = routingListner;
            this.context = context;
            routing = new Routing(Routing.TravelMode.DRIVING);
            routing.registerListener(new RoutingListener() {
                @Override
                public void onRoutingFailure() {
                    routingListner.onRoutingFailure();
                }

                @Override
                public void onRoutingStart() {
                    routingListner.onRoutingStart();
                }

                @Override
                public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
                    routingListner.onRoutingSuccess(mPolyOptions, route);
                }
            });
            routing.execute(pickUp, dropOff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calculate distance between two location based on kilometer and mile which is passed in unit

    public boolean comparisonBetweenTwoLocation(LatLng fistLatlng, LatLng secondLatlng, double kilometer) {
        return totalDistance(fistLatlng, secondLatlng, 'K') < kilometer;
    }

    public double totalDistance(LatLng fistLatlng, LatLng secondLatlng, char unit) {
        DecimalFormat twoDForm = null;
        double dist = 0;
        try {
            twoDForm = new DecimalFormat("#.##");
            double lat1 = fistLatlng.latitude, lon1 = fistLatlng.longitude, lat2 = secondLatlng.latitude, lon2 = secondLatlng.longitude;
            double theta = lon1 - lon2;
            dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit == 'K') {
                dist = dist * 1.609344;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Double.valueOf(twoDForm.format(dist));
    }

    public void navigateToGoogleMap(LatLng source, LatLng dest, Activity activity)

    {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + source.latitude + "," + source.longitude + "&daddr=" + dest.latitude + "," + dest.longitude));
        activity.startActivity(intent);
    }


    // Open google map and draw path on google map

    public Marker drawSingleMarkerOnMap(GoogleMap map, LatLng latLng, int id, String title) {

        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(id))
                .title(title));
        return marker;
    }

    public void drawMultipleMarkerOnMap(GoogleMap googleMap, List<LatLng> latLngList, int id) {
        try {
            if (googleMap != null) {
                for (int i = 0; i < latLngList.size(); i++) {
                    LatLng latLng = new LatLng(latLngList.get(i).latitude, latLngList.get(i).longitude);
                    Location targetLocation = new Location("");//provider name is unecessary
                    targetLocation.setLatitude(latLng.latitude);//your coords of course
                    targetLocation.setLongitude(latLng.longitude);
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(id)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface ProvideLocation {
        void currentLocation(Location location);

        void onError(String str);


    }


}
