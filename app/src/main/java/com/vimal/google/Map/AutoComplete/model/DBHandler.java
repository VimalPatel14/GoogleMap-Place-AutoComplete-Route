package com.vimal.google.Map.AutoComplete.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "taxi.db";

    String id, placeDisplayName, mainAddress, placeId, placeLatitude, placeLongitude, sublocality, locality, adminArea, postalcode, countryName;

    public static final String TABLE_PLACES = "places";
    public static final String PLACES_ID = "id";
    public static final String DISPLAY_NAME = "display_name";
    public static final String MAIN_ADDRESS = "main_address";
    public static final String PLACE_ID = "place_id";
    public static final String PLACE_LATITUDE = "place_latitude";
    public static final String PLACE_LONGITUDE = "place_longitude";
    public static final String PLACE_SUB_LOCALITY = "place_sub_locality";
    public static final String PLACE_LOCALITY = "place_locality";
    public static final String PLACE_ADMIN_AREA = "place_admin_area";
    public static final String PLACE_POSTAL_CODE = "place_postal_code";
    public static final String PLACE_COUNTRY_NAME = "place_country_name";


    String CREATE_PLACE = "CREATE TABLE " + TABLE_PLACES + " (" +
            PLACES_ID + " INTEGER PRIMARY KEY," +
            DISPLAY_NAME + " TEXT," +
            MAIN_ADDRESS + " TEXT," +
            PLACE_ID + " TEXT," +
            PLACE_SUB_LOCALITY + " TEXT," +
            PLACE_LOCALITY + " TEXT," +
            PLACE_ADMIN_AREA + " TEXT," +
            PLACE_POSTAL_CODE + " TEXT," +
            PLACE_COUNTRY_NAME + " TEXT," +
            PLACE_LATITUDE + " TEXT," +
            PLACE_LONGITUDE + " TEXT)";

    private static final String SQL_DELETE_PLACE =
            "DROP TABLE IF EXISTS " + TABLE_PLACES;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLACE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PLACE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertPlaceEntry(FavouritePlaceItem favouritePlaceItem) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DISPLAY_NAME, favouritePlaceItem.getPlaceDisplayName());
        contentValues.put(MAIN_ADDRESS, favouritePlaceItem.getMainAddress());
        contentValues.put(PLACE_ID, favouritePlaceItem.getPlaceId());
        contentValues.put(PLACE_LATITUDE, favouritePlaceItem.getPlaceLatitude());
        contentValues.put(PLACE_LONGITUDE, favouritePlaceItem.getPlaceLongitude());
        contentValues.put(PLACE_SUB_LOCALITY, favouritePlaceItem.getSublocality());
        contentValues.put(PLACE_LOCALITY, favouritePlaceItem.getLocality());
        contentValues.put(PLACE_ADMIN_AREA, favouritePlaceItem.getAdminArea());
        contentValues.put(PLACE_POSTAL_CODE, favouritePlaceItem.getPostalcode());
        contentValues.put(PLACE_COUNTRY_NAME, favouritePlaceItem.getCountryName());
        return sqLiteDatabase.insert(TABLE_PLACES, null, contentValues);
    }

    public void updatePlaceEntry(FavouritePlaceItem favouritePlaceItem) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DISPLAY_NAME, favouritePlaceItem.getPlaceDisplayName());
        contentValues.put(MAIN_ADDRESS, favouritePlaceItem.getMainAddress());
        contentValues.put(PLACE_ID, favouritePlaceItem.getPlaceId());
        contentValues.put(PLACE_LATITUDE, favouritePlaceItem.getPlaceLatitude());
        contentValues.put(PLACE_LONGITUDE, favouritePlaceItem.getPlaceLongitude());
        contentValues.put(PLACE_SUB_LOCALITY, favouritePlaceItem.getSublocality());
        contentValues.put(PLACE_LOCALITY, favouritePlaceItem.getLocality());
        contentValues.put(PLACE_ADMIN_AREA, favouritePlaceItem.getAdminArea());
        contentValues.put(PLACE_POSTAL_CODE, favouritePlaceItem.getPostalcode());
        contentValues.put(PLACE_COUNTRY_NAME, favouritePlaceItem.getCountryName());
        sqLiteDatabase.update(TABLE_PLACES, contentValues, PLACES_ID + " =?", new String[]{favouritePlaceItem.getId()});
    }

    public void deletePlace() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("delete  from " + TABLE_PLACES);
    }

    public Cursor getPlaces() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PLACES, null);
    }
}
