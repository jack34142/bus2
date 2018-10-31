package com.imge.bus2.myTools;

import android.util.Log;

import com.google.gson.Gson;
import com.imge.bus2.bean.BusStopsBean;
import com.imge.bus2.bean.RouteNameBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataDeal {
    Gson gson;

    public DataDeal() {
        super();
        gson = new Gson();
    }

    // 解析 json 得到 route 的 id, 編號, 中文名
    public void dealRouteName(String response){
        try{
            RouteNameBean routeNameBean = new RouteNameBean();
            ArrayList<RouteNameBean> list = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("routes");
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                routeNameBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), RouteNameBean.class);
                list.add(routeNameBean);
//                Log.d("DataDeal test", routeNameBean.getDdesc());
            }

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    // 解析 json 得到 去返程stop 的 中文名, 經度, 緯度
    public void dealBusDetails(String response){
        try{
            BusStopsBean busStopsBean = new BusStopsBean();
            ArrayList<BusStopsBean> list = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response);
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                busStopsBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusStopsBean.class);
                list.add(busStopsBean);
//                Log.d("DataDeal test", busStopsBean.getNameZh());
            }

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }


}
