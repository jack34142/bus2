package com.imge.bus2;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.imge.bus2.adapter.TimeRecyclerViewAdapter;
import com.imge.bus2.myTools.DataDownload;

import java.util.List;
import java.util.Set;

public class TimeActivity extends AppCompatActivity {
    Intent intent;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_list);

        setHandler();

        intent = getIntent();
        Set<String> routeIds_match = (Set<String>) intent.getSerializableExtra("routeIds_match");
        Set<String> stops_start = (Set<String>) intent.getSerializableExtra("stops_start");

        if(!routeIds_match.isEmpty()){
            DataDownload dataDownload = new DataDownload(TimeActivity.this);
            dataDownload.getComeTime(routeIds_match, stops_start);
        }

    }

    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        final List<List<String>> routeList = (List<List<String>>) msg.obj;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RecyclerView timeRecyclerView = findViewById(R.id.timeRecyclerView);

                                // LinearLayoutManager 類似 ListView
                                LinearLayoutManager manager = new LinearLayoutManager(TimeActivity.this);
                                // 直列堆疊
                                manager.setOrientation(LinearLayoutManager.VERTICAL);
                                // 分格線
                                timeRecyclerView.addItemDecoration(new DividerItemDecoration(TimeActivity.this, DividerItemDecoration.VERTICAL));
                                timeRecyclerView.setLayoutManager(manager);

                                TimeRecyclerViewAdapter timeRecyclerViewAdapter = new TimeRecyclerViewAdapter(TimeActivity.this, routeList);
                                timeRecyclerView.setAdapter(timeRecyclerViewAdapter);
                            }
                        });

                        break;
                }
            }
        };
    }
}
