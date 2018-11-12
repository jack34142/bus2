package com.imge.bus2;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;
import com.imge.bus2.config.MyConfig;
import com.imge.bus2.model.LocationUtils;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.MyDBHelper;
import com.imge.bus2.myTools.DataDownload;
import com.imge.bus2.myTools.MapTools;
import com.imge.bus2.myTools.SearchTools;
import com.imge.bus2.sharedPreferences.MyLog;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static Handler handler;
    private DataDownload dataDownload;      // 下載資料時使用的物件
    public static Thread downloadThread;        // 下載資料時使用的 Thread

    private Dialog dialog_wait;     // 下載資料時跳出的提示窗
    private TextView dialog_wait_tv;        // 提示窗的文字
    private long firstTime=0;       // 記錄用戶首次點擊返回的時間

    private ImageButton btn_show, btn_hide;
    private LinearLayout top_menu;
    private RadioGroup mode_group;
    private RadioButton mode_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setGps();
        setMap();       // 設置 google 的 call back
        setHandler();       // 設置 handler
        checkDownload();        // 檢查 是否下載過必要資料
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDBHelper.closeDB();       // 關閉 database
        System.exit(0);
    }

    private void initView(){
        btn_show = findViewById(R.id.show);
        btn_hide = findViewById(R.id.hide);
        top_menu = findViewById(R.id.top_menu);
        btn_show.setOnClickListener(showListener);
        btn_hide.setOnClickListener(hideListener);

        mode_group = findViewById(R.id.mode);
        mode_start = findViewById(R.id.mode_start);
        mode_start.setChecked(true);
        mode_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.mode_start:
                        SearchTools.getInstance(MainActivity.this).setMode(1);
                        Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mode_end:
                        SearchTools.getInstance(MainActivity.this).setMode(2);
                        Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show();
                        break;
                }
                SearchTools.getInstance(MainActivity.this).changeMode();
            }
        });

    }

    View.OnClickListener showListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            top_menu.setVisibility(View.VISIBLE);
            btn_show.setVisibility(View.GONE);
            btn_hide.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener hideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            top_menu.setVisibility(View.GONE);
            btn_show.setVisibility(View.VISIBLE);
            btn_hide.setVisibility(View.GONE);
        }
    };

    // 檢查有沒有下載過必要資料
    private void checkDownload(){

        // 檢查有沒有下載過必要資料
        if( MyLog.getDownloadVersion(MainActivity.this) < MyConfig.download_version){

            // 如果沒下載過，則創建這個 下載用的物件
            dataDownload = new DataDownload(MainActivity.this);

            downloadThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // dialog 只能在 main thread 執行
                            setDialog();        // 設定與調整 dialog
                            dialog_wait.show();     // 顯示 dialog
                        }
                    });

                    // 先下載各路線中文名
                    dataDownload.getRouteName();

                    try{
                        // 暫停這個 Thread 直到有人 interrupt
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

    // 設定與調整 dialog
    private void setDialog(){
        dialog_wait = new Dialog(MainActivity.this);
        dialog_wait.setContentView(R.layout.dialog_wait);

        dialog_wait_tv = dialog_wait.findViewById(R.id.dialog_wait_tv);

        // 禁用任何方式取消 Dialog
        dialog_wait.setCancelable(false);

        // 螢幕大小 .d.heightPixels = 高 , d.widthPixels = 寬
        DisplayMetrics d = getResources().getDisplayMetrics();

        // Dialog 大小 p.height = 高 , p.width = 寬
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.widthPixels * 0.85);

        // 修改 dialog 視窗設定
        dialog_wait.getWindow().setAttributes(p);
    };

    private void setGps(){
        // 檢查 gps 權限
        if( LocationUtils.initPermission(MainActivity.this) ){
            // gps listener
            LocationUtils.MyLocationListener mylocationListener = new LocationUtils.MyLocationListener(){

                @Override
                public void onSuccessLocation(Location location) {
                    super.onSuccessLocation(location);
                    MapTools.getInstance().setMyPosition(location.getLatitude(), location.getLongitude());
                    String test = location.getProvider()+" "+LocationUtils.getGPSLocation(MainActivity.this).getProvider();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    super.onProviderEnabled(provider);
                    setWhereAmI();
                }
            };

            LocationUtils.addLocationListener(MainActivity.this, LocationManager.NETWORK_PROVIDER, mylocationListener);
        }
    }

    public void setWhereAmI(){
        Location location = LocationUtils.getMyLocation();
        if(location != null){
            MapTools.getInstance().setCenter(location.getLatitude(), location.getLongitude());
            MapTools.getInstance().setMyPosition(location.getLatitude(), location.getLongitude());
        }else{
            Toast.makeText(MainActivity.this, "尚未找到你的位置，請稍後，並確認gps功能是否正常或開啟", Toast.LENGTH_LONG).show();
        }
    }

    // 設置 google map
    private void setMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapTools.getInstance());        // map 準備完成後自動 call back
        MapTools.getInstance().setActivity(MainActivity.this);      // 我在這個物件會用到 activity
    }

    // 在地圖上顯示公車站位
    private void setMark(){

    /*  取得資料庫中 busStop 的資料，Map<String, List> map
        *   key = stop name
        *   index = 0 >> (Set<String>) routeIds  ... ( 經過這個站點的公車有哪些 )
        *   index = 1 >> latitude
        *   index = 2 >> longitude
        * */
        BusStopDAO busStopDAO = new BusStopDAO(MainActivity.this);
        Map<String, List> map = busStopDAO.getAll();

        // 在 map 準備完成後，顯示在地圖上
        MapTools.getInstance().checkMapReady(map);
    }

    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        downloadThread = null;      // 這個 Thread 用不到了，刪掉
                        setMark();      // 在地圖上顯示公車站位
                        dialog_wait.dismiss();      // dialog 隱藏
                        break;
                    case 1:
                        // 下載的過程中，對使用者做適當的提示
                        dialog_wait_tv.setText("( " + msg.arg2 + " / 2 ) 資料處理中 "+ msg.arg1 + "%" );
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.where:
                setWhereAmI();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // requestCode == 1 是 MyGpsTools 設置的，用來檢查 Gps 權限
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "gps權限已取得", Toast.LENGTH_SHORT).show();
                setGps();       // 重新設置 gps listener
                setMap();       // 重新設置 map
            } else { // if permission is not granted
                Toast.makeText(MainActivity.this, "若不提供GPS權限，將自動定位至桃園火車站。", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);     // gps 設置頁面
//                activity.startActivityForResult(intent,0);         //此为设置完成后返回到获取界面
            }
        }
    }

}
