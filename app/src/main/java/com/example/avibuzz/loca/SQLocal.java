package com.example.avibuzz.loca;

/**
 * Created by avibuzz on 30/3/17.
 */

class SQLocal {
    int _id;
    String _location,_latitude,_longitude;

    public SQLocal(int id,String location, double latitude, double longitude) {
        this._id=id;
        this._location=location;
        this._latitude=String.valueOf(latitude);
        this._longitude=String.valueOf(longitude);


    }
    public SQLocal(){}

    public SQLocal(String location, double latitude, double longitude) {
        this._location=location;
        this._latitude=String.valueOf(latitude);
        this._longitude=String.valueOf(longitude);
    }

    public SQLocal(int id) {
        this._id=id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_location() {
        return _location;
    }

    public void set_location(String _location) {
        this._location = _location;
    }

    public String get_latitude() {
        return _latitude;
    }

    public void set_latitude(String _latitude) {
        this._latitude = _latitude;
    }

    public String get_longitude() {
        return _longitude;
    }

    public void set_longitude(String _longitude) {
        this._longitude = _longitude;
    }



}

