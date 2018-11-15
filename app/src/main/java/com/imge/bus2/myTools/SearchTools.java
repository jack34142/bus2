package com.imge.bus2.myTools;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.imge.bus2.R;
import com.imge.bus2.mySQLite.BusStopDAO;
import com.imge.bus2.mySQLite.RouteStopsDAO;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchTools {
    Activity activity;
    private static SearchTools instance;
    private TextView start, end, match;
    private int mode = 1;
    ImageButton start_cancel, end_cancel;
    private Set<String> routeIds_match = new HashSet<>();

    private SearchTools(Activity activity) {
        super();
        this.activity = activity;
        start = activity.findViewById(R.id.start);
        end = activity.findViewById(R.id.end);
        match = activity.findViewById(R.id.match);

        start_cancel = activity.findViewById(R.id.start_cancel);
        end_cancel = activity.findViewById(R.id.end_cancel);
        start_cancel.setOnClickListener(startCancelListener);
        end_cancel.setOnClickListener(endCancelListener);
    }

    View.OnClickListener startCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapTools.stops_start = new HashSet<>();
            show(MapTools.stops_start);
            changeMode();
            matchRoutes();
        }
    };

    View.OnClickListener endCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapTools.stops_end = new HashSet<>();
            show(MapTools.stops_end);
            changeMode();
            matchRoutes();
        }
    };

    // 單例
    public static SearchTools getInstance(Activity activity){
        if (instance == null){
            synchronized (SearchTools.class){
                instance = new SearchTools(activity);
            }
        }
        return instance;
    }

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
                if( stops_match.contains(stopName) ){
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
