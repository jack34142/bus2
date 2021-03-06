package com.imge.bus2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.imge.bus2.R;
import com.imge.bus2.mySQLite.RouteNameDAO;
import com.imge.bus2.myTools.TimeSort;
import java.util.List;

public class TimeRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<List<String>> routeList;
    private RouteNameDAO routeNameDAO;

    public TimeRecyclerViewAdapter(Context context, List<List<String>> routeList, int goBack) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        routeNameDAO = new RouteNameDAO(context);

        // 依照抵達時間重新排序
        TimeSort timeSort = new TimeSort(routeList, goBack);
        routeList = timeSort.group();

        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.time_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myHolder = (MyViewHolder) holder;

        List<String> timeList = routeList.get(position);

        String value = timeList.get(1);
        if( value.length() >= 4 || value.equals("") ){      // 4個字的 = 末班已過 , 5個字的 = XX:XX (發車時間), 空白就是空白
            myHolder.timeList_value.setText(timeList.get(1));
        }else if(value.equals("0")){        // 0分後抵達, 顯示即將到站
            myHolder.timeList_value.setText("即將到站");
        }else{      // 顯示幾分後到達
            myHolder.timeList_value.setText(timeList.get(1)+" 分");
        }

        // 公車路線名稱
        String routeNameZh = routeNameDAO.get(timeList.get(0));
        myHolder.timeList_nameZh.setText(routeNameZh);

        // 下一站站點名稱
        myHolder.timeList_nextStop.setText(timeList.get(2));
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView timeList_value, timeList_nameZh, timeList_nextStop;

        public MyViewHolder(View itemView) {
            super(itemView);
            timeList_value = itemView.findViewById(R.id.timeList_value);
            timeList_nameZh = itemView.findViewById(R.id.timeList_nameZh);
            timeList_nextStop = itemView.findViewById(R.id.timeList_nextStop);
        }
    }




}
