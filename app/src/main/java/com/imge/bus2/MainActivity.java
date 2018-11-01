package com.imge.bus2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.sharedPreferences.MyLog;

public class MainActivity extends AppCompatActivity {
    DataDownload dataDownload;
    public static Object downloadObj = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataDownload = new DataDownload(MainActivity.this);
        setDownloadThread();
//        dataDownload.getRouteName();
//        dataDownload.getBusStops();
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

}
