package com.imge.bus2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.sharedPreferences.MyLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataDownload dataDownload = new DataDownload(MainActivity.this);
//        dataDownload.getBusStops();
//        if(!MyLog.getIsDownload(MainActivity.this)){
//            dataDownload.getRouteName();
//        }
        dataDownload.getRouteName();
        dataDownload.getBusStops();
    }
}
