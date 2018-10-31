package com.imge.bus2.myTools;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imge.bus2.MainActivity;
import com.imge.bus2.model.MyVolley;

public class DataDownload {
    Context context;

    public DataDownload(Context context) {
        super();
        this.context = context;
    }

    public void getBusDetails(){
        String url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetStop.json?routeIds=5022";

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataDeal dataDeal = new DataDeal();
                dataDeal.dealBusDetails(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataDownload", "getBusDetails() 下載 json 失敗");
                error.printStackTrace();
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);


    }





}
