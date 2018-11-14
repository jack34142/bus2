package com.imge.bus2.myTools;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSort {
    private List<List<String>> routeList;
    private int a, b;

    public TimeSort(List<List<String>> routeList, int goBack) {
        super();
        this.routeList = routeList;

        switch (goBack){
            case 1:
                a = 1;
                b = 2;
                break;
            case 2:
                a = 3;
                b = 4;
                break;
        }

    }

    public List<List<String>> group(){
        List<List<String>> wait = new ArrayList<>();
        List<List<String>> stop = new ArrayList<>();
        List<List<String>> whatever = new ArrayList<>();
        List<List<String>> comming = new ArrayList<>();

        int len_routeList = routeList.size();
        for(int i=0; i<len_routeList; i++){
            List<String> timeList = routeList.get(i);
            String routeId = timeList.get(0);
            String comeTime = timeList.get(a);
            String nextStop = timeList.get(b);

            timeList = new ArrayList<>();
            timeList.add(routeId);
            timeList.add(comeTime);
            timeList.add(nextStop);

            if (comeTime.length() == 5){        // 尚未發車，下一班時間
                wait.add(timeList);
            }else if (comeTime.length() == 4){      // 末班已過
                stop.add(timeList);
            }else if (comeTime.equals("")){     // 空白
                whatever.add(timeList);
            }else{      // 已發車，再幾分到站
                comming.add(timeList);
            }
        }

        routeList = new ArrayList<>();
        wait = sortString(wait);
        comming = sortInt(comming);

        routeList.addAll(comming);
        routeList.addAll(wait);
        routeList.addAll(stop);
        routeList.addAll(whatever);

        return routeList;
    }

    public List<List<String>> sortString(List<List<String>> lists){

        boolean bubbleSort = true;

        while( bubbleSort ){
            bubbleSort = false;

            for (int i=0; i<lists.size()-1; i++){

                String x = lists.get(i).get(1);
                String y = lists.get(i+1).get(1);

                if ( y.compareTo(x) < 0 ){
                    lists.add(i, lists.get(i+1) );      // 在index = i 的位置複製 index = i+1 的資料
                    lists.remove(i+2);      // 原本 index = i+1 的位置向後一格，刪除
                    bubbleSort = true;
                }
            }
        }

        return lists;
    }

    public List<List<String>> sortInt(List<List<String>> lists){

        boolean bubbleSort = true;

        while( bubbleSort ){
            bubbleSort = false;

            for (int i=0; i<lists.size()-1; i++){
                int x = Integer.parseInt( lists.get(i).get(1) );
                int y = Integer.parseInt( lists.get(i+1).get(1) );

                if ( y < x ){
                    lists.add(i, lists.get(i+1) );      // 在index = i 的位置複製 index = i+1 的資料
                    lists.remove(i+2);      // 原本 index = i+1 的位置向後一格，刪除
                    bubbleSort = true;
                }
            }
        }

        return lists;
    }




}
