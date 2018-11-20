package com.imge.bus2.myTools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imge.bus2.MainActivity;
import com.imge.bus2.config.MyConfig;
import com.imge.bus2.model.MyInterent;
import com.imge.bus2.model.MyVolley;
import com.imge.bus2.mySQLite.RouteNameDAO;
import java.util.Map;
import java.util.Set;

public class DataDownload {

    private static final String TAG = "DataDownload";
    private Context context;
    private DataDeal dataDeal;
    private StringRequest request;
    private static int count = 0;

    public DataDownload(Context context) {
        super();
        this.context = context;
        dataDeal = new DataDeal(context);
    }

    // 下載 route 的 id, 編號, 中文名
    public void getRouteName(){

        count = 0;
        request = new StringRequest(MyConfig.getRouteName_url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataDeal.dealRouteName(response);       // 解析 json
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                errorThread(error, "getRouteName() 出錯");
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }

    // 下載 去+返程stop 的 經過的路線, 中文名, 經度, 緯度
    public void getBusStops(){
        RouteNameDAO routeNameDAO = new RouteNameDAO(context);
        Map<String, String> myRouteName = routeNameDAO.getAll();
        Set<String> routeIds_set = myRouteName.keySet();
        String routeIds = routeIds_set.toString();
        routeIds = routeIds.substring(1,routeIds.length()-1);
        routeIds = routeIds.replace(", ",",");
//        Log.d(TAG, routeIds);

        count = 0;
        request = new StringRequest(MyConfig.getBusStop_url + routeIds, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataDeal.dealBusStops(response);       // 解析 json
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorThread(error, "getBusStops() 出錯");
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }

    // 下載預估時間用的
    public void getComeTime(final Set<String> routeId_set, final Set<String> stops_start){
        String routeIds = routeId_set.toString();
        routeIds = routeIds.substring(1,routeIds.length()-1);
        routeIds = routeIds.replace(", ",",");

        request = new StringRequest(MyConfig.getComeTime_url + routeIds, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataDeal.dealComeTime(response, routeId_set, stops_start);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getComeTime() 出錯");
                error.printStackTrace();
                MyVolley.getInstance(context).addToRequestQue(request);
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }

    // 下載特定公車的抵達時間
    public void getComeTime(final String routeId, final int goBack){

        request = new StringRequest(MyConfig.getComeTime_url + routeId, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataDeal.dealComeTime(response, routeId, goBack);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getComeTime() 出錯");
                error.printStackTrace();
                MyVolley.getInstance(context).addToRequestQue(request);
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }

    private void errorThread(final VolleyError error, final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity activity = (Activity)context;

                // 提示使用者沒有開啟網路，並於3秒後重試
                if( !MyInterent.getIsConn(context) ){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "請確保網路開啟 3秒後將重試", Toast.LENGTH_SHORT).show();
                        }
                    });

                    try{
                        Thread.sleep(3000);
                    }catch (Exception e){}
                }else{
                    count++;
                }

                if(count == 5){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "逾時次數過多，伺服器可能正在更新，請稍候再試", Toast.LENGTH_LONG).show();
                            if(MainActivity.handler != null){
                                MainActivity.handler.sendEmptyMessage(0);
                            }
                        }
                    });
                }else{
                    Log.e(TAG, msg);
                    error.printStackTrace();
                    MyVolley.getInstance(context).addToRequestQue(request);
                }
            }
        }).start();
    }





}
