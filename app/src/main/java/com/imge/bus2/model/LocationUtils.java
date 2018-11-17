package com.imge.bus2.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

public class LocationUtils {

    private static final long REFRESH_TIME = 5000L;
    private static final float METER_POSITION = 4.0f;
    private static LocationListener mLocationListener;
    private static Location myLocation;

    public static class MyLocationListener implements LocationListener {

        @Override
        public final void onLocationChanged(Location location) {      // 每次定位時執行
            LocationUtils.myLocation = location;        // manager.getLastKnownLocation(provider) 經常回傳空值，以 getMyLocation() 代替
            onSuccessLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {       // gps功能開啟或關閉時執行

        }

        @Override
        public void onProviderEnabled(String provider) {        // gps 功能啟用時執行

        }

        @Override
        public void onProviderDisabled(String provider) {       // gps 功能關閉時執行

        }

        public void onSuccessLocation(Location location){
            // please override this function
        };

    }

    public static boolean initPermission(Activity activity) {
        // 檢查權限
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 請求權限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    // 取得 gps 定位信息
    public static Location getGPSLocation(@NonNull Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);

        // 检查權限
        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        // 檢查是否支持 gps 定位
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 取得最後的GPS定位信息，如果是第一次打開，通常是拿不到信息的
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        return location;
    }

    // 取得 network 定位信息
    public static Location getNetWorkLocation(Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);

        // 檢查權限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        // 檢查是否支持 network 定位
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // 取得最後的 network 定位信息
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;
    }

    public static String getBestProvider(Context context, Criteria criteria){
        LocationManager manager = getLocationManager(context);
        if (criteria == null) {
            criteria = new Criteria();
        }
        String provider = manager.getBestProvider(criteria, true);

        return provider;
    }

    // 取得最好定位方式的信息
    public static Location getBestLocation(Context context, Criteria criteria) {
        Location location;
        LocationManager manager = getLocationManager(context);

        String provider = getBestProvider(context, criteria);

        if (TextUtils.isEmpty(provider)) {
            // 如果找不到適合的 provider , 就使用 NETWORK_PROVIDER
            location = getNetWorkLocation(context);
        } else {
            // 檢查權限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            // 以最好的方式定位取得的最後信息
            location = manager.getLastKnownLocation(provider);
        }

        return location;
    }

    public static Location getMyLocation(){
        return myLocation;
    }

    // 以預設的時間和距離加入監聽
    public static void addLocationListener(Context context, String provider, LocationListener locationListener) {

        addLocationListener(context, provider, REFRESH_TIME, METER_POSITION, locationListener);
    }

    // 自定監聽的距離和時間加入監聽
    public static void addLocationListener(Context context, String provider, long time, float meter, LocationListener locationListener) {
        if (locationListener != null) {
            mLocationListener = locationListener;
        }
        if (locationListener == null) {
            // 內部類別 , 空白監聽
            mLocationListener = new MyLocationListener();
        }

        LocationManager manager = getLocationManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(myLocation == null){
            myLocation = getNetWorkLocation(context);
        }

        unRegisterListener(context);
        manager.requestLocationUpdates(provider, time, meter, mLocationListener);

    }

    // 取消監聽
    public static void unRegisterListener(Context context) {
        if (mLocationListener != null) {
            LocationManager manager = getLocationManager(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // 移除監聽
            manager.removeUpdates(mLocationListener);
        }
    }

    // 取得 LocationManager 物件
    private static LocationManager getLocationManager(@NonNull Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

}
