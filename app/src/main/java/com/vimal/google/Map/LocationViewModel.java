package com.vimal.google.Map;


import android.content.Context;

import androidx.lifecycle.ViewModel;


public class LocationViewModel extends ViewModel {

    public LocationHelper getLocationHelper(Context mContext) {
        return LocationHelper.getInstance(mContext);
    }
}
