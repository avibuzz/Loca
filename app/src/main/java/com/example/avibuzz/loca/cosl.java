package com.example.avibuzz.loca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

public class cosl extends Activity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    Button save,disDb,set_as_home;
    EditText locname;
    TextView display;
    double latitude;
    double longitude;
    Spinner spinner;


    private static final String TAG=cosl.class.getSimpleName();//some change mainactivity.class
    private final static int PLAY_SERVICE_RESOLUTION_RREQUEST=1000;
    private Location lastlocation;
    private GoogleApiClient googleApiClient;
    private boolean mRequestLocationUpdate=false;

    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL=10000;
    private static int FASTEST_INTERVAL=5000;
    private static int DISPLACEMENT=10;
    ArrayList<String> location=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    final SQLDatabaseHandeler db =new SQLDatabaseHandeler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosl);


        save=(Button)findViewById(R.id.save);
        locname=(EditText)findViewById(R.id.locname);
        disDb=(Button)findViewById(R.id.disDb);
        display=(TextView)findViewById(R.id.display);
        spinner=(Spinner) findViewById(R.id.spinner);
        set_as_home=(Button) findViewById(R.id.go);


        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,location);

        if(checkPlayService())
        {
            buildGoogleApiClient();
            createLocationRequest();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  setLocation();
            }
        });

        try
        {
            List<SQLocal> locations=db.getAllLocations();
            for(SQLocal cn : locations){
                String log=" "+cn.get_location();
                location.add(log);
                Log.v("name",log);
                spinner.setAdapter(adapter);

            }}catch (NullPointerException e){}

        set_as_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int what=spinner.getSelectedItemPosition();
                Log.v("what",Integer.toString(what));

                //display particular
                SQLocal cn=db.getLocation(what+1);
                String log_place= cn.get_location();
                String log_latitude= cn.get_latitude();
                String log_longitude= cn.get_longitude();

                Intent i=new Intent(cosl.this,MainActivity.class);
                i.putExtra("latitude",log_latitude);
                i.putExtra("longitude",log_longitude);
                i.putExtra("home",log_place);
                startActivity(i);




            }
        });

        disDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SQLocal> locations=db.getAllLocations();
                for(SQLocal cn : locations){
                    String log= "id: "+cn.get_id()
                            +" ,place: "+cn.get_location()
                            +" ,latitude:"+cn.get_latitude()
                            +" ,longitude:"+cn.get_longitude();
                    display.setText(log);
                    Log.v("name",log);
                }

            }
        });

    }





    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient!=null)
        {
            googleApiClient.connect();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayService();
        if(googleApiClient.isConnected()&&mRequestLocationUpdate)
        {
            startLocationUpdate();
        }
    }

    private boolean checkPlayService() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RESOLUTION_RREQUEST).show();
            }else
            {
                Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    private void setLocation(){
        lastlocation= LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastlocation!=null)
        {
            latitude=lastlocation.getLatitude();
            longitude=lastlocation.getLongitude();

            try{db.addLocation(new SQLocal(
                    locname.getText().toString(),
                    latitude,longitude));}catch (NullPointerException e){}
        }else
        {
            Toast.makeText(getApplicationContext(),"Couldn't get the location.Make sure u r connected to ht e internet  ",Toast.LENGTH_SHORT).show();

        }
    }



    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,mLocationRequest,this);
    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest()
    {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if(mRequestLocationUpdate){startLocationUpdate();}

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"Connection failed"+connectionResult.getErrorCode());


    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation=location;
        Toast.makeText(getApplicationContext(),"location changed",Toast.LENGTH_SHORT).show();
        setLocation();

    }
}

