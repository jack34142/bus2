package com.imge.bus2;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.imge.bus2.adapter.TimeFragmentPagetAdapter;
import com.imge.bus2.adapter.TimeRecyclerViewAdapter;
import com.imge.bus2.myTools.DataDownload;

import java.util.List;
import java.util.Set;

public class TimeActivity extends AppCompatActivity {
    Intent intent;
    public static Handler handler;
    FragmentManager fragmentManager;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        initView();
        setHandler();

        intent = getIntent();
        Set<String> routeIds_match = (Set<String>) intent.getSerializableExtra("routeIds_match");
        Set<String> stops_start = (Set<String>) intent.getSerializableExtra("stops_start");

        if(!routeIds_match.isEmpty()){
            DataDownload dataDownload = new DataDownload(TimeActivity.this);
            dataDownload.getComeTime(routeIds_match, stops_start);
        }

    }

    public void initView(){
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.time_viewPager);
        TabLayout tabLayout = findViewById(R.id.time_tabLayout);
        tabLayout.setupWithViewPager(viewPager);        // 用來同步 viewPager 與 tabLayout
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
                                TimeFragmentPagetAdapter adapter = new TimeFragmentPagetAdapter(fragmentManager, routeList);
                                viewPager.setAdapter(adapter);
                            }
                        });
                        break;
                }
            }
        };
    }
}
