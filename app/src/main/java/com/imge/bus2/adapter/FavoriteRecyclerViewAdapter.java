package com.imge.bus2.adapter;

import android.content.Context;
import java.util.List;

public class FavoriteRecyclerViewAdapter extends TimeRecyclerViewAdapter {

    public FavoriteRecyclerViewAdapter(Context context, List<List<String>> routeList, int goBack) {
        super(context, routeList, goBack);
    }

    @Override
    public void updateData(List<List<String>> routeList) {
        for(int i=0; i<routeList.size(); i++){
            if( routeList.get(i).get(3).equals("-2") || routeList.get(i).get(3).equals("-1") ){
                routeList.remove(i);
                i--;
            }
        }
        this.routeList = routeList;
    }
}
