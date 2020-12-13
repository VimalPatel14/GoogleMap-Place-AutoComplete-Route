package com.vimal.google.Map;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vimal.google.Map.AutoComplete.adapter.GooglePlacesAutocompleteAdapterNew1;
import com.vimal.google.Map.AutoComplete.adapter.SavedPlacesAdapter;
import com.vimal.google.Map.AutoComplete.extra.BaseApp;
import com.vimal.google.Map.AutoComplete.map_server.Constant;
import com.vimal.google.Map.AutoComplete.map_server.HyperlinkGoogleMap;
import com.vimal.google.Map.AutoComplete.model.DBHandler;
import com.vimal.google.Map.AutoComplete.model.FavouritePlaceItem;
import com.vimal.google.Map.AutoComplete.model.placeModel;
import com.vimal.google.Map.AutoComplete.utils.AlertUtils;
import com.vimal.google.Map.AutoComplete.utils.CommonImplementation;
import com.vimal.google.Map.AutoComplete.utils.DebugLog;
import com.vimal.google.Map.AutoComplete.utils.OnTaskComplete;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by ${hlink} on 13/4/16.
 */
public class SaveLocationSearchActivity extends AppCompatActivity implements OnMapReadyCallback {


    public boolean isSearch = false;
    ImageView imageViewBack;
    TextView toolBarTitle;
    TextView textViewDone;
    EditText edEnterLocation;
    ImageView imgClose;
    LinearLayout linearmain;
    ListView listview;
    ListView listView1;
    TextView textviewSearchResult;
    ImageView imageView1;
    ImageView imageViewLocation;
    LinearLayout linearLayoutProgressbar;


    //private GooglePlacesAutocompleteAdapter dataAdapter;

    GooglePlacesAutocompleteAdapterNew1 dataAdapter;

    SavedPlacesAdapter savedPlacesAdapter;

    private SupportMapFragment mapFragment;
    private LatLng currentLatlng, previousDropoffLatlng;
    GoogleMap googleMap;
    String AddressData;
    String stringAddress;
    List<placeModel> placeModels;
    List<String> placeList;
    List<String> secondList;

    DBHandler dbHandler;

    String id;

    FavouritePlaceItem favouritePlaceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getStringExtra("id");

        dbHandler = new DBHandler(SaveLocationSearchActivity.this);
        imageViewBack = findViewById(R.id.imageViewBack);
        toolBarTitle = findViewById(R.id.toolBarTitle);
        textViewDone = findViewById(R.id.textViewDone);
        edEnterLocation = findViewById(R.id.edEnterLocation);
        imgClose = findViewById(R.id.imgClose);
        linearmain = findViewById(R.id.linearmain);
        listview = findViewById(R.id.listview);
        listView1 = findViewById(R.id.listview1);
        textviewSearchResult = findViewById(R.id.textviewSearchResult);
        imageView1 = findViewById(R.id.imageView1);
        imageViewLocation = findViewById(R.id.imageViewLocation);
        linearLayoutProgressbar = findViewById(R.id.linearLayoutProgressbar);


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkEdittextEmpty(edEnterLocation)) {
//                    Intent returnIntent = new Intent();
//
//                    returnIntent.putExtra("result", AddressData + ",,," + stringAddress);
//                    setResult(Activity.RESULT_OK, returnIntent);
//                    finish();
                    favouritePlaceItem.setMainAddress(stringAddress);
                    dbHandler.updatePlaceEntry(favouritePlaceItem);
                    finish();


                }
            }
        });
        imageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMap != null && currentLatlng != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLatlng).zoom(16).build();

                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

            }
        });
        favouritePlaceItem = new FavouritePlaceItem();
        favouritePlaceItem.setId(id);
        if (id.equals("1")) {
            favouritePlaceItem.setPlaceDisplayName(getString(R.string.add_home));
        } else if (id.equals("2")) {
            favouritePlaceItem.setPlaceDisplayName(getString(R.string.add_work));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address_search_layout);


        toolBarTitle.setText(getString(R.string.title_savelocation));
        textViewDone.setText(getString(R.string.btn_save));
