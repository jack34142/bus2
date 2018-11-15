package com.imge.bus2.myTools;

import android.app.Activity;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imge.bus2.model.LocationUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapTools implements OnMapReadyCallback {
    private Activity activity;
    private static MapTools instance;
    private Marker myIcon = null;

    private GoogleMap mMap;
    private Thread cheackMapThread;

    public static Map<String, Marker> stopMarkers = new HashMap<>();
    public static Set<String> stops_start = new HashSet<>();
    public static Set<String> stops_end = new HashSet<>();

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
        mMap.setMinZoomPreference(14.5f);

        Location location = LocationUtils.getMyLocation();
        if (location != null){      // 如果定位成功
            // 地圖移到我的位置，並顯示
            setCenter(location.getLatitude(), location.getLongitude());
            setMyPosition(location.getLatitude(), location.getLongitude());
        }else{      // 如果定位失敗
            setMyPosition();      // 地圖移到 桃園車站
        }

        if(cheackMapThread != null){
            // google map 加載完畢，讓 cheackMapThread 繼續運作
            cheackMapThread.interrupt();
        }
        setListener();
    }

    // 標記一個點
    public void setMark(String stopName, double latitude, double longitude){
        LatLng sydney = new LatLng(latitude, longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(stopName));
        stopMarkers.put(stopName, marker);
    }

    // 地圖移到桃園火車站
    public void setMyPosition(){
        LatLng sydney = new LatLng(24.989420, 121.313502);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }

    // 標記我的位置
    public void setMyPosition(Double lat, Double lon){
        if(mMap == null){
            return;
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);

        if(myIcon == null){
            setCenter(lat, lon);
            myIcon = mMap.addMarker(
                    new MarkerOptions()
                            .position(sydney)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            );
        }else{
            myIcon.setPosition(sydney);
        }

    }

    // 地圖移到指定點
    public void setCenter(Double lat, Double lon){
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
    }

    // 設定 mark 點擊事件
    public void setListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                switch (SearchTools.getInstance(activity).getMode()){
                    case 1:
                        setSelected(stops_start, marker);
                        SearchTools.getInstance(activity).show(stops_start);
                        break;
                    case 2:
                        setSelected(stops_end, marker);
                        SearchTools.getInstance(activity).show(stops_end);
                        break;
                    default:
                        break;
                }
                SearchTools.getInstance(activity).matchRoutes();
                return false;
            }
        });
    }

    // mark 點擊後操作
    private void setSelected(Set<String> stops, Marker marker){
        String stopName = marker.getTitle();

        if( !stops.contains(stopName) ){
            stops.add(stopName);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));       // 選取變綠色
        }else{
            stops.remove(stopName);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));     // 取消變回紅色
        }
    }

    // 載入所有公車站牌
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

}
