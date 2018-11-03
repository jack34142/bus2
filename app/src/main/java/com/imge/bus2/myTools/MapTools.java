package com.imge.bus2.myTools;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imge.bus2.MainActivity;
import com.imge.bus2.mySQLite.BusStopDAO;

import java.util.List;
import java.util.Map;

public class MapTools implements OnMapReadyCallback {
    private static MapTools instance;
    GoogleMap mMap;
    private Thread cheackThread;

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

        if(cheackThread != null){
            cheackThread.interrupt();
        }
    }

    public void checkMapReady(final Activity activity, final Map<String, List> map){

            cheackThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mMap == null){
                        try {
                            Thread.sleep(Long.MAX_VALUE);
                        } catch (Exception e) {}
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List value;
                            for (String stopName : map.keySet()) {
                                value = map.get(stopName);

                                MapTools.getInstance().setMark(
                                        stopName,
                                        (Double) value.get(1),
                                        (Double) value.get(2)
                                );
                            }
                        }
                    });

                }
            });
            cheackThread.start();
    }

    public void setMark(String stopName, double latitude, double longitude){
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(stopName));
    }
}
