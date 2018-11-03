package com.imge.bus2;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

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
    public static Handler handler;
    private Dialog dialog_wait;
    public static Thread downloadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataDownload = new DataDownload(MainActivity.this);
        setMap();
        setHandler();
        checkDownload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDBHelper.closeDB();
    }

    // 檢查有沒有下載過必要資料
    private void checkDownload(){

        // 檢查有沒有下載過必要資料
        if( !MyLog.getIsDownload(MainActivity.this) ){
            downloadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 如果沒有，先下載各路線中文名
                    dataDownload.getRouteName();

                    try{
                        Thread.sleep(Long.MAX_VALUE);
                    }catch (Exception e){}

                    // 再來下載站點詳細資料
                    dataDownload.getBusStops();
                }
            });
            downloadThread.start();
        }else{
            // 如果下載過，那就直接 setMark()
            // 否則會在 dataDownload.getBusStops() 後執行
            setMark();
        }
    }

    private void setDialog(){
        dialog_wait = new Dialog(MainActivity.this);
        dialog_wait.setContentView(R.layout.dialog_wait);
        dialog_wait.setCancelable(false);

        // 螢幕大小 .d.heightPixels = 高 , d.widthPixels = 寬
        DisplayMetrics d = getResources().getDisplayMetrics();

        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.widthPixels * 0.85);
        dialog_wait.getWindow().setAttributes(p);
    };

    // 啟用 map
    private void setMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapTools.getInstance());
    }

    private void setMark(){
        BusStopDAO busStopDAO = new BusStopDAO(MainActivity.this);
        Map<String, List> map = busStopDAO.getAll();
        MapTools.getInstance().checkMapReady(MainActivity.this, map);
    }

    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        downloadThread = null;
                        setMark();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
