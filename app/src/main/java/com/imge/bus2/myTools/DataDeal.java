package com.imge.bus2.myTools;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.imge.bus2.MainActivity;
import com.imge.bus2.Time2Activity;
import com.imge.bus2.TimeActivity;
import com.imge.bus2.bean.BusStopsBean;
import com.imge.bus2.bean.ComeTimeBean;
import com.imge.bus2.bean.RouteNameBean;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.RouteNameDAO;
import com.imge.bus2.mySQLite.RouteStopsDAO;
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
    private static final String TAG = "DataDeal";
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

                // 提示下載進度
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i*100/ary_len;
                msg.arg2 = 1;
                MainActivity.handler.sendMessage(msg);
//                Log.d(TAG, routeNameBean.getDdesc());
            }

            Map<String, String> map = new HashMap<>();
            int list_len = list.size();
            for(int i=0; i<list_len; i++){
                routeNameBean = list.get(i);
                // map key = routeName , value = 路線編號 + 路線名稱
                map.put(routeNameBean.getID(), routeNameBean.getNameZh()+" "+routeNameBean.getDdesc());
            }

            RouteNameDAO routeNameDAO = new RouteNameDAO(context);
            routeNameDAO.insert(map);

            Log.d(TAG, "dealRouteName() 下載完成");
            MainActivity.downloadThread.interrupt();

        }catch (Exception e){
            Log.e(TAG, "dealBusDetails 解析 json 失敗");
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

                // 提示下載進度
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i*100/ary_len;
                msg.arg2 = 2;
                MainActivity.handler.sendMessage(msg);
//                Log.d(TAG, busStopsBean.getRouteId());
            }

            // 整理資料
            organizeBusStops(list);

            Log.d(TAG, "organizeBusStops() 下載完成");
            MyLog.setDownloadVersion(context);
            MainActivity.handler.sendEmptyMessage(0);

        }catch (Exception e){
            Log.e(TAG, "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }
    }

    private void organizeBusStops(List<BusStopsBean> list){

        Map<String, List> stop_details = new HashMap<>();
        Map<String, Set<String>> route_stops = new HashMap<>();

    /*
                List value
                index = 0 >> routeIds ( List<String> )
                index = 1 >> latitude ( Double )
                index = 2 >> longitude ( Double )
         */
        List details;
        Set<String> routeIds;
        Set<String> stops;

        BusStopsBean busStopsBean;
        int len_list = list.size();
        for (int i=0; i<len_list; i++) {
            busStopsBean = list.get(i);

            int j = 1;
            String stopName = busStopsBean.getNameZh() + j;
            String routeId = busStopsBean.getRouteId();
            while (stop_details.containsKey(stopName)) {
                details = stop_details.get(stopName);
                Double lat_old = (Double) details.get(1);
                Double lon_old = (Double) details.get(2);
                Double lat_new = Double.parseDouble(busStopsBean.getLatitude());
                Double lon_new = Double.parseDouble(busStopsBean.getLongitude());
                // 每一經緯度 約 100公里
                Double distance = Math.abs(Math.pow(lat_old - lat_new, 2) + Math.pow(lon_old - lon_new, 2)) * 100 * 1000;

                // 同名且小於 50 公尺，視為同一個站點
                if (distance < 50) {
                    routeIds = (Set<String>) details.get(0);
                    routeIds.add(routeId);
                    details.set(0, routeIds);
                    stop_details.put(stopName, details);
                    break;
                } else {
                    j++;
                    stopName = busStopsBean.getNameZh() + j;
                    continue;
                }
            }

            // 找不到同名且小於 50 公尺的站點
            if (!stop_details.containsKey(stopName)) {
                details = new ArrayList();
                routeIds  = new HashSet<>();

                routeIds.add(routeId);
                details.add(routeIds);
                details.add(Double.parseDouble(busStopsBean.getLatitude()));
                details.add(Double.parseDouble(busStopsBean.getLongitude()));
                stop_details.put(stopName, details);
            }

            // 儲存各公車經過的站點
            if( route_stops.containsKey(routeId) ){
                stops = route_stops.get(routeId);
            }else{
                stops = new HashSet<>();
            }
            stops.add(stopName);
            route_stops.put(routeId, stops);

        }

        BusStopDAO busStopDAO = new BusStopDAO(context);
        busStopDAO.insert(stop_details);
        RouteStopsDAO routeStopsDAO = new RouteStopsDAO(context);
        routeStopsDAO.insert(route_stops);
//        Log.d(TAG, route_stops.get("5022").toString());
    }

    int last_value;     // 上一站的時間 ( 用來判斷公車的下一站用的 )
    // 處理公車預估時間
    public void dealComeTime(String response, Set<String> routeId_set, Set<String> stops_start){

        // 因為會有同名的站點，所以我在站名後面加數字做區別
        // 但原本沒有這數字，所以要去掉才能做比較
        stops_start = orginizeStopName(stops_start);

        Gson gson = new Gson();
    /* routeList.add( timeList )  >> 一筆資料 一個路線
        * List<String> timeList;
        * index = 0 >> routeId
        * index = 1 >> comeTime_go
        * index = 2 >> nextStop_go
        * index = 3 >> comeTime_back
        * index = 4 >> nextStop_back
        * */
        List<List<String>> routeList = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(response);
            for(String routeId : routeId_set){
                String comeTime_go, nextStop_go, comeTime_back, nextStop_back;
                comeTime_go = nextStop_go = comeTime_back = nextStop_back = "";

                List<String> timeList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray(routeId);

                int len_jsonArray = jsonArray.length();
                for (int i=0; i<len_jsonArray; i++){
                    ComeTimeBean comeTimeBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), ComeTimeBean.class);

                    int goBack = comeTimeBean.getGoBack();
                    switch(goBack){
                        case 1:
                            nextStop_go = getNextStop(nextStop_go, comeTime_go, comeTimeBean);      // 找下一站站點
                            comeTime_go = getComeTime(comeTime_go, comeTimeBean, stops_start);      // 找抵達時間
                            break;
                        case 2:
                            nextStop_back = getNextStop(nextStop_back, comeTime_back, comeTimeBean);        // 找下一站站點
                            comeTime_back = getComeTime(comeTime_back, comeTimeBean, stops_start);      // 找抵達時間
                            break;
                        default:
                            break;
                    }
                }

                timeList.add(routeId);
                timeList.add(comeTime_go);
                timeList.add(nextStop_go);
                timeList.add(comeTime_back);
                timeList.add(nextStop_back);

                routeList.add(timeList);
            }

            Message msg = new Message();
            msg.what = 0;
            msg.obj = routeList;
            TimeActivity.handler.handleMessage(msg);

        }catch (Exception e){
            Log.e(TAG,"dealComeTime() 解析 json 失敗");
            e.printStackTrace();
        }

    }

    // 處理特定公車抵達時間
    public void dealComeTime(String response, String routeId, int goBack){
        Gson gson = new Gson();
        /* routeList.add( timeList )  >> 一筆資料 一個路線
         * List<String> timeList;
         * index = 0 >> comeTime
         * index = 1 >> stopName
         * */
        List<List<String>> routeList = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray(routeId);

            int len_jsonArray = jsonArray.length();
            for (int i=0; i<len_jsonArray; i++){

                ComeTimeBean comeTimeBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), ComeTimeBean.class);

                if(comeTimeBean.getGoBack() == goBack){
                    List<String> timeList = new ArrayList<>();
                    timeList.add(transValue(comeTimeBean));
                    timeList.add(comeTimeBean.getStopName());

                    routeList.add(timeList);
                }else{
                    continue;
                }

            }

            Message msg = new Message();
            msg.what = 0;
            msg.obj = routeList;
            Time2Activity.handler.handleMessage(msg);

        }catch (Exception e){
            Log.e(TAG,"dealComeTime() 解析 json 失敗");
            e.printStackTrace();
        }

    }

    private Set<String> orginizeStopName(Set<String> stops_start){
        Set<String> set = new HashSet<>();
        for(String stopName : stops_start){
            stopName = stopName.substring(0, stopName.length()-1);      // 去掉最後的號碼
            set.add(stopName);
        }
        return set;
    }

    private String getComeTime(String comeTime, ComeTimeBean comeTimeBean, Set<String> stops_start){
        if(stops_start.isEmpty()){      // 如果沒有選搭車站, 就不用找了
            return comeTime;
        }
        if( comeTime.equals("") ){
            if( stops_start.contains(comeTimeBean.getStopName()) ){
                String value = comeTimeBean.getValue();
                if( value.equals("null") ){
                    comeTime = comeTimeBean.getComeTime();
                }else if( value.equals("-3") ){
                    comeTime = "末班已過";
                }else{
                    comeTime = value;
                }
            }
        }
        return comeTime;
    }

    private String getNextStop(String nextStop, String comeTime, ComeTimeBean comeTimeBean){
        if( nextStop.equals("") ){
            String value = comeTimeBean.getValue();
            if( !value.equals("null") && !value.equals("-3") ){
                nextStop = comeTimeBean.getStopName();
                last_value = 0;
            }
        }else{      // 如果同時有多台公車進行
            if(comeTime.equals("")){        // 找尋經過搭車站前的公車
                int value = Integer.parseInt( comeTimeBean.getValue() );

                if(value >= last_value){        // 這一站抵達時間大於等於下一站
                    last_value = value;     // 記錄上一站的抵達時間
                }else{      // 這一站抵達時間小於下一站 ( 找到另一台公車的下一站 )
                    nextStop = comeTimeBean.getStopName();      // 記錄這台公車
                    last_value = 0;     // 上一站的抵達時間歸 0
                }
            }
        }
        return nextStop;
    }

    // 跟據 value 轉成對應文字
    private String transValue(ComeTimeBean comeTimeBean){
        String value = comeTimeBean.getValue();

        if (value.equals("null")){
            return comeTimeBean.getComeTime();
        }else if(value.equals("-3")){
            return "末班已過";
        }else if(value.equals("0")){
            return "即將到站";
        }else{
            return value + " 分";
        }
    }






}
