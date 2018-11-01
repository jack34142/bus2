package com.imge.bus2.myTools;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTools implements OnMapReadyCallback {
    private static MapTools instance;
    GoogleMap mMap;

    private MapTools() {
        super();
    }

    public static MapTools getInstance(){
        if (instance == null){
            synchronized (MapTools.class){
                instance = new MapTools();
            }
        }
        return instance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(25.034419, 121.302718);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }

    public void setMark(String stopName, double latitude, double longitude){
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(stopName));
    }
}
