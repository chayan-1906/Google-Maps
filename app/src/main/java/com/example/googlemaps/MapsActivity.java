package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    //private final static int FINE_LOCATION_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView ( R.layout.activity_maps );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ( ).findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );

        setTitle ( "Maps" );

        prepareLocationServices ();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        showMeUserCurrentLocation ();
    }

    /* Method to request permission to access user's location */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void giveMePermissionToAccessLocation () {
        ActivityCompat.requestPermissions ( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 1 );
        //requestPermissions ( new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1 );
    }

    /* Overridden method, result after permission... */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        if (requestCode==1) {
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
                showMeUserCurrentLocation ();
            else
                FancyToast.makeText ( getApplicationContext (), "Access Denied", FancyToast.ERROR, FancyToast.LENGTH_SHORT, true ).show ();
        }
    }

    /* Display user's current location in Map */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showMeUserCurrentLocation() {

        /* If user denied to access his/her location, request to give permission */
        if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission ( this, Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED)
            giveMePermissionToAccessLocation ();
        else {  /* If user has given the permission to access the location */
            mMap.setMyLocationEnabled ( true );
            fusedLocationProviderClient.getLastLocation ().addOnCompleteListener ( new OnCompleteListener<Location> ( ) {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult ( );
                    if (location != null) { /* if Location is not null... */
                        LatLng latLng = new LatLng ( location.getLatitude ( ), location.getLongitude ( ) );
                        mMap.clear ( );
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom ( latLng, 16.0f );
                        mMap.moveCamera ( cameraUpdate );
                    } else  /* if Location is null... */
                        FancyToast.makeText ( getApplicationContext ( ), "Something went wrong", FancyToast.LENGTH_LONG, FancyToast.ERROR, true ).show ( );
                }
            } );
        }
    }

    private void prepareLocationServices() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( this );
    }
}