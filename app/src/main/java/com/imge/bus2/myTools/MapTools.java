package com.imge.bus2.myTools;

import android.app.Activity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;

public class MapTools implements OnMapReadyCallback {
    private Activity activity;
    private static MapTools instance;

    private GoogleMap mMap;
    private Thread cheackMapThread;
    public static Thread cheackGpsThread;

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

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMark();
        checkGpsReady();

        if(cheackMapThread != null){
            cheackMapThread.interrupt();
        }
    }

    public void checkGpsReady(){
        cheackGpsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if ( !MyGpsTools.getGpsPermission() ){
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception e) {}
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double[] myPos = MyGpsTools.getMyPos();
                        setMyPosition(myPos[0], myPos[1]);
                    }
                });
            }
        });
        cheackGpsThread.start();
    }

    public void checkMapReady(final Map<String, List> map){

        cheackMapThread = new Thread(new Runnable() {
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
        cheackMapThread.start();
    }

    public void setMark(){
        LatLng sydney = new LatLng(24.989420, 121.313502);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }

    public void setMark(String stopName, double latitude, double longitude){
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(stopName));
    }

    public void setMyPosition(Double lat, Double lon){
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }
}
