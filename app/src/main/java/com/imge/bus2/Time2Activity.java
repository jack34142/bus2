package com.imge.bus2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.imge.bus2.adapter.Time2RecyclerViewAdapter;
import com.imge.bus2.myTools.DataDownload;
import java.util.List;

public class Time2Activity extends AppCompatActivity {
    private Intent intent;
    public static Handler handler;
    private String routeId;        // 所選路線, 所選搭車站
    private int goBack;
    private RecyclerView timeRecyclerView;
    private Time2RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        initView();
        setHandler();
        setDownload();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        timeRecyclerView = findViewById(R.id.timeRecyclerView);
        // LinearLayoutManager 類似 ListView
        LinearLayoutManager manager = new LinearLayoutManager(Time2Activity.this);
        // 直列堆疊
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // 分格線
        timeRecyclerView.addItemDecoration(new DividerItemDecoration(Time2Activity.this, DividerItemDecoration.VERTICAL));
        timeRecyclerView.setLayoutManager(manager);

        routeId = intent.getStringExtra("routeId");
        goBack = intent.getIntExtra("goBack", 0);
    }

    // 下載資料
    private void setDownload(){
        DataDownload dataDownload = new DataDownload(Time2Activity.this);
        dataDownload.getComeTime(routeId, goBack);
    }

    private void setMyAdapter(final List<List<String>> routeList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(adapter == null){
                    adapter = new Time2RecyclerViewAdapter(Time2Activity.this, routeList);
                    timeRecyclerView.setAdapter(adapter);
                }else{
                    adapter.updateData(routeList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        List<List<String>> routeList = (List<List<String>>) msg.obj;
                        setMyAdapter(routeList);
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.time_update:
                setDownload();
                break;
            default:
                break;
        }
        return true;
    }
}
