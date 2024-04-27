package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;


package edu.odu.cs.teamblack.cs411.thecouponapp.services;
import javax.swing.DefaultBoundedRangeModel;
import android.location.location;
import android.os.Build;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.gpsFragment;

public class gpsFragment extends Fragment {


public class gpsService extends Service {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;

    // reference to UI elements

    TextView tv_lat, tv_lon, tv_altitude, tv_accurancy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationupdate, sw_gps;
    // variable to remember if we are tracking location or not
    boolean updateOn = false;
    

    LocationRequest locationRequest;

    LocationCallBack locationCallBack;
    //googles API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate  (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView(R.layout.gpsFragment);  ///change activity_main -- or to gpsService gps or gps_fragments

        // give each ui variable a value

        tv_lat = findViewBy(R.id.tv_lat);
        tv_lon = findViewBy(R.id.tv_lon);
        tv_altitude = findViewBy(R.id.tv_altitude);
        tv_speed = findViewBy(R.id.tv_speed);
        tv_sensor = findViewBy(R.id.tv_sensor);
        tv_updates = findViewBy(R.id.tv_updates);
        tv_address= findViewBy(R.id.tv_address);
        sw_gps = findViewBy(R.id.sw_gps);
        sw_locationupdate = findViewBy(R.id.sw_locationupdate);

        //set all properties of Location Request
        LocationRequest = new LocationRequest();

        LocationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        LocationRequest.setFastInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(locationRequest.Priority_BALANCED_POWER_ACCURACY);
        //event is triggered whenever update interval is met
        
        LocationCallBack = new LocationCallBack() {

        @Override 
        public void onLocationResult(LocationResult locationResult){
            //save thr location
            updateUIValues(locationResult.getLastLocation());
        }
    };

        sw_gps.setOnCLickListener(new View.OnCLickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()){
                    // most accurate - use gps
                    locationRequest.setPriority(Locationrequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.Priority_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }

        });

        sw_locationupdate.setOnCLickListener(new View.OnCLickListener() {
            @Override
            public void onClick(View v){
                if (sw_locationupdate.isChecked()){
                    //turn on location tracking
                    startLocationUpdates();
                }
                else{
                    //turn off location tracking
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();

    } //end onCreate method

    private void startLocationUpdates(){
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(LocationRequest, locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates(){
        tv_updates.setText("location is not being tracked");
        tv_lat.setText("location is not being tracked");
        tv_lon.setText("location is not being tracked");
        tv_speed.setText("location is not being tracked");
        tv_address.setText("location is not being tracked");
        tv_accurancy.setText("location is not being tracked");
        tv_altitude.setText("location is not being tracked");
        tv_sensor.setText("location not being tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }


 @Override
 public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
        case PERMISSION_FINE_LOCATION:
        if (grantResults[0] = PackageManager.PERMISSION_GRANTED){
            updateGPS();
        }
        else {
            Toast.makeText(this, "this app requires permission to be granted in order to work properly", Toast.Length_Short).show();
            finish();
        }
        break;
    }
 }

    private void updateGPS () {
        //get permission from the user to track to GPS
        //get the current location from fused client
        // update the ui set all properties in their associated view items
    
        fusedLocationProviderClient = LocationServices.getFusedFusedLocationProviderClient(gpsService.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user provides the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. Put the values of location
                    updateUIValues(location);
                }
            });
        }
        else {
            //permissions not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }

    private void updateUIValues(Location location){
        //update all of the text view objects with a new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_long.setText(String.valueOf(location.getLongitude()));
        tv_accurancy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.settext("Not available");
        }

        if (location.hasAltitude()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.settext("Not available");
        }

        Geocoder geocoder= new Geocoder(gpsService.this);
        try {
            List <Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e) {
            tv_address.setText("Unable to get street address");
        }
    }

}

}