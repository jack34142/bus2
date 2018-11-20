package com.imge.bus2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.imge.bus2.adapter.FavoriteFragmentPagerAdapter;
import com.imge.bus2.mySQLite.FavoriteDAO;
import com.imge.bus2.myTools.CountDown;
import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.myTools.FavoriteCountDown;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {
    public static Handler handler;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private TextView countText;     // 底下倒數計時文字
    private CountDown countDown;        // 倒數計時工具
    private FavoriteFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        setHandler();

        Toast.makeText(FavoriteActivity.this, "避免流量偷跑，最小化時會暫停更新", Toast.LENGTH_SHORT).show();
        countDown = new FavoriteCountDown();        // 倒數計時工具
        countDown.start();      // Thread 開始運行
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countDown != null){
            countDown.go();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDown.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDown.close();
    }

    private void initView(){
        setTitle("我的最愛");
        countText = findViewById(R.id.time_count);

        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.time_viewPager);
        TabLayout tabLayout = findViewById(R.id.time_tabLayout);
        tabLayout.setupWithViewPager(viewPager);        // 用來同步 viewPager 與 tabLayout
    }

    private void setDownload(){
        FavoriteDAO favoriteDAO = new FavoriteDAO(FavoriteActivity.this);
        Set<String> routeId_set = favoriteDAO.getAll();

        if(!routeId_set.isEmpty()){
            DataDownload dataDownload = new DataDownload(FavoriteActivity.this);
            dataDownload.getFavorite(routeId_set);
        }else{
            Toast.makeText(FavoriteActivity.this, "我的最愛是空的喔", Toast.LENGTH_SHORT).show();
            findViewById(R.id.time_progressBar).setVisibility(View.GONE);

            if(adapter != null){
                adapter.updateData(new ArrayList<List<String>>());
                viewPager.setAdapter(adapter);
            }
            countDown.resetCount();
        }

    }

    private void dealDownlosd(final List<List<String>> routeList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(adapter == null){
                    adapter = new FavoriteFragmentPagerAdapter(fragmentManager, routeList);
                    viewPager.setAdapter(adapter);
                }else{
                    adapter.updateData(routeList);
                }
            }
        });
        countDown.resetCount();     // 重新計數
    }


    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        List<List<String>> routeList = (List<List<String>>) msg.obj;
                        dealDownlosd(routeList);
                        break;
                    case 1:
                        setDownload();
                        break;
                    case 2:
                        if(msg.arg1 == 0){
                            countText.setText("更新中 ...");
                        }else{
                            countText.setText("更新倒數 " + msg.arg1 + " 秒");
                        }
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
                countDown.updateNow();
                break;
            default:
                break;
        }
        return true;
    }
}