/*
        mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(map, mapFragment);
        fragmentTransaction.addToBackStack(SupportMapFragment.class.getName());
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);*/

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        placeModels = new ArrayList<>();
        dataAdapter = new GooglePlacesAutocompleteAdapterNew1(getApplicationContext(), placeModels);
        listview.setAdapter(dataAdapter);

        listView1.setAdapter(savedPlacesAdapter);
        listView1.setVisibility(View.GONE);


      /*  dataAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.adapter_google_places_autocomplete, LocationSearchActivity.this);
        listview.setAdapter(dataAdapter);*/

        HyperlinkGoogleMap.getInstance().stopLocationUpdate();
        HyperlinkGoogleMap.getInstance().startLocationUpdate(SaveLocationSearchActivity.this, new HyperlinkGoogleMap.ProvideLocation() {


            @Override
            public void currentLocation(Location location) {
                currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onError(String str) {

            }
        });


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

                        //dataAdapter.getFilter().filter(s.toString());

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
                        imageView1.setVisibility(View.INVISIBLE);
                        imageViewLocation.setVisibility(View.INVISIBLE);
                        listview.setVisibility(View.VISIBLE);
                        listView1.setVisibility(View.GONE);
                    } else {
                        imageView1.setVisibility(View.INVISIBLE);
                        imageViewLocation.setVisibility(View.INVISIBLE);
                        listview.setVisibility(View.GONE);
                        listView1.setVisibility(View.GONE);
                    }
                } else {
                    imageView1.setVisibility(View.VISIBLE);
                    imageViewLocation.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);
                    listView1.setVisibility(View.GONE);
                }


                /*if(count==0 )
                {imageView1.setVisibility(View.VISIBLE);

                    imageViewLocation.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);

                }else if(count!=1 && start==0 && before==count-1)
                {
                    imageView1.setVisibility(View.INVISIBLE);
                    imageViewLocation.setVisibility(View.INVISIBLE);
                    listview.setVisibility(View.VISIBLE);
                }else if(count!=1 && start==0 && before==0 || before!=0)
                {
                    imageView1.setVisibility(View.VISIBLE);
                    imageViewLocation.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);
                }

                else {
                    imageViewLocation.setVisibility(View.INVISIBLE);
                    imageView1.setVisibility(View.INVISIBLE);
                    listview.setVisibility(View.VISIBLE);
                }*/

            }
        });

//        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                stringAddress = favouritePlaceItems.get(position).getPlaceDisplayName() + favouritePlaceItems.get(position).getMainplaceName();
//
//                GetLocation(favouritePlaceItems.get(position).getMainplaceName() + " , " + favouritePlaceItems.get(position).getSubPlaceName(), favouritePlaceItems.get(position).getPlaceId());
//
//                closeKeyboard(SaveLocationSearchActivity.this);
//
//            }
//        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //stringAddress = (String) parent.getItemAtPosition(position);
                stringAddress = placeModels.get(position).getMainTitle() + " , " + placeModels.get(position).getSecondaryTitle();
                //AddressData = GooglePlacesAutocompleteAdapter.getLocationFromAddress(LocationSearchActivity.this, stringAddress);
//                AddressData = getLocationFromAddress(LocationSearchActivity.this, stringAddress);

                favouritePlaceItem.setPlaceId(placeModels.get(position).getPlaceID());
                GetLocation(placeModels.get(position).getMainTitle() + " , " + placeModels.get(position).getSecondaryTitle(), placeModels.get(position).getPlaceID());

                closeKeyboard(SaveLocationSearchActivity.this);
