package com.imge.bus2.myTools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.imge.bus2.MainActivity;
import com.imge.bus2.R;
import com.imge.bus2.TimeActivity;
import com.imge.bus2.model.KeyBoard;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.RouteStopsDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchTools {
    Activity activity;
    private static SearchTools instance;
    private TextView start, end, match;
    private int mode = 1;
    private RadioGroup mode_group;      // 選擇模式用
    private RadioButton mode_start;     // 選擇搭車站模式
    private ImageButton start_cancel, end_cancel, search;
    private Button btn_time;
    private Set<String> routeIds_match = new HashSet<>();
    private EditText editText;

    private SearchTools(Activity activity) {
        super();
        this.activity = activity;
        start = activity.findViewById(R.id.start);
        end = activity.findViewById(R.id.end);
        match = activity.findViewById(R.id.match);

        mode_group = activity.findViewById(R.id.mode);
        mode_start = activity.findViewById(R.id.mode_start);
        mode_start.setChecked(true);
        mode_group.setOnCheckedChangeListener(myRadioListener);

        start_cancel = activity.findViewById(R.id.start_cancel);
        end_cancel = activity.findViewById(R.id.end_cancel);
        start_cancel.setOnClickListener(startCancelListener);
        end_cancel.setOnClickListener(endCancelListener);

        btn_time = activity.findViewById(R.id.time);
        btn_time.setOnClickListener(myTimeListener);

        editText = activity.findViewById(R.id.editText);
        search = activity.findViewById(R.id.search);
        search.setOnClickListener(searchListener);
        search.setOnLongClickListener(searchLongLinstener);
    }

    // 單例
    public static SearchTools getInstance(Activity activity){
        if (instance == null){
            synchronized (SearchTools.class){
                instance = new SearchTools(activity);
            }
        }
        return instance;
    }

    // 改變模式
    RadioGroup.OnCheckedChangeListener myRadioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.mode_start:
                    mode = 1;
                    break;
                case R.id.mode_end:
                    mode = 2;
                    break;
            }
            changeMode();
        }
    };

    // 顯示所選路線時刻表
    View.OnClickListener myTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Set<String> routeIds_match = getRouteIds_match();
            Intent intent = new Intent(activity, TimeActivity.class);
            intent.putExtra("routeIds_match", (HashSet<String>)routeIds_match );
            intent.putExtra("stops_start", (HashSet<String>)MapTools.stops_start );
            activity.startActivity(intent);
        }
    };

    View.OnClickListener startCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapTools.stops_start = new HashSet<>();
            show(1);
            changeMode();
            matchRoutes();
        }
    };

    View.OnClickListener endCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapTools.stops_end = new HashSet<>();
            show(2);
            changeMode();
            matchRoutes();
        }
    };

    private int index = 0;
    private String str_old = "";
    private List<String> strList = new ArrayList<>();
    View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            search();
        }
    };

    Dialog dialog_list;
    ListView listView;
    Button list_cancel;
    View.OnLongClickListener searchLongLinstener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            search();
            setDialogList();
            return true;
        }
    };

    // 搜尋站點
    private void search(){
        KeyBoard.hideKeyBoard(activity);

        String str = editText.getText().toString();
        str = str.trim();       // 去頭尾空白
        if (str.equals("")){        // 不為空
            Toast.makeText(activity, "請輸入內容", Toast.LENGTH_SHORT).show();
            return;
        }else if(str.equals(str_old)){      // 如果搜索文字與上次相同
            index++;        // index +1 ( 查詢用 )
        }else{      // 如果搜索文字與上次不同
            str_old = str;
            strList = new ArrayList<>();        // 新的 List
            for(String stopName : MapTools.stopMarkers.keySet() ){
                if (stopName.indexOf(str) >= 0){        // 找到相符的站點
                    strList.add(stopName);      // 加到 List
                }
            }

            if(strList.isEmpty()){      // 如果找不到相符站點
                Toast.makeText(activity, "查無此站", Toast.LENGTH_SHORT).show();
                return;
            }

            // 提示隱藏功能
            Toast.makeText(activity, "長按箭號，可查看全部搜索項", Toast.LENGTH_SHORT).show();
            index = 0;
        }

        // 超過 index，歸 0
        if (index >= strList.size()){
            index = 0;
        }
        MapTools.getInstance().searchStop(strList.get(index));      // 查詢並移到點上
    }

    // 設定與調整 dialog
    private void setDialogList(){
        if(strList.isEmpty()){      // 沒有查到站點
            Toast.makeText(activity, "無搜索項", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog_list = new Dialog(activity);
        dialog_list.setContentView(R.layout.dialog_list);

        // 列出站點
        listView = dialog_list.findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, strList);
        listView.setAdapter(adapter);

        // 取消按鍵
        list_cancel = dialog_list.findViewById(R.id.list_cancel);
        list_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_list.cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position-1;     // 改變 index 並執行 search
                search();       // search 會 index++
                dialog_list.dismiss();
            }
        });

        dialog_list.show();

