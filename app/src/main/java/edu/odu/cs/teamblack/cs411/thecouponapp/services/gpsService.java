package edu.odu.cs.teamblack.cs411.thecouponapp.services;
import javax.swing.DefaultBoundedRangeModel;

import com.google.android.gms.location.FusedLocationProviderClient;


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
    //googles API for location services
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate  (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }
 
    private void updateGPS () {
        //get permission from the user to track to GPS
        //get the current location from fused client
        // update the ui set all properties in their associated view items
    
        fusedLocationProviderClient = LocationServices.getFusedFusedLocationProviderClient(gpsService.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provides the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()) {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. Put the values of location.XXX into UI Components
                    updateUIValues();
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

}
