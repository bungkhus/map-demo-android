package com.bungkhus.ministudio.mapdemo;

import android.graphics.Point;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapPolygon;
import com.airbnb.android.airmapview.AirMapPolyline;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnCameraChangeListener;
import com.airbnb.android.airmapview.listeners.OnCameraMoveListener;
import com.airbnb.android.airmapview.listeners.OnInfoWindowClickListener;
import com.airbnb.android.airmapview.listeners.OnLatLngScreenLocationCallback;
import com.airbnb.android.airmapview.listeners.OnMapClickListener;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.airbnb.android.airmapview.listeners.OnMapMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnCameraChangeListener, OnMapInitializedListener,
        OnMapClickListener, OnCameraMoveListener, OnMapMarkerClickListener,
        OnInfoWindowClickListener, OnLatLngScreenLocationCallback {

    private AirMapView map;
    private DefaultAirMapViewBuilder mapViewBuilder;
    private FloatingActionButton fab;
    private AirMapInterface airMapInterface = null;

    LocationHelper.LocationResult locationResult;
    LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        map = (AirMapView) findViewById(R.id.map);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapInitializedListener(this);
        map.setOnInfoWindowClickListener(this);
        map.initialize(getSupportFragmentManager());

        setupNativeMap();

        if (airMapInterface != null) {
            map.initialize(getSupportFragmentManager(), airMapInterface);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Lokasi Anda Berhasil Terkirim",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setupNativeMap(){
        try {
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.NATIVE).build();
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "Sorry, native Google Maps are not supported by this device. " +
                            "Please make sure you have Google Play Services installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onCameraChanged(LatLng latLng, int zoom) {
        appendLog("Map onCameraChanged triggered with lat: " + latLng.latitude + ", lng: "
                + latLng.longitude);
    }

    @Override public void onMapInitialized() {
        appendLog("Map onMapInitialized triggered");
        this.locationResult = new LocationHelper.LocationResult(){
            @Override
            public void gotLocation(Location location){

                //Got the location!
                if(location!=null){

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Log.e("MAP DEMO", "lat: " + latitude + ", long: " + longitude);
                    LatLng latLng = new LatLng(latitude, longitude);
                    map.animateCenterZoom(latLng, 15);
                    // Add Circle
                    map.drawCircle(latLng, 500);
                    // enable my location
                    map.setMyLocationEnabled(true);
                }else{
//                    LatLng latLng = new LatLng(-6.8763, 107.6234);
//                    map.animateCenterZoom(latLng, 15);
//                    // Add Circle
//                    map.drawCircle(latLng, 500);
//                    // enable my location
//                    map.setMyLocationEnabled(true);
                    Log.e("MAP DEMO", "Location is null.");
                    Toast.makeText(MainActivity.this, "Location is null",
                            Toast.LENGTH_SHORT).show();
                }

            }

        };

        // initialize our useful class,
        this.locationHelper = new LocationHelper();
        locationHelper.getLocation(this, this.locationResult);
        locationHelper.stopGettingLocationUpdates();
    }

//    void setupMapLocation(LatLng latLng){
////        addMarker("Airbnb HQ", latLng, 1);
//        map.animateCenterZoom(latLng, 15);
//        // Add Circle
//        map.drawCircle(latLng, 500);
//        // enable my location
//        map.setMyLocationEnabled(true);
//    }
//
//    private void addMarker(String title, LatLng latLng, int id) {
//        map.addMarker(new AirMapMarker.Builder()
//                .id(id)
//                .position(latLng)
//                .title(title)
//                .iconId(R.drawable.pin)
//                .build());
//    }

    @Override public void onMapClick(LatLng latLng) {
        if (latLng != null) {
            appendLog(
                    "Map onMapClick triggered with lat: " + latLng.latitude + ", lng: "
                            + latLng.longitude);

            map.getMapInterface().getScreenLocation(latLng, this);
        } else {
            appendLog("Map onMapClick triggered with null latLng");
        }
    }

    @Override public void onCameraMove() {
        appendLog("Map onCameraMove triggered");
    }

    private void appendLog(String msg) {
        Log.d("MAP DEMO", msg);
    }

    @Override public void onMapMarkerClick(AirMapMarker airMarker) {
        appendLog("Map onMapMarkerClick triggered with id " + airMarker.getId());
    }

    @Override public void onInfoWindowClick(AirMapMarker airMarker) {
        appendLog("Map onInfoWindowClick triggered with id " + airMarker.getId());
    }

    @Override public void onLatLngScreenLocationReady(Point point) {
        appendLog("LatLng location on screen (x,y): (" + point.x + "," + point.y + ")");
    }
}
