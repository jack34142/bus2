package com.imge.bus2.myTools;

import android.app.Activity;
import android.content.Context;
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

    public static SearchTools getInstance(Activity activity){
        if (instance == null){
            synchronized (SearchTools.class){
                instance = new SearchTools(activity);
            }
        }
        return instance;
    }

    public int getMode(){
        return mode;
    }

    public void setMode(int mode){
        this.mode = mode;
    }

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

        Set<String> routeIds = new HashSet<>();
        BusStopDAO busStopDAO = new BusStopDAO(activity);

        for(String stopName : stops_A){
            List details = busStopDAO.get(stopName);
            routeIds.addAll((Set<String>) details.get(0));
        }

        Set<String> stops_match = new HashSet<>();
        RouteStopsDAO routeStopsDAO = new RouteStopsDAO(activity);
        for (String routeId : routeIds){
            stops_match.addAll( routeStopsDAO.get(routeId) );
        }

        for (String stopName : stopMarkers.keySet()){
            Marker marker = stopMarkers.get(stopName);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            if(!stops_match.isEmpty()){
                if( stops_match.contains(stopName) ){
                    marker.setVisible(true);
                }else{
                    marker.setVisible(false);
                }
            }else{
                marker.setVisible(true);
            }

            if(stops_B.contains(stopName)){
                marker.setVisible(true);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        }

    }


    public void matchRoutes(){
        Set<String> stops_start = MapTools.stops_start;;
        Set<String> stops_end = MapTools.stops_end;

        Set<String> routeIds_start = new HashSet<>();
        Set<String> routeIds_end = new HashSet<>();
        BusStopDAO busStopDAO = new BusStopDAO(activity);

        for(String stopName : stops_start){
            List details = busStopDAO.get(stopName);
            routeIds_start.addAll((Set<String>) details.get(0));
        }

        for(String stopName : stops_end){
            List details = busStopDAO.get(stopName);
            routeIds_end.addAll((Set<String>) details.get(0));
        }


        String routeIds_str = "[]";
        if(!stops_start.isEmpty() && !stops_end.isEmpty()){
            routeIds_start.retainAll(routeIds_end);
            routeIds_match = routeIds_start;
        }else if( !stops_start.isEmpty() ){
            routeIds_match = routeIds_start;
        }else if(!stops_end.isEmpty()){
            routeIds_match = routeIds_end;
        }else{
            routeIds_match = new HashSet<>();
        }

        routeIds_str = routeIds_match.toString();
        routeIds_str = routeIds_str.substring(1, routeIds_str.length()-1 );
        match.setText("匹配公車：" + routeIds_str);
    }

    public Set<String> getRouteIds_match(){
        return routeIds_match;
    }

}
