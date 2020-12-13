package com.vimal.google.Map.AutoComplete.map_server;

import com.google.android.gms.maps.model.PolylineOptions;

public interface RoutingListener {
    void onRoutingFailure();

    void onRoutingStart();

    void onRoutingSuccess(PolylineOptions mPolyOptions, Route route);
}
