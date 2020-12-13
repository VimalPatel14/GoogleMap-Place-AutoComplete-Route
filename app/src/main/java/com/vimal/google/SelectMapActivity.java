package com.vimal.google;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.vimal.google.Map.MapLocationUpdateActivity;
import com.vimal.google.Map.MapRouteActivity;
import com.vimal.google.Map.MapRouteSearchActivity;
import com.vimal.google.Map.MapSearchActivity;
import com.vimal.google.Map.R;
import com.vimal.google.Map.service.MyService;

public class SelectMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(SelectMapActivity.this, MyService.class)); //start service which is MyService.java
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(SelectMapActivity.this, MyService.class)); //stop service which is MyService.java
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SelectMapActivity.this, MapSearchActivity.class);
                startActivity(login);
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SelectMapActivity.this, MapLocationUpdateActivity.class);
                startActivity(login);
            }
        });

        findViewById(R.id.routesearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SelectMapActivity.this, MapRouteSearchActivity.class);
                startActivity(login);
            }
        });

        findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SelectMapActivity.this, MapRouteActivity.class);
                startActivity(login);
            }
        });
    }
}