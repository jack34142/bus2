package com.imge.bus2.myTools;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imge.bus2.model.LocationUtils;

import java.util.List;
import java.util.Map;

public class MapTools implements OnMapReadyCallback {
    private Activity activity;
    private static MapTools instance;
    Marker myIcon;

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

        setMyPosition();      // 先把地圖移到 桃園車站
        checkGpsReady();        // 如果 gps 正常運作，再把地圖移到使用者當前位置

        if(cheackMapThread != null){
            // google map 加載完畢，讓 cheackMapThread 繼續運作
            cheackMapThread.interrupt();
        }
    }

    public void checkGpsReady(){
        Location location = LocationUtils.getBestLocation(activity, null);
        if(location != null){
            setMyPosition(location.getLatitude(), location.getLongitude());
        }
    }

    public void checkMapReady(final Map<String, List> map){

        cheackMapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 如果 google map 還沒加載完畢，先等一下
                if(mMap == null){
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception e) {}
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在地圖上顯示站牌
                        for (String stopName : map.keySet()) {
                            List value = map.get(stopName);

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

    // 標記一個點
    public void setMark(String stopName, double latitude, double longitude){
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(stopName));
    }

    // 地圖移到桃園火車站
    public void setMyPosition(){
        LatLng sydney = new LatLng(24.989420, 121.313502);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }

    // 地圖移到指定位置
    public void setMyPosition(Double lat, Double lon){
        if(mMap == null){
            return;
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);

        if(myIcon == null){
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
        }else{
            myIcon.remove();
        }

        myIcon = mMap.addMarker(
                new MarkerOptions()
                        .position(sydney)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        );


    }
}
