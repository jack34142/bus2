package com.imge.bus2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.imge.bus2.R;
import com.imge.bus2.Time2Activity;
import com.imge.bus2.mySQLite.FavoriteDAO;
import com.imge.bus2.mySQLite.RouteNameDAO;
import java.util.List;

public class TimeRecyclerViewAdapter extends RecyclerView.Adapter {
    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<List<String>> routeList;
    protected RouteNameDAO routeNameDAO;
    protected FavoriteDAO favoriteDAO;
    protected int goBack;

    public TimeRecyclerViewAdapter(Context context, List<List<String>> routeList, int goBack) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        routeNameDAO = new RouteNameDAO(context);
        favoriteDAO = new FavoriteDAO(context);

        this.goBack = goBack;
        updateData(routeList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.time_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder) holder;

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
        final String routeId = timeList.get(0);
        final String routeNameZh = routeNameDAO.get(timeList.get(0));
        myHolder.timeList_nameZh.setText(routeNameZh);

        // 下一站站點名稱
        myHolder.timeList_nextStop.setText(timeList.get(2));

        // -2表示未創建, -1表示取消
        String arriveStop = favoriteDAO.get(routeId, goBack);
        if( arriveStop.equals("-2") || arriveStop.equals("-1") ){
            myHolder.timeList_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }else {
            myHolder.timeList_favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
        }

        // 點擊事件, 顯示該車抵達所有站點的時間
        myHolder.timeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Time2Activity.class);
                intent.putExtra("routeId", routeId);
                intent.putExtra("goBack", goBack);
                intent.putExtra("title", routeNameZh);
                context.startActivity(intent);
            }
        });

        // 右邊我的最愛按鈕
        myHolder.timeList_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String arriveStop = favoriteDAO.get(routeId, goBack);
                String arriveStopNew = routeList.get(position).get(3);

                if( !arriveStop.equals("-2") ){
                    if( !arriveStop.equals("-1") ){
                        favoriteDAO.update(routeId, goBack, "-1");
                        myHolder.timeList_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }else{
                        favoriteDAO.update(routeId, goBack, arriveStopNew);
                        myHolder.timeList_favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                    }
                }else{
                    favoriteDAO.insert(routeId, goBack, arriveStopNew);
                    myHolder.timeList_favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView timeList_value, timeList_nameZh, timeList_nextStop;
        LinearLayout timeItem;
        ImageButton timeList_favorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            timeItem = itemView.findViewById(R.id.timeItem);
            timeList_value = itemView.findViewById(R.id.timeList_value);
            timeList_nameZh = itemView.findViewById(R.id.timeList_nameZh);
            timeList_nextStop = itemView.findViewById(R.id.timeList_nextStop);
            timeList_favorite = itemView.findViewById(R.id.timeList_favorite);
        }
    }

    public void updateData(List<List<String>> routeList){
        this.routeList = routeList;
    }

}
