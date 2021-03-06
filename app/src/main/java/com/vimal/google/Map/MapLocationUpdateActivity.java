package com.vimal.google.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapLocationUpdateActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private Context mContext;
    private LocationViewModel locationViewModel;
    private Location locationObj;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maplocationupdate);
        mContext = this;


        setupViewModel();
        setupViews();
        getCurrentLocation();

    }  private void setupViewModel() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    public void checkLocationPermission() {
        int hasWriteStoragePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
                return;
            }
            subscribeToLocationUpdate();
        } else {
            subscribeToLocationUpdate();
        }
    }


    private void getCurrentLocation() {

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location cLocation) {
                if (cLocation != null) {
                    LatLng cLatlong = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
//                      String address = getAddress(cLocation.getLatitude(), cLocation.getLongitude());
/*
                    Log.d("Address",address);
                    txtpickup.setText(""+address);
                    mPickuplat=cLocation.getLatitude();
                    mPickuplng=cLocation.getLongitude();
                    setPickupPin(cLatlong);*/
                     map.addMarker(new MarkerOptions().position(cLatlong));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(cLatlong, 15));
                } else {
                    Toast.makeText(MapLocationUpdateActivity.this,
                            "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void setupViews() {

                checkLocationPermission();


        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void stopLocationUpdates() {
        locationViewModel.getLocationHelper(mContext).stopLocationUpdates();
    }



    private void subscribeToLocationUpdate() {
        locationViewModel.getLocationHelper(mContext).observe(this, new Observer<Location>() {

            @Override
            public void onChanged(@Nullable Location location) {
                Toast.makeText(mContext, "on changed called", Toast.LENGTH_SHORT).show();
                locationObj = location;

                plotMarkers(locationObj);
            }
        });
    }

    ArrayList<Location> locationArrayList = new ArrayList<>();
    private void plotMarkers(Location locationObj) {

        if(map != null){
            map.clear();
            LatLng india = new LatLng(locationObj.getLatitude(), locationObj.getLongitude());
            map.addMarker(new MarkerOptions().position(india));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            map.moveCamera(CameraUpdateFactory.newLatLng(india));
            map.animateCamera(zoom);



            locationArrayList.add(locationObj);
            Log.d("Location list", "plotMarkers: "+locationArrayList.size());

            //Draw Line
            LatLng singleLatLong = null;
            ArrayList<LatLng> pnts = new ArrayList<LatLng>();
            if(locationArrayList != null) {
                for(int i = 0 ; i < locationArrayList.size(); i++) {
                    double routePoint1Lat = locationArrayList.get(i).getLatitude();
                    double routePoint2Long = locationArrayList.get(i).getLongitude();
                    singleLatLong = new LatLng(routePoint1Lat,
                            routePoint2Long);
                    pnts.add(singleLatLong);

                    map.addPolyline(new PolylineOptions().
                            addAll(pnts)
                            .width(8)
                            .color(Color.BLUE)
                            .zIndex(30));
                }
            }



        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        subscribeToLocationUpdate();
                    }

                } else {

                    // permission denied
                }
                return;
            }

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }
}
