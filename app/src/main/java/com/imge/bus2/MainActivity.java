package com.imge.bus2;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.imge.bus2.model.MyFileIO;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.MyDBHelper;
import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.myTools.MapTools;
import com.imge.bus2.sharedPreferences.MyLog;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DataDownload dataDownload;
    public static Object downloadObj = new Object();
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataDownload = new DataDownload(MainActivity.this);
        setMap();
        setHandler();
        setDownloadThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDBHelper.closeDB();
    }

    // 檢查有沒有下載過必要資料
    private void setDownloadThread(){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 檢查有沒有下載過必要資料
                    if(!MyLog.getIsDownload(MainActivity.this)){

                        dataDownload.getRouteName();        // 如果沒有，先下載各路線中文名
                        synchronized (downloadObj) {
                            try {
                                downloadObj.wait();
                            } catch (Exception e) {
                                Log.e("MainActivity", "getRouteName() wait() 出錯");
                                e.printStackTrace();
                            }
                        }

                        dataDownload.getBusStops();     // 再來下載站點詳細資料
                        synchronized (downloadObj) {
                            try {
                                downloadObj.wait();
                            } catch (Exception e) {
                                Log.e("MainActivity", "getBusStops() wait() 出錯");
                                e.printStackTrace();
                            }
                        }
                    }

                    handler.sendEmptyMessage(0);

                }
            }).start();
    }

    // 啟用 map
    private void setMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapTools.getInstance());
    }

    private void setMark(){
        BusStopDAO busStopDAO = new BusStopDAO(MainActivity.this);
        Map<String, List> map = busStopDAO.getAll();
        List value;
        for(String stopName : map.keySet()){
            value = map.get(stopName);
            MapTools.getInstance().setMark(
                    stopName,
                    (Double) value.get(1),
                    (Double) value.get(2)
            );
        }
    }

    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        setMark();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
