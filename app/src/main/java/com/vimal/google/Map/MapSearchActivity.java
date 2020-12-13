package com.vimal.google.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vimal.google.Map.AutoComplete.adapter.GooglePlacesAutocompleteAdapterNew;
import com.vimal.google.Map.AutoComplete.adapter.SavedPlacesAdapter;
import com.vimal.google.Map.AutoComplete.extra.BaseApp;
import com.vimal.google.Map.AutoComplete.map_server.Constant;
import com.vimal.google.Map.AutoComplete.map_server.HyperlinkGoogleMap;
import com.vimal.google.Map.AutoComplete.model.DBHandler;
import com.vimal.google.Map.AutoComplete.model.FavouritePlaceItem;
import com.vimal.google.Map.AutoComplete.model.placeModel;
import com.vimal.google.Map.AutoComplete.utils.AlertUtils;
import com.vimal.google.Map.AutoComplete.utils.CommonImplementation;
import com.vimal.google.Map.AutoComplete.utils.OnTaskComplete;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

public class MapSearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private Context mContext;
    private LocationViewModel locationViewModel;
    private GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentlocal;

    public boolean isSearch = false;
    EditText edEnterLocation;
    ImageView imgClose;
    LinearLayout linearmain;
    ListView listview;
    TextView textviewSearchResult;

    GooglePlacesAutocompleteAdapterNew dataAdapter;
    SavedPlacesAdapter savedPlacesAdapter;
    private LatLng currentLatlng, previousDropoffLatlng;
    String stringAddress;
    List<placeModel> placeModels;
    List<String> placeList;
    List<String> secondList;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsearch);
        mContext = this;
        dbHandler = new DBHandler(MapSearchActivity.this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (dbHandler.getPlaces().getCount() == 0) {
            FavouritePlaceItem favouritePlaceItem = new FavouritePlaceItem(getString(R.string.add_home), "", "", "", "", "", "", "", "", "");
            dbHandler.insertPlaceEntry(favouritePlaceItem);
            FavouritePlaceItem favouritePlaceItem1 = new FavouritePlaceItem(getString(R.string.add_work), "", "", "", "", "", "", "", "", "");
            dbHandler.insertPlaceEntry(favouritePlaceItem1);
        }

        edEnterLocation = findViewById(R.id.edEnterLocation);
        imgClose = findViewById(R.id.imgClose);
        linearmain = findViewById(R.id.linearmain);
        listview = findViewById(R.id.listview);
        textviewSearchResult = findViewById(R.id.textviewSearchResult);

        setupViewModel();
        checkLocationPermission();


        HyperlinkGoogleMap.getInstance().stopLocationUpdate();
        HyperlinkGoogleMap.getInstance().startLocationUpdate(MapSearchActivity.this, new HyperlinkGoogleMap.ProvideLocation() {


            @Override
            public void currentLocation(Location location) {
                currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onError(String str) {

            }
        });

        placeModels = new ArrayList<>();
        dataAdapter = new GooglePlacesAutocompleteAdapterNew(getApplicationContext(), placeModels);
        listview.setAdapter(dataAdapter);

        edEnterLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edEnterLocation.setText("");
                edEnterLocation.setCursorVisible(true);
                isSearch = false;
                return false;
            }
        });


        edEnterLocation.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    if (!isSearch) {
                        placeModels.clear();
                        if (placeModels != null) {
                            dataAdapter.notifyDataSetChanged();
                        }
                        if (currentLatlng != null) {
                            new AutocompleteApi(currentLatlng, s.toString()).execute();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(final CharSequence s, int start, int before, final int count) {
                if (!isSearch) {
                    if (s.length() >= 1) {
                        listview.setVisibility(View.VISIBLE);
                    } else {
                        listview.setVisibility(View.GONE);
                    }
                } else {
                    listview.setVisibility(View.INVISIBLE);
                }
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stringAddress = placeModels.get(position).getMainTitle() + " , " + placeModels.get(position).getSecondaryTitle();

                GetLocation(placeModels.get(position).getMainTitle() + " , " + placeModels.get(position).getSecondaryTitle(), placeModels.get(position).getPlaceID());

                closeKeyboard(MapSearchActivity.this);


            }
        });


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edEnterLocation.setText("");
            }
        });

    }

    private void setupViewModel() {
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
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentlocal = location;
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(MapSearchActivity.this);
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
                        supportMapFragment.getMapAsync(MapSearchActivity.this);
                    }
                }
            });
        }
    }


    private void subscribeToLocationUpdate() {
        locationViewModel.getLocationHelper(mContext).observe(this, new Observer<Location>() {

            @Override
            public void onChanged(@Nullable Location location) {

            }
        });
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
        LatLng latLng = new LatLng(currentlocal.getLatitude(), currentlocal.getLongitude());
        map.addMarker(new MarkerOptions().position(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Toast.makeText(EasyLocationPickerActivity.this, " "+latLng.toString(), Toast.LENGTH_SHORT).show();
                //move camera to new user selected positio
                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                map.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }


    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private class AutocompleteApi extends AsyncTask<Void, Void, StringBuilder> {
        private final LatLng latLng;
        private final String s;


        public AutocompleteApi(LatLng latLng, String s) {
            this.latLng = latLng;
            this.s = s;
        }

        @Override
        protected StringBuilder doInBackground(Void... voids) {
            return autocomplete(latLng, s);
        }

        @Override
        protected void onPostExecute(StringBuilder strings) {
            if (strings != null) {
                try {
                    // Create a JSON object hierarchy from the results
                    JSONObject jsonObj = new JSONObject(strings.toString());
                    JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                    // Extract the Place descriptions from the results
                    ArrayList<String> resultList = new ArrayList<String>(predsJsonArray.length());
                    placeList = new ArrayList<String>(predsJsonArray.length());
                    secondList = new ArrayList<String>(predsJsonArray.length());

                    placeModels.clear();
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                        System.out.println("============================================================");
                        resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                        placeModel placeModel = new placeModel();
                        placeList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                        placeModel.setMainTitle(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                        placeModel.setPlaceID(predsJsonArray.getJSONObject(i).getString("place_id"));
                        if (predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").has("secondary_text")) {
                            placeModel.setSecondaryTitle(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"));
                            secondList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"));
                        } else {
                            placeModel.setSecondaryTitle("");
                            secondList.add("");
                        }
                        placeModels.add(placeModel);
                    }
                } catch (JSONException e) {

                }
                try {
                    if (placeModels != null) {
                        dataAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public StringBuilder autocomplete(LatLng latLng, String input) {
            ArrayList<String> resultList = null;
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(Constant.PLACES_API_BASE + Constant.TYPE_AUTOCOMPLETE + Constant.OUT_JSON);
                sb.append("?key=AIzaSyDCUI-rOn-H1Aiiu5UTOOLpXK3YgMBWISk");
                //sb.append("&components=country:au");
                if (BaseApp.currentRide.getCurrentLatitude() != null && BaseApp.currentRide.getCurrentLOngitude() != null) {
                    sb.append("&location=" + BaseApp.currentRide.getCurrentLatitude() + "," + BaseApp.currentRide.getCurrentLOngitude());
                }

                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//                sb.append("&radius=" + String.valueOf(50));
                sb.append("&components=country:in");

                URL url = new URL(sb.toString());
                System.out.println("URL: " + url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {

                return null;
            } catch (IOException e) {

                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return jsonResults;


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //get all details regarding place
    private void GetLocation(final String str, String placeID) {
        // AlertUtils.showCustomProgressDialog(LocationSearchActivity.this);

        CommonImplementation.getInstance().doGetLocation(null, "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&sensor=false&key=AIzaSyB2tihwL918R-w2ahZuRQSCZBW1pkq523s&language=" + Locale.ENGLISH, new OnTaskComplete() {
            @Override
            public void onSuccess(Response data, boolean success) {
                //AlertUtils.dismissDialog();
                if (data.code() == 200) {

                    String LAT = "";
                    String Long = "";
                    String Location = "";
                    String City = "";
                    String State = "";
                    String postal_code = "";
                    String country = "";

                    try {

                        String response = data.body().string();
                        JSONObject object = new JSONObject(response);

                        String status = object.getString("status");

                        if (object.has("result")) {
                            //JSONArray results = object.getJSONArray("results");
                            int i = 0;
                            //Log.i("i", i + "," + results.length());
                            JSONObject r = object.getJSONObject("result");
                            JSONArray addressComponentsArray = r.getJSONArray("address_components");
                            do {

                                JSONObject addressComponents = addressComponentsArray.getJSONObject(i);
                                JSONArray typesArray = addressComponents.getJSONArray("types");
                                String types = typesArray.getString(0);

                                if (types.equalsIgnoreCase("sublocality")) {
                                    Location = addressComponents.getString("short_name");
                                    Log.i("Locality", Location);

                                } else if (types.equalsIgnoreCase("locality")) {
                                    City = addressComponents.getString("long_name");
                                    Log.i("City", City);

                                } else if (types.equalsIgnoreCase("administrative_area_level_1")) {
                                    State = addressComponents.getString("long_name");
                                    Log.i("State", State);

                                } else if (types.equalsIgnoreCase("postal_code")) {
                                    postal_code = addressComponents.getString("long_name");
                                    Log.i("postal_code", postal_code);
                                } else if (types.equalsIgnoreCase("country")) {
                                    country = addressComponents.getString("long_name");
                                    Log.i("country", country);
                                }

                                i++;
                            } while (i < addressComponentsArray.length());


                            JSONObject geometry = r.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");

                            LAT = location.getString("lat");
                            Long = location.getString("lng");


                                  /*  Log.i("JSON Geo Locatoin =>", currentLocation);
                                    return currentLocation;*/

                            String Data = LAT + ",,," + Long + ",,," + Location + ",,," + City + ",,," + State + ",,," + postal_code + ",,," + country;

                            LatLng latLng = new LatLng(Double.parseDouble(LAT), Double.parseDouble(Long));
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(Location);
                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            //increase value 5 to 10 for more zoom on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            map.addMarker(markerOptions);

                            closeKeyboard(MapSearchActivity.this);

                            listview.setVisibility(View.GONE);
                            edEnterLocation.setText("");
                            edEnterLocation.setCursorVisible(true);
                            isSearch = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (data.code() == 404) {

                }
            }

            @Override
            public void onFailure() {
                AlertUtils.dismissDialog();
            }
        });
    }


}