//                if (!checkEdittextEmpty(edEnterLocation)) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result", AddressData + ",,," + stringAddress);
//                    setResult(Activity.RESULT_OK, returnIntent);
//                    finish();
//                }


            }
        });


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edEnterLocation.setText("");
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        if (BaseApp.currentRide.getCurrentLatitude() != null && BaseApp.currentRide.getCurrentLOngitude() != null) {
            currentLatlng = new LatLng(Double.parseDouble(BaseApp.currentRide.getCurrentLatitude()), Double.parseDouble(BaseApp.currentRide.getCurrentLOngitude()));

            if (BaseApp.currentRide.isFillAddressDropoff()) {
                if (BaseApp.currentRide.getDropOffLatitude() != 0.0 && BaseApp.currentRide.getDropOffLongitude() != 0.0) {
                    previousDropoffLatlng = new LatLng(BaseApp.currentRide.getDropOffLatitude(), BaseApp.currentRide.getDropOffLongitude());
                    setMApWithOutDestination(previousDropoffLatlng);
                } else {
                    setMApWithOutDestination(currentLatlng);

                }
            } else {
                if (BaseApp.currentRide.getPickLatitude() != 0.0 && BaseApp.currentRide.getPickLongitude() != 0.0) {
                    setMApWithOutDestination(new LatLng(BaseApp.currentRide.getPickLatitude(), BaseApp.currentRide.getPickLongitude()));
                } else {
                    setMApWithOutDestination(currentLatlng);

                }

            }

        }


        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = googleMap.getCameraPosition().target;
                if (center != null)

                    getAdressFromLatlong(center);


            }
        });
    }

    public void setMApWithOutDestination(LatLng latLngPickUp) {


        if (googleMap != null && latLngPickUp != null) {

            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLngPickUp).zoom(16).build();

            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            linearLayoutProgressbar.setVisibility(View.GONE);

        }

    }

    public void getAdressFromLatlong(LatLng latLng) {
        // DebugLog.e("latlong is::::"+ latLng);

        try {
            closeKeyboard(SaveLocationSearchActivity.this);
            //edEnterLocation.setFocusable(false);
            isSearch = true;

            edEnterLocation.setCursorVisible(false);
            textviewSearchResult.setVisibility(View.INVISIBLE);
            Geocoder geocoder;
            if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                List<Address> addresses = new ArrayList<Address>();
                geocoder = new Geocoder(SaveLocationSearchActivity.this, Locale.ENGLISH);

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // DebugLog.e(addresses.toString());
                if (!addresses.isEmpty()) {
                    String area = null;
                    Address locationAddress = addresses.get(0);
                    if (addresses.get(0).getAddressLine(0) != null) {
                        area = addresses.get(0).getAddressLine(0);
                        if (addresses.get(0).getAddressLine(1) != null) {
                            area = area + " " + addresses.get(0).getAddressLine(1);
                        }

                    }

                    if (BaseApp.currentRide.isFillAddressDropoff()) {
                        edEnterLocation.setText(BaseApp.currentRide.getDropOffAddress());
                        isSearch = false;
                        BaseApp.currentRide.setFillAddressDropoff(false);
                    } else if (BaseApp.currentRide.isFillAddressPickup()) {
                        edEnterLocation.setText(BaseApp.currentRide.getPickupAddress());
                        isSearch = false;
                        BaseApp.currentRide.setFillAddressPickup(false);
                    } else {
                        edEnterLocation.setText(area);
                       /* if(BaseApp.currentRide.getPickupAddress()!=null)
                        {
                            edEnterLocation.setText(BaseApp.currentRide.getPickupAddress());
                        }*/
                    }


                    stringAddress = area;
                    String city = "";
                    if (locationAddress.getSubAdminArea() != null) {
                        city = locationAddress.getSubAdminArea();
                    } else {
                        city = locationAddress.getLocality();
                    }


                    AddressData = latLng.latitude + ",,," + latLng.longitude + ",,," + locationAddress.getSubLocality() + ",,," + locationAddress.getLocality() + ",,," + locationAddress.getAdminArea() + ",,," + locationAddress.getPostalCode() + ",,," + locationAddress.getCountryName();
                    favouritePlaceItem.setPlaceLatitude(latLng.latitude + "");
                    favouritePlaceItem.setPlaceLongitude(latLng.longitude + "");
                    favouritePlaceItem.setSublocality(locationAddress.getSubLocality());
                    favouritePlaceItem.setLocality(locationAddress.getLocality());
                    favouritePlaceItem.setAdminArea(locationAddress.getAdminArea());
                    favouritePlaceItem.setPostalcode(locationAddress.getPostalCode());
                    favouritePlaceItem.setCountryName(locationAddress.getCountryName());

                    DebugLog.e("on Get address result is full string" + AddressData);

                } else {

                    edEnterLocation.setText("");

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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


    public static String getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context, Locale.ENGLISH);
        List<Address> address;
        String data = "";


        try {
            // address = coder.getFromLocationName(strAddress, 5);
            address = coder.getFromLocationName(strAddress, 15);
            if (address == null || address.size() <= 0) {
                return data;
            }
            Address location = address.get(0);

            System.out.println("============" + location.getLatitude() + "===================" + location.getLongitude() + "====================");
            System.out.println("=====Local=======" + location.getSubLocality());
            System.out.println("=====City=======" + location.getLocality());
            System.out.println("=====State=======" + location.getAdminArea());
            System.out.println("=====PinCode=======" + location.getPostalCode());
            System.out.println("=====Country=======" + location.getCountryName());
            System.out.println("=====Country=======" + location.getSubAdminArea());
            System.out.println("=====Country=======" + location.getLocale());
            System.out.println("=====Country=======" + location.getCountryCode());

            data = location.getLatitude() + ",,," + location.getLongitude() + ",,," + location.getSubLocality() + ",,," + location.getLocality() + ",,," + location.getAdminArea() + ",,," +
                    location.getPostalCode() + ",,," + location.getCountryName();


        } catch (Exception ex) {

            ex.printStackTrace();
            return data;
        }
        return data;

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
                sb.append("?key=AIzaSyAItYsfjpg1pEWBr1DU8LKWW-gvODrDKA8");
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

                            String Data = LAT + ",,," + Long + ",,," + Location + ",,," + City + ",,," + State + ",,," +
                                    postal_code + ",,," + country;

                            favouritePlaceItem.setPlaceLatitude(LAT);
                            favouritePlaceItem.setPlaceLongitude(Long);
                            favouritePlaceItem.setSublocality(Location);
                            favouritePlaceItem.setLocality(City);
                            favouritePlaceItem.setAdminArea(State);
                            favouritePlaceItem.setPostalcode(postal_code);
                            favouritePlaceItem.setCountryName(country);
                            favouritePlaceItem.setMainAddress(str);

                            dbHandler.updatePlaceEntry(favouritePlaceItem);

                            finish();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra("result", Data + ",,," + str);
//                            setResult(Activity.RESULT_OK, returnIntent);
//                            finish();
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

    public boolean checkEdittextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

}
