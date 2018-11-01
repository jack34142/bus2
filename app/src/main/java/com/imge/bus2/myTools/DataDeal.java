package com.imge.bus2.myTools;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.imge.bus2.MainActivity;
import com.imge.bus2.bean.BusStopsBean;
import com.imge.bus2.bean.RouteNameBean;
import com.imge.bus2.sharedPreferences.MyLog;
import com.imge.bus2.sharedPreferences.MyRouteName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDeal {
    Context context;
    Gson gson;

    public DataDeal(Context context) {
        super();
        this.context = context;
        gson = new Gson();
    }

    // 解析 json 得到 route 的 id, 編號, 中文名
    public void dealRouteName(String response){
        try{
            RouteNameBean routeNameBean = new RouteNameBean();
            List<RouteNameBean> list = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("routes");
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                routeNameBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), RouteNameBean.class);
                list.add(routeNameBean);
//                Log.d("DataDeal test", routeNameBean.getDdesc());

                MyRouteName.setRouteName(context, list);
            }

            MyLog.setIsDownload(context);
            synchronized (MainActivity.downloadObj) {
                MainActivity.downloadObj.notify();
            }

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    // 解析 json 得到 去+返程stop 的 經過的路線, 中文名, 經度, 緯度
    public void dealBusStops(String response){
        try{
            BusStopsBean busStopsBean = new BusStopsBean();
            List<BusStopsBean> list = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response);
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                busStopsBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusStopsBean.class);
                list.add(busStopsBean);
//                Log.d("DataDeal test", busStopsBean.getRouteId());
            }

            organizeBusStops(list);

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    private void organizeBusStops(List<BusStopsBean> list){

        Map<String, String> map = new HashMap<>();

        BusStopsBean busStopsBean;
        int len_list = list.size();
        for (int i=0; i<len_list; i++){
            busStopsBean = list.get(i);
            MapTools.getInstance().setMark(
                    busStopsBean.getNameZh(),
                    Double.parseDouble(busStopsBean.getLatitude()),
                    Double.parseDouble(busStopsBean.getLongitude()) );
        }
    }


}
