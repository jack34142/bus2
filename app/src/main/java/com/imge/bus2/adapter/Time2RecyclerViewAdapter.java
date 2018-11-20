package com.imge.bus2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.imge.bus2.R;
import java.util.List;

public class Time2RecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<List<String>> routeList;

    public Time2RecyclerViewAdapter(Context context, List<List<String>> routeList) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
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

        // 抵達時間
        myHolder.timeList_value.setText(timeList.get(0));

        // 站點名稱
        myHolder.timeList_nameZh.setText(timeList.get(1));
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView timeList_value, timeList_nameZh, timeList_nextStop;
        ImageButton timeList_favorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            timeList_value = itemView.findViewById(R.id.timeList_value);
            timeList_nameZh = itemView.findViewById(R.id.timeList_nameZh);
            timeList_nextStop = itemView.findViewById(R.id.timeList_nextStop);
            timeList_nextStop.setVisibility(View.GONE);
            timeList_favorite = itemView.findViewById(R.id.timeList_favorite);
            timeList_favorite.setVisibility(View.GONE);
        }
    }

    public void updateData(List<List<String>> routeList){
        this.routeList = routeList;
    }

}
