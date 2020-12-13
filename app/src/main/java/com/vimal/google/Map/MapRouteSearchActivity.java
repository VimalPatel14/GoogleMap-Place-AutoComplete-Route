package com.vimal.google.Map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapRouteSearchActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    private FloatingActionButton myLocation;
    private GoogleMap map;
    TextView txtpickup, txtDropOff;
    LatLng currentLatlng;
    // UserDetails mUserDetails;
    double mLat, mLng;

    public static int flagforcamerafocus = 0;
    List<Marker> listOfRemovableMarker;
    private final float currentZoom = 12.8F;
    private final float IsZoome = 0;
    private final float ZoomLavel = 12.8F;
    private final float ISHide = 0;
    private LatLng center, dropLngLat;
    private final Location prevLoc = new Location("");
    private final Location newLoc = new Location("");
    private final int mapType = 1;
    private Marker myMarkerPickUp;
    private Marker myMarkerDropOff;
    private Polyline polyline;
    String currentCity = "";
    String currentCountry = "";
    String dropoffCity = "";
    String dropoffCountry = "";
    String result;
    Double mPickuplat, mPickuplng;
    public static Handler handler, mapHandler;
    public static Runnable myRunnable;

    private final boolean handler_updats = true;

    private final boolean skip = false;

    public static LatLng currentLatLng = null;
    Marker marker;
    LatLng cLatlong;
    int decision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maproutesearch);
        txtpickup = findViewById(R.id.txt_pickup);
        txtDropOff = findViewById(R.id.txtDropoff);
        getLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtDropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddressVal(1);
            }
        });

    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permissions, 0);
            } else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getCurrentLocation();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

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
                    cLatlong = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
                    String address = getAddress(cLocation.getLatitude(), cLocation.getLongitude());

                    Log.d("Address", address);
                    txtpickup.setText("" + address);
                    mPickuplat = cLocation.getLatitude();
                    mPickuplng = cLocation.getLongitude();
                    setPickupPin(cLatlong);
                    // map.addMarker(new MarkerOptions().position(cLatlong).title(address));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(cLatlong, 17));
                } else {
                    Toast.makeText(MapRouteSearchActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getAddress(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                address = addresses.get(0).getAddressLine(0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void getAddressVal(int i) {
        decision = i;
        Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
        startActivityForResult(intent, 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            try {
                result = data.getStringExtra("result");
                Log.d("Result", result);


                String[] LocationData = result.split(",,,");
                txtDropOff.setText(LocationData[7]);

                Log.d("Sriram", "pickup " + mPickuplat + " " + mPickuplng);
                center = new LatLng(mPickuplat, mPickuplat);
                dropLngLat = new LatLng(Double.parseDouble(LocationData[0]), Double.parseDouble(LocationData[1]));

                Log.d("Sriram,", "Drop " + LocationData[0] + " " + LocationData[1]);
                setPickupPin(cLatlong);
                setDropOffPin(dropLngLat);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cLatlong != null) {
            setPickupPin(cLatlong);
        }
        if (dropLngLat != null) {
            setDropOffPin(dropLngLat);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(cLatlong);
            builder.include(dropLngLat);
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, (int) getResources().getDimension(R.dimen.dp_70));


            Routing routing = new Routing(Routing.TravelMode.DRIVING);
            routing.registerListener(MapRouteSearchActivity.this);
            routing.execute(cLatlong, dropLngLat);
            map.moveCamera(cameraUpdate);
        }


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

    private void clearRemovableMarker() {
        if (listOfRemovableMarker != null) {
            for (int i = 0; i < listOfRemovableMarker.size(); i++) {
                listOfRemovableMarker.get(i).remove();
            }
        }
    }

}