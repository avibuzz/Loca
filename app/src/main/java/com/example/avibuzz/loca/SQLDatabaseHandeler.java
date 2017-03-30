package com.example.avibuzz.loca;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by avibuzz on 30/3/17.
 */

class SQLDatabaseHandeler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "homeLocations";
    private static final String TABLE_LOCATION = "locations";
    private static final String KEY_ID = "id";
    private static final String KEY_PLACES = "places";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";


    public SQLDatabaseHandeler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE "
                + TABLE_LOCATION + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_PLACES + " TEXT," + KEY_LATITUDE + " TEXT," +
                KEY_LONGITUDE + " TEXT " + ")";


        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);

    }

    void addLocation(SQLocal contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACES, contact.get_location());
        values.put(KEY_LATITUDE, contact.get_latitude());
        values.put(KEY_LONGITUDE, contact.get_longitude());

        db.insert(TABLE_LOCATION, null, values);
        Log.v("work", "work");
        db.close();
        Log.v("work", "work");


    }


    SQLocal getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_LOCATION,
                new String[]{KEY_ID, KEY_PLACES, KEY_LATITUDE, KEY_LONGITUDE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);


        if (cursor != null)
            cursor.moveToFirst();


        SQLocal locations = new SQLocal(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3)));
        return locations;

    }

    public List<SQLocal> getAllLocations() {
        List<SQLocal> locationList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {

            do {

                SQLocal locations = new SQLocal();
                locations.set_id(Integer.parseInt(cursor.getString(0)));
                locations.set_location(cursor.getString(1));
                locations.set_latitude(cursor.getString(2));
                locations.set_longitude(cursor.getString(3));

                locationList.add(locations);
            } while (cursor.moveToNext());

        }
        return locationList;
    }

    public void deleteContact(SQLocal local) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, KEY_ID + " = ?",
                new String[]{String.valueOf(local.get_id())});
        db.close();
    }
}