//        // 螢幕大小 .d.heightPixels = 高 , d.widthPixels = 寬
//        DisplayMetrics d = activity.getResources().getDisplayMetrics();
//
//        // Dialog 大小 p.height = 高 , p.width = 寬
//        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
//        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        p.width = (int) (d.widthPixels * 0.85);
//
//        // 修改 dialog 視窗設定
//        dialog_list.getWindow().setAttributes(p);
    };

    // 取得 mode ( 1=選擇搭車站, 2=選擇目的地 )
    public int getMode(){
        return mode;
    }

    // 設定模式 ( 1=選擇搭車站, 2=選擇目的地 )
    public void setMode(int mode){
        this.mode = mode;
    }

    // 顯示已選擇的站牌
    public void show(Set<String> stops){
        String stops_str = stops.toString();
        stops_str = stops_str.substring(1, stops_str.length()-1 );
        switch (mode){
            case 1:
                stops_str = "搭車站：" + stops_str;
                start.setText(stops_str);
                break;
            case 2:
                stops_str = "目的地：" + stops_str;
                end.setText(stops_str);
                break;
            default:
                break;
        }
    }

    // 刪除用的
    public void show(int mode){
        switch (mode){
            case 1:
                start.setText("搭車站：");
                break;
            case 2:
                end.setText("目的地：");
                break;
            default:
                break;
        }
    }

    // 顯示匹配 A 站點的公車路線 會經過的站牌，並顯示已選擇的 B 站點
    public void changeMode(){
        Map<String, Marker> stopMarkers = MapTools.stopMarkers;

        Set<String> stops_A, stops_B;
        stops_A = new HashSet<>();
        stops_B = new HashSet<>();
        switch(mode){
            case 1:
                stops_A = MapTools.stops_end;
                stops_B = MapTools.stops_start;
                break;
            case 2:
                stops_A = MapTools.stops_start;
                stops_B = MapTools.stops_end;
                break;
            default:
                break;
        }

        BusStopDAO busStopDAO = new BusStopDAO(activity);
        // 取得經過 A 站的所有公車路線
        Set<String> routeIds = new HashSet<>();
        for(String stopName : stops_A){
            List details = busStopDAO.get(stopName);
            routeIds.addAll((Set<String>) details.get(0));
        }

        RouteStopsDAO routeStopsDAO = new RouteStopsDAO(activity);
        // 取得以上公車所經過的所有站牌
        Set<String> stops_match = new HashSet<>();
        for (String routeId : routeIds){
            stops_match.addAll( routeStopsDAO.get(routeId) );
        }


        for (String stopName : stopMarkers.keySet()){
            Marker marker = stopMarkers.get(stopName);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));     // 全部點設成紅色

            if(!stops_match.isEmpty()){
                if( stops_match.contains(stopName)){
                    marker.setVisible(true);        // 經過的站牌打開
                }else{
                    marker.setVisible(false);       // 不相關的關掉
                }
            }else{
                marker.setVisible(true);        // 如果沒有相關的站牌, 那就全部打開
            }

            // 已選取的點改為綠色
            if(stops_B.contains(stopName)){
                marker.setVisible(true);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        }

    }

    // 既經過 A 點也經過 B 點的路線
    public void matchRoutes(){
        Set<String> stops_start = MapTools.stops_start;;
        Set<String> stops_end = MapTools.stops_end;

        Set<String> routeIds_start = new HashSet<>();
        Set<String> routeIds_end = new HashSet<>();
        BusStopDAO busStopDAO = new BusStopDAO(activity);

        // 經過 A 點路線
        for(String stopName : stops_start){
            List details = busStopDAO.get(stopName);
            routeIds_start.addAll((Set<String>) details.get(0));
        }

        // 經過 B 點路線
        for(String stopName : stops_end){
            List details = busStopDAO.get(stopName);
            routeIds_end.addAll((Set<String>) details.get(0));
        }

        if(!stops_start.isEmpty() && !stops_end.isEmpty()){     // AB 路線皆不為空
            routeIds_start.retainAll(routeIds_end);     // 取交集
            routeIds_match = routeIds_start;
        }else if( !stops_start.isEmpty() ){     // A 不為空
            routeIds_match = routeIds_start;        // 取A
        }else if(!stops_end.isEmpty()){     // B不為空
            routeIds_match = routeIds_end;      // 取B
        }else{      // 皆為空
            routeIds_match = new HashSet<>();       // 那就空
        }

        String routeIds_str = routeIds_match.toString();
        routeIds_str = routeIds_str.substring(1, routeIds_str.length()-1 );
        match.setText("匹配公車：" + routeIds_str);
    }

    public Set<String> getRouteIds_match(){
        return routeIds_match;
    }

}
