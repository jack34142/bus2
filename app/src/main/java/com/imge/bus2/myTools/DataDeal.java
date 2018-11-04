package com.imge.bus2.myTools;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.imge.bus2.MainActivity;
import com.imge.bus2.bean.BusStopsBean;
import com.imge.bus2.bean.RouteNameBean;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.RouteNameDAO;
import com.imge.bus2.sharedPreferences.MyLog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataDeal {
    private Context context;
    private Gson gson;

    public DataDeal(Context context) {
        super();
        this.context = context;
        gson = new Gson();
    }

    // 解析 json 得到 route 的 id, 編號, 中文名
    public void dealRouteName(String response){
        try{
            RouteNameBean routeNameBean;
            List<RouteNameBean> list = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("routes");
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                routeNameBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), RouteNameBean.class);
                list.add(routeNameBean);
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i*100/ary_len;
                msg.arg2 = 1;
                MainActivity.handler.sendMessage(msg);
//                Log.d("DataDeal test", routeNameBean.getDdesc());
            }

            Map<String, String> map = new HashMap<>();
            int list_len = list.size();
            for(int i=0; i<list_len; i++){
                routeNameBean = list.get(i);
                map.put(routeNameBean.getID(), routeNameBean.getNameZh()+" "+routeNameBean.getDdesc());
            }

            RouteNameDAO routeNameDAO = new RouteNameDAO(context);
            routeNameDAO.insert(map);

            Log.d("DataDeal", "dealRouteName() 下載完成");
            MainActivity.downloadThread.interrupt();

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    // 解析 json 得到 去+返程stop 的 經過的路線, 中文名, 經度, 緯度
    public void dealBusStops(String response){
        try{
            BusStopsBean busStopsBean;
            List<BusStopsBean> list = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response);
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                busStopsBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusStopsBean.class);
                list.add(busStopsBean);

                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i*100/ary_len;
                msg.arg2 = 2;
                MainActivity.handler.sendMessage(msg);
//                Log.d("DataDeal test", busStopsBean.getRouteId());
            }

            organizeBusStops(list);
            Log.d("DataDeal", "organizeBusStops() 下載完成");
            MyLog.setIsDownload(context);
            MainActivity.handler.sendEmptyMessage(0);

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    private void organizeBusStops(List<BusStopsBean> list){

        Map<String, List> map = new HashMap<>();

    /*
                List value
                index = 0 >> routeIds ( List<String> )
                index = 1 >> latitude ( Double )
                index = 2 >> longitude ( Double )
         */
        List value;
        Set<String> routeIds;

        BusStopsBean busStopsBean;
        int len_list = list.size();
        for (int i=0; i<len_list; i++) {
            busStopsBean = list.get(i);

            int j = 1;
            String stopName = busStopsBean.getNameZh() + j;
            while (map.containsKey(stopName)) {
                value = map.get(stopName);
                Double lat_old = (Double) value.get(1);
                Double lon_old = (Double) value.get(2);
                Double lat_new = Double.parseDouble(busStopsBean.getLatitude());
                Double lon_new = Double.parseDouble(busStopsBean.getLongitude());
                // 每一經緯度 約 100公里
                Double distance = Math.abs(Math.pow(lat_old - lat_new, 2) + Math.pow(lon_old - lon_new, 2)) * 100 * 1000;

//                Log.d("DataDeal test", distance.toString());
                if (distance < 50) {
                    routeIds = (Set<String>) value.get(0);
                    routeIds.add(busStopsBean.getRouteId());
                    value.set(0, routeIds);
                    map.put(stopName, value);
                    break;
                } else {
                    j++;
                    stopName = busStopsBean.getNameZh() + j;
                    continue;
                }
            }

            if (!map.containsKey(stopName)) {
                value = new ArrayList();
                routeIds  = new HashSet<>();

                routeIds.add(busStopsBean.getRouteId());
                value.add(routeIds);
                value.add(Double.parseDouble(busStopsBean.getLatitude()));
                value.add(Double.parseDouble(busStopsBean.getLongitude()));
                map.put(stopName, value);
            }
        }

        JSONArray jsonArray = new JSONArray();
        try{
            for(String stopName : map.keySet()){
                value = map.get(stopName);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("stopName", stopName);
                jsonObject.put("latitude", (Double)value.get(1));
                jsonObject.put("longitude", (Double)value.get(2));
                jsonObject.put("routeIds", ((Set<String>)value.get(0)).toString() );
                jsonArray.put(jsonObject);
            }
        }catch (Exception e){
            Log.e("DataDeal", "organizeBusStops 製作 json 失敗");
            e.printStackTrace();
        }

        BusStopDAO busStopDAO = new BusStopDAO(context);
        busStopDAO.insert(map);

    }


}
