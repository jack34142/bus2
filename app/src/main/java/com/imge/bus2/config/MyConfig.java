package com.imge.bus2.config;

import android.location.LocationManager;

public class MyConfig {
    public static int download_version = 1;     // 資料版本，搭配 MyLog.getDownloadVersion()、MyLog.setDownloadVersion() 使用
    public static int update_frq = 20;

    public static String provider = LocationManager.NETWORK_PROVIDER;       // 定位準確度較低，速度快
//    public static String provider = LocationManager.GPS_PROVIDER;       // 定位準確度高，速度慢

    public static String getRouteName_url = "https://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=d7a0513d-1a91-4ae6-a06f-fbf83190ab2a&rid=8cbcf170-8641-4a0d-8fe8-256a36f4c6cb";
    public static String getBusStop_url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetStop.json?routeIds=";
    public static String getComeTime_url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetEstimateTime.json?routeIds=";
}
