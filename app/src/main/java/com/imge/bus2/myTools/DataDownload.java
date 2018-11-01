package com.imge.bus2.myTools;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imge.bus2.MainActivity;
import com.imge.bus2.model.MyVolley;
import com.imge.bus2.sharedPreferences.MyRouteName;

import java.util.Map;
import java.util.Set;

public class DataDownload {
    Context context;
    DataDeal dataDeal;

    public DataDownload(Context context) {
        super();
        this.context = context;
        dataDeal = new DataDeal(context);
    }

    // 下載 route 的 id, 編號, 中文名
    public void getRouteName(){
        String url = "https://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=d7a0513d-1a91-4ae6-a06f-fbf83190ab2a&rid=8cbcf170-8641-4a0d-8fe8-256a36f4c6cb";

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dataDeal.dealRouteName(response);       // 解析 json
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataDownload", "getBusStops() 下載 json 失敗");
                error.printStackTrace();
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }

    // 下載 去+返程stop 的 經過的路線, 中文名, 經度, 緯度
    public void getBusStops(){
        Map<String, String> myRouteName = MyRouteName.getRouteName(context);
        Set<String> routeIds_set = myRouteName.keySet();
        String routeIds = routeIds_set.toString();
        routeIds = routeIds.substring(1,routeIds.length()-1);
        routeIds = routeIds.replace(", ",",");
//        Log.d("DataDownload test", routeIds);

        String url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetStop.json?routeIds=" + routeIds;

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dataDeal.dealBusStops(response);       // 解析 json
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataDownload", "getBusStops() 下載 json 失敗");
                error.printStackTrace();
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);


    }





}
