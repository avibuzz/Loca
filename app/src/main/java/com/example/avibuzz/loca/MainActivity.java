package com.example.avibuzz.loca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private static final String TAG=MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICE_RESOLUTION_RREQUEST=1000;
    private Location lastlocation;
    private GoogleApiClient googleApiClient;
    private boolean mRequestLocationUpdate=false;

    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL=10000;
    private static int FASTEST_INTERVAL=5000;
    private static int DISPLACEMENT=10;
    private TextView lblLocation,homloc,diffrence;
    private Button showlocation,startlocationupdates;
    String homelatitude;
    String homelongitude;
    String homename;

    Toolbar toolbar;NavigationView navigationView;
    TextView homsey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //seting the fragment
       /*jailoc fragment =new jailoc();
       android.support.v4.app.FragmentTransaction fragmentTransaction=
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();*/


        lblLocation=(TextView)findViewById(R.id.lblLocation);
        showlocation=(Button)findViewById(R.id.buttonShowLocation);
       
        diffrence=(TextView)findViewById(R.id.diff);
        homloc=(TextView)findViewById(R.id.homloc);





        try {
            Intent getResult = getIntent();
            String latitude = getResult.getStringExtra("latitude");
            String longitude= getResult.getStringExtra("longitude");
            String home = getResult.getStringExtra("home");
            homsey=(TextView)findViewById(R.id.homsey);
            homsey.setText("home is"+home);

            SharedPreferences sharedPrefer= getSharedPreferences("home", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPrefer.edit();
            editor.putString("Share_place",home);
            editor.putString("Share_latitude",latitude);
            editor.putString("Share_longitide",longitude);
            editor.apply();
            homloc.setText(latitude+" ,"+longitude);





            Log.v("lati", latitude);
        }catch (NullPointerException e){}








        if(checkPlayService())
        {
            buildGoogleApiClient();
            createLocationRequest();
        }



        showlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{displayLocation();
                    togglePeriodLocationUpdate();}catch (NullPointerException e){Toast.makeText(getApplicationContext(),"Please choose a home location",Toast.LENGTH_SHORT).show();}
            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_jai) {
            // Handle the camera action
            jailoc fragment =new jailoc();
            android.support.v4.app.FragmentTransaction fragmentTransaction=
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();




       /* } else if (id == R.id.nav_gallery) {*/

        } else if (id == R.id.nav_Cosh) {


            Intent i=new Intent(MainActivity.this,cosl.class);
            startActivity(i);

        } /*else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        if(resultCode!=ConnectionResult.SUCCESS)
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

    private void displayLocation(){
        lastlocation= LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastlocation!=null)
        {
            double latitude=lastlocation.getLatitude();
            double longitude=lastlocation.getLongitude();
            lblLocation.setText(latitude+", "+longitude);


            SharedPreferences sharedPref=getSharedPreferences("home",Context.MODE_PRIVATE);
            String homelatitude=sharedPref.getString("Share_latitude",null);
            String homelongitude=sharedPref.getString("Share_longitide",null);

            double dlat=latitude-Double.parseDouble(homelatitude);
            double dlon=longitude-Double.parseDouble(homelongitude);
            diffrence.setText(dlat+" , "+dlon);

        }else
        {
            lblLocation.setText("Couldn't get the location.Make sure u r connected to ht e internet  ");
        }
    }

    private void togglePeriodLocationUpdate()
    {
        if(!mRequestLocationUpdate)
        {

            mRequestLocationUpdate=true;
            startLocationUpdate();
        }else
        {

            mRequestLocationUpdate=false;
            stopLocationUpdate();

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
        displayLocation();

    }
}

