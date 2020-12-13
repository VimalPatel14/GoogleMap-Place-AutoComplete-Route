package com.vimal.google.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vimal.google.Map.AutoComplete.map_server.Route;
import com.vimal.google.Map.AutoComplete.map_server.Routing;
import com.vimal.google.Map.AutoComplete.map_server.RoutingListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapRouteActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener{

    private GoogleMap map;
    private LatLng start, dropLngLat;
    private Marker myMarkerPickUp;
    private Marker myMarkerDropOff;
    private Polyline polyline;
    Location currentlocal;
    private LocationViewModel locationViewModel;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maproute);
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

    }

    private void getLocationPermission() {

        int hasWriteStoragePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentlocal = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(MapRouteActivity.this);
                    }
                }
            });
        } else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentlocal = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(MapRouteActivity.this);
                    }
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        getCurrentLocation();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        start = new LatLng(currentlocal.getLatitude(), currentlocal.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 12));
        dropLngLat = new LatLng(Double.parseDouble("20.9467"), Double.parseDouble("72.9520"));
        setPickupPin(start);
        setDropOffPin(dropLngLat);

        List<LatLng> path = new ArrayList();
        path.add(new LatLng(currentlocal.getLatitude(), currentlocal.getLongitude()));
        path.add(new LatLng(Double.parseDouble("20.9467"), Double.parseDouble("72.9520")));


        if (dropLngLat != null) {
            setDropOffPin(dropLngLat);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(start);
            builder.include(dropLngLat);
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, (int) getResources().getDimension(R.dimen.dp_70));


            Routing routing = new Routing(Routing.TravelMode.DRIVING);
            routing.registerListener(MapRouteActivity.this);
            routing.execute(start, dropLngLat);
            map.moveCamera(cameraUpdate);
        }

        locationViewModel.getLocationHelper(MapRouteActivity.this).observe(this, new Observer<Location>() {

            @Override
            public void onChanged(@Nullable Location location) {
                Toast.makeText(MapRouteActivity.this, "on changed called", Toast.LENGTH_SHORT).show();
                start = new LatLng(location.getLatitude(), location.getLongitude());
                setPickupPin(start);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        if (map != null) {
            PolylineOptions polyoptions = new PolylineOptions();
            if (polyline != null) {
                polyline.remove();
            }
            polyoptions.color(Color.parseColor("#101010"));
            polyoptions.width(4);
            polyoptions.addAll(mPolyOptions.getPoints());
            polyline = map.addPolyline(polyoptions);
        }
    }

    private void setDropOffPin(LatLng latLng) {
        if (map != null) {
            if (myMarkerDropOff != null) {
                myMarkerDropOff.remove();
            }
            myMarkerDropOff = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff)));
        }
    }

    private void setPickupPin(LatLng latLng) {
        if (map != null) {
            if (myMarkerPickUp != null) {
                myMarkerPickUp.remove();
//                mapHandler = null;
            }
            myMarkerPickUp = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.picup)));
        }
    }

    private void removeDropOffPin() {
        if (map != null) {
            if (myMarkerDropOff != null) {
                myMarkerDropOff.remove();
            }

        }
    }


}