package com.imge.bus2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.myTools.MapTools;
import com.imge.bus2.sharedPreferences.MyLog;

public class MainActivity extends AppCompatActivity {
    DataDownload dataDownload;
    public static Object downloadObj = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataDownload = new DataDownload(MainActivity.this);
        setMap();
        setDownloadThread();
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
                    }



                }
            }).start();
    }

    // 啟用 map
    private void setMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapTools.getInstance());
    }

}
